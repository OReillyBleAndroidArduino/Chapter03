void setup() {
  Serial.begin(9600);
  while (!Serial) {;} // wait for serial to begin
  Serial.println("Hello World!");
  Serial.println(); // flush the previous buffer
}

void loop() {
}
