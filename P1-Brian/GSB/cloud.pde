//*****************************************************************************
// TITLE:         POINT CLOUD  
// DESCRIPTION:   CLOUD class for GSB to manipulate and dislpay points
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************

//*****************************************************************************
class CLOUD {                     
int n=6;                            // current number of control points
pt [] P = new pt[5000];            // decalres an array of  points
int p=0;                          // index to the currently selected vertex being dragged
CLOUD() {declarePoints(); resetPoints(); }
CLOUD(int pn) {n=pn; declarePoints(); resetPoints(); }

// ************************************** CREATE POINTS *********************************
void declarePoints() {for (int i=0; i<P.length; i++) P[i]=new pt();} // init the vertices to be on a circle
void resetPoints() {for (int i=0; i<n; i++) {P[i]=new pt(width/2,height/10.); P[i].rotateBy(-2.*PI*i/n, ScreenCenter());}; } // init the points to be on a circle
void resetPointsOnCircle() {for (int i=0; i<n; i++) {P[i].x=height/2; P[i].y=height/10; P[i].rotateBy(-2.*PI*i/n, ScreenCenter());}; } // init the points to be on a circle
void resetPointsOnLine() {for (int i=0; i<n; i++) {P[i].x=height*(i+1)/(n+1); P[i].y=height/2;}; } // init the points to be on a circle
void appendPoint(pt Q)  { P[n++].setTo(Q); p=n-1; }; // add point at end of list
void insertPoint(pt Q) {for (int i=n-1; i>p; i--) P[i+1].setTo(P[i]); n++; p++; P[p].setTo(Q);  };
void deletePoint() { for (int i=p; i<n-1; i++) P[i].setTo(P[i+1]); n--; p=max(0,p-1);}
void empty()  { n=0; };      // resets the vertex count to zero
void perturb(float e) {for (int i=0; i<n; i++) { P[i].x+=random(e); P[i].y+=random(e); } ; }

// ************************************** IMPORT POINTS FORM ANOTHER CLOUD *********************************
void copyFrom(CLOUD D) {for (int i=0; i<max(n,D.n); i++) P[i].setTo(D.P[i]); n=D.n;}
void extractFrom(pt [] Q, int start, int end )  {n=end-start+1; for (int i=0; i<n; i++) P[i].setTo(Q[i+start]); };  // makes P be the pvn first points of Q

// ************************************** SELECT AND TRANSFORM POINTS *********************************
void pickClosestPoint(pt M) {p=0; for (int i=1; i<n; i++) if (d(M,P[i])<d(M,P[p])) p=i;}
void dragPoint() { P[p].moveWithMouse(); P[p].clipToWindow(); }      // moves selected point (index p) by amount mouse moved recently
void dragPoint(vec V) {P[p].translateBy(V);}
void translatePoints(vec V) {for (int i=0; i<n; i++) P[i].translateBy(V); };   
void scalePointsRelative(float s, pt G) {for (int i=0; i<n; i++) P[i]=L(G,s,P[i]);};  
void scalePoints(float s, pt G) {for (int i=0; i<n; i++) P[i].translateTowards(s,G);};  
void scalePoints(float s, pt G, vec V) {for (int i=0; i<n; i++) P[i].translateTowards(s,G);};  
void scalePoints(float s) {scalePoints(s,verticesCenter());};
void scalePointsAroundCenterOfMass(pt M, vec V) {pt C=verticesCenter(); scalePoints(dot(V,U(M,C))/d(M,C),C);};
void rotatePoints(float a, pt G) {for (int i=0; i<n; i++) P[i].rotateBy(a,G);}; // rotates points around pt G by angle a
void rotatePointsAroundCenterOfMass(float a) {rotatePoints(a,verticesCenter());}; // rotates points around their center of mass by angle a
void rotatePoints(pt G, pt P, pt Q) {rotatePoints(angle(V(G,P),V(G,Q)),verticesCenter());}; // rotates points around G by angle <GP,GQ>
void rotatePointsAroundCenterOfMass(pt P, pt Q) {rotatePoints(verticesCenter(),P,Q);}; // rotates points around their center of mass G by angle <GP,GQ>
void frame() {
     float sx=height; float sy=height; float bx=0.0; float by=0.0; 
     for (int i=0; i<n; i++) {if (P[i].x>bx) {bx=P[i].x;}; if (P[i].x<sx) {sx=P[i].x;}; if (P[i].y>by) {by=P[i].y;}; if (P[i].y<sy) {sy=P[i].y;}; };
     float m=max(bx-sx,by-sy);  float dx=(m-(bx-sx))/2; float dy=(m-(by-sy))/2; 
     for (int i=0; i<n; i++) {P[i].x=(P[i].x-sx+dx)*4*height/5/m+height/10;  P[i].y=(P[i].y-sy+dy)*4*height/5/m+height/10;};    }   

// ************************************** REGISTER *********************************
void registerTo(CLOUD Q) {  // vertex registration
  pt A=verticesCenter(); pt B=Q.verticesCenter(); 
  float s=0; for (int i=0; i<min(n,Q.n); i++) s+=dot(V(A,P[i]),R(V(B,Q.P[i])));
  float c=0; for (int i=0; i<min(n,Q.n); i++) c+=dot(V(A,P[i]),V(B,Q.P[i]));
  float a = atan2(s,c);
  translatePoints(V(A,B));
  rotatePoints(a,B); 
  } 
  
// ************************************** VIEW *********************************
pt first() {return P[0];}  // returns first point
pt last() {return P[n-1];}  // returns last point
pt picked() {return P[p];}  // returns picked point
void drawArrowsTo(CLOUD R) {for (int i=0; i<min(n,R.n); i++) arrow(P[i],R.P[i]);};
void drawCorrespondenceTo(CLOUD R) {for (int i=0; i<min(n,R.n); i++) show(P[i],R.P[i]);};
void drawPoints() {for (int i=0; i<n; i++) P[i].show();}
void drawPoints(int r) {for (int i=0; i<n; i++) P[i].show(r);}
void drawDots() {beginShape(POINTS); for (int i=0; i<n; i++) v(P[i]); endShape();}  // fast draw of points
void writePointIDs() {for (int i=0; i<n; i++) label(P[i],S(15,U(verticesCenter(),P[i])),str(i)); }; 
void writePointLetters() {for (int i=0; i<n; i++) label(P[i],str(char(i+65))); }; 
void drawEdges() {beginShape(); for (int i=0; i<n; i++) v(P[i]); endShape();}  // fast draw of edges


// ************************************** MEASURE *********************************
pt verticesCenter() {pt G=P(); for (int i=0; i<n; i++) G.addPt(P[i]); return S(1./n,G);} 
pt ClosestVertex(pt M) {pt R=P[0]; for (int i=1; i<n; i++) if (d(M,P[i])<d(M,R)) R=P[i]; return P(R);}
float distanceTo(pt M) {return d(M,ClosestVertex(M));}

// ************************************** DELAUNAY & VORONOI *********************************
void drawDelaunayTriangles(int o) { 
   pt X = new pt(0,0);
   float r=1;  // radius of circumcircle
   for (int i=0; i<n-2; i++) for (int j=i+1; j<n-1; j++) for (int k=j+1; k<n; k++) {
      X=CircumCenter(P[i],P[j],P[k]);  r=d(X,P[i]);
      boolean found=false; 
      for (int m=0; m<n; m++) if ((m!=i)&&(m!=j)&&(m!=k)&&(X.disTo(P[m])<=r)) found=true;  
      if (!found) show(P[i],P[j],P[k],o);
      }; // end triple loop
   };

void drawVoronoiEdges() { 
   pt X = new pt(0,0);
   float r=1;  // radius of circumcircle
   for (int i=0; i<n-2; i++) for (int j=i+1; j<n-1; j++) for (int k=j+1; k<n; k++) {
      X=CircumCenter(P[i],P[j],P[k]);  r=d(X,P[i]);
      boolean found=false; 
      for (int m=0; m<n; m++) if ((m!=i)&&(m!=j)&&(m!=k)&&(X.disTo(P[m])<=r)) found=true;  
      if (!found) {
        if(cw(P[k],X,P[i])==cw(P[k],P[j],P[i])) show(X,Shadow(P[k],X,P[i])); 
        if(cw(P[i],X,P[j])==cw(P[i],P[k],P[j])) show(X,Shadow(P[i],X,P[j])); 
        if(cw(P[j],X,P[k])==cw(P[j],P[i],P[k])) show(X,Shadow(P[j],X,P[k]));}
      }; // end triple loop
   };
   
void paintVoronoi() {int R=height/15;  noStroke(); for(int r=R; r>1; r--) for (int i=0; i<n; i++) {fill(color(random(256))); P[i].show(r);};
   }
   
// ************************************** SAVE TO FILE AND READ BACK *********************************
void savePts() {savePts("data/P.pts");}
void savePts(String fn) { String [] inppts = new String [n+1];
    int s=0; inppts[s++]=str(n); for (int i=0; i<n; i++) {inppts[s++]=str(P[i].x)+","+str(P[i].y);};
    saveStrings(fn,inppts);  };
void loadPts() {loadPts("data/P.pts");}
void loadPts(String fn) { String [] ss = loadStrings(fn);
    String subpts;
    int s=0; int comma; n = int(ss[s]);
    for(int i=0;i<n; i++) { comma=ss[++s].indexOf(',');
      P[i]=new pt (float(ss[s].substring(0, comma)), float(ss[s].substring(comma+1, ss[s].length()))); }; };

// ************************************** MORPH *********************************
void linearMorph(CLOUD A, float t, CLOUD B) {
   n=min(A.n,B.n); 
   for (int i=0; i<n; i++) P[i]=L(A.P[i],t,B.P[i]);
   }

// ************************************** SPIRAL MOTION *********************************
void spiral(pt G, float s, float a, float t) {for (int i=0; i<n; i++) P[i]=spiralPt(P[i],G,s,a,t);}

void spiral(CLOUD A, float t, CLOUD B) {  // moves P by a fraction t of spiral from A to B
  float a =spiralAngle(A.P[0],A.P[A.n-1],B.P[0],B.P[B.n-1]); 
  float s =spiralScale(A.P[0],A.P[A.n-1],B.P[0],B.P[B.n-1]);
  pt G = spiralCenter(a, s, A.P[0], B.P[0]);
  spiral(G,s,a,t);
  }

}
