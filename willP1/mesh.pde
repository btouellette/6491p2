// CORNER TABLE FOR TRIANGLE MESHES by Jarek Rosignac
// Last edited Feb 17, 2008
float rr=2; // radius for displaying vertices as balls
String [] fn= {"mesh.vts"};
int fni=0; int fniMax=fn.length; 


Mesh M = new Mesh();     // creates a default triangle meshvoid computeValenceAndResetNormals() {      // caches valence of each vertex
 
//========================== class MESH ===============================
class Mesh {

//  ==================================== INIT, CREATE, COPY ====================================
 Mesh() {}
 int maxnv = 45000;                         //  max number of vertices
 int maxnt = maxnv*2;                       // max number of triangles
 void declare() {c=0; sc=0; prevc=0;
   for (int i=0; i<maxnv; i++) {G[i]=new pt(0,0); Nv[i]=new vec(0,0);};   // init vertices and normals
   for (int i=0; i<maxnt; i++) {Nt[i]=new vec(0,0); visible[i]=true;} ;}       // init triangle normals and skeleton lab els
 void init() {c=0; prevc=0; sc=0; nv=0; nt=0; nc=0;  for (int i=0; i<maxnt; i++) visible[i]=true;} // init counts and visibility flags
 void makeGrid (int w) { // make a 2D grid of vertices
  for (int i=0; i<w; i++) {for (int j=0; j<w; j++) { G[w*i+j].setTo(height*.8*j/(w-1)+height/10,height*.8*i/(w-1)+height/10);}}    
  for (int i=0; i<w-1; i++) {for (int j=0; j<w-1; j++) {                  // define the triangles for the grid
    V[(i*(w-1)+j)*6]=i*w+j;       V[(i*(w-1)+j)*6+2]=(i+1)*w+j;       V[(i*(w-1)+j)*6+1]=(i+1)*w+j+1;
    V[(i*(w-1)+j)*6+3]=i*w+j;     V[(i*(w-1)+j)*6+5]=(i+1)*w+j+1;     V[(i*(w-1)+j)*6+4]=i*w+j+1;}; };
  nv = w*w;
  nt = 2*(w-1)*(w-1); 
  nc=3*nt;  }
 void update() {computeO(); computeValence(); }
  // ============================================= CORNER OPERATORS =======================================
 int nc = 0;                                // current number of corners (3 per triangle)
 int c = 0;                                 // current corner shown in image and manipulated with keys: n, p, o, l, r
 int sc=0;                                  // saved value of c
 int[] V = new int [3*maxnt];               // V table (triangle/vertex indices)
 int[] O = new int [3*maxnt];               // O table (opposite corner indices)
 int[] Tc = new int[3*maxnt];               // corner type used for some applications


// operations on an arbitrary corner
int t (int c) {return int(c/3);};          // triangle of corner    
int n (int c) {return 3*t(c)+(c+1)%3;};   // next corner in the same t(c)    
int p (int c) {return n(n(c));};  // previous corner in the same t(c)  
int v (int c) {return V[c] ;};   // id of the vertex of c             
pt g (int c) {return G[v(c)];};  // shortcut to get the location of the vertex v(c) of corner c
boolean b (int c) {return O[c]==-1;};       // if c faces a border (has no opposite)  ********************** change this to be c and not -1
int o (int c) {if (b(c)) return c; else return O[c];}; // opposite (or self if it has no opposite)
int l (int c) {return o(n(c));}; // left neighbor (or next if n(c) has no opposite)                      
int r (int c) {return o(p(c));}; // right neighbor (or previous if p(c) has no opposite)                    
int s (int c) {return n(l(c));}; // swings around v(c) or around a border loop

// operations on the selected corner c
int t() {return t(c);}
int n() {return n(c);}
int p() {return p(c);}
 int v() {return v(c);}
int o() {return o(c);}
boolean b() {return b(c);}             // border: returns true if corner has no opposite
int l() {return l(c);}
int r() {return r(c);}
int s() {return s(c);}
pt g() {return g(c);}            // shortcut to get the point of the vertex v(c) of corner c

// normals and mid-edge verex Ids for subdivision

vec Nv (int c) {return(Nv[V[c]]);}; vec Nv() {return Nv(c);}            // shortcut to get the normal of v(c) 
vec Nt (int c) {return(Nt[t(c)]);}; vec Nt() {return Nt(c);}            // shortcut to get the normal of t(c) 
int w (int c) {return(W[c]);};               // temporary indices to mid-edge vertices associated with corners during subdivision
  
void previous() {c=p(c);};
void next() {c=n(c);};
void opposite() {if(!b(c)) {c=o(c);};};
void left() {next(); opposite();};
void right() {previous(); opposite();};
void swing() {left(); next(); };

void writeCorner (int c) {println("c="+c+", n="+n(c)+", p="+p(c)+", o="+o(c)+", v="+v(c)+", t="+t(c)+"."+", nt="+nt+", nv="+nv ); }; 
void writeCorner () {writeCorner (c);}
void writeCorners () {for (int c=0; c<nc; c++) {println("T["+c+"]="+t(c)+", visible="+visible[t(c)]+", v="+v(c)+",  o="+o(c));};}

pt cg(int c) {pt cPt = A(g(c),A(g(c),triCenter(t(c))));  return(cPt); };   // computes point at corner
pt corner(int c) {return A(g(c),A(g(c),triCenter(t(c))));   };   // returns corner point
void showCorner(int c, float r) {corner(c).show(r); };   // renders corner c as small ball

// ============================================= O TABLE CONSTRUCTION =========================================
void computeOnaive() {                         // sets the O table from the V table, assumes consistent orientation of triangles
  for (int i=0; i<3*nt; i++) {O[i]=-1;};  // init O table to -1: has no opposite (i.e. is a border corner)
  for (int i=0; i<nc; i++) {  for (int j=i+1; j<nc; j++) {       // for each corner i, for each other corner j
      if( (v(n(i))==v(p(j))) && (v(p(i))==v(n(j))) ) {O[i]=j; O[j]=i;};};};}// make i and j opposite if they match         

void computeO() { 
  int val[] = new int [nv]; for (int v=0; v<nv; v++) val[v]=0;  for (int c=0; c<nc; c++) val[v(c)]++;   //  valences
  int fic[] = new int [nv]; int rfic=0; for (int v=0; v<nv; v++) {fic[v]=rfic; rfic+=val[v];};  // head of list of incident corners
  for (int v=0; v<nv; v++) val[v]=0;   // valences wil be reused to track how many incident corners were encountered for each vertex
  int [] C = new int [nc]; for (int c=0; c<nc; c++) C[fic[v(c)]+val[v(c)]++]=c;  // vor each vertex: the list of val[v] incident corners starts at C[fic[v]]
  for (int c=0; c<nc; c++) O[c]=-1;    // init O table to -1 meaning that a corner has no opposite (i.e. faces a border)
  for (int v=0; v<nv; v++)             // for each vertex...
     for (int a=fic[v]; a<fic[v]+val[v]-1; a++) for (int b=a+1; b<fic[v]+val[v]; b++)  { // for each pair (C[a],C[b[]) of its incident corners
        if (v(n(C[a]))==v(p(C[b]))) {O[p(C[a])]=n(C[b]); O[n(C[b])]=p(C[a]); }; // if C[a] follows C[b] around v, then p(C[a]) and n(C[b]) are opposite
        if (v(n(C[b]))==v(p(C[a]))) {O[p(C[b])]=n(C[a]); O[n(C[a])]=p(C[b]); };        };               
  c=0; sc=0; // current corner
  }
    
// ============================================= DISPLAY =======================================
pt Cbox = P(width/2,height/2);                   // mini-max box center
float Rbox=1000;                                        // Radius of enclosing ball
boolean showLabels=false; 
void computeBox() {
  pt Lbox =  G[0].make();  pt Hbox =  G[0].make();
  for (int i=1; i<nv; i++) { 
    Lbox.x=min(Lbox.x,G[i].x); Lbox.y=min(Lbox.y,G[i].y); 
    Hbox.x=max(Hbox.x,G[i].x); Hbox.y=max(Hbox.y,G[i].y); 
    };
  Cbox.setTo(A(Lbox,Hbox));  Rbox=Cbox.disTo(Hbox); 
  };
void showMesh() {
  int col=60;
  stroke(green); strokeWeight(8); showBorder(); strokeWeight(1); // shows border as fat cyan curve
  noSmooth(); noStroke();
  if(showTriangles) showTriangles();  
  if (showEdges) {stroke(dblue); strokeWeight(1); for(int i=0; i<nc; i++) drawEdge(i); };  
  if (showSelectedTriangle) {noStroke(); fill(dgreen); shade(t(c)); noFill(); }; 
  if (showVertices) {noStroke(); noSmooth();fill(white); for (int v=0; v<nv; v++)  G[v].show(rr); noFill();};
  if (showLabels) { fill(black); 
      for (int i=0; i<nv; i++) {label(G[i],labelD,"v"+str(i)); }; 
      for (int i=0; i<nc; i++) {label(triCenter(i),labelD,"t"+str(i)); }; noFill();};
  noStroke(); fill(dred); showCorner(prevc,1.5*rr); fill(red); showCorner(c,int(rr));  
  }
//  ==========================================================  EDGES ===========================================
boolean showEdges=true;
void findShortestEdge() {c=cornerOfShortestEdge();  } 
int cornerOfShortestEdge() {  // assumes manifold
  float md=d(g(p(0)),g(n(0))); int ma=0;
  for (int a=1; a<nc; a++) if (vis(a)&&(d(g(p(a)),g(n(a)))<md)) {ma=a; md=d(g(p(a)),g(n(a)));}; 
  return ma;
  } 
void drawEdge(int c) {show(g(p(c)),g(n(c))); };  // draws edge of t(c) opposite to corner c
void showBorderOfVisible() {for (int i=0; i<nc; i++) {if (visible[t(i)]) { if(b(i)) drawEdge(i); else if(!visible[t(o(i))]) drawEdge(i);}; }; };          // draws all border edges
void showBorder() {for (int i=0; i<nc; i++) {if (b(i)) {drawEdge(i);}; }; };         // draws all border edges

//  ==========================================================  TRIANGLES ===========================================
 boolean showTriangles=true;
 boolean showSelectedTriangle=true;
 int nt = 0;                   // current number of triangles
 void addTriangle(int i, int j, int k) {V[nc++]=i; V[nc++]=j; V[nc++]=k; visible[nt++]=true;}
 void addTriangle(int i, int j, int k, boolean visibleTirangle) {V[nc++]=i; V[nc++]=j; V[nc++]=k; visible[nt++]=visibleTirangle;}
 boolean[] visible = new boolean[maxnt];    // set if triangle visible
 boolean vis(int c) {return visible[t(c)]; };   // true if triangle of c is visible
 int[] Mt = new int[maxnt];                 // triangle markers for distance and other things   
 boolean [] VisitedT = new boolean [maxnt];  // triangle visited
 pt triCenter(int i) {return A( G[V[3*i]], G[V[3*i+1]], G[V[3*i+2]] ) ;}  // computes center of triangle t(i) 
 void writeTri (int i) {println("T"+i+": V = ("+V[3*i]+":"+v(o(3*i))+","+V[3*i+1]+":"+v(o(3*i+1))+","+V[3*i+2]+":"+v(o(3*i+2))+")"); };
 void shade(int t) {show(g(3*t),g(3*t+1),g(3*t+2));}; // shade tris
 void showTriangles() {  for(int t=0; t<nt; t++) {if(visible[t]) fill(cyan); else fill(yellow); show(g(3*t),g(3*t+1),g(3*t+2),-2);}; noFill();}; 
 void classifyTriangles(LOOP C) {for(int t=0; t<nt; t++) visible[t]=C.contains(triCenter(t)); }
//  ==========================================================  VERTICES ===========================================
 boolean showVertices=false;
 int nv = 0;                              // current  number of vertices
 pt[] G = new pt [maxnv];                   // geometry table (vertices)
 pt[] G2 = new pt [maxnv]; //2008-03-06 JJ misc
 int[] Mv = new int[maxnv];                  // vertex markers
 int [] Valence = new int [maxnv];          // vertex valence (count of incident triangles)
 boolean [] Border = new boolean [maxnv];   // vertex is border
 boolean [] VisitedV = new boolean [maxnv];  // vertex visited
 int r=5;                                // radius of spheres for displaying vertices
int addVertex(pt P) { G[nv].setTo(P); nv++; return nv-1;};
int addVertex(float x, float y) { G[nv++]=P(x,y); return nv-1;};
void move(int c) {g(c).addScaled(pmouseY-mouseY,Nv(c));}
void move(int c, float d) {g(c).addScaled(d,Nv(c));}
void move() {move(c); }
void moveROI() {
     pt Q = new pt(0,0);
     for (int i=0; i<nv; i++) Mv[i]=0;  // resets the valences to 0
     computeDistance(5);
     for (int i=0; i<nv; i++) VisitedV[i]=false;  // resets the valences to 0
     for (int i=0; i<nc; i++) if(!VisitedV[v(i)]&&(Mv[v(i)]!=0)) move(i,1.*(pmouseY-mouseY+mouseX-pmouseX)*(rings-Mv[v(i)])/rings/10);  // moves ROI
     computeDistance(7);
     Q.setTo(g());
     smoothROI();
     g().setTo(Q);
     }
     
//  ==========================================================  NORMALS ===========================================
boolean showNormals=false;
vec[] Nv = new vec [maxnv];                 // vertex normals or laplace vectors
vec[] Nt = new vec [maxnt];                // triangles normals


// ============================================================= SMOOTHING ============================================================
void computeValence() {      // caches valence of each vertex
  for (int i=0; i<nv; i++) Valence[i]=0;  // resets the valences to 0
  for (int i=0; i<nc; i++) Valence[v(i)]++; 
  }

void clearNormals() {      // caches valence of each vertex
  for (int i=0; i<nv; i++) Nv[i]=V(0,0);  // resets the valences to 0
  }

void computeLaplaceVectors() {  // computes the vertex normals as sums of the normal vectors of incident tirangles scaled by area/2
  computeValence(); clearNormals();
  for (int i=0; i<nc; i++) {Nv[v(p(i))].add( V( g(i), SpiekerCenter(g(i),g(p(i)),g(n(i))) ) );};  // ***** fix to be proportional to area
  for (int i=0; i<nv; i++) {Nv[i].div(Valence[i]);};                         };
void tuck(float s) {for (int i=0; i<nv; i++) {G[i].addScaled(s,Nv[i]);}; };  // displaces each vertex by a fraction s of its normal
void smoothen() {computeLaplaceVectors(); tuck(0.6); computeLaplaceVectors(); tuck(-0.6);};
void tuckROI(float s) {for (int i=0; i<nv; i++) if (Mv[i]!=0) G[i].addScaled(s,Nv[i]); };  // displaces each vertex by a fraction s of its normal
void smoothROI() {computeLaplaceVectors(); tuckROI(0.5); computeLaplaceVectors(); tuckROI(-0.5);};
// ============================================================= SUBDIVISION ============================================================
int[] W = new int [3*maxnt];               // mid-edge vertex indices for subdivision (associated with corner opposite to edge)
void splitEdges() {            // creates a new vertex for each edge and stores its ID in the W of the corner (and of its opposite if any)
  for (int i=0; i<3*nt; i++) {  // for each corner i
    if(b(i)) {G[nv]=A(g(n(i)),g(p(i))); W[i]=nv++;}
    else {if(i<o(i)) {G[nv]=A(g(n(i)),g(p(i))); W[o(i)]=nv; W[i]=nv++; }; }; }; } // if this corner is the first to see the edge
  
void bulge() {              // tweaks the new mid-edge vertices according to the Butterfly mask
  for (int i=0; i<3*nt; i++) {
    if((!b(i))&&(i<o(i))) {    // no tweak for mid-vertices of border edges
     if (!b(p(i))&&!b(n(i))&&!b(p(o(i)))&&!b(n(o(i))))
      {G[W[i]].addScaled(0.25,A(A(g(l(i)),g(r(i))),A(g(l(o(i))),g(r(o(i))))).vecTo(A(g(i),g(o(i))))); }; }; }; };
  
void splitTriangles() {    // splits each tirangle into 4
  for (int i=0; i<3*nt; i=i+3) {
    V[3*nt+i]=v(i); V[n(3*nt+i)]=w(p(i)); V[p(3*nt+i)]=w(n(i));
    V[6*nt+i]=v(n(i)); V[n(6*nt+i)]=w(i); V[p(6*nt+i)]=w(p(i));
    V[9*nt+i]=v(p(i)); V[n(9*nt+i)]=w(n(i)); V[p(9*nt+i)]=w(i);
    V[i]=w(i); V[n(i)]=w(n(i)); V[p(i)]=w(p(i));
    };
  nt=4*nt; nc=3*nt;  };
  
void refine() {update(); splitEdges(); bulge(); splitTriangles(); update(); }
  
//  ========================================================== FILL HOLES ===========================================
void fanHoles() {for (int cc=0; cc<nc; cc++) if (visible[t(cc)]&&b(cc)) fanThisHole(cc);  }
void fanThisHole() {fanThisHole(c);}
void fanThisHole(int cc) {   // fill shole with triangle fan (around average of parallelogram predictors). Must then call computeO to restore O table
 if(!b(cc)) return ; // stop if cc is not facing a border
 G[nv].setTo(0,0);   // tip vertex of fan
 int o=0;              // tip corner of new fan triangle
 int n=0;              // triangle count in fan
 int a=n(cc);          // corner running along the border
 while (n(a)!=cc) {    // walk around the border loop 
   if(b(p(a))) {       // when a is at the left-end of a border edge
      G[nv].addPt( T(g(a),V(g(p(a)),g(a)))); // add parallelogram prediction and mid-edge point
      o=3*nt; V[o]=nv; V[n(o)]=v(n(a)); V[p(o)]=v(a); visible[nt]=true; nt++; // add triangle to V table, make it visible
      O[o]=p(a); O[p(a)]=o;        // link opposites for tip corner
      O[n(o)]=-1; O[p(o)]=-1;
      n++;}; // increase triangle-count in fan
    a=s(a);} // next corner along border
 G[nv].scale(1./n); // divide fan tip to make it the average of all predictions
 a=o(cc);       // reset a to walk around the fan again and set up O
 int l=n(a);   // keep track of previous
 int i=0; 
 while(i<n) {a=s(a); if(v(a)==nv) { i++; O[p(a)]=l; O[l]=p(a); l=n(a);}; };  // set O around the fan
 nv++;  nc=3*nt;  // update vertex count and corner count
 };

// =========================================== GEODESIC MEASURES, DISTANCES =============================
 boolean  showPath=false, showDistance=false;  
 boolean[] P = new boolean [3*maxnt];       // marker of corners in a path to parent triangle
 int[] Distance = new int[maxnt];           // triangle markers for distance fields 
 int[] SMt = new int[maxnt];                // sum of triangle markers for isolation
 int prevc = 0;                             // previously selected corner
 int rings=2;                           // number of rings for colorcoding

void computeDistance(int maxr) {
  int tc=0;
  int r=1;
  for(int i=0; i<nt; i++) {Mt[i]=0;};  Mt[t(c)]=1; tc++;
  for(int i=0; i<nv; i++) {Mv[i]=0;};
  while ((tc<nt)&&(r<=maxr)) {
      for(int i=0; i<nc; i++) {if ((Mv[v(i)]==0)&&(Mt[t(i)]==r)) {Mv[v(i)]=r;};};
     for(int i=0; i<nc; i++) {if ((Mt[t(i)]==0)&&(Mv[v(i)]==r)) {Mt[t(i)]=r+1; tc++;};};
     r++;
     };
  rings=r;
  }
  
void computeIsolation() {
  println("Starting isolation computation for "+nt+" triangles");
  for(int i=0; i<nt; i++) {SMt[i]=0;}; 
  for(c=0; c<nc; c+=3) {println("  triangle "+t(c)+"/"+nt); computeDistance(1000); for(int j=0; j<nt; j++) {SMt[j]+=Mt[j];}; };
  int L=SMt[0], H=SMt[0];  for(int i=0; i<nt; i++) { H=max(H,SMt[i]); L=min(L,SMt[i]);}; if (H==L) {H++;};
  c=0; for(int i=0; i<nt; i++) {Mt[i]=(SMt[i]-L)*255/(H-L); if(Mt[i]>Mt[t(c)]) {c=3*i;};}; rings=255;
  for(int i=0; i<nv; i++) {Mv[i]=0;};  for(int i=0; i<nc; i++) {Mv[v(i)]=max(Mv[v(i)],Mt[t(i)]);};
  println("finished isolation");
  }
  
void computePath() {                 // graph based shortest path between t(c0 and t(prevc), prevc is the previously picekd corner
  for(int i=0; i<nt; i++) {Mt[i]=0;}; Mt[t(prevc)]=1; // Mt[0]=1;
  for(int i=0; i<nc; i++) {P[i]=false;};
  int r=1;
  boolean searching=true;
  while (searching) {
     for(int i=0; i<nc; i++) {
       if (searching&&(Mt[t(i)]==0)&&(o(i)!=-1)) {
         if(Mt[t(o(i))]==r) {
           Mt[t(i)]=r+1; 
           P[i]=true; 
           if(t(i)==t(c)){searching=false;};
           };
         };
       };
     r++;
     };
  for(int i=0; i<nt; i++) {Mt[i]=0;};  // graph distance between triangle and t(c)
  rings=1;      // track ring number
  int b=c;
  int k=0;
   while (t(b)!=t(prevc)) {rings++;  
   if (P[b]) {b=o(b); print(".o");} else {if (P[p(b)]) {b=r(b);print(".r");} else {b=l(b);print(".l");};}; Mt[t(b)]=rings; };
  }
 void  showDistance() { for(int t=0; t<nt; t++) {if(Mt[t]==0) fill(cyan); else fill(255*Mt[t]/rings); shade(t);}; } 

//  ==========================================================  DELETE ===========================================
void hideROI() { for(int i=0; i<nt; i++) if(Mt[i]>0) visible[i]=false; }

//  ==========================================================  GARBAGE COLLECTION ===========================================
void clean() {excludeInvisibleTriangles();  compactVO(); compactV();}  // removes deleted triangles and unused vertices
void excludeInvisibleTriangles () {for (int b=0; b<nc; b++) {if (!visible[t(o(b))]) {O[b]=-1;};};}
void compactVO() {  
  int[] U = new int [nc];
  int lc=-1; for (int c=0; c<nc; c++) {if (visible[t(c)]) {U[c]=++lc; }; };
  for (int c=0; c<nc; c++) {if (!b(c)) {O[c]=U[o(c)];} else {O[c]=-1;}; };
  int lt=0;
  for (int t=0; t<nt; t++) {
    if (visible[t]) {
      V[3*lt]=V[3*t]; V[3*lt+1]=V[3*t+1]; V[3*lt+2]=V[3*t+2]; 
      O[3*lt]=O[3*t]; O[3*lt+1]=O[3*t+1]; O[3*lt+2]=O[3*t+2]; 
      visible[lt]=true; 
      lt++;
      };
    };
  nt=lt; nc=3*nt;    
  println("      ...  NOW: nv="+nv +", nt="+nt +", nc="+nc );
  }

void compactV() {  
  println("COMPACT VERTICES: nv="+nv +", nt="+nt +", nc="+nc );
  int[] U = new int [nv];
  boolean[] deleted = new boolean [nv];
  for (int v=0; v<nv; v++) {deleted[v]=true;};
  for (int c=0; c<nc; c++) {deleted[v(c)]=false;};
  int lv=-1; for (int v=0; v<nv; v++) {if (!deleted[v]) {U[v]=++lv; }; };
  for (int c=0; c<nc; c++) {V[c]=U[v(c)]; };
  lv=0;
  for (int v=0; v<nv; v++) {
    if (!deleted[v]) {G[lv].setTo(G[v]);  deleted[lv]=false; 
      lv++;
      };
    };
 nv=lv;
 println("      ...  NOW: nv="+nv +", nt="+nt +", nc="+nc );
  }

// ============================================================= ARCHIVAL ============================================================
boolean flipOrientation=false;            // if set, save will flip all triangles

void saveMesh() {
  String [] inppts = new String [nv+1+nt+1];
  int s=0;
  inppts[s++]=str(nv);
  for (int i=0; i<nv; i++) {inppts[s++]=str(G[i].x)+","+str(G[i].y);};
  inppts[s++]=str(nt);
  if (flipOrientation) {for (int i=0; i<nt; i++) {inppts[s++]=str(V[3*i])+","+str(V[3*i+2])+","+str(V[3*i+1]);};}
    else {for (int i=0; i<nt; i++) {inppts[s++]=str(V[3*i])+","+str(V[3*i+1])+","+str(V[3*i+2]);};};
  saveStrings("mesh.vts",inppts);  println("saved on file");
  };

void loadMesh() {
  println("loading fn["+fni+"]: "+fn[fni]); 
  String [] ss = loadStrings(fn[fni]);
  String subpts;
  int s=0;   int comma1, comma2;   float x, y, z;   int a, b, c;
  nv = int(ss[s++]);
    print("nv="+nv);
    for(int k=0; k<nv; k++) {int i=k+s; 
      comma1=ss[i].indexOf(',');   
      x=float(ss[i].substring(0, comma1));
      String rest = ss[i].substring(comma1+1);
      y=float(ss[i].substring(comma1+1));
      G[k].setTo(x,y);
    };
  s=nv+1;
  nt = int(ss[s]); nc=3*nt;
  println(", nt="+nt);
  s++;
  for(int k=0; k<nt; k++) {int i=k+s;
      comma1=ss[i].indexOf(',');   a=int(ss[i].substring(0, comma1));  
      String rest = ss[i].substring(comma1+1, ss[i].length()); comma2=rest.indexOf(',');  
      b=int(rest.substring(0, comma2)); c=int(rest.substring(comma2+1, rest.length()));
      V[3*k]=a;  V[3*k+1]=b;  V[3*k+2]=c;
    }
  }; 



//  ==========================================================  FLIP ===========================================
void flipWhenLonger() {for (int c=0; c<nc; c++) if (d(g(n(c)),g(p(c)))>d(g(c),g(o(c)))) flip(c); } 
void flip() {flip(c);}
void flip(int c) {      // flip edge opposite to corner c, FIX border cases
  if (b(c)) return;
    V[n(o(c))]=v(c); V[n(c)]=v(o(c));
    int co=o(c); O[co]=r(c); if(!b(p(c))) O[r(c)]=co; if(!b(p(co))) O[c]=r(co); if(!b(p(co))) O[r(co)]=c; O[p(c)]=p(co); O[p(co)]=p(c);  }
 
//  ==========================================================  SIMPLIFICATION  ===========================================
void collapse() {collapse(c);}
void collapse(int c) {if (b(c)) return;      // collapse edge opposite to corner c, does not check anything !!! assumes manifold
   int b=n(c), oc=o(c), vpc=v(p(c));
   visible[t(c)]=false; visible[t(oc)]=false;
   for (int a=b; a!=p(oc); a=n(l(a))) V[a]=vpc;
   O[l(c)]=r(c); O[r(c)]=l(c); O[l(oc)]=r(oc); O[r(oc)]=l(oc);  }

// ============================================================= corner stack ============================================================
 int stack[] = new int[10000];
 int stackHeight=1;
 int pop() {if (stackHeight==0){ println("Stack is empty"); stackHeight=1;}; return(stack[--stackHeight]);}
 void push(int c) {stack[stackHeight++]=c; }
 void resetStack() {stackHeight=1;};

  } // ==== END OF MESH CLASS
  
float log2(float x) {float r=0; if (x>0.00001) { r=log(x) / log(2);} ; return(r);}
vec labelD=new vec(-4,+4);           // offset vector for drawing labels

