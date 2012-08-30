#include <Wire.h>
#include <Servo.h>

#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define  SERVO1         11 // digital pin 11, Ear left.
#define  SERVO2         12 // digital pin 12, Ear right(or not).
#define  SERVO3         13 // digital pin 13, Mouth.
// guess "Ear parts" doesn't need to woriking separated. so, I think Two servos can getting same signal at the same time.
// by change the physically servo's direction. well, that gonna be okay.

AndroidAccessory acc("Yunsu Choi",
"AndroFace",
"JellyBean Dispencer",
"1.0",
"http://github.com/YunsuChoi",
"0000000012345678");

Servo servos[3]; //define numbers of servo

void setup()
{
  Serial.begin(115200); // To monitoring ADK by serial port with monitor function in Arduino IDE
  Serial.print("\r\nStart");

  servos[0].attach(SERVO1);
  servos[0].write(0);
  servos[1].attach(SERVO2);
  servos[1].write(180);
  servos[2].attach(SERVO3);
  servos[2].write(90);

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

  if (acc.isConnected()) {
    int len = acc.read(msg, sizeof(msg), -1);

    if (len > 0) { // assumes only one command per packet
      if (msg[0] == 0x1){
        if (msg[1] == 0x0) // 0x10 select servos[NUMBER], this line calls #1 servo function on Demokit app
            servos[0].write(map(msg[2], 0, 255, 0, 180));  //  left arm(0~255)
        else if (msg[1] == 0x1) // This line(0x11) calls #2 servo servo function on Demokit app
            servos[1].write(map(msg[2], 255, 0, 0, 180));  //  right arm(255~0)
        else if (msg[1] == 0x2) // This line(0x11) calls #3 servo servo function on Demokit app
            servos[2].write(map(msg[2], 0, 255, 45, 160));  //  head control range (0~255), Decreased head parts movement angle (65~140)
         */
      }
    }
  }
  else { // When does NOT Connected to Phone.
    // reset outputs to default values when no input in a 10seconds.
    servos[0].write(0);  // left   arm set to initial position(lay)
    servos[1].write(180); // right arm set to initial position(lay)
    servos[2].write(90);  // head face heading to front angle.
  }
} // loop function close
