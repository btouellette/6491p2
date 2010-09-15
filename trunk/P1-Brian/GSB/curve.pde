//*****************************************************************************
// TITLE:         CURVE  
// DESCRIPTION:   Class for open polygonal curves, inherits from COULD
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
class CURVE extends CLOUD {
 int b=0, c=0;
 CURVE(int n) {super(n); }

// ************************************** INDICES *********************************
 int next(int j) { return j+1; };  // next vertex 
 int prev(int j) { return j-1; };  // previous vertex                                                      
 void bcSet() {b=n/3; c=2*n/3;}
// ************************************** INSERT POINTS *********************************
 void insert(pt M) {                // grabs closest vertex or adds vertex at closest edge. It will be dragged by te mouse
     p=0; for (int i=0; i<n; i++) if (d(M,P[i])<d(M,P[p])) p=i; 
     int e=-1;
     float d = d(M,P[p]);
     for (int i=0; i<n-1; i++) {float x=x(P[i],M,P[i+1]), y=abs(ay(P[i],M,P[i+1])); if ( 0.2<x && x<0.8 && y<d && y<height/20) {e=i; d=y;}; }
     if (e!=-1) { for (int i=n-1; i>e; i--) P[i+1].setTo(P[i]); n++; p=e+1; P[p].setToMouse();  };
     }

  // ************************************** SELECT AND TRANSFORM POINTS *********************************
 int closestVertex(pt M) {int c=0; for (int i=0; i<n; i++) if (d(M,P[i])<d(M,P[c])) c=i; return c;} // identifies closeest vertex
 void scalePoints(float s) {scalePoints(s,edgesCenter());};
 void scalePointsAroundCenterOfMass(pt M, vec V) {pt C=edgesCenter(); scalePoints(dot(V,U(M,C))/d(M,C),C);};
 void rotatePointsAroundCenterOfMass(float a) {rotatePoints(a,edgesCenter());}; // rotates points around their center of mass by angle a
 void rotatePointsAroundCenterOfMass(pt P, pt Q) {rotatePoints(edgesCenter(),P,Q);}; // rotates points around their center of mass G by angle <GP,GQ>
 void rotatePoints(pt G, pt P, pt Q) {rotatePoints(angle(V(G,P),V(G,Q)),edgesCenter());}; // rotates points around G by angle <GP,GQ>
 void bSet(pt M) {b=closestVertex(M);}
 void cSet(pt M) {c=closestVertex(M);}
// ************************************** REGISTER *********************************
void registerToCurve(CURVE Q) {  // vertex registration
  pt A=edgesCenter(); pt B=Q.edgesCenter(); 
  float s=0; 
  float c=0; 
  for (int i=0; i<min(n,Q.n)-1; i++) {
      float d=(d(P[i],P[i+1])+d(Q.P[i],Q.P[i+1]))/2;
      s+=d*dot(V(A,A(P[i],P[i+1])),R(V(B,A(Q.P[i],Q.P[i+1]))));
      c+=d*dot(V(A,A(P[i],P[i+1])),V(B,A(Q.P[i],Q.P[i+1]))); 
      }
  float a = atan2(s,c);
  translatePoints(V(A,B));
  rotatePoints(a,B); 
  } 

// ************************************** MORPH *********************************
void curvatureMorph(CURVE A, float t, CURVE B) {
   copyFrom(A);
   P[1]=T(P[0],pow(d(B.P[0],B.P[1])/d(A.P[0],A.P[1]),t),V(A.P[0],A.P[1])); 
   n=min(A.n,B.n); 
   for (int i=2; i<n; i++) {
//     float d=d(A.P[i-1],A.P[i])*pow(d(B.P[i-1],B.P[i])/d(A.P[i-1],A.P[i]),t);
     float d=(1.-t)*d(A.P[i-1],A.P[i])+t*d(B.P[i-1],B.P[i]);
     float a=(1.-t)*angle(V(A.P[i-2],A.P[i-1]),V(A.P[i-1],A.P[i]))+t*angle(V(B.P[i-2],B.P[i-1]),V(B.P[i-1],B.P[i]));
     P[i]=T(P[i-1],S(d,R(U(V(P[i-2],P[i-1])),a)));
     }
   }
   
void curvatureMorphXX(CURVE A, float t, CURVE B) {
   copyFrom(A);
   P[1] = T(P[0] , 1.-t+t*(d(B.P[0],B.P[1])/d(A.P[0],A.P[1])) , V(A.P[0],A.P[1]) ); 
   n=min(A.n,B.n); 
   for (int i=2; i<n; i++) {
     float d=d(A.P[i-1],A.P[i]) + (1.-t)+t*d(B.P[i-1],B.P[i])/d(A.P[i-1],A.P[i]);
     float a=(1.-t)*angle(V(A.P[i-2],A.P[i-1]),V(A.P[i-1],A.P[i]))+t*angle(V(B.P[i-2],B.P[i-1]),V(B.P[i-1],B.P[i]));
     P[i]=T(P[i-1],S(d,R(U(V(P[i-2],P[i-1])),a)));
     }
   }
   
void spiral(CURVE A, float t, CURVE B) {  // moves P by a fraction t of spiral from A to B
  float a =spiralAngle(A.P[0],A.P[A.n-1],B.P[0],B.P[B.n-1]); 
  float s = A.length() / B.length();
  pt G = spiralCenter(a, s, A.P[0], B.P[0]);
  spiral(G,s,a,t);
  }
  
