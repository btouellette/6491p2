int mnpt=20000;
pt G[] = new pt [mnpt];
int nv=0;
int Vm[] = new int[3*mnpt];
int E[][] = new int[mnpt][2];
boolean gE[]= new boolean[mnpt];
int ne=0;
int nt=0;
int nc=0;
void init3D() {for (int i=0; i<mnpt; i++) G[i]=new pt(0,0); }
void reset3D() {nv=0; nt=0; nv=0; nc=0;}
int addvertex(pt P) { G[nv].x=P.x; G[nv].y=P.y; nv++; return nv-1;};
void addTriangle(int i, int j, int k) {Vm[nc++]=i; Vm[nc++]=j; Vm[nc++]=k;nt=nc/3; }
void triangulate() { // triangulates trimmed loops
   for (int e=0; e<ne; e++) if(gE[e]) { 
     for (int j=0; j<nv; j++)  { 
        if (cw(G[E[e][0]],G[E[e][1]],G[j])) {
            pt CC=CircumCenter(G[E[e][0]],G[E[e][1]],G[j]); 
            float r=G[j].disTo(CC);
            boolean found=false;
            for (int k=0; k<nv; k++) 
                if ((cw(G[E[e][0]],G[E[e][1]],G[k]))&&(G[k].disTo(CC)+0.1<r)) found=true;
            if (!found) addTriangle(E[e][0],E[e][1],j);
            };    };     };    
     }
            
void show3DVertices() {for (int i=0; i<nv; i++) G[i].show(2); }
void showTriangles() { noStroke(); fill(200,200,200);  for (int c=0; c<nt*3; c+=3) 
  { beginShape(); G[Vm[c]].v(); G[Vm[c+1]].v(); G[Vm[c+2]].v();  endShape(CLOSE);};}


// EDGES
void removeOppositePairs() {for (int i=0; i<ne; i++) if(gE[i]) for (int j=0; j<ne; j++) if(gE[j]) if ((E[i][0]==E[j][1])&&(E[i][1]==E[j][0])) {gE[i]=false; gE[j]=false; }}
void addEdge(int s, int t) {E[ne][0]=s;  E[ne][1]=t; gE[ne]=true; ne++; };
void addEdge(pt P, pt Q) {
  int s=0; float d=10000; for(int i=0; i<nv; i++) if (P.disTo(G[i])<d) {d=P.disTo(G[i]); s=i;};
  int t=0;  d=10000; for(int i=0; i<nv; i++) if (Q.disTo(G[i])<d) {d=Q.disTo(G[i]); t=i;}; 
  E[ne][0]=s;  E[ne][1]=t; gE[ne]=true; ne++;
  };

// OTHER
//void add3Dvertex(pt P) { G[nv].x=P.x; G[nv].y=P.y; G[nv].z=H[int(P.x/h)][int(P.y/h)].z; nv++;};
//void add3Dvertex(pt P, float h) { G[nv].x=P.x; G[nv].y=P.y; G[nv].z=h; nv++;};
