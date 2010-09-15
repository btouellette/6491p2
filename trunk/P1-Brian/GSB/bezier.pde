//*****************************************************************************
// TITLE:         BEZIER  
// DESCRIPTION:   Tools for processing Bezier curves
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
pt cubicBezier(pt A, pt B, pt C, pt D, float t) {return( s( s( s(A,t,B) ,t, s(B,t,C) ) ,t, s( s(B,t,C) ,t, s(C,t,D) ) ) ); }
void splitBezier(pt A, pt B, pt C, pt D, int rec) {
  if (rec==0) {B.v(); C.v(); D.v(); return;};
  pt E=A(A,B);   pt F=A(B,C);   pt G=A(C,D);  
           pt H=A(E,F);   pt I=A(F,G);  
                    pt J=A(H,I); J.show(3);   
  splitBezier(A,E,H,J,rec-1);   splitBezier(J,I,G,D,rec-1); 
 }
 
void drawSplitBezier(pt A, pt B, pt C, pt D, float t) {
  pt E=s(A,t,B); E.show(2);  pt F=s(B,t,C); F.show(2);  pt G=s(C,t,D); G.show(2);  E.to(F); F.to(G);
           pt H=s(E,t,F); H.show(2);   pt I=s(F,t,G);  I.show(2); H.to(I);
                    pt J=s(H,t,I); J.show(4);   
 }

void drawCubicBezier(pt A, pt B, pt C, pt D) { beginShape();  for (float t=0; t<=1; t+=0.02) {cubicBezier(A,B,C,D,t).v(); };  endShape(); }
void drawEdges(pt A, pt B, pt C, pt D) { A.to(B); B.to(C); C.to(D); }
float cubicBezierAngle (pt A, pt B, pt C, pt D, float t) {pt P = s(s(A,t,B),t,s(B,t,C)); pt Q = s(s(B,t,C),t,s(C,t,D)); vec V=P.makeVecTo(Q); float a=atan2(V.y,V.x); return(a);}  
vec cubicBezierTangent (pt A, pt B, pt C, pt D, float t) {pt P = s(s(A,t,B),t,s(B,t,C)); pt Q = s(s(B,t,C),t,s(C,t,D)); vec V=P.makeVecTo(Q); V.makeUnit(); return(V);}  

void retrofitBezier(pt[] PP, pt[] QQ) {                            // sets control polygon QQ so that tits Bezier curve interpolates PP
  QQ[0]=P(PP[0]);
  QQ[1]=S(-15./18.,PP[0],54./18.,PP[1],-27./18.,PP[2],6./18.,PP[3]);
  QQ[2]=S(-15./18.,PP[3],54./18.,PP[2],-27./18.,PP[1],6./18.,PP[0]);
  QQ[3]=P(PP[3]);
  }

void drawParabolaInHat(pt A, pt B, pt C, int rec) {
   if (rec==0) { B.showSegmentTo(A); B.showSegmentTo(C); } 
   else { 
     float w = (A.makeVecTo(B).norm()+C.makeVecTo(B).norm())/2;
     float l = A.makeVecTo(C).norm()/2;
     float t = l/(w+l);
     pt L = new pt(A); 
     L.translateBy(t,A.makeVecTo(B)); 
     pt R = new pt(C); R.translateBy(t,C.makeVecTo(B)); 
     pt M = A(L,R);
     drawParabolaInHat(A,L, M,rec-1); drawParabolaInHat(M,R, C,rec-1); 
     };
   };

vec cubic (pt A, pt B, pt C, pt D, pt E) {return(new vec( (-A.x+4*B.x-6*C.x+4*D.x-E.x)/6, (-A.y+4*B.y-6*C.y+4*D.y-E.y)/6  ));}

pt cubic(pt A, pt B, pt D, pt E, float s, float t, float u) {
   float ct1, ct2, ct3;
    vec AB, AD, AE;
    ct1 = (s*u + s - u - s*s) * s;     ct2 = (u*u + s - s*u - u) * u;     ct3 = s*u + 1 - s - u;
    AB = A.vecTo(B); AB.div(ct1);      AD = A.vecTo(D); AD.div(ct2);       AE = A.vecTo(E); AE.div(ct3);
    vec a = AB.make(); a.mul(-1.0); a.add(AD); a.add(AE);   
    vec b = AB.make(); b.mul(u+1); b.addScaled(-(s+1),AD); b.addScaled(-(u+s),AE);
   vec c = AB.make(); c.mul(-u);  c.addScaled(s,AD); c.addScaled(u*s,AE); 
    
   vec V = a.make(); V.mul(t*t*t); V.addScaled(t*t,b); V.addScaled(t,c);
   pt R = A.make();  R.addVec(V);
   return (R);
   };
  
vec bulgeVec(pt A, pt B, pt C, pt D) {return A(B.makeVecTo(A),C.makeVecTo(D)).makeScaledBy(-3./8.); }

pt prop(pt A, pt B, pt D, pt E) {float a=d(A,B), b=d(B,D), c=d(D,E), t=a+b+c, d=b/(1+pow(c/a,1./3) ); return cubic(A,B,D,E,a/t,(a+d)/t,(a+b)/t); }

vec vecToCubic (pt A, pt B, pt C, pt D, pt E) {return V( (-A.x+4*B.x-6*C.x+4*D.x-E.x)/6, (-A.y+4*B.y-6*C.y+4*D.y-E.y)/6);}