 // ************************************** MEASURE *********************************
 pt edgesCenter() {pt G=P(); float D=0; for (int i=0; i<n-1; i++) {float d=d(P[i],P[i+1]); D+=d; G.addPt(S(d,A(P[i],P[i+1])));} return S(1./D,G);} 
 float length () {float L=0; for (int i=0; i<n-1; i++) L+=P[i].disTo(P[i+1]);  return(L); }
 float length (int s, int e) {float L=0; for (int i=s; i<e; i++) L+=d(P[i],P[i+1]); return(L); }  
 void scaleToLength(float L) {float s=L/length(); scalePointsRelative(s,edgesCenter());}
 void matchLengthOf(CURVE C) {float s=C.length()/length(); scalePointsRelative(s,edgesCenter());}

 // ************************************** DISPLAY *********************************
 void showWithHead(color col, int k) {if(n<1) return; 
   stroke(col); strokeWeight(1); drawEdges(); strokeWeight(3); drawDots(); fill(white); P[0].show(12); fill(col); label(P[0],str(k)); noFill(); 
   strokeWeight(1); P[b].show(2);  P[c].show(3); 
   }
 void drawEdges() {beginShape(); for (int i=0; i<n; i++) v(P[i]); endShape();}  // fast draw of edges
 float distanceTo(pt M) {return d(M,Projection(M));}
 pt Projection(pt M) { 
     int v=0; for (int i=1; i<n; i++) if (d(M,P[i])<d(M,P[v])) v=i; 
     int e=-1; float d = d(M,P[v]);
     for (int i=0; i<n-1; i++) {float x=x(P[i],M,P[i+1]); if ( 0<x && x<1) { float y=abs(ay(P[i],M,P[i+1])); if(y<d) {e=i; d=y;} } }
     if (e!=-1) return Shadow(P[e],M,P[e+1]); else return P(P[v]);
     }
 void showWithLabels(color c) {stroke(c); drawEdges(); fill(white); drawPoints(12); fill(c); writePointLetters(); noFill(); }
 void drawBeziers() {addEnds(); for (int i=2; i<n-3; i++) drawBezier(i); chopEnds();}
 void drawBezier(int i) {drawCubicBezier(P[i], T(P[i],1./6,V(P[prev(i)],P[next(i)])), T(P[next(i)],-1./6,V(P[i],P[next(next(i))])), P[next(i)]); }
 
// ************************************** INTERSECTIONS *********************************
boolean stabbed(pt A, pt B) {for (int i=0; i<n-1; i++) if(edgesIntersect(A,B,P[i],P[i+1])) return true; return false; }
boolean stabbed(pt A, pt B, int j) {for (int i=0; i<n-1; i++) if((i!=j)&&edgesIntersect(A,B,P[i],P[i+1])) return true; return false;  }

//  ************************************* RESAMPLE ***********************************************
 pt at (float t) { 
    pt Q = P(P[0]); 
    if (t<0 || t>1) {return Q; }
    float rd=t*length();
    float cl=0;  // length of remaining portion of current edge
    int nk=1;    // index of the next vertex 
    while (rd>0)  { // keep adding samples to R
       cl=Q.disTo(P[nk]);                                                 
       if (rd<=cl) {Q.translateTowardsBy(rd,P[nk]); return Q; }  // next sample is along the current edge (or at its end)
              else {rd-=cl; Q.setTo(P[nk++]); };                 // move to the end-vertex of the current edge
        };
    return Q;
    }

