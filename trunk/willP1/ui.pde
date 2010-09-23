void mousePressed() {MYcheckButtons(); if(!ui_picked) MYmousePressed(); } 
void mouseDragged() {if(!ui_picked) MYmouseDragged(); } 
void mouseReleased() {ui_picked=false; MYmouseReleased();} 
void keyReleased() {MYkeyReleased(); } 
void keyPressed() { 
  int w = int(key)-48; if(0<w && w<10) scribe(str(w)); // shows briefly value of numeric key pressed
  if (key==' ') showHelpText=!showHelpText ; 
  if (key=='?') debug=true;  // toggle debug mode
  if (key=='$') picture(); 
  MYkeyPressed();
  };     
  
void mouseMoved() {if(!ui_picked) MYmouseMoved(); } 

void showHelp() {
   image(pic, width-pic.width, 0); 
   pushMatrix(); translate(20,20);
   fill(black);
   MYshowHelp();
   fill(dblue);
   text("  ",0,0); translate(0,20);
   text("Press SPACE to hide/show this menu",0,0); translate(0,20);
   text("Press '$' to snap a picture (when running Processing, not in browser)",0,0); translate(0,20);
   text("Built using Jarek Rossignac's Geometry SandBox (GSB)",0,0); translate(0,20);
   popMatrix(); noFill();
   }
  


// BUTTONS for toggles and actions
Boolean ui_picked=false; // true when user click on a button
int ui_dy=15, but_hw=5;
class BUTTON {
 String label="not assigned"; Boolean isTrue=false; Boolean click=false;
  BUTTON (String S, Boolean pisTrue) {label=S; isTrue=pisTrue;}
  BUTTON (String S, int x) {label=S; click=true; isTrue=true;}
  boolean check(int row) {
    boolean clicked=false;
    if (height<mouseX && mouseX<height+but_hw*2 && (row+1)*ui_dy<mouseY+but_hw && mouseY<(row+1)*ui_dy+but_hw) { 
      if(!click) isTrue=!isTrue; else clicked=true;
      ui_picked=true; 
      };
    if(clicked) println(label);
    return clicked;
    }
  void show(int row) { 
     fill(black); scribe(label,height+but_hw+11,(row+1)*ui_dy+6); 
     if(click) fill(metal); if(!isTrue) fill(white); 
     rect(height+but_hw,(row+1)*ui_dy,but_hw*2,but_hw*2);  
     }
  }  // end BUTTON


   
