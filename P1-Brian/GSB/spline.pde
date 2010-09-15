//*****************************************************************************
// TITLE:         J-SPLINES  
// DESCRIPTION:   tols for roducing and processing J-splines
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
pt s(pt A, float s, pt B) {return(new pt(A.x+s*(B.x-A.x),A.y+s*(B.y-A.y))); };
pt b(pt A, pt B, pt C, float s) {return( s(s(B,s/4.,A),0.5,s(B,s/4.,C))); };                          // returns a tucked B towards its neighbors
pt f(pt A, pt B, pt C, pt D, float s) {return( s(s(A,1.+(1.-s)/8.,B) ,0.5, s(D,1.+(1.-s)/8.,C))); };    // returns a bulged mid-edge point 
pt B(pt A, pt B, pt C, float s) {return( s(s(B,s/4.,A),0.5,s(B,s/4.,C))); };                          // returns a tucked B towards its neighbors
pt F(pt A, pt B, pt C, pt D, float s) {return( s(s(A,1.+(1.-s)/8.,B) ,0.5, s(D,1.+(1.-s)/8.,C))); };    // returns a bulged mid-edge point 
pt limit(pt A, pt B, pt C, pt D, pt E, float s, int r) {
  if (r==0) return C.clone();
  else return limit(b(A,B,C,s),f(A,B,C,D,s),b(B,C,D,s),f(B,C,D,E,s),b(C,D,E,s),s,r-1);
  }

//---- biLaplace fit
vec fitVec (pt B, pt C, pt D) { return A(V(C,B),V(C,D)); }
pt fitPt (pt B, pt C, pt D) {return A(B,D);};  
pt fitPt (pt B, pt C, pt D, float s) {return T(C,s,fitVec(B,C,D));};  
pt fitPt(pt A, pt B, pt C, pt D, pt E, float s) {pt PB = fitPt(A,B,C,s); pt PC = fitPt(B,C,D,s);  pt PD = fitPt(C,D,E,s); return fitPt(PB,PC,PD,-s);}
pt fitPt(pt A, pt B, pt C, pt D, pt E) {float s=sqrt(2.0/3.0); pt PB = fitPt(A,B,C,s); pt PC = fitPt(B,C,D,s);  pt PD = fitPt(C,D,E,s); return fitPt(PB,PC,PD,-s);}
//---- proportional biLaplace fit
vec proVec (pt B, pt C, pt D) { return S(V(C,B), d(C,B)/(d(C,B)+d(C,D)),V(C,D)); }
pt proPt (pt B, pt C, pt D) {return T(B,d(C,B)/(d(C,B)+d(C,D)),V(B,D));};  
pt proPt (pt B, pt C, pt D, float s) {return T(C,s,proVec(B,C,D));};  
pt proPt(pt A, pt B, pt C, pt D, pt E, float s) {pt PB = proPt(A,B,C,s); pt PC = proPt(B,C,D,s);  pt PD = proPt(C,D,E,s); return proPt(PB,PC,PD,-s);}
pt proPt(pt A, pt B, pt C, pt D, pt E) {float s=sqrt(2.0/3.0); pt PB = proPt(A,B,C,s); pt PC = proPt(B,C,D,s);  pt PD = proPt(C,D,E,s); return proPt(PB,PC,PD,-s);}