 void resample(int nrv) { if(n==0) return;
    float L = length();                            
    pt R[] = new pt[nrv+1];  // temporary array for new samples
    pt Q = new pt(0,0);
    float d = L / (nrv-1);  // desired distance between new samples
    float rd=d;  // remaining distance to next sample
    float cl=0;  // length of remaining portion of current edge
    int nk=1;    // index of the next vertex on the original curve
    int c=0;     // number of already added points
     Q.setTo(P[0]);     // Set Q as first vertex         
     R[c++]=P(Q);       // add Q as first sample and increment counter n
     while (c<nrv-1)  { // keep adding samples to R
       cl=Q.disTo(P[nk]);                                                 
       if (rd<=cl) {Q.translateTowardsBy(rd,P[nk]); R[c++]=P(Q); cl-=rd; rd=d; }  // next sample is along the current edge (or at its end)
              else {rd-=cl; Q.setTo(P[nk++]); };                                 // move past the end-vertex of the current edge
        };
     R[c++]=P(P[n-1]);                       // last sample is last vertex
   for (int i=0; i<c; i++) P[i].setTo(R[i]); // copy new samples to P
   n=c;      // reset vertex count                                
   }

void refine(float s) { 
      pt[] Q = new pt [2*n];     
      for (int i=0; i<n-1; i++) { Q[2*i]= P(P[i]); Q[2*i+1]=A(P[i],P[i+1]); };
      Q[2*n-2]=P(P[n-1]);
      n=2*n-1; 
      for (int i=0; i<n; i++) P[i]=P(Q[i]);
     }
void coarsen() {n=(n+1)/2; for (int i=1; i<n; i++) P[i]=P(P[2*i]); } 

//  ************************************* SUBDIVIDE  ***********************************************

 void subdivide(float s) { 
     addEnds();
     pt[] Q = new pt [2*n];     
     int j=0; for (int i=1; i<n-2; i++) { Q[j++]= B(P[prev(i)],P[i],P[next(i)],s); Q[j++]=F(P[prev(i)],P[i],P[next(i)],P[next(next(i))],s); };
      int k=n-2; Q[j++]=B(P[prev(k)],P[k],P[next(k)],s);
      n=j; 
      for (int i=0; i<n; i++) P[i].setTo(Q[i]);
      chopEnds();
     }
     
 void subdivideProportional() { 
   addEnds();
   pt[] Q = new pt [2*n];     // new control points
   int j=0;
   for (int i=1; i<n-2; i++) {Q[j++]=P(P[i]); Q[j++]=prop(P[prev(i)],P[i],P[next(i)],P[next(next(i))]); }
   Q[j++]=P(P[n-2]);
   n=j; 
   for (int i=0; i<n; i++) P[i].setTo(Q[i]);
   chopEnds();
  }


// ************************************** SMOOTHING *********************************

void beautify() {resample(40); smoothen(); smoothen(); subdivide(0.5); subdivide(0.5); subdivide(0.5); resample(120); }

vec [] L = new vec[1000];

void smoothen(float s) {
  for (int i=1; i<n-1; i++) L[i]=V(P[i],A(P[i-1],P[i+1]));
  for (int i=1; i<n-1; i++) arrow(P[i],L[i]);
  for (int i=1; i<n-1; i++) P[i].add(s,L[i]); 
  }
 void smoothen() {addEnds(); for (int i=0; i<5; i++) {computeL2(); applyL(0.5); computeL2(); applyL(-0.5); } chopEnds();}
 void computeL2() {for (int i=2; i<n-2; i++) L[i]=vecToCubic(P[prev(prev(i))],P[prev(i)],P[i],P[next(i)],P[next(next(i))]);};
 void applyL(float s) {for (int i=2; i<n-2; i++) P[i].translateBy(s,L[i]);};

// ************************************** ENDS *********************************
      
 void drawEnds() {addEnds(); P[0].to(P[1]); P[1].to(P[2]);  P[n-1].to(P[n-2]); P[n-2].to(P[n-3]); P[0].show(2); P[1].show(2); P[n-1].show(2); P[n-2].show(2); chopEnds(); }
 void chopEnds() {n-=4;  for (int i=0; i<n; i++) { P[i].setTo(P[i+2]);};  } 
 void addEnds() {for (int i=n-1; 0<=i; i--) P[i+2].setTo(P[i]); n+=4;  adjustEnds(); }   
 void adjustEnds() {
    pt P0= End0(P[2],P[next(2)],P[next(next(2))]);
    pt P1= End1(P[2],P[next(2)],P[next(next(2))]);
    pt Pn= End1(P[n-3],P[prev(n-3)],P[prev(prev(n-3))]);
    pt Pnn=End0(P[n-3],P[prev(n-3)],P[prev(prev(n-3))]);
    P[0].setTo(P0); P[1].setTo(P1);   P[n-2].setTo(Pn); P[n-1].setTo(Pnn); 
     }

 } // end CURVE
 
pt   End1(pt P0, pt P1, pt P2) { return L(P1,2,P0); }
pt   End0(pt P0, pt P1, pt P2) { return T(L(P1,2,P0),V(P2,P1)); }
