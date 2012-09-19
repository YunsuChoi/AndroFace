#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

const int firstLED = 3;
const int secondLED= 4;

Servo myservo1; //define numbers of servo
Servo myservo2;

int angle = 0;

int angle1;
int angle2;

void setup(){
  Serial.begin(9600); // To monitoring ADK by serial port with monitor function in Arduino IDE
  Serial.print("\r\nStart");

  myservo1.attach(9);
    myservo1.write(0);
  myservo2.attach(10);
    myservo2.write(0);
  analogWrite(firstLED, 0);
  analogWrite(secondLED, 0);
}

void loop(){
  for(angle=0; angle<180; angle += 1){
      angle1 = 180 - angle;
      angle2 = 0 + (angle);
      analogWrite(firstLED, angle);
      analogWrite(secondLED, angle);

      myservo1.write(angle1);
      myservo2.write(angle2);
      
      delay(10); // DO NOT set below 2
    }

  for(angle=180; angle>=1; angle--){
      angle1 = 180 - angle;
      angle2 = 0 + (angle);
      analogWrite(firstLED, angle);
      analogWrite(secondLED, angle);

      myservo1.write(angle1);
      myservo2.write(angle2);

      delay(10);
  }
}
