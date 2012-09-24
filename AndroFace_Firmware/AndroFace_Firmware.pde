#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

const int firstLED = 3;
const int secondLED= 4;

Servo myservo1; // ear left
Servo myservo2; //ear right
Servo myservo3; // mouth

int angle = 0;

int angle1;
int angle2;

AndroidAccessory acc("Yunsu Choi",
"Pedometer",
"Pedometer Printing Board",
"1.0",
"http://github.com/YunsuChoi",
"0000000012345678");

//AndroidAccessory acc("Yunsu Choi",
//"AndroFace",
//"JellyBean Dispencer",
//"1.0",
//"http://github.com/YunsuChoi",
//"0000000012345678");

Servo servos[3]; //define numbers of servo

void setup()
{
  Serial.begin(9600); // To monitoring ADK by serial port with monitor function in Arduino IDE
  Serial.print("\r\nStart");

  myservo1.attach(9);
    myservo1.write(0);
  myservo2.attach(10);
    myservo2.write(0);
  myservo3.attach(11);
    myservo3.write(90);
    
  analogWrite(firstLED, 0);
  analogWrite(secondLED, 0);
  
  acc.powerOn(); //Ignite voltage on a elements
}
void loop() // loop phase is imcomplete, 
{
  byte err;
  byte idle;
  static byte count = 0;
  byte msg[3];

  if (acc.isConnected()) {
    Serial.print("Accessory connected. ");
    int len = acc.read(msg, sizeof(msg), -1);
    Serial.print("Message length: ");
    Serial.println(len, DEC);
  }
  delay(0); // control response timing
  // Led control
    if(brightness > 255){
      increment = -1;
    }
    else if(brightness <1){
      increment = 1;
    }
    
  if (acc.isConnected()) {
    int len = acc.read(msg, sizeof(msg), -1);
    if (len > 0) { // assumes only one command per packet
      if (msg[0] == 0x1) {
        if (msg[1] == 0x0) { // 0x10 select servos[NUMBER], this line calls #1 servo function on Demokit app
                myservo3.write(90);
              /*
              for(angle=0; angle<180; angle += 1){
                angle1 = 180 - angle;
                angle2 = 0 + (angle);
                analogWrite(firstLED, angle);
                analogWrite(secondLED, angle);
          
                myservo1.write(angle1);
                myservo2.write(angle2);

                delay(10); // DO NOT set delay(x) below 2
              }
              */
        }
      
        else if (msg[1] == 0x1) { // This line(0x1) calls #2 function on the app
              myservo3.write(0);
          /*
          for(angle=180; angle>=1; angle--){
              angle1 = 180 - angle;
              angle2 = 0 + (angle);
              analogWrite(firstLED, angle);
              analogWrite(secondLED, angle);
        
              myservo1.write(angle1);
              myservo2.write(angle2);

              delay(10);
         }
         */
      }
    }
  }
  else { // When does NOT Connected to Phone.
    // reset outputs to default values when no input in a 10seconds.
    myservo1.write(0);  // left   arm set to initial position(lay)
    myservo2.write(180); // right arm set to initial position(lay)
    myservo2.write(90);
    // servos[2].write(90);  // head face heading to front angle.
  }
}
