/* 
 Project AndroFace 
 Author: Yunsu Choi 
 Source Code: https://github.com/YunsuChoi
 */

#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

Servo myservo3; // mouth

AndroidAccessory acc("Yunsu Choi",
"AndroFace",
"Project AndroFace",
"1.0",
"http://github.com/YunsuChoi",
"0000000012345678");

void setup()
{
  //Serial.begin(115200); // To monitoring ADK by serial port with monitor function in Arduino IDE
  //Serial.print("\r\nStart");

  myservo3.attach(11);
  myservo3.write(90);

  acc.powerOn(); //Ignite voltage on a elements
}
void loop() // loop phase is imcomplete, 
{
  byte msg[3];
  delay(0); // control response timing

  if (acc.isConnected()) { //whenever connected to Android phone.
    int len = acc.read(msg, sizeof(msg), -1);
    if (len > 0) { // assumes only one command per packet
      if (msg[0] == 0x1) {
        if (msg[1] == 0x0) { // Function #1 from Android app

          if (msg[2]==1){ // data from android.
            myservo3.write(90);
            delay(5000); // DO NOT set delay(x) below 2
            myservo3.write(0);
          }

          else if(msg[2]!=1)
          {
            myservo3.write(0);
          }
        }
      }
    }
  }
  else { // When does NOT Connected to Phone.
    myservo3.write(0); // Close Mouth
  }
}
