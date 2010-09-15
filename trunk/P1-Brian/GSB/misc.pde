//*****************************************************************************
// TITLE:         UTILITIES  
// DESCRIPTION:   for GSB to click variables
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
// COUNTER
class COUNTER {  // utilities for tracking a value
  int value=0;
  void reset() {value=0;};
  int i() {value++; return value;} // increment
  int d() {value++; return value;} // decrement
  }
  
// LOADING SVG IMAGE  
PShape bot;
void loadSVG() {bot = loadShape("bot1.svg"); } // include in MYsetup. The file "bot1.svg" must be in the data folder
void showSVG() { // include in MYdraw.
  float w=float(width)/612; float h=float(height)/792;
  if (w>h) shape(bot, floor((width-int(h/w*width))/2), 0, floor(h/w*width)-1, height-1); 
      else shape(bot, 0, floor((height-int(w/h*height))/2), width-1, floor(w/h*height)-1); 
  }
 
 
