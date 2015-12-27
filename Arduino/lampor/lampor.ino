/*
 * AM-HRR3 transmitter test.
 * Connect two push switches up to onPin and offPin, pulled high.
 *
 * Emits a homeeasy simple protocol ON message when the onPin is brought low,
 * and vice versa for offPin.
 *
 * Barnaby Gray 12/2008.
 */


int txPin = 13;

void setup()
{
  pinMode(txPin, OUTPUT);

  Serial.begin(9600);
}

void sendBit(boolean b) {
  if (b) { //high bit
    digitalWrite(txPin, HIGH);
    delayMicroseconds(1125);
    digitalWrite(txPin, LOW);
    delayMicroseconds(375);
  }
  else { //low bit
    digitalWrite(txPin, HIGH);
    delayMicroseconds(375);
    digitalWrite(txPin, LOW);
    delayMicroseconds(1125);
  }
}

void sendPair(boolean b) {
  sendBit(false);
  sendBit(b);
}
void control_lights(int house_code, int unit_code, boolean on){
  digitalWrite(txPin, LOW);
  delayMicroseconds(10000);
  send_package(house_code, unit_code, on);
  digitalWrite(txPin, LOW);
  delayMicroseconds(10000);
  send_package(house_code, unit_code, on);
  digitalWrite(txPin, LOW);
  delayMicroseconds(10000);
  send_package(house_code, unit_code, on);
}

void send_package(int house_code, int unit_code, boolean on){
  house_code = house_code - 1;//1 is really 0
  unit_code = unit_code -1;
  boolean bit_0, bit_1, bit_2, bit_3;
  byte mask = 1;
  //mask out bits
  for(mask = 0b0001; mask<=8; mask<<=1){
    sendPair(house_code & mask);
  }
  for(mask = 0b0001; mask<=8; mask<<=1){
    sendPair(unit_code & mask);
    Serial.println(unit_code & mask);
  }  
  //send on
  sendPair(false);
  sendPair(true);
  sendPair(true);
  sendPair(on);

  sendBit(false);   
 
}

int h_code, u_code, is_on;

void loop() {
  Serial.println("Enter house code: ");
  while (Serial.available() <= 0);
   //transmit(true);
  while (Serial.available() > 0){
    h_code = Serial.parseInt();
    u_code = Serial.parseInt();
    is_on = Serial.parseInt();
    if (Serial.read() == '\n'){
      control_lights(h_code, u_code, is_on);
    }
  }

}
