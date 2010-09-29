//*****************************************************************************
// TITLE:         LOOP  
// DESCRIPTION:   Class for closed-loop polygonal curves, inherits from COULD
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:         ADD CENTER OF MASS AND INERTIA AND CONSTRAINED DELAUNAY TRIANGULATION AND SELF-INTERSECTION TEST
//*****************************************************************************
class LOOP extends CLOUD {
 // ************************************** CREATE *********************************
 LOOP(int n) {super(n);}
 void makeSquare() {P[0]=P(width*0.8,height*0.8);P[1]=P(width*0.8,height*0.2);P[2]=P(width*0.2,height*0.2);P[3]=P(width*0.2,height*0.8);}
 void delete() { for (int i=p; i<n-1; i++) P[i].setTo(P[n(i)]); n--; p=p(p);}
 void insert(pt M) {                // grabs closeest vertex or adds vertex at closest edge. It will be dragged by te mouse
     p=0; for (int i=0; i<n; i++) if (d(M,P[i])<d(M,P[p])) p=i; 
     int e=-1;
     float d = d(M,P[p]);
     for (int i=0; i<n; i++) {float x=x(P[i],M,P[n(i)]), y=abs(ay(P[i],M,P[n(i)])); if ( 0.2<x && x<0.8 && y<d && y<height/20) {e=i; d=y;}; }
     if (e!=-1) { for (int i=n-1; i>e; i--) P[i+1].setTo(P[i]); n++; p=n(e); P[p].setToMouse();  };
     }
// ADDED FOR PROJECT 1
// Inserts point into specific position in loop
// We want to insert the new point after P[ind]
void insert(pt M, int ind) {
  for(int i = n-1; i > ind; i--) {
    P[i+1] = P[i];
  }
  n++;
  P[n(ind)] = M;
}

void insert(pt M, boolean check) {
  if(!check) {
    insert(M);
  }
  for(int i = 0; i < n; i++) {
    if(angle(P[n(i)], P[i], M) < 0.1) {
      insert(M, i);
      break;
     } 
   }
}

 // ************************************** TRAVERSAL UTILITIES *********************************
 int n(int j) {  if (j==n-1) {return (0);}  else {return(j+1);}  };  // next point in loop
 int p(int j) {  if (j==0) {return (n-1);}  else {return(j-1);}  };  // previous point in loop                                                     

 // ************************************** SELECT AND TRANSFORM POINTS *********************************
 void scalePoints(float s) {scalePoints(s,edgesCenter());};
 void scalePointsAroundCenterOfMass(pt M, vec V) {pt C=edgesCenter(); scalePoints(dot(V,U(M,C))/d(M,C),C);};
 void rotatePointsAroundCenterOfMass(float a) {rotatePoints(a,edgesCenter());}; // rotates points around their center of mass by angle a
 void rotatePointsAroundCenterOfMass(pt P, pt Q) {rotatePoints(edgesCenter(),P,Q);}; // rotates points around their center of mass G by angle <GP,GQ>
 void rotatePoints(pt G, pt P, pt Q) {rotatePoints(angle(V(G,P),V(G,Q)),edgesCenter());}; // rotates points around G by angle <GP,GQ>
 // ************************************** REGISTER *********************************
void registerToLoop(LOOP Q) {  // vertex registration
  pt A=edgesCenter(); pt B=Q.edgesCenter(); 
  float s=0, c=0;
  for (int i=0; i<min(n,Q.n)-1; i++) {
      float d=(d(P[i],P[n(i)])+d(Q.P[i],Q.P[Q.n(i)]))/2;
      s+=d*dot(V(A,A(P[i],P[n(i)])),R(V(B,A(Q.P[i],Q.P[Q.n(i)]))));
      c+=d*dot(V(A,A(P[i],P[n(i)])),V(B,A(Q.P[i],Q.P[Q.n(i)]))); 
      }
  float a = atan2(s,c);
  translatePoints(V(A,B));
  rotatePoints(a,B); 
  } 


