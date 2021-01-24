#include <WiFi.h>
#include <FirebaseESP32.h>
#include <PZEM004Tv30.h>
#include <analogWrite.h>
#include "DHT.h"
#include <NTPClient.h>
#include <TimeLib.h>
#include <SPI.h>

#define FIREBASE_HOST "ENTER_YOUR_FIREBASE_URL"
#define FIREBASE_AUTH "ENTER_FIREBASE_KEY"

#define WIFI_SSID "YOUR_SSID"
#define WIFI_PASSWORD "YOUR_PWD"

#define DHTTYPE DHT11

char daysOfTheWeek[7][12] = {"Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday"};
char monthsOfYear[12][12] = {"January", "February", "March", "April", "May", "June", "July" , "August", "September", "October", "November", "December"};

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP);

String formattedDate, formattedTime, dayStamp, timeStamp, autoday, withoutsec, months, finalMonth;
int monthint, monthdiff;
int hi, ti, flame_detected, mq6 ,mq135 ,soil, dbdistance,level;
float h ,t;
uint8_t DHTPin = 27;
 
DHT dht(DHTPin, DHTTYPE);
PZEM004Tv30 pzem(&Serial2);

int in1 = 4;
int in2 = 0;
int in3 = 2;
int in4 = 15;
int fire = 5;
int buzzer = 26;
int trigPin = 18;
int echoPin = 19;
long duration;
int distance;
int voltagei ,frequencyi ;
FirebaseData firebaseData;
WiFiClient client;

void setup()
{
  Serial.begin(115200);

  pinMode(in1, OUTPUT);
  pinMode(in2, OUTPUT);
  pinMode(in3, OUTPUT);
  pinMode(in4, OUTPUT);
  pinMode(fire, INPUT);
  pinMode(trigPin, OUTPUT); 
  pinMode(echoPin, INPUT); 
  pinMode(buzzer, OUTPUT);

  pzem.resetEnergy();
  pzem.setAddress(0x42);

  dht.begin();
  
  WiFi.begin(WIFI_SSID, WIFI_PASSWORD);
  Serial.print("Connecting to Wi-Fi");
  while (WiFi.status() != WL_CONNECTED)
  {
    Serial.print(".");
    delay(300);
  }
  Serial.print("Connected with IP: ");
  Serial.println(WiFi.localIP());
  Serial.println();

  timeClient.begin();
  timeClient.setTimeOffset(19800);

  while(!timeClient.update()) {
    timeClient.forceUpdate();
  }
 
  Firebase.begin(FIREBASE_HOST, FIREBASE_AUTH);
  Firebase.reconnectWiFi(true);
}

