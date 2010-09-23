//*****************************************************************************
// TITLE:         Jarek's Geometry Sand Box (GSB) : 2D geometry processing development environment  
// DESCRIPTION:   Application specific code is in tabs MYxxxx...
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:         August 1010
//*****************************************************************************
// Usage: Edit MYsetup, MYdraw, MYhelp, MYkeyPressed, MYmousePressed, MYmouseReleased, MYmouseDragged procedures in the tab MYxxxx. 
// If needed, for clarity add tabs with names starting by MY
// Assumes files pic.jpg (your face) and P.pts (saved point locations) are in the data folder

boolean showHelpText=false, showMenu=true;      // toggled by keys to show/hide help text
boolean debug=false;           // temporarily set when key '?' is pressed and used to print some debugging values
boolean makingPicture=false;    // used to prevent the menu from showing when making pictures
PImage pic; // picture of author's face that is displayed in the help pane, read from file pic.jpg in data folder

void setup() {  // Executed once
  size(900,700); // Opens graphic canvas (wider for manu on the right)
  smooth();   strokeJoin(ROUND); strokeCap(ROUND); // set drawing modes for high-quality slower drawings
  rectMode(CENTER); // needed for buttons to work properly !
  PFont font = loadFont("ArialMT-24.vlw"); textFont(font, 16);      // load font for writing on the canvas
  pic = loadImage("data/face.png");                                  // load image names pic from file pic.jpg in folder data
  MYsetup();                                                        // execute application specific setup tasks (see MYxxx)
  } 

void draw() {
  background(white);  strokeWeight(1);                        // clears screen with white background and sets line thickness to 1
  if (showHelpText) showHelp();                               // shows the welcome page with author's picture
  else { 
     MYdraw();  // renders the next frame (see in MYxxx)
     if(showMenu&&!makingPicture) {stroke(black); line(height-4,0,height-4,height); stroke(metal); line(height-3,0,height-3,height); MYshowButtons();}; // show menu
     }
  if(makingPicture) {saveFrame("pictures/P"+Format0(pictureCounter++,3)+".jpg"); makingPicture=false;} 
  if(debug) {println(); debug=false;};
  };  // end of draw

   

