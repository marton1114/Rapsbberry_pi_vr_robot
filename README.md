# Rapsbberry_pi_vr_robot

# ELŐFELTÉTELEK
- a működéshez szükséges telepíteni az UV4L-t és néhány python module-t

# UV4L szerver
- localhost:8080/stream
- beállítások: "sudo nano /etc/uv4l/uv4l-raspicam.conf"
- beállítások után újraindítás: sudo service uv4l_raspicam restart

# Xbox controllerrel való irányítás esetén
sudo python3 controller.py 

# Telefonnal való irányítás esetén:
- "Marci" nevű, "12345678" jelszavú Hotspot indítása
- "python3 robot_car.py" parancs futtatása