 // ************************************** MEASURE AND TEST *********************************
 pt edgesCenter() {pt G=P(); float D=0; for (int i=0; i<n-1; i++) {float d=d(P[i],P[i+1]); D+=d; G.addPt(S(d,A(P[i],P[i+1])));} return S(1./D,G);} 
 
 float length () {float L=0; for (int i=0; i<n; i++) L+=d(P[i],P[n(i)]);  return(L); }
 
 boolean contains(pt M) {  
   boolean r=false;
   boolean p0=cw(P[0],M);
   boolean p=p0;
   for (int i=1; i<n; i++) {
     boolean c=cw(P[i],M); 
     if (p!=c) { if(cw(P[i-1],P[i],M)==p) r=!r; }; 
     p=c;
     }
   if (p!=p0) { if(cw(P[n-1],P[0],M)==p) r=!r; }; 
   return r;
   }
   
 float distanceTo(pt M) {return d(M,Projection(M));}
 
 pt Projection(pt M) {
     int v=0; for (int i=1; i<n; i++) if (d(M,P[i])<d(M,P[v])) v=i; 
     int e=-1;
     float d = d(M,P[v]);
     for (int i=0; i<n; i++) {float x=x(P[i],M,P[n(i)]); if ( 0<x && x<1) { float y=abs(ay(P[i],M,P[n(i)])); if(y<d) {e=i; d=y;} } }
     if (e!=-1) return Shadow(P[e],M,P[n(e)]); else return P(P[v]);
      }

 float area () {float A=0; for (int i=0; i<n; i++) A+=trapezeArea(P[i],P[n(i)]); return A; }

 pt Barycenter () {
      pt G=P(); 
      pt O=P(); 
      float area=0;
      for (int i=0; i<n; i++) {float a = triangleArea(O,P[i],P[n(i)]); area+=a; G.addScaledPt(a,A(O,P[i],P[n(i)])); };
      G.scaleBy(1./area); 
      return(G); 
      }
      
 float moment(pt G) {
   float m=0;
   for (int i=0; i<n; i++) {
          vec GA = V(G,P[i]); vec GB =V(G,P[n(i)]);
           m+=dot(R(GA),GB)*(dot(GA,GA)+dot(GA,GB)+dot(GB,GB));
           }  
     return m/12.;   
     }
  
 float alignentAngle(pt G) { // of the perimeter
    float xx=0, xy=0, yy=0, px=0, py=0, mx=0, my=0;
    for (int i=0; i<n; i++) {xx+=(P[i].x-G.x)*(P[i].x-G.x); xy+=(P[i].x-G.x)*(P[i].y-G.y); yy+=(P[i].y-G.y)*(P[i].y-G.y);};
    return atan2(2*xy,xx-yy)/2.;
    }
    
// ************************************** VIEWING *********************************
 void drawEdges() {beginShape(); for (int i=0; i<n; i++) v(P[i]); endShape(CLOSE);}  // fast draw of edges
 void showWithLabels(color c) {stroke(c); drawEdges(); if(showLetters.isTrue) {fill(white); drawPoints(12); fill(c); writePointLetters(); noFill();}
//   else { strokeWeight(5); stroke(dblue); drawDots(); if (showIDs.isTrue) {fill(dblue); writePointIDs(); noFill();}; } }
   else { strokeWeight(5); stroke(dblue); drawDots(); if (true) {fill(dblue); writePointIDs(); noFill();}; } }
    
