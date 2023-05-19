# Rapsbberry_pi_vr_robot

# Rövid leírás
Az alábbi projekt tartalma két Python fájl és egy Android alkalmazás. Ezek lehetővé teszik, hogy egy megfelelően összeállított, Raspberry Pi-al működő, kamerával felszerelt robot autót irányítsunk az Android telefonunkkal. Az alkalmazás használható kontrollerként, de elérhető benne egy VR szemüveges mód is, amely során a robotra felszerelt kamerát a telefon mozgásával irányíthatjuk. VR mód használata esetén lehetőség van xbox 360 kontroller csatlakoztatására, amellyel irányíthatjuk az autó kerekeit.

<img src="https://raw.githubusercontent.com/marton1114/Rapsbberry_pi_vr_robot/main/menu.jpg" width="50%" height="50%">
![menu]()
![controller]()
![vr]()

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
