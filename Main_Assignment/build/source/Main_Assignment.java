import processing.core.*; 
import processing.data.*; 
import processing.event.*; 
import processing.opengl.*; 

import processing.serial.*; 
import ddf.minim.*; 
import ddf.minim.analysis.*; 
import ddf.minim.effects.*; 
import ddf.minim.signals.*; 
import ddf.minim.spi.*; 
import ddf.minim.ugens.*; 

import java.util.HashMap; 
import java.util.ArrayList; 
import java.io.File; 
import java.io.BufferedReader; 
import java.io.PrintWriter; 
import java.io.InputStream; 
import java.io.OutputStream; 
import java.io.IOException; 

public class Main_Assignment extends PApplet {

// Create Both Shader Objects

PShader starshader;
PShader distortshader;

// Create Objects for Inputs

SoundInput s_input;
ArduinoInput input;

// Create Frame Buffer Objects

PGraphics starFBO;
PGraphics distortFBO;

// Declare Variables for zoom, speed, mix volume, and channel volumes, and audio

float v_zoom;
float v_speed;
float v_volume;
float v_lA;
float v_rA;

boolean audcon;

//-------------------------------------

public void setup() {

  //Set Up window

 
 noStroke();
 background(0);

 //Innitialise Sound and Arduino inputs

 s_input = new SoundInput(this,"Go.mp3");
 input = new ArduinoInput(this);

 //Starfield Shader setup

 starshader = loadShader("starshade.glsl"); //load starfield shader
 starshader.set("iResolution", PApplet.parseFloat(width), PApplet.parseFloat(height), 0); //set shader resolution
 starFBO = createGraphics(width, height, P3D); //create FBO
 starFBO.shader(starshader); //set starFBO to star field shader

 distortshader = loadShader("distort.glsl"); //load distort shader
 distortshader.set("iResolution", PApplet.parseFloat(width), PApplet.parseFloat(height), 0); //set shader resolution
 distortFBO = createGraphics(width, height, P3D); //create FBO
 distortFBO.shader(distortshader); //set distortFBO to distort shader
 }

//-------------------------------------

public void updateShaderParams() { //update shader Parameters

  float[] sensorValues = input.getSensor(); //store sensor Values

  v_zoom = map(sensorValues[1],0.0f,1024.0f,0.3f,1.1f); //set zoom var to mapped pot input
  v_speed = map(sensorValues[0],0.0f,1024.0f,0.005f,0.015f); //set speed var to mapped pot input
  v_volume = map(s_input.getMvolume(),0.0f,0.7f,10,30); //set volume var to mapped volume input
  //Check which channel is louder and set the difference to the respective variable, and set the other variable to 0.001
  if(s_input.getLvolume() > s_input.getRvolume()) {
    v_lA = s_input.getLvolume() - s_input.getRvolume();
    v_rA = 0.001f;
  } else {
    v_rA = s_input.getRvolume() - s_input.getLvolume();
    v_lA = 0.001f;
  }

 //pass variables to star field shader
 starshader.set("zoom", v_zoom);
 starshader.set("speed",v_speed);
 if(keyPressed == true){
 starshader.set("iterations",(int)v_volume);
 starshader.set("l_v",v_lA);
 starshader.set("r_v",v_rA);
 }
}
//-------------------------------------

public void draw() {

 updateShaderParams(); //update shader details

 starFBO.beginDraw();
 starshader.set("iGlobalTime", millis() / 1000.0f); // pass in a millisecond clock to enable animation
 shader(starshader); //set shader to star field shader
 starFBO.rect(0, 0, width, height); // We draw a rect here for our shader to draw onto
 starFBO.endDraw();

 float[] sensorValues = input.getSensor(); //get sensor values

 if (sensorValues[2] == 0.0f){ //check if button is pressed
  image(starFBO,0,0,width,height); //draw plain star field shader
} else {
  distortFBO.beginDraw();
  distortshader.set("iGlobalTime", millis() / 1000.0f); // pass in a millisecond clock to enable animation
  distortshader.set("ispec", starFBO); //set texture to star
  shader(distortshader); //set shader to star field shader
  distortFBO.rect(0, 0, width, height); //draw rectangle for shader to draw to
  distortFBO.endDraw();
  image(distortFBO,0,0,width,height); //draw distorted star field shader
}
}
 //import the Serial library
Serial port;    // The serial port, this is a new instance of the Serial class (an Object)

class ArduinoInput {

  int end = 10;   // the number 10 is ASCII for linefeed (end of serial.println), later we will look for this to break up individual messages
  String serial;  // declare a new string called 'serial'. A string is a sequence of characters (data type known as "char")
  float[] sensorValues = {0, 0, 0, 0, 0, 0, 0, 0, 0};

  //-----------------------------------------------------
  ArduinoInput(PApplet papp) {

    port = new Serial(papp, Serial.list()[1], 9600); // initializing the object by assigning a port and baud rate (must match that of Arduino)
    port.clear(); // function from serial library that throws out the first reading, in case we started reading in the middle of a string from Arduino
    serial = port.readStringUntil(end); // function that reads the string from serial port until a println and then assigns string to our string variable (called 'serial')
    serial = null;  // initially, the string will be null (empty)

    // println(Serial.list());
  }
  //-----------------------------------------------------
  public float[] getSensor(){
     while (port.available () > 0) { //as long as there is data coming from serial port, read it and store it
      serial = port.readStringUntil(end);
    }

    if (serial != null) { // if the string is not empty, print the following
      String[] a = split(serial, ','); // a new array (called 'a') that stores values into seperate cells (seperated by commas specified in your Arduino program)

      for(int i = 0; i < a.length; i++){
        sensorValues[i] = parseFloat(a[i]); // Convert our string values to ints
      }
    }
    return sensorValues;
  }
}





 //Inport Minim Library
Minim minim;
AudioPlayer player; // add Minim object and player object

class SoundInput{
  SoundInput(PApplet papp,String p_file){ //Initialise SoundInput class
    minim = new Minim(papp);
    player = minim.loadFile(p_file); //load audio file
    player.play(); //start playing audio file
  }
  public float getMvolume(){ //mix  volume function
    v_mix = player.mix.level();
    return v_mix; //gets the root mean square of both channels in the audio buffer
  }
  public float getLvolume(){ //left volume function
    v_left = player.left.level();
    return v_left; //gets the root mean square of the left channel in the audio buffer
  }
  public float getRvolume(){ //right volume function
    v_right = player.right.level();
    return v_right; //gets the root mean square of the right channel in the audio buffer
  }

  // Declare Variables for channels
  float v_left;
  float v_right;
  float v_mix;


}
  public void settings() {  size(640, 480, P3D); }
  static public void main(String[] passedArgs) {
    String[] appletArgs = new String[] { "Main_Assignment" };
    if (passedArgs != null) {
      PApplet.main(concat(appletArgs, passedArgs));
    } else {
      PApplet.main(appletArgs);
    }
  }
}
