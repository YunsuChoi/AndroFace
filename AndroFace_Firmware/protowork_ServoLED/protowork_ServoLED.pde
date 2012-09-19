#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

const int firstLED = 3;
const int secondLED= 4;

Servo myservo1; //define numbers of servo

int angle = 0;

void setup(){
  Serial.begin(9600); // To monitoring ADK by serial port with monitor function in Arduino IDE
  Serial.print("\r\nStart");

  myservo1.attach(9);
//  myservo2.attach(10, minPulse, maxPulse);
  myservo1.write(0);
  analogWrite(firstLED, 180);
  analogWrite(secondLED, 180);
//  myservo2.write(0);
}

void loop(){
  for(angle=0; angle<180; angle++){
      analogWrite(firstLED, angle);
      analogWrite(secondLED, angle);
            delay(10);
      myservo1.write(angle);

    }
  for(angle=180; angle>=1; angle--){
    analogWrite(firstLED, angle);
    analogWrite(secondLED, angle);
          delay(10);
    myservo1.write(angle);

  }
}
