#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

Servo myservo; //define numbers of servo
int angle = 0;

const int firstLED = 3;
const int secondLED= 4;

int brightness = 0;
int increment = 1;


void setup(){
  Serial.begin(9600); // To monitoring ADK by serial port with monitor function in Arduino IDE
  Serial.print("\r\nStart");

  myservo.attach(9);
}

void loop(){
  
  for(angle=0; angle<180; angle++){
      myservo.write(angle);
      delay(2);
    }
  for(angle=180; angle>=1; angle--){
    myservo.write(angle);
    delay(2);
  }
}
