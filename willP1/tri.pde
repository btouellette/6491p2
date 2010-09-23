//*****************************************************************************
// TITLE:         TRIANGLES  
// DESCRIPTION:   Triangle utilities for GSB to manipulate and dislpay points
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
/*************************************
cw(B,C): true if smallest angle turn from OB to OC is cloclwise (cw), where O=(0,0)
cw(A,B,C): true if A-B-C makes a clockwise turn at B
area(A,B,C): signed Triangle area (positive if A-B-C is clockwise
x(A,B,C): local x-coordinate of B in system (AC,R(AC),A)
y(A,B,C): local y-coordinate of B in system (AC,R(AC),A)
Shadow(A,B,C): projection of B on line(A,C)
ax(A,B,C): signed distance from A to the projection of B onto line(A,C)
ay(A,B,C): igned distance from A to the projection of B onto line(A,R(AC))
show(A,B,C): draws triangle
show(A,B,C): draws triangle enlarged by r (offset edgees)
Centroid(A,B,C): center of mass  = (A+B+C)/3
CircumCenter(A,B,C): center of circle passing through the 3 points
circumRadius(A,B,C): radius of circumcenter
OrthoCenter(A,B,C): intersection of altitudes
SpiekerCenter(A,B,C): center of mass of the perimeter 
spiekerRadius(A,B,C): radius of circle with same perimeter
InCenter(A,B,C): center of inscribed circle
inRadius(A,B,C): radius of incircle 
makeOffset(A,B,C): // intersection of r-offsets of lines (A,B) and (B,C)
makeProjection(A,B,C): Projection of B on line (B,C)
(A,B,C):
**************************************/
// measure
boolean cw(pt B, pt C) {return C.y*B.x>C.x*B.y;}  // cw(B,C): Boolean true if smallest angle turn from OB to OC is cloclwise (cw), where O=(0,0)
boolean cw(pt A, pt B, pt C) {return (C.y-A.y)*(B.x-A.x)>(C.x-A.x)*(B.y-A.y);} // cw(A,B,C): Boolean true if A-B-C makes a clockwise turn at B
float area(pt A, pt B, pt C) {return 0.5*((C.y-A.y)*(B.x-A.x)+(A.x-C.x)*(B.y-A.y));} // area(A,B,C): signed Triangle area (positive if A-B-C is clockwise
float x(pt A, pt B, pt C) {return ((C.x-A.x)*(B.x-A.x)+(C.y-A.y)*(B.y-A.y))/(sq(C.x-A.x)+sq(C.y-A.y));} // x(A,B,C): local x-coordinate of B in system (AC,R(AC),A)
float y(pt A, pt B, pt C) {return ((C.y-A.y)*(B.x-A.x)+(A.x-C.x)*(B.y-A.y))/(sq(C.x-A.x)+sq(C.y-A.y));} // y(A,B,C): local y-coordinate of B in system (AC,R(AC),A)
float ay(pt A, pt B, pt C) {return ((C.y-A.y)*(B.x-A.x)+(A.x-C.x)*(B.y-A.y))/sqrt(sq(C.x-A.x)+sq(C.y-A.y));} // ay(A,B,C): signed distance from A to the projection of B onto line(A,R(AC))
pt projection(pt A, pt B, pt C) {return L(A,y(A,B,C),C);} // returns projection of B on line(A,C)
float thickness(pt A, pt B, pt C) {return min(abs(ay(A,B,C)),abs(ay(B,C,A)),abs(ay(C,A,B))); } 

// area , moment
float triangleArea(pt A, pt B, pt C) {return(dot(A.makeVecTo(B).makeTurnedLeft(),A.makeVecTo(C))/2.); };
float triangleMoment(pt A, pt B, pt C) {
  float b = A.disTo(B); 
  vec T=A.makeVecTo(B); T.normalize();
  vec N = T.makeTurnedLeft(); 
  vec AC=A.makeVecTo(C); 
  float h = dot(AC,N);
  float a = dot(AC,T);
  return ( b*b*b*h - a*b*b*h + a*a*b*h + b*h*h*h )/36.; };
 
