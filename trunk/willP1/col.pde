//*****************************************************************************
// TITLE:         COLOR UTILITIES FOR GSB  
// DESCRIPTION:   defines color names and tools 
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
color col(int a, int r, int g, int b) {return a<<24 | r<<16 | g<<8 | b ;} // makes color fast with alpha (opacity), red, green, blue between 0 and 255
color col(int r, int g, int b) {return r<<16 | g<<8 | b ;} // makes color fast without alpha
color setA(color c, int a) {return (a<<24) | ((c<<8)>>8) ;} // sets alpha for color c
int colA(color c) {return (c>>24) & 0xFF;}
int colR(color c) {return (c>>16) & 0xFF;}
int colG(color c) {return (c>>8) & 0xFF;}
int colB(color c) {return c & 0xFF;}
color interCol(color C0, float t, color C1) {return col( int((1.-t)*colA(C0)+t*colA(C1)) , int((1.-t)*colR(C0)+t*colR(C1)) , int((1.-t)*colG(C0)+t*colG(C1)) , int((1.-t)*colB(C0)+t*colB(C1)) );}
color brown=#8B5701, red=#FF0000, magenta=#FF00FB, blue=#0300FF, cyan=#00FDFF, green=#00FF01, yellow=#FEFF00, skin=#F5C7B7, black=#000000, grey=#868686, white=#FFFFFF, orange=#FFC000, metal=#BEC9E0;
color lbrown=#C19100, lred=#FF3E3E, lmagenta=#FF7EFD, lblue=#807EFF, lcyan=#7EFFFE, lgreen=#80FF7E, lyellow=#FFFF6C, lskin=#FFECE5, lorange=#FFDF7E;
color dbrown=#674001, dred=#810000, dmagenta=#81007F, dblue=#010081, dcyan=#008180, dgreen=#018100, dyellow=#B4B400, dskin=#815531, dorange=#E58F02;

//color [] C=new color[1000];   
//void setRandomColors() {for (int i=0; i<n; i++) C[i]=color(int(random(255)),int(random(255)),int(random(255)));}

void showColors() {noStroke();
pushMatrix();
fill(lbrown); show(P(10,10),10); translate(20,0); 
fill(lred); show(P(10,10),10); translate(20,0); 
fill(lmagenta); show(P(10,10),10); translate(20,0); 
fill(lblue); show(P(10,10),10); translate(20,0); 
fill(lcyan); show(P(10,10),10); translate(20,0); 
fill(lgreen); show(P(10,10),10); translate(20,0); 
fill(lyellow); show(P(10,10),10); translate(20,0); 
fill(lskin); show(P(10,10),10); translate(20,0); 
fill(lorange); show(P(10,10),10); translate(20,0); 
fill(metal); show(P(10,10),10); translate(20,0); 
popMatrix(); pushMatrix(); translate(0,20);
fill(brown); show(P(10,10),10); translate(20,0); 
fill(red); show(P(10,10),10); translate(20,0); 
fill(magenta); show(P(10,10),10); translate(20,0); 
fill(blue); show(P(10,10),10); translate(20,0); 
fill(cyan); show(P(10,10),10); translate(20,0); 
fill(green); show(P(10,10),10); translate(20,0); 
fill(yellow); show(P(10,10),10); translate(20,0); 
fill(skin); show(P(10,10),10); translate(20,0); 
fill(orange); show(P(10,10),10); translate(20,0); 
fill(grey); show(P(10,10),10); translate(20,0); 
popMatrix(); pushMatrix(); translate(0,40);
fill(dbrown); show(P(10,10),10); translate(20,0); 
fill(dred); show(P(10,10),10); translate(20,0); 
fill(dmagenta); show(P(10,10),10); translate(20,0); 
fill(dblue); show(P(10,10),10); translate(20,0); 
fill(dcyan); show(P(10,10),10); translate(20,0); 
fill(dgreen); show(P(10,10),10); translate(20,0); 
fill(dyellow); show(P(10,10),10); translate(20,0); 
fill(dskin); show(P(10,10),10); translate(20,0); 
fill(dorange); show(P(10,10),10); translate(20,0); 
fill(black); show(P(10,10),10); translate(20,0); 
popMatrix();
}
 
