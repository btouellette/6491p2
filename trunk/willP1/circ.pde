//*****************************************************************************
// TITLE:         CIRCLES  
// DESCRIPTION:   Circle utilities for GSB to manipulate and dislpay points
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
/*************************
show(P,r): draws circle of center P and radius r 
*************************/

void showArcThrough (pt A, pt B, pt C) {
  if (abs(dot(V(A,B),R(V(A,C))))<0.01*d2(A,C)) {show(A,C); return;}
   pt O = CircumCenter ( A,  B,  C); 
   float r=d(O,A);
   vec OA=V(O,A), OB=V(O,B), OC=V(O,C);
   float b = angle(OA,OB), c = angle(OA,OC); 
   if(0<c && c<b || b<0 && 0<c)  c-=TWO_PI; 
   else if(b<c && c<0 || c<0 && 0<b)  c+=TWO_PI; 
   beginShape(); v(A); for (float t=0; t<1; t+=0.01) v(R(A,t*c,O)); v(C); endShape();
   }

float bulge (pt A, pt B, pt C) {  // returns bulge (<0 when A-B-C is ccw)
  if (abs(dot(V(A,B),R(V(A,C))))<0.01*d2(A,C)) return 0;
   pt O = CircumCenter (A,  B,  C); //show(O,2);
   float r=d(O,A); if (cw(A,B,C)) r=-r;
   float d=d(A(A,C),O); if (cw(A,O,C)) d=-d;
   return r+d;
   }

//************************************************************************
//**** CIRCLE CLASS
//************************************************************************

CIRCLE C(pt P, float r) {return new CIRCLE(P,r);}
class CIRCLE { pt C=P(height/2,height/2); float r=height/4; 
  // CREATE
  CIRCLE () {}
  CIRCLE (pt P, float s) {C.setTo(P); r=s;};
  void draw() {show(C,r);}
  pt CP(pt P) {return T(C,r,P);}
  }
//*********** END CIRCLE CLASS

float interpolateAngles(float a, float t, float b) {if(b<a) b+=TWO_PI; float m=(t-a)/(b-a); if (m>PI) m-=TWO_PI; return m;}

   
pt ptOnCircle(float a, pt O, float r) {return P(r*cos(a)+O.x,r*sin(a)+O.y);}   

  
float edgeCircleIntesectionParameter (pt A, pt B, pt C, float r) {  // computes parameter t such that A+tAB is on circle(C,r)
  vec T = V(A,B); float n = n(T); T.normalize();
  float t=rayCircleIntesectionParameter(A,T,C,r);
  return t*n;
}

float rayCircleIntesectionParameter (pt A, vec T, pt C, float r) {  // computes parameter t such that A+tT is on circle(C,r) or -1
  vec AC = V(A,C);
  float d=dot(AC,T); 
  float h = dot(AC,R(T)); 
  float t=-1;
  if (abs(h)<r) {
    float w = sqrt(sq(r)-sq(h));
    float t1=(d-w);
    float t2=(d+w);
     if ((0<=t1)&&(t1<=t2)) t = t1; else if (0<=t2) t = t2; 
   }
  return t;
}

pt circleInversion(pt A, pt C, float r) {vec V=V(C,A); return S(C,sq(r/n(V)),A); }

pt circleCenterForAngle(pt A, pt B, float a) {                   // computes center of the circle of points C such that angle(A,C,B)=a
   pt M = A(A,B); float d=d(A,B)/2; float h=d*tan(PI/2.-a); vec V=R(S(h,U(V(A,B))));  return T(M,V); }

pt CCl(pt C1, float r1, pt C2, float r2) { // computes  the intersection of two circles that is on the left of (C1,C2)
   float d=d(C1,C2);
   float d1=(sq(r1)-sq(r2)+sq(d))/(d*2);
   float h=sqrt(sq(r1)-sq(d1));
   return T(T(C1,d1,C2),-h,R(V(C1,C2)));}
   
pt CCr(pt C1, float r1, pt C2, float r2) { // computes  the intersection of two circles that is on the right of (C1,C2)
   float d=d(C1,C2);
   float d1=(sq(r1)-sq(r2)+sq(d))/(d*2);
   float h=sqrt(sq(r1)-sq(d1));
   return T(T(C1,d1,C2),h,R(V(C1,C2)));}   

pt interArc(pt A, pt B, pt C, pt D) {
  pt M = A(B,C);
   float ab=d(A,B); float bc=d(B,C); float cd=d(C,D); float t=(ab/(ab+bc)+(1.-cd/(bc+cd)))/2; 
   M= A(pointOnArcThrough(A,B,t,C),pointOnArcThrough(B,t,C,D));
    return M;}


pt pointOnArcThrough (pt A, float t, pt B, pt C) {
  pt X=new pt();
   pt O = CircumCenter ( A,  B,  C);
   float r=(O.disTo(A) + O.disTo(B)+ O.disTo(C))/3.;
   float a = V(O,A).angle(); 
   float ab = positive(angle(V(O,A),V(O,B)));  if(cw(A,B,C)) ab-=TWO_PI;
   return ptOnCircle(a+t*ab,O,r);
   }
   
pt pointOnArcThrough (pt A, pt B, float t, pt C) {
  pt X=new pt();
   pt O = CircumCenter ( A,  B,  C);
   float r=(O.disTo(A) + O.disTo(B)+ O.disTo(C))/3.;
   float b = V(O,B).angle(); 
   float bc = positive(angle(V(O,B),V(O,C)));  if(cw(A,B,C)) bc-=TWO_PI;
   return ptOnCircle(b+t*bc,O,r);
   }

pt midArc(pt A, pt B, pt C) {
  vec T=U(A,C); float d=d(B,C); float c=dot(U(A,B),T), s=dot(U(A,B),R(T));
  if(abs(s)<0.1) return B;
  return T(B,d*(c-1)/s,R(T));
  }
  


