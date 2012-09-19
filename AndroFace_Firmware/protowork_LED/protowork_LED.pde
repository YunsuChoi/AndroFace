#include <Wire.h>
#include <Servo.h>


const int firstLED = 3;
const int secondLED= 4;

void setup(){
  Serial.begin(9600); // To monitoring ADK by serial port with monitor function in Arduino IDE
  Serial.print("\r\nStart");
}

int brightness = 0;
int increment = 1;

void loop(){
      
      if(brightness > 255){
         increment = -1; 
        }
        else if(brightness < 1){
          increment = 1;
        }
      brightness = brightness + increment;
      
      analogWrite(firstLED, brightness);
      analogWrite(secondLED, brightness);

      delay(10);
}
