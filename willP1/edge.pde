//*****************************************************************************
// TITLE:         EDGES  
// DESCRIPTION:   Tools for processing Bezier curves
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
//boolean edgesIntersect(pt A, pt B, pt C, pt D) {boolean hit=true; 
//    if (leftTurn(A,B,C)==leftTurn(A,B,D)) hit=false; 
//    if (leftTurn(C,D,A)==leftTurn(C,D,B)) hit=false; 
//     return hit; }
//boolean edgesIntersect(pt A, pt B, pt C, pt D,float e) {
//  return ((A.isLeftOf(C,D,e) && B.isLeftOf(D,C,e))||(B.isLeftOf(C,D,e) && A.isLeftOf(D,C,e)))&&
//         ((C.isLeftOf(A,B,e) && D.isLeftOf(B,A,e))||(D.isLeftOf(A,B,e) && C.isLeftOf(B,A,e))) ;   }
//pt linesIntersection(pt A, pt B, pt C, pt D) {vec AB = A.makeVecTo(B);  vec CD = C.makeVecTo(D);  vec N=CD.makeTurnedLeft();  vec AC = A.makeVecTo(C);
//   float s = dot(AC,N)/dot(AB,N); return A.makeTranslatedBy(s,AB); }
//   
   //************** INTRERESECTIONS *****************
boolean edgesIntersect(pt A, pt B, pt C, pt D) {return cw(A,B,C)!=cw(A,B,D) && cw(C,D,A)!=cw(C,D,B) ; }
boolean edgesIntersect(pt A, pt B, pt C, pt D,float e) {
  return ((A.isLeftOf(C,D,e) && B.isLeftOf(D,C,e))||(B.isLeftOf(C,D,e) && A.isLeftOf(D,C,e)))&&
         ((C.isLeftOf(A,B,e) && D.isLeftOf(B,A,e))||(D.isLeftOf(A,B,e) && C.isLeftOf(B,A,e))) ;   }
pt linesIntersection(pt A, pt B, pt C, pt D) {vec AB = V(A,B);  vec CD = V(C,D);  vec N=R(CD); vec AC = V(A,C); float s = dot(AC,N)/dot(AB,N); return T(A,s,AB); }



