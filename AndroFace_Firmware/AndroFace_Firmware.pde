#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

const int firstLED = 3;
const int secondLED = 4;

Servo myservo1; // ear left
Servo myservo2; // ear right
Servo myservo3; // mouth

int angle = 0;
int angle1; //left ear
int angle2; // right ear

AndroidAccessory acc("Yunsu Choi",
"AndroFace",
"Project AndroFace",
"1.0",
"http://github.com/YunsuChoi",
"0000000012345678");

void setup()
{
  Serial.begin(115200); // To monitoring ADK by serial port with monitor function in Arduino IDE
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
  byte msg[3];
  /* for debug?
  if (acc.isConnected()) {
   Serial.print("Accessory connected.\n");
   int len = acc.read(msg, sizeof(msg), -1);
   Serial.print("Message length: ");
   Serial.println(len, DEC);
   }
   */
  delay(0); // control response timing

  if (acc.isConnected()) { //whenever connected to Android phone.
    int len = acc.read(msg, sizeof(msg), -1);
    if (len > 0) { // assumes only one command per packet
      if (msg[0] == 0x1) {
        if (msg[1] == 0x0) { // Function #1 from Android app
          // int print_msg = (int) msg[2]; // get data from Android and store by int type
          if (msg[2]==1){ // data from android.
            //acc.write(msg, 3);
            analogWrite(firstLED, 180);
            delay(1000);
            analogWrite(firstLED, 0);
            delay(1000);
            analogWrite(firstLED, 180);
          }
          else if(msg[2]!=1)
          {
            //acc.write(msg, 3);
            analogWrite(firstLED, 0);
          }
          /*
            while (msg[2]!=1){ // data from android.
           // http://beanbox.co.kr/textcube?page=5
           for(angle=0; angle<180; angle += 1){
           angle1 = 180 - angle;
           angle2 = 0 + (angle);
           analogWrite(firstLED, angle);
           analogWrite(secondLED, angle);
           
           myservo1.write(angle1);
           myservo2.write(angle2);
           myservo3.write(90);
           delay(10); // DO NOT set delay(x) below 2
           }
           for(angle=180; angle>=1; angle--){
           angle1 = 180 - angle;
           angle2 = 0 + (angle);
           analogWrite(firstLED, angle);
           analogWrite(secondLED, angle);
           
           myservo1.write(angle1);
           myservo2.write(angle2);
           myservo3.write(0);
           delay(10);
           }  
           if (msg[2]!=0) break;
           }
           */
        }
      }
      /* Function #2 from Android app
       else if (msg[1] == 0x1) { // This line(0x1) calls #2 function on the app
       for(angle=0; angle<180; angle += 1){
       angle1 = 180 - angle;
       angle2 = 0 + (angle);
       analogWrite(firstLED, angle);
       analogWrite(secondLED, angle);
       
       myservo1.write(angle1);
       myservo2.write(angle2);
       myservo3.write(90);
       delay(10); // DO NOT set delay(x) below 2
       }
       for(angle=180; angle>=1; angle--){
       angle1 = 180 - angle;
       angle2 = 0 + (angle);
       analogWrite(firstLED, angle);
       analogWrite(secondLED, angle);
       
       myservo1.write(angle1);
       myservo2.write(angle2);
       myservo3.write(0);
       delay(10);
       }
       }
       */
    }
  }
  else { // When does NOT Connected to Phone.
    // reset outputs to default values when no input in a 10seconds.
    myservo1.write(180);  // left Ear
    myservo2.write(0); // right Ear
    myservo3.write(90); // Close Mouth
    analogWrite(firstLED, 50); // eye on (0~180)
    analogWrite(secondLED, 10); // eye on
  }
}