// render
void show(pt A, pt B, pt C)  {beginShape();  A.v(); B.v(); C.v(); endShape(CLOSE);}  // show(A,B,C): render triangle
void show(pt A, pt B, pt C, float r) {
  if (thickness(A,B,C)<abs(2*r)) return;
  float s=r; if (cw(A,B,C)) s=-s; pt AA = A.makeOffset(B,C,s); pt BB = B.makeOffset(C,A,s); pt CC = C.makeOffset(A,B,s); 
  beginShape();  AA.v(); BB.v(); CC.v(); endShape(CLOSE);
    }
void showAltitude(pt A, pt B, pt C) {vec V=R(V(A,C)); vec U=S(y(A,B,C),V); arrow(B,U); }
   
// centers and their radii: http://www.jimloy.com/geometry/centers.htm
pt Centroid(pt A, pt B, pt C)  {return A(A,B,C);}        // Centroid(A,B,C): (A+B+C)/3 (center of mass of triangle), intersection of bisectors
pt CircumCenter (pt A, pt B, pt C) {vec AB = V(A,B); vec AC = R(V(A,C)); return T(A,1./2/dot(AB,AC),S(-n2(AC),R(AB),n2(AB),AC)); }; // CircumCenter(A,B,C): center of circumscribing circle, where medians meet)
float circumRadius (pt A, pt B, pt C) {float a=d(B,C), b=d(C,A), c=d(A,B), s=(a+b+c)/2, d=sqrt(s*(s-a)*(s-b)*(s-c)); return a*b*c/4/d;} // radiusCircum(A,B,C): radius of circumcenter
 
pt OrthoCenter(pt A, pt B, pt C)  {float a=d2(B,C), b=d2(C,A), c=d2(A,B), x=(a+b-c)*(a-b+c), y=(a+b-c)*(-a+b+c), z=(a-b+c)*(-a+b+c), t=x+y+z; x=x/t; y=y/t; z=z/t; return S(x,A,y,B,z,C);} // OrthoCenter(A,B,C): intersection of altitudes
pt SpiekerCenter (pt A, pt B, pt C) {float ab=d(A,B), bc=d(B,C), ca=d(C,A), s=ab+bc+ca; return S(ab/s,A(A,B),bc/s,A(B,C),ca/s,A(C,A));  }    // SpiekerCenter(A,B,C): center of mass of the perimeter 
float spiekerRadius (pt A, pt B, pt C) {float ab=d(A,B), bc=d(B,C), ca=d(C,A), s=ab+bc+ca; return s/(2*PI);  }    // spiekerRadius(A,B,C): radius of circle with same perimeter
pt InCenter (pt A, pt B, pt C)  {float Z=area(A,B,C), a=B.disTo(C), b=C.disTo(A), c=A.disTo(B), s=a+b+c, r=2*Z/s, R=a*b*c/(2*r*s); return S(a/s,A,b/s,B,c/s,C); } // InCenter(A,B,C): incenter (center of inscribed circle)
float inRadius (pt A, pt B, pt C)  {float Z=area(A,B,C), a=d(B,C), b=d(C,A), c=d(A,B), s=a+b+c;  return 2*Z/s;} //inRadius(A,B,C): radius of incircle  

float radiusMonotonic (pt A, pt B, pt C) {    // size of bubble pushed through (A,C) and touching B, >0 when ABC is clockwise
    float a=d(B,C), b=d(C,A), c=d(A,B);
    float s=(a+b+c)/2; float d=sqrt(s*(s-a)*(s-b)*(s-c)); float r=a*b*c/4/d;
    if (abs(angle(A,B,C))>PI/2) r=sq(d(A,C)/2)/r;
    if (abs(angle(C,A,B))>PI/2) r=sq(d(C,B)/2)/r;
    if (abs(angle(B,C,A))>PI/2) r=sq(d(B,A)/2)/r;
    if (cw(A,B,C)) r=-r;
    return r;
   };

       
// constructions of points and offsetrs    
pt Offset(pt A, pt B, pt C, float r) {  // intersection of r-offsets of lines (A,B) and (B,C)
    float a = angle(V(B,A),V(B,C))/2;
    float d = r/sin(a); 
    vec N = U(A(U(V(B,A)),U(V(B,C)))); 
    return T(B,d,N); };
    
pt Shadow(pt A, pt B, pt C) {return L(A,x(A,B,C),C); };  // Projection of B on line (B,C)