 void showInertia() {stroke(orange); noFill(); pt G=Barycenter(); show(G,3); 
   float a=alignentAngle(G);  float m=moment(G); float r = sqrt(sqrt(m/PI*2.)); vec V=R(V(r,0),a); show(G,V); show(G,-1,V);  show(G,r);}

// ************************************** INTERSECTIONS *********************************
 int stabbed(pt A, pt B) {for (int i=0; i<n; i++) if(edgesIntersect(A,B,P[i],P[n(i)])) return i; return -1;}
 int stabbed(pt A, pt B, int j) {for (int i=0; i<n; i++) if ((i!=j)&&(edgesIntersect(A,B,P[i],P[n(i)]))) return i; return -1; }

// ************************************** RESAMPLE *********************************
 // ADDED FOR PROJECT 1
 void spray() {
   pt[] Q = new pt[5000];
   int ind = 0;
   // For every point in the loop spray towards the next point along interior and exterior
   for(int i=0; i<n; i++) {
     pt src = P[i];
     pt dst = P[n(i)];
     Q[ind] = P[i]; ind++;
     for(int k=5; k<6; k++) {
       pt newPt = T(L(src, k*0.1, dst), 1, R(U(V(src, dst))));
       Q[ind] = newPt; ind++;
       newPt = T(L(src, k*0.1, dst), 1, R(R(R(U(V(src, dst))))));
       Q[ind] = newPt; ind++;
     }
   }
   // Now copy them back into the points array
   empty();
   for(int i=0; i<ind; i++) {
     appendPoint(Q[i]);
   }
 }
 
 void refine(float s) { 
      pt[] Q = new pt [2*n];     
      for (int i=0; i<n; i++) { Q[2*i]= b(P[p(i)],P[i],P[n(i)],s); Q[2*i+1]=f(P[p(i)],P[i],P[n(i)],P[n(n(i))],s); };
      n*=2; for (int i=0; i<n; i++) P[i].setTo(Q[i]);
     }
 void coarsen() {n/=2; for (int i=0; i<n; i++) P[i].setTo(P[2*i]); } 
 
 void resample(int nn) { // resamples the curve with new nn vertices
    float L = length();  // current total length                           
    float d = L / nn;   // desired arc-length spacing                        
    float rd=d;        // remaining distance to next sample
    float cl=0;        // length of remaining portion of current edge
    int k=0,nk;        // counters
    pt [] R = new pt [nn]; // temporary array for the new points
    pt Q;
    int s=0;
    Q=P[0];         
    R[s++]=P(Q);     
    while (s<nn) {
       nk=n(k);
       cl=d(Q,P[nk]);                            
       if (rd<cl) {Q=T(Q,rd,P[nk]); R[s++]=P(Q);  
     cl-=rd; rd=d; } 
       else {rd-=cl; Q.setTo(P[nk]); k++; };
       };
     n=s;   for (int i=0; i<n; i++)  P[i].setTo(R[i]);
   }
   

// ************************************** SMOOTHING *********************************
vec [] L = new vec[1000];

