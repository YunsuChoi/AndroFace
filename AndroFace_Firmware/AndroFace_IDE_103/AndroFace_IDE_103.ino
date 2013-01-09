/* 
 Project AndroFace 
 Author: Yunsu Choi
 Source Code: https://github.com/YunsuChoi/AndroFace
 */

#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

Servo myservo; 

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

  myservo.attach(8);
  myservo.write(0);

  acc.powerOn(); //Ignite voltage on a elements
}
void loop() {
  byte msg[3];
  delay(0); // control response timing

  //bail:
  if (acc.isConnected()) { //whenever connected to Android phone.
    int len = acc.read(msg, sizeof(msg), -1);
    if (len > 0) { // assumes only one command per packet
      if (msg[0] == 0x1) {
        if (msg[1] == 0x0) { // Function #1 from Android app
          if (msg[2]==1){ // data from android.
            mouthOpen();
          }
          else if(msg[2]!=1) {
            mouthClose();
          }
        }
      }
    }
    else { // When does NOT Connected to Phone.
      mouthClose();
    }
  }
}

void mouthOpen(){
  myservo.write(90); 
  delay(5000);
}

void mouthClose(){
  myservo.write(0);
  delay(1000);
  myservo.detach();
}
