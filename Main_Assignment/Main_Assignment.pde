// Create Both Shader Objects

PShader starshader;
PShader distortshader;

// Create Objects for Inputs

SoundInput s_input;
ArduinoInput input;

// Create Frame Buffer Objects

PGraphics starFBO;
PGraphics distortFBO;

// Declare Variables for zoom, speed, mix volume, and channel volumes

float v_zoom;
float v_speed;
float v_volume;
float v_lA;
float v_rA;

//-------------------------------------

void setup() {

  //Set Up window

 size(640, 480, P3D);
 noStroke();
 background(0);

 //Innitialise Sound and Arduino inputs

 s_input = new SoundInput(this,"Go.mp3");
 input = new ArduinoInput(this);

 //Starfield Shader setup

 starshader = loadShader("starshade.glsl"); //load starfield shader
 starshader.set("iResolution", float(width), float(height), 0); //set shader resolution
 starFBO = createGraphics(width, height, P3D); //create FBO
 starFBO.shader(starshader); //set starFBO to star field shader

 distortshader = loadShader("distort.glsl"); //load distort shader
 distortshader.set("iResolution", float(width), float(height), 0); //set shader resolution
 distortFBO = createGraphics(width, height, P3D); //create FBO
 distortFBO.shader(distortshader); //set distortFBO to distort shader
 }

//-------------------------------------

void updateShaderParams() { //update shader Parameters

  float[] sensorValues = input.getSensor(); //store sensor Values

  v_zoom = map(sensorValues[1],0.0,1024.0,0.3,1.1); //set zoom var to mapped pot input
  v_speed = map(sensorValues[0],0.0,1024.0,0.005,0.015); //set speed var to mapped pot input
  v_volume = map(s_input.getMvolume(),0.0,0.7,10,30); //set volume var to mapped volume input
  //Check which channel is louder and set the difference to the respective variable, and set the other variable to 0.001
  if(s_input.getLvolume() > s_input.getRvolume()) {
    v_lA = s_input.getLvolume() - s_input.getRvolume();
    v_rA = 0.001;
  } else {
    v_rA = s_input.getRvolume() - s_input.getLvolume();
    v_lA = 0.001;
  }

 //pass variables to star field shader
 starshader.set("zoom", v_zoom);
 starshader.set("speed",v_speed);
 starshader.set("iterations",(int)v_volume);
 starshader.set("l_v",v_lA);
 starshader.set("r_v",v_rA);


}



//-------------------------------------

void draw() {

 updateShaderParams(); //update shader details

 starFBO.beginDraw();
 starshader.set("iGlobalTime", millis() / 1000.0); // pass in a millisecond clock to enable animation
 shader(starshader); //set shader to star field shader
 starFBO.rect(0, 0, width, height); // We draw a rect here for our shader to draw onto
 starFBO.endDraw();

 float[] sensorValues = input.getSensor(); //get sensor values

 if (sensorValues[2] == 0.0){ //check if button is pressed
  image(starFBO,0,0,width,height); //draw plain star field shader
} else {
  distortFBO.beginDraw();
  distortshader.set("iGlobalTime", millis() / 1000.0); // pass in a millisecond clock to enable animation
  distortshader.set("ispec", starFBO); //set texture to star
  shader(distortshader); //set shader to star field shader
  distortFBO.rect(0, 0, width, height); //draw rectangle for shader to draw to
  distortFBO.endDraw();
  image(distortFBO,0,0,width,height); //draw distorted star field shader
}
}
