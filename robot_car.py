#!/usr/bin/env python3

import xbox

import socket
import sys
import select
import fcntl
import struct
import time
import signal
import math

from gpiozero import AngularServo
import RPi.GPIO as GPIO



# SIGNAL HANDLING ########################

def handler(signum, frame):
    close_program()


def close_program():
    print("Exiting...")
    # close all the sockets
    for s in inputs:
        s.close()
    # exit the program
    sys.exit()
    #joy.close()

signal.signal(signal.SIGINT, handler)


# GPIO HANDLING ##########################


servo = AngularServo(2, min_pulse_width=0.0006, max_pulse_width=0.0023)

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


def control_servo(degree):
    if (degree < 90 and degree > -90):
        servo.angle = degree


def control_hbridge(x, y):
    # nem megyek elore vagy hatra
    if (y == 0):
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
    # elore megy
    elif (y > 0.0):
        pi_pwm0.ChangeDutyCycle(abs(y * 100))
        pi_pwm1.ChangeDutyCycle(abs(y * 100))

        # balra es egyenesen
        if (x < 0.0):
            pi_pwm1.ChangeDutyCycle(clamp(abs(y * 100) - abs(x * 100), 0, 100))
        # jobbra egyenesen
        elif (x > 0.0):
            pi_pwm0.ChangeDutyCycle(clamp(abs(y * 100) - abs(x * 100), 0, 100))

        GPIO.output(IN1, GPIO.HIGH)
        GPIO.output(IN2, GPIO.LOW)
        GPIO.output(IN3, GPIO.HIGH)
        GPIO.output(IN4, GPIO.LOW)
    # hatra megy
    elif (y < 0.0):
        pi_pwm0.ChangeDutyCycle(abs(y * 100))
        pi_pwm1.ChangeDutyCycle(abs(y * 100))

        # balra es egyenesen
        if (x < 0.0):
            pi_pwm1.ChangeDutyCycle(clamp(abs(y * 100) - abs(x * 100), 0, 100))
        # jobbra egyenesen
        elif (x > 0.0):
            pi_pwm0.ChangeDutyCycle(clamp(abs(y * 100) - abs(x * 100), 0, 100))

        GPIO.output(IN1, GPIO.LOW)
        GPIO.output(IN2, GPIO.HIGH)
        GPIO.output(IN3, GPIO.LOW)
        GPIO.output(IN4, GPIO.HIGH)


# SOCKET HANDLING ########################



#print("Waiting 10 seconds to make sure the device connects to the WIFI")
#time.sleep(10);

# create a socket object
server_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)

# get local machine ip-address
def get_ip_address(ifname):
    s = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    return socket.inet_ntoa(fcntl.ioctl(
        s.fileno(),
        0x8915,  # SIOCGIFADDR
        struct.pack('256s', bytes(ifname[:15], 'utf-8'))
    )[20:24])


host = get_ip_address('wlan0')
print(host)


# set a port number
port = 5005

# bind the socket to a public host, and a well-known port
server_socket.bind((host, port))

# set the server to listen for incoming connections
server_socket.listen(5)

print("Server listening on {}:{}".format(host, port))

# set the socket to non-blocking mode
server_socket.setblocking(False)

# initialize the list of input sockets
inputs = [server_socket, sys.stdin]

# loop forever
while True:
    # wait for input from any socket
    read_sockets, write_sockets, error_sockets = select.select(inputs, [], [])

    # loop through the input sockets that have data
    for socket in read_sockets:
        # if the socket is the server socket, it means there's a new incoming connection
        if socket == server_socket:
            # accept the connection and get a new socket object and address
            client_socket, address = server_socket.accept()
            print("New connection from {}:{}".format(address[0], address[1]))
            # add the new client socket to the input list
            inputs.append(client_socket)
        # if the socket is stdin, it means the user entered a command
        elif socket == sys.stdin:
            # read the input command
            command = sys.stdin.readline().strip()
            # if the command is the escape key, exit the program
            if command == chr(27):
                close_program()
        # otherwise, it means there's data from a client socket
        else:
            # receive the data from the client socket
            data = socket.recv(12)
            # if there's no data, it means the client closed the connection

            if not data:
                print("Connection closed by {}:{}".format(socket.getpeername()[0], socket.getpeername()[1]))
                # remove the client socket from the input list
                inputs.remove(socket)
            # otherwise, print the received data
            else:
                float_array = list(struct.unpack('f' * (len(data) // 4), data))

                #print(float_array)

                control_servo(float_array[0])

                control_hbridge(float_array[1], float_array[2])

GPIO.cleanup()
joy.close()
