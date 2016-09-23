import ddf.minim.*;
import ddf.minim.analysis.*;
import ddf.minim.effects.*;
import ddf.minim.signals.*;
import ddf.minim.spi.*;
import ddf.minim.ugens.*; //Inport Minim Library
Minim minim;
AudioPlayer player; // add Minim object and player object

class SoundInput{
  SoundInput(PApplet papp,String p_file){ //Initialise SoundInput class
    minim = new Minim(papp);
    player = minim.loadFile(p_file); //load audio file
    player.play(); //start playing audio file
  }
  float getMvolume(){ //mix  volume function
    v_mix = player.mix.level();
    return v_mix; //gets the root mean square of both channels in the audio buffer
  }
  float getLvolume(){ //left volume function
    v_left = player.left.level();
    return v_left; //gets the root mean square of the left channel in the audio buffer
  }
  float getRvolume(){ //right volume function
    v_right = player.right.level();
    return v_right; //gets the root mean square of the right channel in the audio buffer
  }

  // Declare Variables for channels
  float v_left;
  float v_right;
  float v_mix;


}
