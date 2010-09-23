//*****************************************************************************
// TITLE:         IO  
// DESCRIPTION:   Tools for managing files, images, and sribing on the screen
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
//***************************** SCRIBING *********************************
// Utilities to paint text on the canvas with specified colors and position
void scribe(String S,color c) {fill(c); text(S,20,20); noFill();}
void scribeBlack(String S, int i) {fill(black); text(S,20,20+i*20); noFill();}
void scribeBlackRight(String S, int i) {fill(black); text(S,height-20-8*S.length(),20+i*20); noFill();}
void scribeBlack(String S) {fill(black); text(S,20,20); noFill();}
void scribe(String S, float x, float y) {text(S,x,y);}
void scribe(String S) {text(S,20,20);}
void scribeMouseCoordinates() {fill(black); text("("+mouseX+","+mouseY+")",mouseX+7,mouseY+25); noFill();}

//***************************** FORMATTING NUMBERS *********************************
String  Format(int v, int n) {String s=str(v); String spaces = "                            ";
   int L = max(0,n-s.length());
   String front = spaces.substring(0, L);
   return(front+s);
  }; 

String  Format0(int v, int n) {
   String s=str(v); String spaces = "00000000000000000000000000";
   int L = max(0,n-s.length());
   String front = spaces.substring(0, L);
   return(front+s);
  };

String  Format(String s, int n) {String spaces = "                                 ";
    int L = max(0,n-s.length());
    String back = spaces.substring(0, L);
    return(s+back);
  };

String  Format(float f, int n, int z) {
   String sign = "-"; if (f>=0) sign="+";
   String spaces = "                                ";
   String s=nf(abs(f),n,z); 
   while (s.indexOf("0")==0) {s=s.substring(1,s.length());};
   int b=s.indexOf("."); int a=max(0,n-b); int c=s.length()-b-1;  int d=0;
   if (c>z) {s=s.substring(0,b+1+z); c=z;} else { d=z-c;};
   String front = spaces.substring(0, a);
   String back = spaces.substring(0, d);
   return(front+sign+s+back);
  }; 

//***************************** DATA FILES *********************************
int numberOfExamples=1; // number of files
int currentExample=0; // file last read
void saveNumberOfExamples() { String [] S = new String[1]; S[0]=str(numberOfExamples);  saveStrings("data/ne",S); println("saved number of examples = "+numberOfExamples); }
void loadNumberOfExamples() { String [] S = loadStrings("data/ne"); numberOfExamples=int(S[0]); println("read number of examples = "+numberOfExamples); }

//***************************** FIGURES *********************************
int io_pic=0; // picture number for saving sequences of pictures of making movies
void picture() {saveFrame("pictures/P"+Format0(pictureCounter++,3)+".jpg"); makingPicture=false; showMenu=!showMenu;}

