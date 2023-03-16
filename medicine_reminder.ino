
#include "BluetoothSerial.h"

#if !defined(CONFIG_BT_ENABLED) || !defined(CONFIG_BLUEDROID_ENABLED)
#error Bluetooth is not enabled! Please run `make menuconfig` to and enable it
#endif

#if !defined(CONFIG_BT_SPP_ENABLED)
#error Serial Bluetooth not available or not enabled. It is only available for the ESP32 chip. 
#endif

BluetoothSerial SerialBT;

const int ledPin1 = 5;
const int ledPin2 = 18;
const int ledPin3 = 19;
const int ledPin4 = 22;
const int ledPin5 = 26;
const int ledPin6 = 32;

#define DOOR_SENSOR_PIN  23  // ESP32 pin GIOP23 connected to door sensor's pin

int doorState;
#define BUZZER_PIN 21 // ESP32 GIOP21 pin connected to Buzzer's pin
char inComing;
char STOP = '9';

void setup() {
  Serial.begin(115200);
  SerialBT.begin("MedicineReminder"); //Bluetooth device name
  Serial.println("The device started, now you can pair it with bluetooth!");

  pinMode(DOOR_SENSOR_PIN, INPUT_PULLUP);
  pinMode (ledPin1, OUTPUT);
  pinMode (ledPin2, OUTPUT);
  pinMode (ledPin3, OUTPUT);
  pinMode (ledPin4, OUTPUT);
  pinMode (ledPin5, OUTPUT);
  pinMode (ledPin6, OUTPUT);
  pinMode(BUZZER_PIN, OUTPUT);       // set ESP32 pin to output mode
}
#define CLOSED LOW
#define OPENED HIGH
#define MEDICINE_ALERT_RECIVED 20
#define DOOR_OPENED_AFTER_ALERT 21


int j=0,PREV_STATE=CLOSED;
bool SETTINGS=false;

void Reset(){
      digitalWrite (ledPin1, LOW);
      digitalWrite (ledPin2, LOW);
      digitalWrite (ledPin3, LOW);
      digitalWrite (ledPin4, LOW);
      digitalWrite (ledPin5, LOW);
      digitalWrite (ledPin6, LOW);
      digitalWrite(BUZZER_PIN,LOW);
}

void loop() {

   

  doorState = digitalRead(DOOR_SENSOR_PIN); 

 if (doorState == OPENED){

    if(PREV_STATE==MEDICINE_ALERT_RECIVED) {
      PREV_STATE=DOOR_OPENED_AFTER_ALERT;
      SerialBT.write(STOP);
    }

    inComing='0';
    digitalWrite(BUZZER_PIN, LOW);  // turn off
    doorState = digitalRead(DOOR_SENSOR_PIN);
    
  }
  else if(doorState == CLOSED && PREV_STATE == DOOR_OPENED_AFTER_ALERT){
      Reset();
      PREV_STATE=CLOSED;
  }
  else{
   
    if (SerialBT.available()) 
    {
      inComing = SerialBT.read();
      Serial.println(inComing);
      SETTINGS=false;

      switch(inComing){
        case '1':
          digitalWrite (ledPin1, HIGH);	
          break;
        case '2':
          digitalWrite (ledPin2, HIGH);
          break;
        case '3':
          digitalWrite (ledPin3, HIGH);	
          break;
        case '4':
          digitalWrite (ledPin4, HIGH);
          break;
        case '5':
          digitalWrite (ledPin5, HIGH);
          break;
        case '6':
          digitalWrite (ledPin6, HIGH);	
          break;
        case 'f': Reset();
        default:SETTINGS=true;
      }

      if(!SETTINGS) {
        PREV_STATE=MEDICINE_ALERT_RECIVED;
        digitalWrite(BUZZER_PIN, HIGH);
        delay(100);
      }
      inComing='0';
    
    }
  }
}