void loop() {
float current = pzem.current();
float power = pzem.power();
float energy = pzem.energy();
float voltage = pzem.voltage();
float frequency = pzem.frequency();
float pf = pzem.pf();
voltagei = (int) voltage;
frequencyi = (int) frequency;
digitalWrite(trigPin, LOW);
delayMicroseconds(2);
digitalWrite(trigPin, HIGH);
delayMicroseconds(10);
digitalWrite(trigPin, LOW);
duration = pulseIn(echoPin, HIGH);
distance = duration*0.034/2;
flame_detected = digitalRead(fire);
mq6 = digitalRead(12);
mq135 = analogRead(35);
soil = analogRead(36);
h = dht.readHumidity();
t = dht.readTemperature();
hi = (int) h;
ti = (int) t;

  while(!timeClient.update()) {
    timeClient.forceUpdate();
  }
  
  formattedDate = timeClient.getFormattedDate();
  int splitT = formattedDate.indexOf("T");
  dayStamp = formattedDate.substring(0, splitT);
  months=dayStamp.substring(5,7);
  monthint=months.toInt();
  monthdiff=monthint-1;
  finalMonth = String(monthsOfYear[monthdiff]);
  timeStamp = formattedDate.substring(splitT+1, formattedDate.length()-1);
  withoutsec=timeStamp.substring(0,5);
  autoday=String(daysOfTheWeek[timeClient.getDay()]);
if(Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port1/days"))
  {
String p1days=firebaseData.stringData();
 p1days.replace('[', ' ');
 p1days.replace(']', ' ');
Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port1/ontime");
String p1otime=firebaseData.stringData();
Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port1/offtime");
String p1oftime=firebaseData.stringData();
String myarrayp1[] = {p1days} ;
for ( int i = 0; i < sizeof(myarrayp1); i++ )  
{
if(myarrayp1[i]==autoday)
{
  if(p1otime.equals(withoutsec))
{
  Firebase.setString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port1","ON");
}

if(p1oftime.equals(withoutsec))
{
  Firebase.setString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port1","OFF");
}
}
  }
  }

  if(Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port2/days"))
  {
String p2days=firebaseData.stringData();
 p2days.replace('[', ' ');
 p2days.replace(']', ' ');
Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port2/ontime");
String p2otime=firebaseData.stringData();
Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port2/offtime");
String p2oftime=firebaseData.stringData();
String myarrayp2[] = {p2days} ;
for ( int i = 0; i < sizeof(myarrayp2); i++ )  
{
if(myarrayp2[i]==autoday)
{
  if(p2otime.equals(withoutsec))
{
  Firebase.setString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port2","ON");
}

if(p2oftime.equals(withoutsec))
{
  Firebase.setString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port2","OFF");
}
}
  }
  }

  if(Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port3/days"))
  {
String p3days=firebaseData.stringData();
  p3days.replace('[', ' ');
  p3days.replace(']', ' ');
Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port3/ontime");
String p3otime=firebaseData.stringData();
Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port3/offtime");
String p3oftime=firebaseData.stringData();
String myarrayp3[] = {p3days} ;
if(p3days.indexOf(autoday) > 0)
{
  if(p3otime.equals(withoutsec))
{
  Firebase.setString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port3","ON");
}

if(p3oftime.equals(withoutsec))
{
  Firebase.setString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port3","OFF");
}
}
  }

  if(Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port4/days"))
  {
String p4days=firebaseData.stringData();
 p4days.replace('[', ' ');
 p4days.replace(']', ' ');
Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port4/ontime");
String p4otime=firebaseData.stringData();
Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/AutomaticOnOff/port4/offtime");
String p4oftime=firebaseData.stringData();
String myarrayp4[] = {p4days} ;
for ( int i = 0; i < sizeof(myarrayp4); i++ )  
{
if(myarrayp4[i]==autoday)
{
  if(p4otime.equals(withoutsec))
{
  Firebase.setString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port4","ON");
}

if(p4oftime.equals(withoutsec))
{
  Firebase.setString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port4","OFF");
}
}
  }
  } 
  if( !isnan(current) ){
        if(Firebase.getFloat(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/current/"+finalMonth+"/"+dayStamp))
        {
          float oldVal=firebaseData.floatData();
          float newVal=oldVal+(current/1000.000f);
          Firebase.setFloat(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/current/"+finalMonth+"/"+dayStamp,newVal);
        }
        else
        {
          float newVal=current/1000.000f;
          Firebase.setFloat(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/current/"+finalMonth+"/"+dayStamp,newVal);
        }
    }

    if( !isnan(power) ){
        if(Firebase.getFloat(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/power/"+finalMonth+"/"+dayStamp))
        {
          float oldVal=firebaseData.floatData();
          float newVal=oldVal+(power/10.0f);
          Firebase.setFloat(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/power/"+finalMonth+"/"+dayStamp,newVal);
        }
        else
        { 
          float newVal=power/10.0f;
          Firebase.setFloat(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/power/"+finalMonth+"/"+dayStamp,newVal);
        }
    }
    
    if( !isnan(voltage) ){
        Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/voltage",voltagei);
    } 

    if( !isnan(frequency) ){
        Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/frequency",frequencyi);
  }
 
    if( !isnan(pf) ){
        Firebase.setFloat(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/electricity/pf",pf);
    }
    
   if(Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port1"))
  {
String val=firebaseData.stringData();
if(val=="ON")
{
   digitalWrite(in1,LOW);
}
else
{
   digitalWrite(in1,HIGH);
}
  }

  if(Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port2"))
  {
String val=firebaseData.stringData();
if(val=="ON")
{
   digitalWrite(in2,LOW);
}
else
{
   digitalWrite(in2,HIGH);
}
  }
  
  if(Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port3"))
  {
String val=firebaseData.stringData();
if(val=="ON")
{
   digitalWrite(in3,LOW);
}
else
{
   digitalWrite(in3,HIGH);
}
  }

  if(Firebase.getString(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/components/port4"))
  {
String val=firebaseData.stringData();
if(val=="ON")
{
   digitalWrite(in4,LOW);
}
else
{
   digitalWrite(in4,HIGH);
}
  }
  
  Firebase.getInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/water/distance");
  dbdistance = firebaseData.intData();
  level = dbdistance - distance;
  Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/water/level",level);
  Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/air/temp", ti);
  Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/air/humidity", hi);
  Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/gas/gasleak", mq6);
  Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/air/mq135q", mq135);
  Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/plant/moisture", soil);
  Firebase.setInt(firebaseData, "users/3Oou95cKpPX6OPBduFNTOHx6sO32/fire/fireleak", flame_detected);
  if(flame_detected==0)
  {
      digitalWrite(buzzer, HIGH);
  }
  else
  {
      digitalWrite(buzzer, LOW);
  }
  delay(1000);
  }
