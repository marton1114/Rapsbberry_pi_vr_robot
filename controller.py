import xbox
import RPi.GPIO as GPIO
import time
import os

joy = xbox.Joystick()

def clamp(n, smallest, largest):
    return max(smallest, min(n, largest))

GPIO.setmode(GPIO.BCM)
GPIO.setwarnings(False)



# Motor 1 pins
ENA = 12 #23
IN1 = 24
IN2 = 25

# Motor 2 pins
ENB = 13 #17
IN3 = 27
IN4 = 22

# Set up GPIO pins as output
GPIO.setup(ENA, GPIO.OUT)
GPIO.setup(ENB, GPIO.OUT)
GPIO.setup(IN1, GPIO.OUT)
GPIO.setup(IN2, GPIO.OUT)
GPIO.setup(IN3, GPIO.OUT)
GPIO.setup(IN4, GPIO.OUT)

GPIO.output(ENA, GPIO.LOW)
GPIO.output(ENB, GPIO.LOW)
GPIO.output(IN1, GPIO.LOW)
GPIO.output(IN2, GPIO.LOW)
GPIO.output(IN3, GPIO.LOW)
GPIO.output(IN4, GPIO.LOW)


pi_pwm0 = GPIO.PWM(ENA, 100)
pi_pwm1 = GPIO.PWM(ENB, 100)

pi_pwm0.start(0)
pi_pwm1.start(0)


def control_hbridge(x, y):
    #GPIO.cleanup()
    # nem megyek előre vagy hátra
    if (y == 0):
#        GPIO.output(ENA, GPIO.HIGH)
#        GPIO.output(ENB, GPIO.HIGH)
        pi_pwm0.ChangeDutyCycle(abs(x * 100))
        pi_pwm1.ChangeDutyCycle(abs(x * 100))

        # csak balra megy
        if (x > 0):
            GPIO.output(IN1, GPIO.LOW)
            GPIO.output(IN2, GPIO.HIGH)
            GPIO.output(IN3, GPIO.HIGH)
            GPIO.output(IN4, GPIO.LOW)
        # csak jobbra megy
        elif (x < 0):
            GPIO.output(IN1, GPIO.HIGH)
            GPIO.output(IN2, GPIO.LOW)
            GPIO.output(IN3, GPIO.LOW)
            GPIO.output(IN4, GPIO.HIGH)
        # nem megy semerre
        else:
            GPIO.output(IN1, GPIO.HIGH)
            GPIO.output(IN2, GPIO.LOW)
            GPIO.output(IN3, GPIO.LOW)
            GPIO.output(IN4, GPIO.HIGH)
#            GPIO.output(ENA, GPIO.LOW)
#            GPIO.output(ENB, GPIO.LOW)
    # előre megy
    elif (y > 0.0):
        pi_pwm0.ChangeDutyCycle(abs(y * 100))
        pi_pwm1.ChangeDutyCycle(abs(y * 100))
#        GPIO.output(ENA, GPIO.HIGH)
#        GPIO.output(ENB, GPIO.HIGH)

        # balra és egyenesen
        if (x < 0.0):
            pi_pwm1.ChangeDutyCycle(clamp(abs(y * 100) - abs(x * 100), 0, 100))
            #GPIO.output(ENB, GPIO.LOW)
        # jobbra egyenesen
        elif (x > 0.0):
            pi_pwm0.ChangeDutyCycle(clamp(abs(y * 100) - abs(x * 100), 0, 100))
            #GPIO.output(ENA, GPIO.LOW)

        GPIO.output(IN1, GPIO.HIGH)
        GPIO.output(IN2, GPIO.LOW)
        GPIO.output(IN3, GPIO.HIGH)
        GPIO.output(IN4, GPIO.LOW)
    # hátra megy
    elif (y < 0.0):
        pi_pwm0.ChangeDutyCycle(abs(y * 100))
        pi_pwm1.ChangeDutyCycle(abs(y * 100))
        #GPIO.output(ENA, GPIO.HIGH)
        #GPIO.output(ENB, GPIO.HIGH)

        # balra és egyenesen
        if (x < 0.0):
            pi_pwm1.ChangeDutyCycle(clamp(abs(y * 100) - abs(x * 100), 0, 100))
            #GPIO.output(ENB, GPIO.LOW)
        # jobbra egyenesen
        elif (x > 0.0):
            pi_pwm0.ChangeDutyCycle(clamp(abs(y * 100) - abs(x * 100), 0, 100))
            #GPIO.output(ENA, GPIO.LOW)

        GPIO.output(IN1, GPIO.LOW)
        GPIO.output(IN2, GPIO.HIGH)
        GPIO.output(IN3, GPIO.LOW)
        GPIO.output(IN4, GPIO.HIGH)




while (True):
    x = joy.leftX()
    y = joy.rightTrigger() - joy.leftTrigger()

    if joy.B():
        break

    control_hbridge(x, y)
    time.sleep(0.1)

GPIO.cleanup()
joy.close()
