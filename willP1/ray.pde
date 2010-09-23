//*****************************************************************************
// TITLE:         RAYS  
// DESCRIPTION:   Tools for processing rays
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
RAY ray(pt A, pt B) {return new RAY(A,B); }
RAY ray(pt Q, vec T) {return new RAY(Q,T); }
RAY ray(pt Q, vec T, float d) {return new RAY(Q,T,d); }
RAY ray(RAY R) {return new RAY(R.Q,R.T,R.r); }
RAY leftTangentToCircle(pt P, pt C, float r) {return tangentToCircle(P,C,r,-1); }
RAY rightTangentToCircle(pt P, pt C, float r) {return tangentToCircle(P,C,r,1); }
RAY tangentToCircle(pt P, pt C, float r, float s) {
  float n=d(P,C); float w=sqrt(sq(n)-sq(r)); float h=r*w/n; float d=h*w/r; vec T = S(d,U(V(P,C)),s*h,R(U(V(P,C)))); return ray(P,T,w);}

class RAY {
    pt Q = new pt(300,300);  // start
    vec T = new vec(1,0);    // direction
    float r = 50;           // length for display arrow and dragging
    float d = 300;            // distance to hit
  RAY () {};
  RAY (pt pQ, vec pT) {Q.setTo(pQ);T.setTo(U(pT)); };
  RAY (pt pQ, vec pT, float pd) {Q.setTo(pQ);T.setTo(U(pT)); d=max(0,pd);};
  RAY(pt A, pt B) {Q.setTo(A); T.setTo(U(V(A,B))); d=d(A,B);}
  RAY(RAY B) {Q.setTo(B.Q); T.setTo(B.T); d=B.d; T.normalize();}
  void drag() {pt P=T(Q,r,T); if(P.disToMouse()<Q.disToMouse()) {float dd=d(Q,Mouse()); pt O=T(Q,dd,T); O.moveWithMouse(); Q.track(dd,O); T.setTo(U(V(Q,O)));} else Q.moveWithMouse();}
  void setTo(pt P, vec V) {Q.setTo(P); T.setTo(U(V)); }
  void setTo(RAY B) {Q.setTo(B.Q); T.setTo(B.T); d=B.d; T.normalize();}
  void showArrow() {arrow(Q,r,T); }
  void showLine() {show(Q,d,T);}
  pt at(float s) {return new pt(Q,s,T);}         pt at() {return new pt(Q,d,T);}
  void turn(float a) {T.rotateBy(a);}            void turn() {T.rotateBy(PI/180.);}

  float disToLine(pt A, vec N) {float n=dot(N,T); float t=0; if(abs(n)>0.000001) t = -dot(N,V(A,Q))/n; return t;}

  boolean hitsEdge(pt A, pt B) {boolean hit=isRightOf(A,Q,T)!=isRightOf(B,Q,T); if (cw(A,B,Q)==(dot(T,R(V(A,B)))>0)) hit=false; return hit;} // if hits
  float disToEdge(pt A, pt B) {vec N = U(R(V(A,B))); float t=0; float n=dot(N,T); if(abs(n)>0.000001) t=-dot(N,V(A,Q))/n; return t;} // distance to edge along ray if hits
  pt intersectionWithEdge(pt A, pt B) {return at(disToEdge(A,B));}                                                                   // hit point if hits
  RAY reflectedOfEdge(pt A, pt B) {pt X=intersectionWithEdge(A,B); vec V =T.makeReflectedVec(R(U(V(A,B)))); float rd=d-disToEdge(A,B); return ray (X,V,rd); } // bounced ray
  RAY surfelOfEdge(pt A, pt B) {pt X=intersectionWithEdge(A,B); vec V = R(U(V(A,B))); float rd=d-disToEdge(A,B); return ray (X,V,rd); } // bounced ray

  float disToCircle(pt C, float r) { return rayCircleIntesectionParameter(Q,T,C,r);}  // distance to circle along ray
  pt intersectionWithCircle(pt C, float r) {return at(disToCircle(C,r));}            // intersection point if hits
  boolean hitsCircle(pt C, float r) {return disToCircle(C,r)!=-1;}                   // hit test
  RAY reflectedOfCircle(pt C, float r) {pt X=intersectionWithCircle(C,r); vec V =T.makeReflectedVec(U(V(C,X))); float rd=d-disToCircle(C,r); return ray (X,V,rd); }
  }
     
boolean isRightOf(pt A, pt Q, vec T) {return dot(R(T),V(Q,A)) > 0 ; };                               // A is on right of ray(Q,T) (as seen on screen)