 void smoothen() {for (int i=0; i<5; i++) {computeL2(); applyL(0.5); computeL2(); applyL(-0.5); };}
 void computeL2() {for (int i=0; i<n; i++) L[i]=cubic(P[p(p(i))],P[p(i)],P[i],P[n(i)],P[n(n(i))]);};
 void computeL() {for (int i=0; i<n; i++) L[i]=S(0.5,V(P[i],A(P[p(i)],P[n(i)])));};
 void applyL(float s) {for (int i=0; i<n; i++) P[i].translateBy(s,L[i]);};

void smoothenDEMO(float s) {
  vec [] L = new vec[n];
  for (int i=1; i<n-1; i++) L[i]=V(P[i],A(P[i-1],P[i+1]));
  for (int i=1; i<n-1; i++) arrow(P[i],L[i]);
  for (int i=1; i<n-1; i++) P[i].add(s,L[i]); 
  }
  
// ************************************** SUBDIVISION *********************************
void subdivide(float s) {subdivide(s,s);}
void subdivide(float a, float b) { 
      pt[] Q = new pt [2*n];     
      for (int i=0; i<n; i++) { Q[2*i]= B(P[p(i)],P[i],P[n(i)],a); Q[2*i+1]=F(P[p(i)],P[i],P[n(i)],P[n(n(i))],b); };
      n*=2; for (int i=0; i<n; i++) P[i].setTo(Q[i]);
     }

// ************************************** ART *********************************
void lace() {for (int i=0; i<n-1; i++) for (int k=i; k<n; k++) show(P[i],P[k]); }
void lace2() {for (int i=0; i<n; i++) {int j=i; for (int k=0; k<n/3; k++) j=n(j); show(P[i],P[j]); }}

// ************************************** TRIANGULATION *********************************
void drawShrunkTriangles2(int o) { 
   pt X = new pt(0,0);
   float r=1;  // radius of circumcircle
   for (int i=0; i<n-2; i++) for (int j=i+1; j<n-1; j++) for (int k=j+1; k<n; k++) {
      X=CircumCenter(P[i],P[j],P[k]);  r=d(X,P[i]);
      boolean found=false; 
      for (int m=0; m<n; m++) if ((m!=i)&&(m!=j)&&(m!=k)&&(X.disTo(P[m])<=r)) found=true;  
      if (!found && contains(Centroid(P[i],P[j],P[k]))) if(o==0) show(P[i],P[j],P[k]); else show(P[i],P[j],P[k],-o);
      }; // end triple loop
   };

void drawShrunkTriangles(int o) { 
   for (int i=0; i<n; i++) showDelaunay(i,n(i),-o);
      }; // end triple loop

void  showDelaunay(int i, int j, float o) {
   int mk=0; 
   float mb=100000;
   for (int k=0; k<n; k++) if(k!=i && k!=j) {float b=bulge(P[i],P[k],P[j]); if(0<b & b<mb) {mb=b; mk=k;}} // add check for boundary crossing
   show(P[i],P[mk],P[j], o);
   }
   
void delaunayFromEdges() { 
      for (int i=0; i<n; i++)  for (int j=0; j<n; j++)  
                 if (cw(P[i],P[n(i)],P[j])) {
                    pt CC=CircumCenter(P[i],P[n(i)],P[j]); 
                    float r=d(P[i],CC);
                   // float r=radius(P[i],P[n(i)],P[j]);
                    boolean found=false;
                    for (int k=0; k<n; k++) if (d(P[k],CC)+0.1<r) found=true;
                    if (!found) show(P[i],P[n(i)],P[j]);
                    };
    }
void showDelaunayOfPoints(float o) { 
      for (int i=0; i<n-2; i++)  for (int j=i+1; j<n-1; j++)  for (int k=j+1; k<n; k++)  {
                   pt CC=CircumCenter(P[i],P[j],P[k]);  float r=d(P[i],CC);                
                   boolean found=false;
                   for (int m=0; m<n; m++) if (d(P[m],CC)+0.001<r) found=true;
                   if (!found) {if(contains(A(P[i],P[j],P[k]))) {fill(green); show(P[i],P[j],P[k],o);} };                   //Removed "else fill(yellow);" from before show and added braces. if (cw(P[i],P[n(i)],P[j])) {}
                     

                   };
    }
    
void updatePoints(Mesh M) {
    for (int i=0; i<M.nv; i++) M.G[i].setTo(P[i]);
}
   
void makeDelaunayOfPoints(Mesh M) { 
    M.init(); // empty the mesh
    for (int i=0; i<n; i++) M.addVertex(P[i]); // add all the vertices of the loop to be vertices of the mesh
    for (int i=0; i<n-2; i++)  for (int j=i+1; j<n-1; j++)  for (int k=j+1; k<n; k++)  { // generate all candidate triangles
        pt CC=CircumCenter(P[i],P[j],P[k]);  float r=d(P[i],CC);  // compute their circumcenter and radius          
        boolean empty=true; for (int m=0; m<n; m++) if (d(P[m],CC)+0.001<r) empty=false;  // check whether circle is empty 
        if (empty) {if (cw(P[i],P[j],P[k])) M.addTriangle(i,j,k,contains(A(P[i],P[j],P[k]))); else M.addTriangle(i,k,j,contains(A(P[i],P[j],P[k]))); }; // add properly oriented triangle to the mesh
        }
    M.computeO(); // computes the O table for connectivity
    }
 
 } // end LOOP
 
//************** UTILITIES FOR AREA AND CENTER OF MASS CALCULATIONS *****************
float trapezeArea(pt A, pt B) {return((B.x-A.x)*(B.y+A.y)/2.);}
pt trapezeCenter(pt A, pt B) { return(new pt(A.x+(B.x-A.x)*(A.y+2*B.y)/(A.y+B.y)/3., (A.y*A.y+A.y*B.y+B.y*B.y)/(A.y+B.y)/3.) ); }


