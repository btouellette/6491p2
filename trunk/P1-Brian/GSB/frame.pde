//*****************************************************************************
// TITLE:         BEZIER  
// DESCRIPTION:   Tools for processing Bezier curves
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
class frame {       // frame [O I J]
  pt O = new pt();
  vec I = new vec(1,0);
  vec J = new vec(0,1);
  frame() {}
  frame(pt pO, vec pI, vec pJ) {O.setTo(pO); I.setTo(pI); J.setTo(pJ);  }
  frame(pt A, pt B, pt C) {O.setTo(B); I=A.makeVecTo(C); I.normalize(); J=I.makeTurnedLeft();}
  frame(pt A, pt B) {O.setTo(A); I=A.makeVecTo(B).makeUnit(); J=I.makeTurnedLeft();}
  frame(pt A, vec V) {O.setTo(A); I=V.makeUnit(); J=I.makeTurnedLeft();}
  frame(float x, float y) {O.setTo(x,y);}
  frame(float x, float y, float a) {O.setTo(x,y); this.rotateBy(a);}
  frame(float a) {this.rotateBy(a);}
  frame makeClone() {return(new frame(O,I,J));}
  void reset() {O.setTo(0,0); I.setTo(1,0); J.setTo(0,1); }
  void setTo(frame F) {O.setTo(F.O); I.setTo(F.I); J.setTo(F.J); }
  void setTo(pt pO, vec pI, vec pJ) {O.setTo(pO); I.setTo(pI); J.setTo(pJ); }
  void show() {float d=height/20; O.show(); I.makeScaledBy(d).showArrowAt(O); J.makeScaledBy(d).showArrowAt(O); }
  void showLabels() {float d=height/20; 
               O.makeTranslatedBy(A(I,J).makeScaledBy(-d/4)).showLabel("O",-3,5); 
               O.makeTranslatedBy(d,I).makeTranslatedBy(-d/5.,J).showLabel("I",-3,5); 
               O.makeTranslatedBy(d,J).makeTranslatedBy(-d/5.,I).showLabel("J",-3,5); 
             }
  void translateBy(vec V) {O.translateBy(V);}
  void translateBy(float x, float y) {O.translateBy(x,y);}
  void rotateBy(float a) {I.rotateBy(a); J.rotateBy(a); }
  frame makeTranslatedBy(vec V) {frame F = this.makeClone(); F.translateBy(V); return(F);}
  frame makeTranslatedBy(float x, float y) {frame F = this.makeClone(); F.translateBy(x,y); return(F); }
  frame makeRotatedBy(float a) {frame F = this.makeClone(); F.rotateBy(a); return(F); }
   
  float angle() {return(I.angle());}
  void apply() {translate(O.x,O.y); rotate(angle());}  // rigid body tansform, use between pushMatrix(); and popMatrix();
  void moveTowards(frame B, float s) {O.translateTowards(s,B.O); rotateBy(s*(B.angle()-angle()));}  // for chasing or interpolating frames
  } // end frame class
 
frame makeMidEdgeFrame(pt A, pt B) {return(new frame(A(A,B),A.makeVecTo(B)));}  // creates frame for edge

frame interpolate(frame A, float s, frame B) {   // creates a frame that is a linear interpolation between two other frames
    frame F = A.makeClone(); F.O.translateTowards(s,B.O); F.rotateBy(s*(B.angle()-A.angle()));
    return(F);
    }

frame twist(frame A, float s, frame B) {   // a circular interpolation
  float d=A.O.disTo(B.O);
  float b=angle(A.I,B.I);
  frame F = A.makeClone(); F.rotateBy(s*b);
  pt M = A(A.O,B.O);   
  if ((abs(b)<0.000001) || (abs(b-PI)<0.000001)) F.O.translateTowards(s,B.O); 
  else {
  float h=d/2/tan(b/2); //else print("/b");
     vec W = A.O.makeVecTo(B.O); W.normalize();
     vec L = W.makeTurnedLeft();   L.scaleBy(h);
     M.translateBy(L);  // fill(0); M.show(6);
     L.scaleBy(-1);  L.normalize(); 
     if (abs(h)>=0.000001) L.scaleBy(abs(h+sq(d)/4/h)); //else print("/h");
     pt N = M.makeClone(); N.translateBy(L);  
     F.O.rotateBy(-s*b,M);
     };   
  return(F);
  }
  
