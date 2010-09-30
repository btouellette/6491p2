import processing.core.*; 
import processing.xml.*; 

import java.applet.*; 
import java.awt.Dimension; 
import java.awt.Frame; 
import java.awt.event.MouseEvent; 
import java.awt.event.KeyEvent; 
import java.awt.event.FocusEvent; 
import java.awt.Image; 
import java.io.*; 
import java.net.*; 
import java.text.*; 
import java.util.*; 
import java.util.zip.*; 
import java.util.regex.*; 

public class GSB extends PApplet {

//*****************************************************************************
// TITLE:         Jarek's Geometry Sand Box (GSB) : 2D geometry processing development environment  
// DESCRIPTION:   Application specific code is in tabs MYxxxx...
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:         August 1010
//*****************************************************************************
// Usage: Edit MYsetup, MYdraw, MYhelp, MYkeyPressed, MYmousePressed, MYmouseReleased, MYmouseDragged procedures in the tab MYxxxx. 
// If needed, for clarity add tabs with names starting by MY
// Assumes files pic.jpg (your face) and P.pts (saved point locations) are in the data folder

boolean showHelpText=true, showMenu=true;      // toggled by keys to show/hide help text
boolean debug=false;           // temporarily set when key '?' is pressed and used to print some debugging values
boolean makingPicture=false;    // used to prevent the menu from showing when making pictures
PImage pic; // picture of author's face that is displayed in the help pane, read from file pic.jpg in data folder
PImage pic2;

public void setup() {  // Executed once
  //size(900,700); // Opens graphic canvas (wider for manu on the right)
  size(1200,760); // Opens graphic canvas (wider for manu on the right)
  smooth();   strokeJoin(ROUND); strokeCap(ROUND); // set drawing modes for high-quality slower drawings
  rectMode(CENTER); // needed for buttons to work properly !
  PFont font = loadFont("ArialMT-24.vlw"); textFont(font, 16);      // load font for writing on the canvas
  pic = loadImage("brian.jpg");                                  // load image names pic from file pic.jpg in folder data
  pic2 = loadImage("face.png");
  MYsetup();                                                        // execute application specific setup tasks (see MYxxx)
  } 

public void draw() {
  background(white);  strokeWeight(1);                        // clears screen with white background and sets line thickness to 1
  if (showHelpText) showHelp();                               // shows the welcome page with author's picture
  else { 
     MYdraw();  // renders the next frame (see in MYxxx)
     if(showMenu&&!makingPicture) {stroke(black); line(height-4,0,height-4,height); stroke(metal); line(height-3,0,height-3,height); MYshowButtons();}; // show menu
     }
  if(makingPicture) {saveFrame("pictures/P"+Format0(pictureCounter++,3)+".jpg"); makingPicture=false;} 
  if(debug) {println(); debug=false;};
  };  // end of draw

   

//*****************************************************************************
// TITLE:         Demo of moving points written using the Geometry SandBox Geometry (GSB)  
// DESCRIPTION:   Let's the user manipulate points
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  August 2010
// EDITS:
//*****************************************************************************

// Application specific global variables
int pictureCounter=0;     // used to number screen-shots sequentially
LOOP C = new LOOP(3); 
int pC=0;  // counter of which file number the points are saved to (not used here)
int nFrames=9; // not used

//pt P = P(200, 200);
//float distanceToP, angleToP;
float distanceTo[]=new float[5000];
float angleTo[]= new float[5000];
pt A = P(250, 400), B = P(400, 250), O = P(300, 300);
float prev_radius = 0;
float original_radius;
boolean first_run[] = new boolean[5000];

public void MYsetup() { // executed once at start
   C.declarePoints(); 
   // C.resetPointsOnCircle(); 
   C.loadPts("data/P"+str(pC));
   M.declare(); M.init(); C.makeDelaunayOfPoints(M);
   for(int x=0;x<5000;x++)first_run[x]=true;
  }; 

/**
*** Tests to see if the point is behind the curve in the sense that it
*** is on the opposite side of the bulge of the curve from the line AB
**/
public boolean behindCurveA(pt P)
{
  RAY rA = ray(B,A);  //Ray in the direction of A
  if(isRightOf(P,A,rA.T)!=isRightOf(O,A,rA.T))
  {
    //println("BEHIND CURVE");
    return true;
  }
  //println("NOT BEHIND CURVE");
  return false;
}


/**
*** Tests to see if the point is behind the curve in the sense that it
*** is within the circle formed by going through A, B, and O
**/
public boolean behindCurveB(pt P,pt center,float radius)
{
  if(d(center,P)<radius)
  {
    //println("BEHIND CURVE");
    return true;
  }
  //println("NOT BEHIND CURVE");
  return false;
}

/**
*** Tests to see if the point is behind the curve in the sense that it
*** is between the arc and the line segment formed by AB
**/
public boolean behindCurveC(pt P,pt center,float radius)
{
  RAY rA = ray(B,A);  //Ray in the direction of A
  if((isRightOf(P,A,rA.T)==isRightOf(O,A,rA.T))&&d(center,P)<radius)
  {
    //println("BEHIND CURVE");
    return true;
  }
  //println("NOT BEHIND CURVE");
  return false;
}


public void MYdraw () { // executed at each frame
  scribeBlack("Project 2 (Area-preserving skeleton-driven deformation) by Will Mooney and Brian Ouellette",0);
  /*scribeBlack("The mesh has "+C.n+" points and "+M.nt+" triangles",1);*/
  if(showTriangles.isTrue)  C.showDelaunayOfPoints(-2); 
  if(showMesh.isTrue)  M.showMesh(); 
  if(showEdges.isTrue) {noFill(); strokeWeight(3); stroke(red); C.drawEdges(); }
  if(showLetters.isTrue) {fill(blue); C.writePointLetters(); noFill();   stroke(blue); noFill(); C.drawPoints(13); } // toggle in menu to write letters
  else {stroke(blue); fill(blue); C.drawPoints(3); }; // draws points as small dots
  

  //show(center, R(V(center, O), PI/16)); 
  if(showArc.isTrue)
  {
    stroke(blue);
    noFill();
    A.show(3);
    A.showLabel("A");
    B.show(3);
    B.showLabel("B");
    O.show(3);
    O.showLabel("O");
    showArcThrough(A,O,B);
    
    pt center = CircumCenter(A,O,B); // center of circle passing through the 3 points
    float radius = circumRadius(A,O,B); // radius of circumcenter
    show(center, O);
    center.show(3);
    center.showLabel("center");
    // Angle is -pi/2 to pi/2, this is the arcangle of the arc
    float angle = angle(A, center, B);
    if(first_run[0]) {
      M.computeBarycentric();
    }
    for(int i=0;i<C.n;i++)
    {
      if(first_run[i]) {
        first_run[i] = false;
        // P is some angle further down the arc from point O
        // It is also some distance from the arc measured in terms of the radius
        // Record both
        original_radius = radius;
        distanceTo[i] = d(center,C.P[i]) - radius;
        angleTo[i] = angle(O, center, C.P[i]);
      } else {
        /* Method 2 - Area corrected skeleton bending */
        // Recalculate where P should be with the potential new radius and O position
        // Scale distance to P with the scale in radius
        float distance;
        // The highest number will take priority because I don't know how to do radio buttons in 
        if(useMethod4.isTrue)
        {
          distance = distanceTo[i];
        }
        else if(useMethod3.isTrue)
        {
          //Use Method 3
          // Only do this once for the entire mesh
          if(i==0){// && radius != prev_radius) {
            M.updateBarycentric(C);
          }
          distance = distanceTo[i];
        }
        else if(useMethod2.isTrue)
        {
          //Use Method 2
          distance = (sqrt(radius*original_radius*(radius*original_radius + 2*original_radius*distanceTo[i] + distanceTo[i]*distanceTo[i])) - radius*original_radius)/original_radius;
        }
        else
        {
          //Use Method 1
          distance = distanceTo[i];
        }
        if (Float.isNaN(distance)) {
          C.P[i].setTo(center);
        } else {
          // Only update the distance in this way for methods 1 and 2
          if(useMethod2.isTrue && !(useMethod3.isTrue || useMethod4.isTrue)) {
            // Angle won't scale
            // Take the vector from the center to O and rotate it by the angle to P and then scale it so that the end point is the new P location
            vec tempVec = S(1+distance/radius, R(V(center,O), angleTo[i]));
            C.P[i] = T(center, tempVec);
          }
        }
      }
      //C.P[i].showLabel("P"+i);
    }
    prev_radius = radius;
    C.updatePoints(M);
    for(int i=0; i<C.n;i++) {
      C.P[i].show(3);
    }
    //P.show(3);
    //P.showLabel("P");
  }//End If
  else {for(int x=0;x<5000;x++)first_run[x]=true; }
  }//End Method
 
public void MYmousePressed() {
   C.pickClosestPoint(Mouse());
   if (keyPressed && key=='i') C.insert(Mouse()); // add a point
   if (keyPressed && key=='d') C.deletePoint(); // add a point
   if (keyPressed && key=='a') C.appendPoint(Mouse()); // add a point
   }     
public void MYmouseReleased() {   }  
public void MYmouseMoved() { }
public void MYmouseDragged() {     // application-specific actions executed when the mouse is moved while the mouse button is kept pressed
 if (!mouseIsInWindow()) return;
 if (!keyPressed||(key=='i'||key=='a')) {C.dragPoint(MouseDrag()); } 
 else {
     if (key=='A') A.translateBy(MouseDrag());
     if (key=='B') B.translateBy(MouseDrag());
     if (key=='O') O.translateBy(MouseDrag());
     if (key=='t') C.rotatePointsAroundCenterOfMass(Mouse(),Pmouse()); // rotate all points around their center of mass
     if (key=='m') { A.translateBy(MouseDrag()); B.translateBy(MouseDrag()); O.translateBy(MouseDrag()); } // rotate all points around their center of mass
     if (key=='z') C.scalePointsAroundCenterOfMass(Mouse(),MouseDrag()); // scale all points with respect to their center of mass
     };
 }
   
public void MYkeyPressed() {   // application-specific actions executed when a key is pressed (or each time a key-repeat self-activates)
  if (key=='0') C.empty();
  // if (key=='a') append
  // if (key=='d') delete
  if (key=='f') {M.flip(); M.classifyTriangles(C);}
  // if (key=='i') insert
  if (key=='l') M.left();
  //  if (key=='m') move
  if (key=='n') M.next();
  if (key=='o') M.opposite();
  if (key=='p') M.previous();
  if (key=='r') M.right();
  if (key=='s') M.swing();
  //  if (key=='t') turn
  //  if (key=='z') zoom
  if (key=='w') M.writeCorner();
  
  } 

public void  MYkeyReleased() {  } 
   
public void MYshowHelp() {  // Application specific help text to appear in the help pane when user pressed the SPACE BAR
 text("CS 6491 -- Fall 2010 -- Instructor Jarek Rossignac",0,0); translate(0,20);
 text("Project 2: Area-preserving skeleton-driven deformation",0,0); translate(0,20);
 text("Students: Brian Ouellette and Will Mooney",0,0); translate(0,20);
 text("Date submitted: September 30, 2010",0,0); translate(0,20);
  text("  ",0,0); translate(0,20);
 text("USAGE  ",0,0); translate(0,20);
 text("Press the space-bar to hide/show this help page ",0,0); translate(0,20);
 text("Click near a point & drag to move closest point ",0,0); translate(0,20);
 text("Keep 'i', 'd', or 'a', and click to insert, delete, or append a point",0,0); translate(0,20);
 text("Hold 'm', 't', or 'z', and click&drag to move, turn, or zoom ",0,0); translate(0,20);
 text("Press'0' to delete all points",0,0); translate(0,20);
 text("Press 'n', 'p', 'o', 'l', 'r', 's' to change current corner and 'w' to print it ",0,0); translate(0,20);
 text("Press 'f' to flip the edge opposite to the current corner",0,0); translate(0,20);
 text("  ",0,0); translate(0,20);
 text("ADDITIONAL COMMANDS FOR THIS PROJECT:",0,0); translate(0,20);
 text("Working commands:",0,0); translate(0,20);
 text("m - translate entire mesh and arc",0,0); translate(0,20);
 text("A - translate point A (this is capital A distinct from lowercase a)",0,0); translate(0,20);
 text("B - translate point B",0,0); translate(0,20);
 text("O - translate point O",0,0); translate(0,20);
 text("The center can't be moved explicitly, just by translating the entire arc",0,0); translate(0,20);
 text("Since we are updating the mesh directly, adding and deleting points may result in undefined behavior",0,0); translate(0,20);
 text("  ",0,0); translate(0,20);
 text("  ",0,0); translate(0,20);
 }

// BOTTONS

COUNTER buttonCounter = new COUNTER();

BUTTON computeTriangulation = new BUTTON("compute triangulation",1); // spray the curve and triangulate
BUTTON savePoints = new BUTTON("save",1);  // save points toggle button: the '1' means that this is an action button (not a toggle)
BUTTON loadPoints = new BUTTON("load",1);  // load points toggle button
BUTTON saveMesh = new BUTTON("save mesh",1);  // save points toggle button: the '1' means that this is an action button (not a toggle)
BUTTON loadMesh = new BUTTON("load mesh",1);  // load points toggle button
BUTTON resetPoints = new BUTTON("reset",1);   // reset points in circle
BUTTON snapPicture = new BUTTON("screen shot",1);  // snap screen-shot button the '1' means that this is an action button (not a toggle)
BUTTON showEdges = new BUTTON("show curve",true);  // draw edges
BUTTON showLetters = new BUTTON("show letters",false); // show letters 'A', 'B"
BUTTON lowpass = new BUTTON("smoothen curve",1); // smoothen loop
BUTTON subdivide = new BUTTON("subdivide curve",1); // subdivide loop
BUTTON coarsen = new BUTTON("coarsen curve",1); // subdivide loop
BUTTON resample = new BUTTON("resample curve",1); // resample loop
BUTTON showTriangles = new BUTTON("show triangles",false); // show triangles
BUTTON computeMesh = new BUTTON("rebuild mesh",1); // computes triangle mesh
BUTTON showMesh = new BUTTON("show mesh",true); // show triangle mesh
BUTTON showArc = new BUTTON("show arc",true); // show arc
BUTTON useMethod2 = new BUTTON("Use solver 2: Area-Corrected Skeleton Building",true); // Use method 2
BUTTON useMethod3 = new BUTTON("Use solver 3: As-Rigid-As-Possible",false); // Use method 3
BUTTON useMethod4 = new BUTTON("Use solver 4: Area-Corrected As-Rigid-As-Possible",false); // Use method 4

public void MYshowButtons() {           // shows all my buttons on the right of screen with their status and labels
 fill(metal); scribe("Constrained triangulation",height,15); scribe("Menu:",height,35);
 stroke(metal); strokeWeight(2); 
 buttonCounter.reset();  buttonCounter.i();
 computeTriangulation.show(buttonCounter.i());
 savePoints.show(buttonCounter.i());
 loadPoints.show(buttonCounter.i());
 saveMesh.show(buttonCounter.i());
 loadMesh.show(buttonCounter.i());
 resetPoints.show(buttonCounter.i());
 snapPicture.show(buttonCounter.i());
 showEdges.show(buttonCounter.i());
 showLetters.show(buttonCounter.i());
 lowpass.show(buttonCounter.i());
 subdivide.show(buttonCounter.i());
 coarsen.show(buttonCounter.i());
 resample.show(buttonCounter.i());
 showTriangles.show(buttonCounter.i());
 computeMesh.show(buttonCounter.i());
 showMesh.show(buttonCounter.i());
 showArc.show(buttonCounter.i());
 useMethod2.show(buttonCounter.i());
 useMethod3.show(buttonCounter.i());
 useMethod4.show(buttonCounter.i());
 }

// THE ORDER IN WHICH THE BOTTONS ARE CREATE (ABOVE) AND ACTIVATED (BELOW) MUST BE IDENTICAL

public void MYcheckButtons() {          // checks whether any of my buttons was pressed and temporarily disable GUI 
  buttonCounter.reset();  buttonCounter.i();
  if(computeTriangulation.check(buttonCounter.i())) compute();
  if(savePoints.check(buttonCounter.i())) C.savePts("data/P"+str(pC));
  if(loadPoints.check(buttonCounter.i())) { C.loadPts("data/P"+str(pC)); for(int i = 0; i < 5000; i++) {first_run[i] = true;} }
  if(saveMesh.check(buttonCounter.i())) M.saveMesh();
  if(loadMesh.check(buttonCounter.i())) M.loadMesh();
  if(resetPoints.check(buttonCounter.i())) C.resetPoints();
  if(snapPicture.check(buttonCounter.i())) makingPicture=true;  // set makingPicture used not to show the menu
  showEdges.check(buttonCounter.i());  // toggles showEdges
  showLetters.check(buttonCounter.i());  // toggles showLetters
  if(lowpass.check(buttonCounter.i())) C.smoothen();
  if(subdivide.check(buttonCounter.i())) C.refine(0.5f);
  if(coarsen.check(buttonCounter.i())) C.coarsen();
  if(resample.check(buttonCounter.i())) C.resample(200);
  showTriangles.check(buttonCounter.i());
  if(computeMesh.check(buttonCounter.i())) { C.makeDelaunayOfPoints(M); M.classifyTriangles(C); }
  showMesh.check(buttonCounter.i());
  showArc.check(buttonCounter.i());
  useMethod2.check(buttonCounter.i());
  useMethod3.check(buttonCounter.i());
  useMethod4.check(buttonCounter.i());
  }

// PROJECT 1 COMPUTATION
public void compute() {
  // Make a new loop which will hold the original loop vertices as well as the sprayed points
  LOOP Cnew = new LOOP(3);
  Cnew.empty();
  // Copy in all the original loop vertices
  Cnew.copyFrom(C);
  // Spray points in and around the curve, adjust float passed in to change how this spray is created
  Cnew.refine(1.1f);
  Cnew.resample(60);
  Cnew.refine(1);
  //Cnew.spray();
  //M.classifyTriangles(Cnew);
  // Put all the new points into the mesh
  /*for (int i=0; i<Cnew.n; i++) {
    M.addVertex(Cnew.P[i]);
  }
  /* Can repeat spray from base with different refine value if wanted
  LOOP Cnew2 = new LOOP(3);
  Cnew2.empty();
  Cnew2.copyFrom(C);
  Cnew2.refine(0.5);
  for (int i=0; i<Cnew2.n; i++) {
    M.addVertex(Cnew2.P[i]);
  }
  for (int i=0; i<Cnew2.n; i++) {
    Cnew.appendPoint(Cnew2.P[i]);
  }
  LOOP Cresampled = new LOOP(3);
  Cresampled.empty();
  Cresampled.copyFrom(C);
  Cresampled.resample(100);
  for (int i=0; i<Cresampled.n; i++) {
    Cnew.appendPoint(Cresampled.P[i]);
  }*/
  // Add back in the loop vertices, these were removed during the refine() operation
  for (int i=0; i<C.n; i++) {
    Cnew.appendPoint(C.P[i]);
  }
  Cnew.makeDelaunayOfPoints(M);
  M.classifyTriangles(C);
  println("first pass done");
  if(M.redoDelaunay) {
    println("adding " + M.numNewPoints + " intersections");
    for(int i = 0; i < M.numNewPoints; i++) {
      Cnew.insert(M.newPoints[i], M.newIndex[i]);
      //print(M.newPoints[i].x + "," + M.newPoints[i].y + " ");
    }
    println("second pass");
    Cnew.makeDelaunayOfPoints(M);
    println("classifying " + M.nt);
    M.classifyTriangles(C);
  }
}
//*****************************************************************************
// TITLE:         BEZIER  
// DESCRIPTION:   Tools for processing Bezier curves
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
public pt cubicBezier(pt A, pt B, pt C, pt D, float t) {return( s( s( s(A,t,B) ,t, s(B,t,C) ) ,t, s( s(B,t,C) ,t, s(C,t,D) ) ) ); }
public void splitBezier(pt A, pt B, pt C, pt D, int rec) {
  if (rec==0) {B.v(); C.v(); D.v(); return;};
  pt E=A(A,B);   pt F=A(B,C);   pt G=A(C,D);  
           pt H=A(E,F);   pt I=A(F,G);  
                    pt J=A(H,I); J.show(3);   
  splitBezier(A,E,H,J,rec-1);   splitBezier(J,I,G,D,rec-1); 
 }
 
public void drawSplitBezier(pt A, pt B, pt C, pt D, float t) {
  pt E=s(A,t,B); E.show(2);  pt F=s(B,t,C); F.show(2);  pt G=s(C,t,D); G.show(2);  E.to(F); F.to(G);
           pt H=s(E,t,F); H.show(2);   pt I=s(F,t,G);  I.show(2); H.to(I);
                    pt J=s(H,t,I); J.show(4);   
 }

public void drawCubicBezier(pt A, pt B, pt C, pt D) { beginShape();  for (float t=0; t<=1; t+=0.02f) {cubicBezier(A,B,C,D,t).v(); };  endShape(); }
public void drawEdges(pt A, pt B, pt C, pt D) { A.to(B); B.to(C); C.to(D); }
public float cubicBezierAngle (pt A, pt B, pt C, pt D, float t) {pt P = s(s(A,t,B),t,s(B,t,C)); pt Q = s(s(B,t,C),t,s(C,t,D)); vec V=P.makeVecTo(Q); float a=atan2(V.y,V.x); return(a);}  
public vec cubicBezierTangent (pt A, pt B, pt C, pt D, float t) {pt P = s(s(A,t,B),t,s(B,t,C)); pt Q = s(s(B,t,C),t,s(C,t,D)); vec V=P.makeVecTo(Q); V.makeUnit(); return(V);}  

public void retrofitBezier(pt[] PP, pt[] QQ) {                            // sets control polygon QQ so that tits Bezier curve interpolates PP
  QQ[0]=P(PP[0]);
  QQ[1]=S(-15.f/18.f,PP[0],54.f/18.f,PP[1],-27.f/18.f,PP[2],6.f/18.f,PP[3]);
  QQ[2]=S(-15.f/18.f,PP[3],54.f/18.f,PP[2],-27.f/18.f,PP[1],6.f/18.f,PP[0]);
  QQ[3]=P(PP[3]);
  }

public void drawParabolaInHat(pt A, pt B, pt C, int rec) {
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

public vec cubic (pt A, pt B, pt C, pt D, pt E) {return(new vec( (-A.x+4*B.x-6*C.x+4*D.x-E.x)/6, (-A.y+4*B.y-6*C.y+4*D.y-E.y)/6  ));}

public pt cubic(pt A, pt B, pt D, pt E, float s, float t, float u) {
   float ct1, ct2, ct3;
    vec AB, AD, AE;
    ct1 = (s*u + s - u - s*s) * s;     ct2 = (u*u + s - s*u - u) * u;     ct3 = s*u + 1 - s - u;
    AB = A.vecTo(B); AB.div(ct1);      AD = A.vecTo(D); AD.div(ct2);       AE = A.vecTo(E); AE.div(ct3);
    vec a = AB.make(); a.mul(-1.0f); a.add(AD); a.add(AE);   
    vec b = AB.make(); b.mul(u+1); b.addScaled(-(s+1),AD); b.addScaled(-(u+s),AE);
   vec c = AB.make(); c.mul(-u);  c.addScaled(s,AD); c.addScaled(u*s,AE); 
    
   vec V = a.make(); V.mul(t*t*t); V.addScaled(t*t,b); V.addScaled(t,c);
   pt R = A.make();  R.addVec(V);
   return (R);
   };
  
public vec bulgeVec(pt A, pt B, pt C, pt D) {return A(B.makeVecTo(A),C.makeVecTo(D)).makeScaledBy(-3.f/8.f); }

public pt prop(pt A, pt B, pt D, pt E) {float a=d(A,B), b=d(B,D), c=d(D,E), t=a+b+c, d=b/(1+pow(c/a,1.f/3) ); return cubic(A,B,D,E,a/t,(a+d)/t,(a+b)/t); }

public vec vecToCubic (pt A, pt B, pt C, pt D, pt E) {return V( (-A.x+4*B.x-6*C.x+4*D.x-E.x)/6, (-A.y+4*B.y-6*C.y+4*D.y-E.y)/6);}
//*****************************************************************************
// TITLE:         CIRCLES  
// DESCRIPTION:   Circle utilities for GSB to manipulate and dislpay points
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
/*************************
show(P,r): draws circle of center P and radius r 
*************************/

public void showArcThrough (pt A, pt B, pt C) {
  if (abs(dot(V(A,B),R(V(A,C))))<0.01f*d2(A,C)) {show(A,C); return;}
   pt O = CircumCenter ( A,  B,  C); 
   float r=d(O,A);
   vec OA=V(O,A), OB=V(O,B), OC=V(O,C);
   float b = angle(OA,OB), c = angle(OA,OC); 
   if(0<c && c<b || b<0 && 0<c)  c-=TWO_PI; 
   else if(b<c && c<0 || c<0 && 0<b)  c+=TWO_PI; 
   beginShape(); v(A); for (float t=0; t<1; t+=0.01f) v(R(A,t*c,O)); v(C); endShape();
   }

public float bulge (pt A, pt B, pt C) {  // returns bulge (<0 when A-B-C is ccw)
  if (abs(dot(V(A,B),R(V(A,C))))<0.01f*d2(A,C)) return 0;
   pt O = CircumCenter (A,  B,  C); //show(O,2);
   float r=d(O,A); if (cw(A,B,C)) r=-r;
   float d=d(A(A,C),O); if (cw(A,O,C)) d=-d;
   return r+d;
   }

//************************************************************************
//**** CIRCLE CLASS
//************************************************************************

public CIRCLE C(pt P, float r) {return new CIRCLE(P,r);}
class CIRCLE { pt C=P(height/2,height/2); float r=height/4; 
  // CREATE
  CIRCLE () {}
  CIRCLE (pt P, float s) {C.setTo(P); r=s;};
  public void draw() {show(C,r);}
  public pt CP(pt P) {return T(C,r,P);}
  }
//*********** END CIRCLE CLASS

public float interpolateAngles(float a, float t, float b) {if(b<a) b+=TWO_PI; float m=(t-a)/(b-a); if (m>PI) m-=TWO_PI; return m;}

   
public pt ptOnCircle(float a, pt O, float r) {return P(r*cos(a)+O.x,r*sin(a)+O.y);}   

  
public float edgeCircleIntesectionParameter (pt A, pt B, pt C, float r) {  // computes parameter t such that A+tAB is on circle(C,r)
  vec T = V(A,B); float n = n(T); T.normalize();
  float t=rayCircleIntesectionParameter(A,T,C,r);
  return t*n;
}

public float rayCircleIntesectionParameter (pt A, vec T, pt C, float r) {  // computes parameter t such that A+tT is on circle(C,r) or -1
  vec AC = V(A,C);
  float d=dot(AC,T); 
  float h = dot(AC,R(T)); 
  float t=-1;
  if (abs(h)<r) {
    float w = sqrt(sq(r)-sq(h));
    float t1=(d-w);
    float t2=(d+w);
     if ((0<=t1)&&(t1<=t2)) t = t1; else if (0<=t2) t = t2; 
   }
  return t;
}

public pt circleInversion(pt A, pt C, float r) {vec V=V(C,A); return S(C,sq(r/n(V)),A); }

public pt circleCenterForAngle(pt A, pt B, float a) {                   // computes center of the circle of points C such that angle(A,C,B)=a
   pt M = A(A,B); float d=d(A,B)/2; float h=d*tan(PI/2.f-a); vec V=R(S(h,U(V(A,B))));  return T(M,V); }

public pt CCl(pt C1, float r1, pt C2, float r2) { // computes  the intersection of two circles that is on the left of (C1,C2)
   float d=d(C1,C2);
   float d1=(sq(r1)-sq(r2)+sq(d))/(d*2);
   float h=sqrt(sq(r1)-sq(d1));
   return T(T(C1,d1,C2),-h,R(V(C1,C2)));}
   
public pt CCr(pt C1, float r1, pt C2, float r2) { // computes  the intersection of two circles that is on the right of (C1,C2)
   float d=d(C1,C2);
   float d1=(sq(r1)-sq(r2)+sq(d))/(d*2);
   float h=sqrt(sq(r1)-sq(d1));
   return T(T(C1,d1,C2),h,R(V(C1,C2)));}   

public pt interArc(pt A, pt B, pt C, pt D) {
  pt M = A(B,C);
   float ab=d(A,B); float bc=d(B,C); float cd=d(C,D); float t=(ab/(ab+bc)+(1.f-cd/(bc+cd)))/2; 
   M= A(pointOnArcThrough(A,B,t,C),pointOnArcThrough(B,t,C,D));
    return M;}


public pt pointOnArcThrough (pt A, float t, pt B, pt C) {
  pt X=new pt();
   pt O = CircumCenter ( A,  B,  C);
   float r=(O.disTo(A) + O.disTo(B)+ O.disTo(C))/3.f;
   float a = V(O,A).angle(); 
   float ab = positive(angle(V(O,A),V(O,B)));  if(cw(A,B,C)) ab-=TWO_PI;
   return ptOnCircle(a+t*ab,O,r);
   }
   
public pt pointOnArcThrough (pt A, pt B, float t, pt C) {
  pt X=new pt();
   pt O = CircumCenter ( A,  B,  C);
   float r=(O.disTo(A) + O.disTo(B)+ O.disTo(C))/3.f;
   float b = V(O,B).angle(); 
   float bc = positive(angle(V(O,B),V(O,C)));  if(cw(A,B,C)) bc-=TWO_PI;
   return ptOnCircle(b+t*bc,O,r);
   }

public pt midArc(pt A, pt B, pt C) {
  vec T=U(A,C); float d=d(B,C); float c=dot(U(A,B),T), s=dot(U(A,B),R(T));
  if(abs(s)<0.1f) return B;
  return T(B,d*(c-1)/s,R(T));
  }
  


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
public void declarePoints() {for (int i=0; i<P.length; i++) P[i]=new pt();} // init the vertices to be on a circle
public void resetPoints() {for (int i=0; i<n; i++) {P[i]=new pt(width/2,height/10.f); P[i].rotateBy(-2.f*PI*i/n, ScreenCenter());}; } // init the points to be on a circle
public void resetPointsOnCircle() {for (int i=0; i<n; i++) {P[i].x=height/2; P[i].y=height/10; P[i].rotateBy(-2.f*PI*i/n, ScreenCenter());}; } // init the points to be on a circle
public void resetPointsOnLine() {for (int i=0; i<n; i++) {P[i].x=height*(i+1)/(n+1); P[i].y=height/2;}; } // init the points to be on a circle
public void appendPoint(pt Q)  { P[n++].setTo(Q); p=n-1; }; // add point at end of list
public void insertPoint(pt Q) {for (int i=n-1; i>p; i--) P[i+1].setTo(P[i]); n++; p++; P[p].setTo(Q);  };
public void deletePoint() { for (int i=p; i<n-1; i++) P[i].setTo(P[i+1]); n--; p=max(0,p-1);}
public void empty()  { n=0; };      // resets the vertex count to zero
public void perturb(float e) {for (int i=0; i<n; i++) { P[i].x+=random(e); P[i].y+=random(e); } ; }

// ************************************** IMPORT POINTS FORM ANOTHER CLOUD *********************************
public void copyFrom(CLOUD D) {for (int i=0; i<max(n,D.n); i++) P[i].setTo(D.P[i]); n=D.n;}
public void extractFrom(pt [] Q, int start, int end )  {n=end-start+1; for (int i=0; i<n; i++) P[i].setTo(Q[i+start]); };  // makes P be the pvn first points of Q

// ************************************** SELECT AND TRANSFORM POINTS *********************************
public void pickClosestPoint(pt M) {p=0; for (int i=1; i<n; i++) if (d(M,P[i])<d(M,P[p])) p=i;}
public void dragPoint() { P[p].moveWithMouse(); P[p].clipToWindow(); }      // moves selected point (index p) by amount mouse moved recently
public void dragPoint(vec V) {P[p].translateBy(V);}
public void translatePoints(vec V) {for (int i=0; i<n; i++) P[i].translateBy(V); };   
public void scalePointsRelative(float s, pt G) {for (int i=0; i<n; i++) P[i]=L(G,s,P[i]);};  
public void scalePoints(float s, pt G) {for (int i=0; i<n; i++) P[i].translateTowards(s,G);};  
public void scalePoints(float s, pt G, vec V) {for (int i=0; i<n; i++) P[i].translateTowards(s,G);};  
public void scalePoints(float s) {scalePoints(s,verticesCenter());};
public void scalePointsAroundCenterOfMass(pt M, vec V) {pt C=verticesCenter(); scalePoints(dot(V,U(M,C))/d(M,C),C);};
public void rotatePoints(float a, pt G) {for (int i=0; i<n; i++) P[i].rotateBy(a,G);}; // rotates points around pt G by angle a
public void rotatePointsAroundCenterOfMass(float a) {rotatePoints(a,verticesCenter());}; // rotates points around their center of mass by angle a
public void rotatePoints(pt G, pt P, pt Q) {rotatePoints(angle(V(G,P),V(G,Q)),verticesCenter());}; // rotates points around G by angle <GP,GQ>
public void rotatePointsAroundCenterOfMass(pt P, pt Q) {rotatePoints(verticesCenter(),P,Q);}; // rotates points around their center of mass G by angle <GP,GQ>
public void frame() {
     float sx=height; float sy=height; float bx=0.0f; float by=0.0f; 
     for (int i=0; i<n; i++) {if (P[i].x>bx) {bx=P[i].x;}; if (P[i].x<sx) {sx=P[i].x;}; if (P[i].y>by) {by=P[i].y;}; if (P[i].y<sy) {sy=P[i].y;}; };
     float m=max(bx-sx,by-sy);  float dx=(m-(bx-sx))/2; float dy=(m-(by-sy))/2; 
     for (int i=0; i<n; i++) {P[i].x=(P[i].x-sx+dx)*4*height/5/m+height/10;  P[i].y=(P[i].y-sy+dy)*4*height/5/m+height/10;};    }   

// ************************************** REGISTER *********************************
public void registerTo(CLOUD Q) {  // vertex registration
  pt A=verticesCenter(); pt B=Q.verticesCenter(); 
  float s=0; for (int i=0; i<min(n,Q.n); i++) s+=dot(V(A,P[i]),R(V(B,Q.P[i])));
  float c=0; for (int i=0; i<min(n,Q.n); i++) c+=dot(V(A,P[i]),V(B,Q.P[i]));
  float a = atan2(s,c);
  translatePoints(V(A,B));
  rotatePoints(a,B); 
  } 
  
// ************************************** VIEW *********************************
public pt first() {return P[0];}  // returns first point
public pt last() {return P[n-1];}  // returns last point
public pt picked() {return P[p];}  // returns picked point
public void drawArrowsTo(CLOUD R) {for (int i=0; i<min(n,R.n); i++) arrow(P[i],R.P[i]);};
public void drawCorrespondenceTo(CLOUD R) {for (int i=0; i<min(n,R.n); i++) show(P[i],R.P[i]);};
public void drawPoints() {for (int i=0; i<n; i++) P[i].show();}
public void drawPoints(int r) {for (int i=0; i<n; i++) P[i].show(r);}
public void drawDots() {beginShape(POINTS); for (int i=0; i<n; i++) v(P[i]); endShape();}  // fast draw of points
public void writePointIDs() {for (int i=0; i<n; i++) label(P[i],S(15,U(verticesCenter(),P[i])),str(i)); }; 
public void writePointLetters() {for (int i=0; i<n; i++) label(P[i],str(PApplet.parseChar(i+65))); }; 
public void drawEdges() {beginShape(); for (int i=0; i<n; i++) v(P[i]); endShape();}  // fast draw of edges


// ************************************** MEASURE *********************************
public pt verticesCenter() {pt G=P(); for (int i=0; i<n; i++) G.addPt(P[i]); return S(1.f/n,G);} 
public pt ClosestVertex(pt M) {pt R=P[0]; for (int i=1; i<n; i++) if (d(M,P[i])<d(M,R)) R=P[i]; return P(R);}
public float distanceTo(pt M) {return d(M,ClosestVertex(M));}

// ************************************** DELAUNAY & VORONOI *********************************
public void drawDelaunayTriangles(int o) { 
   pt X = new pt(0,0);
   float r=1;  // radius of circumcircle
   for (int i=0; i<n-2; i++) for (int j=i+1; j<n-1; j++) for (int k=j+1; k<n; k++) {
      X=CircumCenter(P[i],P[j],P[k]);  r=d(X,P[i]);
      boolean found=false; 
      for (int m=0; m<n; m++) if ((m!=i)&&(m!=j)&&(m!=k)&&(X.disTo(P[m])<=r)) found=true;  
      if (!found) show(P[i],P[j],P[k],o);
      }; // end triple loop
   };

public void drawVoronoiEdges() { 
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
   
public void paintVoronoi() {int R=height/15;  noStroke(); for(int r=R; r>1; r--) for (int i=0; i<n; i++) {fill(color(random(256))); P[i].show(r);};
   }
   
// ************************************** SAVE TO FILE AND READ BACK *********************************
public void savePts() {savePts("data/P.pts");}
public void savePts(String fn) { String [] inppts = new String [n+1];
    int s=0; inppts[s++]=str(n); for (int i=0; i<n; i++) {inppts[s++]=str(P[i].x)+","+str(P[i].y);};
    saveStrings(fn,inppts);  };
public void loadPts() {loadPts("data/P.pts");}
public void loadPts(String fn) { String [] ss = loadStrings(fn);
    String subpts;
    int s=0; int comma; n = PApplet.parseInt(ss[s]);
    for(int i=0;i<n; i++) { comma=ss[++s].indexOf(',');
      P[i]=new pt (PApplet.parseFloat(ss[s].substring(0, comma)), PApplet.parseFloat(ss[s].substring(comma+1, ss[s].length()))); }; };

// ************************************** MORPH *********************************
public void linearMorph(CLOUD A, float t, CLOUD B) {
   n=min(A.n,B.n); 
   for (int i=0; i<n; i++) P[i]=L(A.P[i],t,B.P[i]);
   }

// ************************************** SPIRAL MOTION *********************************
public void spiral(pt G, float s, float a, float t) {for (int i=0; i<n; i++) P[i]=spiralPt(P[i],G,s,a,t);}

public void spiral(CLOUD A, float t, CLOUD B) {  // moves P by a fraction t of spiral from A to B
  float a =spiralAngle(A.P[0],A.P[A.n-1],B.P[0],B.P[B.n-1]); 
  float s =spiralScale(A.P[0],A.P[A.n-1],B.P[0],B.P[B.n-1]);
  pt G = spiralCenter(a, s, A.P[0], B.P[0]);
  spiral(G,s,a,t);
  }

}
//*****************************************************************************
// TITLE:         COLOR UTILITIES FOR GSB  
// DESCRIPTION:   defines color names and tools 
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
public int col(int a, int r, int g, int b) {return a<<24 | r<<16 | g<<8 | b ;} // makes color fast with alpha (opacity), red, green, blue between 0 and 255
public int col(int r, int g, int b) {return r<<16 | g<<8 | b ;} // makes color fast without alpha
public int setA(int c, int a) {return (a<<24) | ((c<<8)>>8) ;} // sets alpha for color c
public int colA(int c) {return (c>>24) & 0xFF;}
public int colR(int c) {return (c>>16) & 0xFF;}
public int colG(int c) {return (c>>8) & 0xFF;}
public int colB(int c) {return c & 0xFF;}
public int interCol(int C0, float t, int C1) {return col( PApplet.parseInt((1.f-t)*colA(C0)+t*colA(C1)) , PApplet.parseInt((1.f-t)*colR(C0)+t*colR(C1)) , PApplet.parseInt((1.f-t)*colG(C0)+t*colG(C1)) , PApplet.parseInt((1.f-t)*colB(C0)+t*colB(C1)) );}
int brown=0xff8B5701, red=0xffFF0000, magenta=0xffFF00FB, blue=0xff0300FF, cyan=0xff00FDFF, green=0xff00FF01, yellow=0xffFEFF00, skin=0xffF5C7B7, black=0xff000000, grey=0xff868686, white=0xffFFFFFF, orange=0xffFFC000, metal=0xffBEC9E0;
int lbrown=0xffC19100, lred=0xffFF3E3E, lmagenta=0xffFF7EFD, lblue=0xff807EFF, lcyan=0xff7EFFFE, lgreen=0xff80FF7E, lyellow=0xffFFFF6C, lskin=0xffFFECE5, lorange=0xffFFDF7E;
int dbrown=0xff674001, dred=0xff810000, dmagenta=0xff81007F, dblue=0xff010081, dcyan=0xff008180, dgreen=0xff018100, dyellow=0xffB4B400, dskin=0xff815531, dorange=0xffE58F02;

//color [] C=new color[1000];   
//void setRandomColors() {for (int i=0; i<n; i++) C[i]=color(int(random(255)),int(random(255)),int(random(255)));}

public void showColors() {noStroke();
pushMatrix();
fill(lbrown); show(P(10,10),10); translate(20,0); 
fill(lred); show(P(10,10),10); translate(20,0); 
fill(lmagenta); show(P(10,10),10); translate(20,0); 
fill(lblue); show(P(10,10),10); translate(20,0); 
fill(lcyan); show(P(10,10),10); translate(20,0); 
fill(lgreen); show(P(10,10),10); translate(20,0); 
fill(lyellow); show(P(10,10),10); translate(20,0); 
fill(lskin); show(P(10,10),10); translate(20,0); 
fill(lorange); show(P(10,10),10); translate(20,0); 
fill(metal); show(P(10,10),10); translate(20,0); 
popMatrix(); pushMatrix(); translate(0,20);
fill(brown); show(P(10,10),10); translate(20,0); 
fill(red); show(P(10,10),10); translate(20,0); 
fill(magenta); show(P(10,10),10); translate(20,0); 
fill(blue); show(P(10,10),10); translate(20,0); 
fill(cyan); show(P(10,10),10); translate(20,0); 
fill(green); show(P(10,10),10); translate(20,0); 
fill(yellow); show(P(10,10),10); translate(20,0); 
fill(skin); show(P(10,10),10); translate(20,0); 
fill(orange); show(P(10,10),10); translate(20,0); 
fill(grey); show(P(10,10),10); translate(20,0); 
popMatrix(); pushMatrix(); translate(0,40);
fill(dbrown); show(P(10,10),10); translate(20,0); 
fill(dred); show(P(10,10),10); translate(20,0); 
fill(dmagenta); show(P(10,10),10); translate(20,0); 
fill(dblue); show(P(10,10),10); translate(20,0); 
fill(dcyan); show(P(10,10),10); translate(20,0); 
fill(dgreen); show(P(10,10),10); translate(20,0); 
fill(dyellow); show(P(10,10),10); translate(20,0); 
fill(dskin); show(P(10,10),10); translate(20,0); 
fill(dorange); show(P(10,10),10); translate(20,0); 
fill(black); show(P(10,10),10); translate(20,0); 
popMatrix();
}
 
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
 public int next(int j) { return j+1; };  // next vertex 
 public int prev(int j) { return j-1; };  // previous vertex                                                      
 public void bcSet() {b=n/3; c=2*n/3;}
// ************************************** INSERT POINTS *********************************
 public void insert(pt M) {                // grabs closest vertex or adds vertex at closest edge. It will be dragged by te mouse
     p=0; for (int i=0; i<n; i++) if (d(M,P[i])<d(M,P[p])) p=i; 
     int e=-1;
     float d = d(M,P[p]);
     for (int i=0; i<n-1; i++) {float x=x(P[i],M,P[i+1]), y=abs(ay(P[i],M,P[i+1])); if ( 0.2f<x && x<0.8f && y<d && y<height/20) {e=i; d=y;}; }
     if (e!=-1) { for (int i=n-1; i>e; i--) P[i+1].setTo(P[i]); n++; p=e+1; P[p].setToMouse();  };
     }

  // ************************************** SELECT AND TRANSFORM POINTS *********************************
 public int closestVertex(pt M) {int c=0; for (int i=0; i<n; i++) if (d(M,P[i])<d(M,P[c])) c=i; return c;} // identifies closeest vertex
 public void scalePoints(float s) {scalePoints(s,edgesCenter());};
 public void scalePointsAroundCenterOfMass(pt M, vec V) {pt C=edgesCenter(); scalePoints(dot(V,U(M,C))/d(M,C),C);};
 public void rotatePointsAroundCenterOfMass(float a) {rotatePoints(a,edgesCenter());}; // rotates points around their center of mass by angle a
 public void rotatePointsAroundCenterOfMass(pt P, pt Q) {rotatePoints(edgesCenter(),P,Q);}; // rotates points around their center of mass G by angle <GP,GQ>
 public void rotatePoints(pt G, pt P, pt Q) {rotatePoints(angle(V(G,P),V(G,Q)),edgesCenter());}; // rotates points around G by angle <GP,GQ>
 public void bSet(pt M) {b=closestVertex(M);}
 public void cSet(pt M) {c=closestVertex(M);}
// ************************************** REGISTER *********************************
public void registerToCurve(CURVE Q) {  // vertex registration
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
public void curvatureMorph(CURVE A, float t, CURVE B) {
   copyFrom(A);
   P[1]=T(P[0],pow(d(B.P[0],B.P[1])/d(A.P[0],A.P[1]),t),V(A.P[0],A.P[1])); 
   n=min(A.n,B.n); 
   for (int i=2; i<n; i++) {
//     float d=d(A.P[i-1],A.P[i])*pow(d(B.P[i-1],B.P[i])/d(A.P[i-1],A.P[i]),t);
     float d=(1.f-t)*d(A.P[i-1],A.P[i])+t*d(B.P[i-1],B.P[i]);
     float a=(1.f-t)*angle(V(A.P[i-2],A.P[i-1]),V(A.P[i-1],A.P[i]))+t*angle(V(B.P[i-2],B.P[i-1]),V(B.P[i-1],B.P[i]));
     P[i]=T(P[i-1],S(d,R(U(V(P[i-2],P[i-1])),a)));
     }
   }
   
public void curvatureMorphXX(CURVE A, float t, CURVE B) {
   copyFrom(A);
   P[1] = T(P[0] , 1.f-t+t*(d(B.P[0],B.P[1])/d(A.P[0],A.P[1])) , V(A.P[0],A.P[1]) ); 
   n=min(A.n,B.n); 
   for (int i=2; i<n; i++) {
     float d=d(A.P[i-1],A.P[i]) + (1.f-t)+t*d(B.P[i-1],B.P[i])/d(A.P[i-1],A.P[i]);
     float a=(1.f-t)*angle(V(A.P[i-2],A.P[i-1]),V(A.P[i-1],A.P[i]))+t*angle(V(B.P[i-2],B.P[i-1]),V(B.P[i-1],B.P[i]));
     P[i]=T(P[i-1],S(d,R(U(V(P[i-2],P[i-1])),a)));
     }
   }
   
public void spiral(CURVE A, float t, CURVE B) {  // moves P by a fraction t of spiral from A to B
  float a =spiralAngle(A.P[0],A.P[A.n-1],B.P[0],B.P[B.n-1]); 
  float s = A.length() / B.length();
  pt G = spiralCenter(a, s, A.P[0], B.P[0]);
  spiral(G,s,a,t);
  }
  
 // ************************************** MEASURE *********************************
 public pt edgesCenter() {pt G=P(); float D=0; for (int i=0; i<n-1; i++) {float d=d(P[i],P[i+1]); D+=d; G.addPt(S(d,A(P[i],P[i+1])));} return S(1.f/D,G);} 
 public float length () {float L=0; for (int i=0; i<n-1; i++) L+=P[i].disTo(P[i+1]);  return(L); }
 public float length (int s, int e) {float L=0; for (int i=s; i<e; i++) L+=d(P[i],P[i+1]); return(L); }  
 public void scaleToLength(float L) {float s=L/length(); scalePointsRelative(s,edgesCenter());}
 public void matchLengthOf(CURVE C) {float s=C.length()/length(); scalePointsRelative(s,edgesCenter());}

 // ************************************** DISPLAY *********************************
 public void showWithHead(int col, int k) {if(n<1) return; 
   stroke(col); strokeWeight(1); drawEdges(); strokeWeight(3); drawDots(); fill(white); P[0].show(12); fill(col); label(P[0],str(k)); noFill(); 
   strokeWeight(1); P[b].show(2);  P[c].show(3); 
   }
 public void drawEdges() {beginShape(); for (int i=0; i<n; i++) v(P[i]); endShape();}  // fast draw of edges
 public float distanceTo(pt M) {return d(M,Projection(M));}
 public pt Projection(pt M) { 
     int v=0; for (int i=1; i<n; i++) if (d(M,P[i])<d(M,P[v])) v=i; 
     int e=-1; float d = d(M,P[v]);
     for (int i=0; i<n-1; i++) {float x=x(P[i],M,P[i+1]); if ( 0<x && x<1) { float y=abs(ay(P[i],M,P[i+1])); if(y<d) {e=i; d=y;} } }
     if (e!=-1) return Shadow(P[e],M,P[e+1]); else return P(P[v]);
     }
 public void showWithLabels(int c) {stroke(c); drawEdges(); fill(white); drawPoints(12); fill(c); writePointLetters(); noFill(); }
 public void drawBeziers() {addEnds(); for (int i=2; i<n-3; i++) drawBezier(i); chopEnds();}
 public void drawBezier(int i) {drawCubicBezier(P[i], T(P[i],1.f/6,V(P[prev(i)],P[next(i)])), T(P[next(i)],-1.f/6,V(P[i],P[next(next(i))])), P[next(i)]); }
 
// ************************************** INTERSECTIONS *********************************
public boolean stabbed(pt A, pt B) {for (int i=0; i<n-1; i++) if(edgesIntersect(A,B,P[i],P[i+1])) return true; return false; }
public boolean stabbed(pt A, pt B, int j) {for (int i=0; i<n-1; i++) if((i!=j)&&edgesIntersect(A,B,P[i],P[i+1])) return true; return false;  }

//  ************************************* RESAMPLE ***********************************************
 public pt at (float t) { 
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

 public void resample(int nrv) { if(n==0) return;
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

public void refine(float s) { 
      pt[] Q = new pt [2*n];     
      for (int i=0; i<n-1; i++) { Q[2*i]= P(P[i]); Q[2*i+1]=A(P[i],P[i+1]); };
      Q[2*n-2]=P(P[n-1]);
      n=2*n-1; 
      for (int i=0; i<n; i++) P[i]=P(Q[i]);
     }
public void coarsen() {n=(n+1)/2; for (int i=1; i<n; i++) P[i]=P(P[2*i]); } 

//  ************************************* SUBDIVIDE  ***********************************************

 public void subdivide(float s) { 
     addEnds();
     pt[] Q = new pt [2*n];     
     int j=0; for (int i=1; i<n-2; i++) { Q[j++]= B(P[prev(i)],P[i],P[next(i)],s); Q[j++]=F(P[prev(i)],P[i],P[next(i)],P[next(next(i))],s); };
      int k=n-2; Q[j++]=B(P[prev(k)],P[k],P[next(k)],s);
      n=j; 
      for (int i=0; i<n; i++) P[i].setTo(Q[i]);
      chopEnds();
     }
     
 public void subdivideProportional() { 
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

public void beautify() {resample(40); smoothen(); smoothen(); subdivide(0.5f); subdivide(0.5f); subdivide(0.5f); resample(120); }

vec [] L = new vec[1000];

public void smoothen(float s) {
  for (int i=1; i<n-1; i++) L[i]=V(P[i],A(P[i-1],P[i+1]));
  for (int i=1; i<n-1; i++) arrow(P[i],L[i]);
  for (int i=1; i<n-1; i++) P[i].add(s,L[i]); 
  }
 public void smoothen() {addEnds(); for (int i=0; i<5; i++) {computeL2(); applyL(0.5f); computeL2(); applyL(-0.5f); } chopEnds();}
 public void computeL2() {for (int i=2; i<n-2; i++) L[i]=vecToCubic(P[prev(prev(i))],P[prev(i)],P[i],P[next(i)],P[next(next(i))]);};
 public void applyL(float s) {for (int i=2; i<n-2; i++) P[i].translateBy(s,L[i]);};

// ************************************** ENDS *********************************
      
 public void drawEnds() {addEnds(); P[0].to(P[1]); P[1].to(P[2]);  P[n-1].to(P[n-2]); P[n-2].to(P[n-3]); P[0].show(2); P[1].show(2); P[n-1].show(2); P[n-2].show(2); chopEnds(); }
 public void chopEnds() {n-=4;  for (int i=0; i<n; i++) { P[i].setTo(P[i+2]);};  } 
 public void addEnds() {for (int i=n-1; 0<=i; i--) P[i+2].setTo(P[i]); n+=4;  adjustEnds(); }   
 public void adjustEnds() {
    pt P0= End0(P[2],P[next(2)],P[next(next(2))]);
    pt P1= End1(P[2],P[next(2)],P[next(next(2))]);
    pt Pn= End1(P[n-3],P[prev(n-3)],P[prev(prev(n-3))]);
    pt Pnn=End0(P[n-3],P[prev(n-3)],P[prev(prev(n-3))]);
    P[0].setTo(P0); P[1].setTo(P1);   P[n-2].setTo(Pn); P[n-1].setTo(Pnn); 
     }

 } // end CURVE
 
public pt   End1(pt P0, pt P1, pt P2) { return L(P1,2,P0); }
public pt   End0(pt P0, pt P1, pt P2) { return T(L(P1,2,P0),V(P2,P1)); }
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
// CHANGED FOR PROJECT 1
// If either of the endpoints are the same then don't record the lines as intersecting
public boolean edgesIntersect(pt A, pt B, pt C, pt D) {
  if(abs(A.x - C.x) < 0.01f && abs(A.y - C.y) < 0.01f || 
     abs(A.x - D.x) < 0.01f && abs(A.y - D.y) < 0.01f ||
     abs(B.x - C.x) < 0.01f && abs(B.y - C.y) < 0.01f ||
     abs(B.x - D.x) < 0.01f && abs(B.y - D.y) < 0.01f) {
    return false;
  }
  return cw(A,B,C)!=cw(A,B,D) && cw(C,D,A)!=cw(C,D,B);
}
public boolean edgesIntersect(pt A, pt B, pt C, pt D,float e) {
  return ((A.isLeftOf(C,D,e) && B.isLeftOf(D,C,e))||(B.isLeftOf(C,D,e) && A.isLeftOf(D,C,e)))&&
         ((C.isLeftOf(A,B,e) && D.isLeftOf(B,A,e))||(D.isLeftOf(A,B,e) && C.isLeftOf(B,A,e))) ;   }
public pt linesIntersection(pt A, pt B, pt C, pt D) {vec AB = V(A,B);  vec CD = V(C,D);  vec N=R(CD); vec AC = V(A,C); float s = dot(AC,N)/dot(AB,N); return T(A,s,AB); }



//*****************************************************************************
// TITLE:         BEZIER  
// DESCRIPTION:   Tools for processing Bezier curves
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
class frame {       // frame [O I J]
  pt O = new pt();
  vec I = new vec(1,0);
  vec J = new vec(0,1);
  frame() {}
  frame(pt pO, vec pI, vec pJ) {O.setTo(pO); I.setTo(pI); J.setTo(pJ);  }
  frame(pt A, pt B, pt C) {O.setTo(B); I=A.makeVecTo(C); I.normalize(); J=I.makeTurnedLeft();}
  frame(pt A, pt B) {O.setTo(A); I=A.makeVecTo(B).makeUnit(); J=I.makeTurnedLeft();}
  frame(pt A, vec V) {O.setTo(A); I=V.makeUnit(); J=I.makeTurnedLeft();}
  frame(float x, float y) {O.setTo(x,y);}
  frame(float x, float y, float a) {O.setTo(x,y); this.rotateBy(a);}
  frame(float a) {this.rotateBy(a);}
  public frame makeClone() {return(new frame(O,I,J));}
  public void reset() {O.setTo(0,0); I.setTo(1,0); J.setTo(0,1); }
  public void setTo(frame F) {O.setTo(F.O); I.setTo(F.I); J.setTo(F.J); }
  public void setTo(pt pO, vec pI, vec pJ) {O.setTo(pO); I.setTo(pI); J.setTo(pJ); }
  public void show() {float d=height/20; O.show(); I.makeScaledBy(d).showArrowAt(O); J.makeScaledBy(d).showArrowAt(O); }
  public void showLabels() {float d=height/20; 
               O.makeTranslatedBy(A(I,J).makeScaledBy(-d/4)).showLabel("O",-3,5); 
               O.makeTranslatedBy(d,I).makeTranslatedBy(-d/5.f,J).showLabel("I",-3,5); 
               O.makeTranslatedBy(d,J).makeTranslatedBy(-d/5.f,I).showLabel("J",-3,5); 
             }
  public void translateBy(vec V) {O.translateBy(V);}
  public void translateBy(float x, float y) {O.translateBy(x,y);}
  public void rotateBy(float a) {I.rotateBy(a); J.rotateBy(a); }
  public frame makeTranslatedBy(vec V) {frame F = this.makeClone(); F.translateBy(V); return(F);}
  public frame makeTranslatedBy(float x, float y) {frame F = this.makeClone(); F.translateBy(x,y); return(F); }
  public frame makeRotatedBy(float a) {frame F = this.makeClone(); F.rotateBy(a); return(F); }
   
  public float angle() {return(I.angle());}
  public void apply() {translate(O.x,O.y); rotate(angle());}  // rigid body tansform, use between pushMatrix(); and popMatrix();
  public void moveTowards(frame B, float s) {O.translateTowards(s,B.O); rotateBy(s*(B.angle()-angle()));}  // for chasing or interpolating frames
  } // end frame class
 
public frame makeMidEdgeFrame(pt A, pt B) {return(new frame(A(A,B),A.makeVecTo(B)));}  // creates frame for edge

public frame interpolate(frame A, float s, frame B) {   // creates a frame that is a linear interpolation between two other frames
    frame F = A.makeClone(); F.O.translateTowards(s,B.O); F.rotateBy(s*(B.angle()-A.angle()));
    return(F);
    }

public frame twist(frame A, float s, frame B) {   // a circular interpolation
  float d=A.O.disTo(B.O);
  float b=angle(A.I,B.I);
  frame F = A.makeClone(); F.rotateBy(s*b);
  pt M = A(A.O,B.O);   
  if ((abs(b)<0.000001f) || (abs(b-PI)<0.000001f)) F.O.translateTowards(s,B.O); 
  else {
  float h=d/2/tan(b/2); //else print("/b");
     vec W = A.O.makeVecTo(B.O); W.normalize();
     vec L = W.makeTurnedLeft();   L.scaleBy(h);
     M.translateBy(L);  // fill(0); M.show(6);
     L.scaleBy(-1);  L.normalize(); 
     if (abs(h)>=0.000001f) L.scaleBy(abs(h+sq(d)/4/h)); //else print("/h");
     pt N = M.makeClone(); N.translateBy(L);  
     F.O.rotateBy(-s*b,M);
     };   
  return(F);
  }
  
//*****************************************************************************
// TITLE:         IO  
// DESCRIPTION:   Tools for managing files, images, and sribing on the screen
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
//***************************** SCRIBING *********************************
// Utilities to paint text on the canvas with specified colors and position
public void scribe(String S,int c) {fill(c); text(S,20,20); noFill();}
public void scribeBlack(String S, int i) {fill(black); text(S,20,20+i*20); noFill();}
public void scribeBlackRight(String S, int i) {fill(black); text(S,height-20-8*S.length(),20+i*20); noFill();}
public void scribeBlack(String S) {fill(black); text(S,20,20); noFill();}
public void scribe(String S, float x, float y) {text(S,x,y);}
public void scribe(String S) {text(S,20,20);}
public void scribeMouseCoordinates() {fill(black); text("("+mouseX+","+mouseY+")",mouseX+7,mouseY+25); noFill();}

//***************************** FORMATTING NUMBERS *********************************
public String  Format(int v, int n) {String s=str(v); String spaces = "                            ";
   int L = max(0,n-s.length());
   String front = spaces.substring(0, L);
   return(front+s);
  }; 

public String  Format0(int v, int n) {
   String s=str(v); String spaces = "00000000000000000000000000";
   int L = max(0,n-s.length());
   String front = spaces.substring(0, L);
   return(front+s);
  };

public String  Format(String s, int n) {String spaces = "                                 ";
    int L = max(0,n-s.length());
    String back = spaces.substring(0, L);
    return(s+back);
  };

public String  Format(float f, int n, int z) {
   String sign = "-"; if (f>=0) sign="+";
   String spaces = "                                ";
   String s=nf(abs(f),n,z); 
   while (s.indexOf("0")==0) {s=s.substring(1,s.length());};
   int b=s.indexOf("."); int a=max(0,n-b); int c=s.length()-b-1;  int d=0;
   if (c>z) {s=s.substring(0,b+1+z); c=z;} else { d=z-c;};
   String front = spaces.substring(0, a);
   String back = spaces.substring(0, d);
   return(front+sign+s+back);
  }; 

//***************************** DATA FILES *********************************
int numberOfExamples=1; // number of files
int currentExample=0; // file last read
public void saveNumberOfExamples() { String [] S = new String[1]; S[0]=str(numberOfExamples);  saveStrings("data/ne",S); println("saved number of examples = "+numberOfExamples); }
public void loadNumberOfExamples() { String [] S = loadStrings("data/ne"); numberOfExamples=PApplet.parseInt(S[0]); println("read number of examples = "+numberOfExamples); }

//***************************** FIGURES *********************************
int io_pic=0; // picture number for saving sequences of pictures of making movies
public void picture() {saveFrame("pictures/P"+Format0(pictureCounter++,3)+".jpg"); makingPicture=false; showMenu=!showMenu;}

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
 public void makeSquare() {P[0]=P(width*0.8f,height*0.8f);P[1]=P(width*0.8f,height*0.2f);P[2]=P(width*0.2f,height*0.2f);P[3]=P(width*0.2f,height*0.8f);}
 public void delete() { for (int i=p; i<n-1; i++) P[i].setTo(P[n(i)]); n--; p=p(p);}
 public void insert(pt M) {                // grabs closeest vertex or adds vertex at closest edge. It will be dragged by te mouse
     p=0; for (int i=0; i<n; i++) if (d(M,P[i])<d(M,P[p])) p=i; 
     int e=-1;
     float d = d(M,P[p]);
     for (int i=0; i<n; i++) {float x=x(P[i],M,P[n(i)]), y=abs(ay(P[i],M,P[n(i)])); if ( 0.2f<x && x<0.8f && y<d && y<height/20) {e=i; d=y;}; }
     if (e!=-1) { for (int i=n-1; i>e; i--) P[i+1].setTo(P[i]); n++; p=n(e); P[p].setToMouse();  };
     }
// ADDED FOR PROJECT 1
// Inserts point into specific position in loop
// We want to insert the new point after P[ind]
public void insert(pt M, int ind) {
  for(int i = n-1; i > ind; i--) {
    P[i+1] = P[i];
  }
  n++;
  P[n(ind)] = M;
}

public void insert(pt M, boolean check) {
  if(!check) {
    insert(M);
  }
  for(int i = 0; i < n; i++) {
    if(angle(P[n(i)], P[i], M) < 0.1f) {
      insert(M, i);
      break;
     } 
   }
}

 // ************************************** TRAVERSAL UTILITIES *********************************
 public int n(int j) {  if (j==n-1) {return (0);}  else {return(j+1);}  };  // next point in loop
 public int p(int j) {  if (j==0) {return (n-1);}  else {return(j-1);}  };  // previous point in loop                                                     

 // ************************************** SELECT AND TRANSFORM POINTS *********************************
 public void scalePoints(float s) {scalePoints(s,edgesCenter());};
 public void scalePointsAroundCenterOfMass(pt M, vec V) {pt C=edgesCenter(); scalePoints(dot(V,U(M,C))/d(M,C),C);};
 public void rotatePointsAroundCenterOfMass(float a) {rotatePoints(a,edgesCenter());}; // rotates points around their center of mass by angle a
 public void rotatePointsAroundCenterOfMass(pt P, pt Q) {rotatePoints(edgesCenter(),P,Q);}; // rotates points around their center of mass G by angle <GP,GQ>
 public void rotatePoints(pt G, pt P, pt Q) {rotatePoints(angle(V(G,P),V(G,Q)),edgesCenter());}; // rotates points around G by angle <GP,GQ>
 // ************************************** REGISTER *********************************
public void registerToLoop(LOOP Q) {  // vertex registration
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
 public pt edgesCenter() {pt G=P(); float D=0; for (int i=0; i<n-1; i++) {float d=d(P[i],P[i+1]); D+=d; G.addPt(S(d,A(P[i],P[i+1])));} return S(1.f/D,G);} 
 
 public float length () {float L=0; for (int i=0; i<n; i++) L+=d(P[i],P[n(i)]);  return(L); }
 
 public boolean contains(pt M) {  
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
   
 public float distanceTo(pt M) {return d(M,Projection(M));}
 
 public pt Projection(pt M) {
     int v=0; for (int i=1; i<n; i++) if (d(M,P[i])<d(M,P[v])) v=i; 
     int e=-1;
     float d = d(M,P[v]);
     for (int i=0; i<n; i++) {float x=x(P[i],M,P[n(i)]); if ( 0<x && x<1) { float y=abs(ay(P[i],M,P[n(i)])); if(y<d) {e=i; d=y;} } }
     if (e!=-1) return Shadow(P[e],M,P[n(e)]); else return P(P[v]);
      }

 public float area () {float A=0; for (int i=0; i<n; i++) A+=trapezeArea(P[i],P[n(i)]); return A; }

 public pt Barycenter () {
      pt G=P(); 
      pt O=P(); 
      float area=0;
      for (int i=0; i<n; i++) {float a = triangleArea(O,P[i],P[n(i)]); area+=a; G.addScaledPt(a,A(O,P[i],P[n(i)])); };
      G.scaleBy(1.f/area); 
      return(G); 
      }
      
 public float moment(pt G) {
   float m=0;
   for (int i=0; i<n; i++) {
          vec GA = V(G,P[i]); vec GB =V(G,P[n(i)]);
           m+=dot(R(GA),GB)*(dot(GA,GA)+dot(GA,GB)+dot(GB,GB));
           }  
     return m/12.f;   
     }
  
 public float alignentAngle(pt G) { // of the perimeter
    float xx=0, xy=0, yy=0, px=0, py=0, mx=0, my=0;
    for (int i=0; i<n; i++) {xx+=(P[i].x-G.x)*(P[i].x-G.x); xy+=(P[i].x-G.x)*(P[i].y-G.y); yy+=(P[i].y-G.y)*(P[i].y-G.y);};
    return atan2(2*xy,xx-yy)/2.f;
    }
    
// ************************************** VIEWING *********************************
 public void drawEdges() {beginShape(); for (int i=0; i<n; i++) v(P[i]); endShape(CLOSE);}  // fast draw of edges
 public void showWithLabels(int c) {stroke(c); drawEdges(); if(showLetters.isTrue) {fill(white); drawPoints(12); fill(c); writePointLetters(); noFill();}
//   else { strokeWeight(5); stroke(dblue); drawDots(); if (showIDs.isTrue) {fill(dblue); writePointIDs(); noFill();}; } }
   else { strokeWeight(5); stroke(dblue); drawDots(); if (true) {fill(dblue); writePointIDs(); noFill();}; } }
    
 public void showInertia() {stroke(orange); noFill(); pt G=Barycenter(); show(G,3); 
   float a=alignentAngle(G);  float m=moment(G); float r = sqrt(sqrt(m/PI*2.f)); vec V=R(V(r,0),a); show(G,V); show(G,-1,V);  show(G,r);}

// ************************************** INTERSECTIONS *********************************
 public int stabbed(pt A, pt B) {for (int i=0; i<n; i++) if(edgesIntersect(A,B,P[i],P[n(i)])) return i; return -1;}
 public int stabbed(pt A, pt B, int j) {for (int i=0; i<n; i++) if ((i!=j)&&(edgesIntersect(A,B,P[i],P[n(i)]))) return i; return -1; }

// ************************************** RESAMPLE *********************************
 // ADDED FOR PROJECT 1
 public void spray() {
   pt[] Q = new pt[5000];
   int ind = 0;
   // For every point in the loop spray towards the next point along interior and exterior
   for(int i=0; i<n; i++) {
     pt src = P[i];
     pt dst = P[n(i)];
     Q[ind] = P[i]; ind++;
     for(int k=5; k<6; k++) {
       pt newPt = T(L(src, k*0.1f, dst), 1, R(U(V(src, dst))));
       Q[ind] = newPt; ind++;
       newPt = T(L(src, k*0.1f, dst), 1, R(R(R(U(V(src, dst))))));
       Q[ind] = newPt; ind++;
     }
   }
   // Now copy them back into the points array
   empty();
   for(int i=0; i<ind; i++) {
     appendPoint(Q[i]);
   }
 }
 
 public void refine(float s) { 
      pt[] Q = new pt [2*n];     
      for (int i=0; i<n; i++) { Q[2*i]= b(P[p(i)],P[i],P[n(i)],s); Q[2*i+1]=f(P[p(i)],P[i],P[n(i)],P[n(n(i))],s); };
      n*=2; for (int i=0; i<n; i++) P[i].setTo(Q[i]);
     }
 public void coarsen() {n/=2; for (int i=0; i<n; i++) P[i].setTo(P[2*i]); } 
 
 public void resample(int nn) { // resamples the curve with new nn vertices
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

 public void smoothen() {for (int i=0; i<5; i++) {computeL2(); applyL(0.5f); computeL2(); applyL(-0.5f); };}
 public void computeL2() {for (int i=0; i<n; i++) L[i]=cubic(P[p(p(i))],P[p(i)],P[i],P[n(i)],P[n(n(i))]);};
 public void computeL() {for (int i=0; i<n; i++) L[i]=S(0.5f,V(P[i],A(P[p(i)],P[n(i)])));};
 public void applyL(float s) {for (int i=0; i<n; i++) P[i].translateBy(s,L[i]);};

public void smoothenDEMO(float s) {
  vec [] L = new vec[n];
  for (int i=1; i<n-1; i++) L[i]=V(P[i],A(P[i-1],P[i+1]));
  for (int i=1; i<n-1; i++) arrow(P[i],L[i]);
  for (int i=1; i<n-1; i++) P[i].add(s,L[i]); 
  }
  
// ************************************** SUBDIVISION *********************************
public void subdivide(float s) {subdivide(s,s);}
public void subdivide(float a, float b) { 
      pt[] Q = new pt [2*n];     
      for (int i=0; i<n; i++) { Q[2*i]= B(P[p(i)],P[i],P[n(i)],a); Q[2*i+1]=F(P[p(i)],P[i],P[n(i)],P[n(n(i))],b); };
      n*=2; for (int i=0; i<n; i++) P[i].setTo(Q[i]);
     }

// ************************************** ART *********************************
public void lace() {for (int i=0; i<n-1; i++) for (int k=i; k<n; k++) show(P[i],P[k]); }
public void lace2() {for (int i=0; i<n; i++) {int j=i; for (int k=0; k<n/3; k++) j=n(j); show(P[i],P[j]); }}

// ************************************** TRIANGULATION *********************************
public void drawShrunkTriangles2(int o) { 
   pt X = new pt(0,0);
   float r=1;  // radius of circumcircle
   for (int i=0; i<n-2; i++) for (int j=i+1; j<n-1; j++) for (int k=j+1; k<n; k++) {
      X=CircumCenter(P[i],P[j],P[k]);  r=d(X,P[i]);
      boolean found=false; 
      for (int m=0; m<n; m++) if ((m!=i)&&(m!=j)&&(m!=k)&&(X.disTo(P[m])<=r)) found=true;  
      if (!found && contains(Centroid(P[i],P[j],P[k]))) if(o==0) show(P[i],P[j],P[k]); else show(P[i],P[j],P[k],-o);
      }; // end triple loop
   };

public void drawShrunkTriangles(int o) { 
   for (int i=0; i<n; i++) showDelaunay(i,n(i),-o);
      }; // end triple loop

public void  showDelaunay(int i, int j, float o) {
   int mk=0; 
   float mb=100000;
   for (int k=0; k<n; k++) if(k!=i && k!=j) {float b=bulge(P[i],P[k],P[j]); if(0<b & b<mb) {mb=b; mk=k;}} // add check for boundary crossing
   show(P[i],P[mk],P[j], o);
   }
   
public void delaunayFromEdges() { 
      for (int i=0; i<n; i++)  for (int j=0; j<n; j++)  
                 if (cw(P[i],P[n(i)],P[j])) {
                    pt CC=CircumCenter(P[i],P[n(i)],P[j]); 
                    float r=d(P[i],CC);
                   // float r=radius(P[i],P[n(i)],P[j]);
                    boolean found=false;
                    for (int k=0; k<n; k++) if (d(P[k],CC)+0.1f<r) found=true;
                    if (!found) show(P[i],P[n(i)],P[j]);
                    };
    }
public void showDelaunayOfPoints(float o) { 
      for (int i=0; i<n-2; i++)  for (int j=i+1; j<n-1; j++)  for (int k=j+1; k<n; k++)  {
                   pt CC=CircumCenter(P[i],P[j],P[k]);  float r=d(P[i],CC);                
                   boolean found=false;
                   for (int m=0; m<n; m++) if (d(P[m],CC)+0.001f<r) found=true;
                   if (!found) {if(contains(A(P[i],P[j],P[k]))) {fill(green); show(P[i],P[j],P[k],o);} };                   //Removed "else fill(yellow);" from before show and added braces. if (cw(P[i],P[n(i)],P[j])) {}
                     

                   };
    }
    
public void updatePoints(Mesh M) {
    for (int i=0; i<M.nv; i++) M.G[i].setTo(P[i]);
}
   
public void makeDelaunayOfPoints(Mesh M) { 
    M.init(); // empty the mesh
    for (int i=0; i<n; i++) M.addVertex(P[i]); // add all the vertices of the loop to be vertices of the mesh
    for (int i=0; i<n-2; i++)  for (int j=i+1; j<n-1; j++)  for (int k=j+1; k<n; k++)  { // generate all candidate triangles
        pt CC=CircumCenter(P[i],P[j],P[k]);  float r=d(P[i],CC);  // compute their circumcenter and radius          
        boolean empty=true; for (int m=0; m<n; m++) if (d(P[m],CC)+0.001f<r) empty=false;  // check whether circle is empty 
        if (empty) {if (cw(P[i],P[j],P[k])) M.addTriangle(i,j,k,contains(A(P[i],P[j],P[k]))); else M.addTriangle(i,k,j,contains(A(P[i],P[j],P[k]))); }; // add properly oriented triangle to the mesh
        }
    M.computeO(); // computes the O table for connectivity
    }
 
 } // end LOOP
 
//************** UTILITIES FOR AREA AND CENTER OF MASS CALCULATIONS *****************
public float trapezeArea(pt A, pt B) {return((B.x-A.x)*(B.y+A.y)/2.f);}
public pt trapezeCenter(pt A, pt B) { return(new pt(A.x+(B.x-A.x)*(A.y+2*B.y)/(A.y+B.y)/3.f, (A.y*A.y+A.y*B.y+B.y*B.y)/(A.y+B.y)/3.f) ); }


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
 public void declare() {c=0; sc=0; prevc=0;
   for (int i=0; i<maxnv; i++) {G[i]=new pt(0,0); Nv[i]=new vec(0,0);};   // init vertices and normals
   for (int i=0; i<maxnt; i++) {Nt[i]=new vec(0,0); visible[i]=true;} ;}       // init triangle normals and skeleton lab els
 public void init() {c=0; prevc=0; sc=0; nv=0; nt=0; nc=0;  for (int i=0; i<maxnt; i++) visible[i]=true;} // init counts and visibility flags
 public void makeGrid (int w) { // make a 2D grid of vertices
  for (int i=0; i<w; i++) {for (int j=0; j<w; j++) { G[w*i+j].setTo(height*.8f*j/(w-1)+height/10,height*.8f*i/(w-1)+height/10);}}    
  for (int i=0; i<w-1; i++) {for (int j=0; j<w-1; j++) {                  // define the triangles for the grid
    V[(i*(w-1)+j)*6]=i*w+j;       V[(i*(w-1)+j)*6+2]=(i+1)*w+j;       V[(i*(w-1)+j)*6+1]=(i+1)*w+j+1;
    V[(i*(w-1)+j)*6+3]=i*w+j;     V[(i*(w-1)+j)*6+5]=(i+1)*w+j+1;     V[(i*(w-1)+j)*6+4]=i*w+j+1;}; };
  nv = w*w;
  nt = 2*(w-1)*(w-1); 
  nc=3*nt;  }
 public void update() {computeO(); computeValence(); }
  // ============================================= CORNER OPERATORS =======================================
 int nc = 0;                                // current number of corners (3 per triangle)
 int c = 0;                                 // current corner shown in image and manipulated with keys: n, p, o, l, r
 int sc=0;                                  // saved value of c
 int[] V = new int [3*maxnt];               // V table (triangle/vertex indices)
 int[] O = new int [3*maxnt];               // O table (opposite corner indices)
 int[] Tc = new int[3*maxnt];               // corner type used for some applications
 float barycentric[][] = new float[3*maxnt][3];


// operations on an arbitrary corner
public int t (int c) {return PApplet.parseInt(c/3);};          // triangle of corner    
public int n (int c) {return 3*t(c)+(c+1)%3;};   // next corner in the same t(c)    
public int p (int c) {return n(n(c));};  // previous corner in the same t(c)  
public int v (int c) {return V[c] ;};   // id of the vertex of c             
public pt g (int c) {return G[v(c)];};  // shortcut to get the location of the vertex v(c) of corner c
public boolean b (int c) {return O[c]==-1;};       // if c faces a border (has no opposite)  ********************** change this to be c and not -1
public int o (int c) {if (b(c)) return c; else return O[c];}; // opposite (or self if it has no opposite)
public int l (int c) {return o(n(c));}; // left neighbor (or next if n(c) has no opposite)                      
public int r (int c) {return o(p(c));}; // right neighbor (or previous if p(c) has no opposite)                    
public int s (int c) {return n(l(c));}; // swings around v(c) or around a border loop

// operations on the selected corner c
public int t() {return t(c);}
public int n() {return n(c);}
public int p() {return p(c);}
 public int v() {return v(c);}
public int o() {return o(c);}
public boolean b() {return b(c);}             // border: returns true if corner has no opposite
public int l() {return l(c);}
public int r() {return r(c);}
public int s() {return s(c);}
public pt g() {return g(c);}            // shortcut to get the point of the vertex v(c) of corner c

// normals and mid-edge verex Ids for subdivision

public vec Nv (int c) {return(Nv[V[c]]);}; public vec Nv() {return Nv(c);}            // shortcut to get the normal of v(c) 
public vec Nt (int c) {return(Nt[t(c)]);}; public vec Nt() {return Nt(c);}            // shortcut to get the normal of t(c) 
public int w (int c) {return(W[c]);};               // temporary indices to mid-edge vertices associated with corners during subdivision
  
public void previous() {c=p(c);};
public void next() {c=n(c);};
public void opposite() {if(!b(c)) {c=o(c);};};
public void left() {next(); opposite();};
public void right() {previous(); opposite();};
public void swing() {left(); next(); };

public void writeCorner (int c) {println("c="+c+", n="+n(c)+", p="+p(c)+", o="+o(c)+", v="+v(c)+", t="+t(c)+"."+", nt="+nt+", nv="+nv ); }; 
public void writeCorner () {writeCorner (c);}
public void writeCorners () {for (int c=0; c<nc; c++) {println("T["+c+"]="+t(c)+", visible="+visible[t(c)]+", v="+v(c)+",  o="+o(c));};}

public pt cg(int c) {pt cPt = A(g(c),A(g(c),triCenter(t(c))));  return(cPt); };   // computes point at corner
public pt corner(int c) {return A(g(c),A(g(c),triCenter(t(c))));   };   // returns corner point
public void showCorner(int c, float r) {corner(c).show(r); };   // renders corner c as small ball

// ============================================= O TABLE CONSTRUCTION =========================================
public void computeOnaive() {                         // sets the O table from the V table, assumes consistent orientation of triangles
  for (int i=0; i<3*nt; i++) {O[i]=-1;};  // init O table to -1: has no opposite (i.e. is a border corner)
  for (int i=0; i<nc; i++) {  for (int j=i+1; j<nc; j++) {       // for each corner i, for each other corner j
      if( (v(n(i))==v(p(j))) && (v(p(i))==v(n(j))) ) {O[i]=j; O[j]=i;};};};}// make i and j opposite if they match         

public void computeO() { 
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
public void computeBox() {
  pt Lbox =  G[0].make();  pt Hbox =  G[0].make();
  for (int i=1; i<nv; i++) { 
    Lbox.x=min(Lbox.x,G[i].x); Lbox.y=min(Lbox.y,G[i].y); 
    Hbox.x=max(Hbox.x,G[i].x); Hbox.y=max(Hbox.y,G[i].y); 
    };
  Cbox.setTo(A(Lbox,Hbox));  Rbox=Cbox.disTo(Hbox); 
  };
public void showMesh() {
  int col=60;
  //stroke(green); strokeWeight(8); showBorder(); strokeWeight(1); // shows border as fat cyan curve
  noSmooth(); noStroke();
  if(showTriangles) showTriangles();  
  //if (showEdges) {stroke(dblue); strokeWeight(1); for(int i=0; i<nc; i++) drawEdge(i); };  
  if (showSelectedTriangle) {noStroke(); fill(dgreen); shade(t(c)); noFill(); }; 
  if (showVertices) {noStroke(); noSmooth();fill(white); for (int v=0; v<nv; v++)  G[v].show(rr); noFill();};
  if (showLabels) { fill(black); 
      for (int i=0; i<nv; i++) {label(G[i],labelD,"v"+str(i)); }; 
      for (int i=0; i<nc; i++) {label(triCenter(i),labelD,"t"+str(i)); }; noFill();};
  noStroke(); fill(dred); showCorner(prevc,1.5f*rr); fill(red); showCorner(c,PApplet.parseInt(rr));  
  }
//  ==========================================================  EDGES ===========================================
boolean showEdges=true;
public void findShortestEdge() {c=cornerOfShortestEdge();  } 
public int cornerOfShortestEdge() {  // assumes manifold
  float md=d(g(p(0)),g(n(0))); int ma=0;
  for (int a=1; a<nc; a++) if (vis(a)&&(d(g(p(a)),g(n(a)))<md)) {ma=a; md=d(g(p(a)),g(n(a)));}; 
  return ma;
  } 
public void drawEdge(int c) {show(g(p(c)),g(n(c))); };  // draws edge of t(c) opposite to corner c
public void showBorderOfVisible() {for (int i=0; i<nc; i++) {if (visible[t(i)]) { if(b(i)) drawEdge(i); else if(!visible[t(o(i))]) drawEdge(i);}; }; };          // draws all border edges
public void showBorder() {for (int i=0; i<nc; i++) {if (b(i)) {drawEdge(i);}; }; };         // draws all border edges

//  ==========================================================  TRIANGLES ===========================================
 boolean showTriangles=true;
 boolean showSelectedTriangle=true;
 int nt = 0;                   // current number of triangles
 public void addTriangle(int i, int j, int k) {V[nc++]=i; V[nc++]=j; V[nc++]=k; visible[nt++]=true;}
 public void addTriangle(int i, int j, int k, boolean visibleTirangle) {V[nc++]=i; V[nc++]=j; V[nc++]=k; visible[nt++]=visibleTirangle;}
 boolean[] visible = new boolean[maxnt];    // set if triangle visible
 boolean[] outside = new boolean[maxnt];    // set if triangle outside loop
 boolean[] stabbed = new boolean[maxnt];    // set if triangle stabbed by loop
 float[] prev_area = new float[maxnt];
 public boolean vis(int c) {return visible[t(c)]; };   // true if triangle of c is visible
 int[] Mt = new int[maxnt];                 // triangle markers for distance and other things   
 boolean [] VisitedT = new boolean [maxnt];  // triangle visited
 public pt triCenter(int i) {return A( G[V[3*i]], G[V[3*i+1]], G[V[3*i+2]] ) ;}  // computes center of triangle t(i) 
 public void writeTri (int i) {println("T"+i+": V = ("+V[3*i]+":"+v(o(3*i))+","+V[3*i+1]+":"+v(o(3*i+1))+","+V[3*i+2]+":"+v(o(3*i+2))+")"); };
 public void shade(int t) {show(g(3*t),g(3*t+1),g(3*t+2));}; // shade tris
 public void showTriangles() {
   for(int t=0; t<nt; t++) {
     if(outside[t] && stabbed[t])
       fill(orange);
     else if(stabbed[t])
       fill(yellow); 
     else if(outside[t])
       fill(lred);
     else
       fill(lgreen);
       
     pt tA = g(t);
     pt tB = g(t+1);
     pt tC = g(t+2);
     float tkArea = abs(area(tA,tB,tC));
     if(prev_area[t] == 0.0f) {
       fill(lgreen);
     } else {
       if(abs(tkArea - prev_area[t]) < 0.001f) {
         fill(lgreen);
       } else if (tkArea > prev_area[t]) {
         fill(lblue);
       } else if (tkArea < prev_area[t]) {
         fill(lred);
       }
     }
     prev_area[t] = tkArea;
     
     if(visible[t])
       show(g(3*t),g(3*t+1),g(3*t+2),-2);
   };
   noFill();
 }; 
 // CHANGED FOR PROJECT 1
 boolean redoDelaunay;
 pt[] newPoints = new pt[3*maxnt];
 int[] newIndex = new int[3*maxnt];
 int numNewPoints;
 public void classifyTriangles(LOOP C) {
   redoDelaunay = false;
   numNewPoints = 0;

   for(int t=0; t<nt; t++) {
     outside[t] = false;
     // If the center of mass isn't inside the polygon then there is no way the triangle is contained in the loop
     if(!C.contains(triCenter(t))) {
       outside[t] = true;
     } 
     // Check whether any of the edges intersect any edges along the loop
     // If they do set the triangle stabbed for coloring
     // Test each edge
     pt testPt1, testPt2;
     testPt1 = G[V[3*t]];
     testPt2 = G[V[3*t+1]];
     stabbed[t] = testAndAdd(testPt1, testPt2);
     // Next edge
     testPt1 = G[V[3*t+1]];
     testPt2 = G[V[3*t+2]];
     stabbed[t] = stabbed[t] || testAndAdd(testPt1, testPt2);
     // Last edge
     testPt1 = G[V[3*t+2]];
     testPt2 = G[V[3*t]];
     stabbed[t] = stabbed[t] || testAndAdd(testPt1, testPt2);
     visible[t] = !(outside[t] || stabbed[t]);
   }
 }
 
 public boolean testAndAdd(pt testPt1, pt testPt2) {
   //Stabbed returns -1 for no intersection and the index into P for intersection
   int test = C.stabbed(testPt1, testPt2);
   if(test != -1) {
     // Since each edge is part of two triangles we have to make sure to only add the intersection once
     boolean addedAlready = false;
     pt intersection = linesIntersection(testPt1, testPt2, C.P[test], C.P[n(test)]);
     for(int k = 0; k < numNewPoints; k++) {
       if(abs(intersection.x - newPoints[k].x) < 0.01f && 
          abs(intersection.y - newPoints[k].y) < 0.01f) {
         addedAlready = true;
      }
     }
     if(!addedAlready) {
       newPoints[numNewPoints] = intersection;
       newIndex[numNewPoints] = test;
       numNewPoints++;
       //redoDelaunay = true;
       //testAndAdd(testPt1, intersection);
       //testAndAdd(intersection, testPt2);
     }
     // Return true if we were stabbed
     return true;
   }
   return false;
 }

//  ==========================================================  VERTICES ===========================================
 boolean showVertices=true;//WAS FALSE
 int nv = 0;                              // current  number of vertices
 pt[] G = new pt [maxnv];                   // geometry table (vertices)
 pt[] G2 = new pt [maxnv]; //2008-03-06 JJ misc
 int[] Mv = new int[maxnv];                  // vertex markers
 int [] Valence = new int [maxnv];          // vertex valence (count of incident triangles)
 boolean [] Border = new boolean [maxnv];   // vertex is border
 boolean [] VisitedV = new boolean [maxnv];  // vertex visited
 int r=5;                                // radius of spheres for displaying vertices
public int addVertex(pt P) { G[nv].setTo(P); nv++; return nv-1;};
public int addVertex(float x, float y) { G[nv++]=P(x,y); return nv-1;};
public void move(int c) {g(c).addScaled(pmouseY-mouseY,Nv(c));}
public void move(int c, float d) {g(c).addScaled(d,Nv(c));}
public void move() {move(c); }
public void moveROI() {
     pt Q = new pt(0,0);
     for (int i=0; i<nv; i++) Mv[i]=0;  // resets the valences to 0
     computeDistance(5);
     for (int i=0; i<nv; i++) VisitedV[i]=false;  // resets the valences to 0
     for (int i=0; i<nc; i++) if(!VisitedV[v(i)]&&(Mv[v(i)]!=0)) move(i,1.f*(pmouseY-mouseY+mouseX-pmouseX)*(rings-Mv[v(i)])/rings/10);  // moves ROI
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
public void computeValence() {      // caches valence of each vertex
  for (int i=0; i<nv; i++) Valence[i]=0;  // resets the valences to 0
  for (int i=0; i<nc; i++) Valence[v(i)]++; 
  }

public void clearNormals() {      // caches valence of each vertex
  for (int i=0; i<nv; i++) Nv[i]=V(0,0);  // resets the valences to 0
  }

public void computeLaplaceVectors() {  // computes the vertex normals as sums of the normal vectors of incident tirangles scaled by area/2
  computeValence(); clearNormals();
  for (int i=0; i<nc; i++) {Nv[v(p(i))].add( V( g(i), SpiekerCenter(g(i),g(p(i)),g(n(i))) ) );};  // ***** fix to be proportional to area
  for (int i=0; i<nv; i++) {Nv[i].div(Valence[i]);};                         };
public void tuck(float s) {for (int i=0; i<nv; i++) {G[i].addScaled(s,Nv[i]);}; };  // displaces each vertex by a fraction s of its normal
public void smoothen() {computeLaplaceVectors(); tuck(0.6f); computeLaplaceVectors(); tuck(-0.6f);};
public void tuckROI(float s) {for (int i=0; i<nv; i++) if (Mv[i]!=0) G[i].addScaled(s,Nv[i]); };  // displaces each vertex by a fraction s of its normal
public void smoothROI() {computeLaplaceVectors(); tuckROI(0.5f); computeLaplaceVectors(); tuckROI(-0.5f);};
// ============================================================= SUBDIVISION ============================================================
int[] W = new int [3*maxnt];               // mid-edge vertex indices for subdivision (associated with corner opposite to edge)
public void splitEdges() {            // creates a new vertex for each edge and stores its ID in the W of the corner (and of its opposite if any)
  for (int i=0; i<3*nt; i++) {  // for each corner i
    if(b(i)) {G[nv]=A(g(n(i)),g(p(i))); W[i]=nv++;}
    else {if(i<o(i)) {G[nv]=A(g(n(i)),g(p(i))); W[o(i)]=nv; W[i]=nv++; }; }; }; } // if this corner is the first to see the edge
  
public void bulge() {              // tweaks the new mid-edge vertices according to the Butterfly mask
  for (int i=0; i<3*nt; i++) {
    if((!b(i))&&(i<o(i))) {    // no tweak for mid-vertices of border edges
     if (!b(p(i))&&!b(n(i))&&!b(p(o(i)))&&!b(n(o(i))))
      {G[W[i]].addScaled(0.25f,A(A(g(l(i)),g(r(i))),A(g(l(o(i))),g(r(o(i))))).vecTo(A(g(i),g(o(i))))); }; }; }; };
  
public void splitTriangles() {    // splits each tirangle into 4
  for (int i=0; i<3*nt; i=i+3) {
    V[3*nt+i]=v(i); V[n(3*nt+i)]=w(p(i)); V[p(3*nt+i)]=w(n(i));
    V[6*nt+i]=v(n(i)); V[n(6*nt+i)]=w(i); V[p(6*nt+i)]=w(p(i));
    V[9*nt+i]=v(p(i)); V[n(9*nt+i)]=w(n(i)); V[p(9*nt+i)]=w(i);
    V[i]=w(i); V[n(i)]=w(n(i)); V[p(i)]=w(p(i));
    };
  nt=4*nt; nc=3*nt;  };
  
public void refine() {update(); splitEdges(); bulge(); splitTriangles(); update(); }
  
//  ========================================================== FILL HOLES ===========================================
public void fanHoles() {for (int cc=0; cc<nc; cc++) if (visible[t(cc)]&&b(cc)) fanThisHole(cc);  }
public void fanThisHole() {fanThisHole(c);}
public void fanThisHole(int cc) {   // fill shole with triangle fan (around average of parallelogram predictors). Must then call computeO to restore O table
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
 G[nv].scale(1.f/n); // divide fan tip to make it the average of all predictions
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

public void computeDistance(int maxr) {
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
  
public void computeIsolation() {
  println("Starting isolation computation for "+nt+" triangles");
  for(int i=0; i<nt; i++) {SMt[i]=0;}; 
  for(c=0; c<nc; c+=3) {println("  triangle "+t(c)+"/"+nt); computeDistance(1000); for(int j=0; j<nt; j++) {SMt[j]+=Mt[j];}; };
  int L=SMt[0], H=SMt[0];  for(int i=0; i<nt; i++) { H=max(H,SMt[i]); L=min(L,SMt[i]);}; if (H==L) {H++;};
  c=0; for(int i=0; i<nt; i++) {Mt[i]=(SMt[i]-L)*255/(H-L); if(Mt[i]>Mt[t(c)]) {c=3*i;};}; rings=255;
  for(int i=0; i<nv; i++) {Mv[i]=0;};  for(int i=0; i<nc; i++) {Mv[v(i)]=max(Mv[v(i)],Mt[t(i)]);};
  println("finished isolation");
  }
  
public void computePath() {                 // graph based shortest path between t(c0 and t(prevc), prevc is the previously picekd corner
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
 public void  showDistance() { for(int t=0; t<nt; t++) {if(Mt[t]==0) fill(cyan); else fill(255*Mt[t]/rings); shade(t);}; } 

//  ==========================================================  DELETE ===========================================
public void hideROI() { for(int i=0; i<nt; i++) if(Mt[i]>0) visible[i]=false; }

//  ==========================================================  GARBAGE COLLECTION ===========================================
public void clean() {excludeInvisibleTriangles();  compactVO(); compactV();}  // removes deleted triangles and unused vertices
public void excludeInvisibleTriangles () {for (int b=0; b<nc; b++) {if (!visible[t(o(b))]) {O[b]=-1;};};}
public void compactVO() {  
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

public void compactV() {  
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

public void saveMesh() {
  String [] inppts = new String [nv+1+nt+1];
  int s=0;
  inppts[s++]=str(nv);
  for (int i=0; i<nv; i++) {inppts[s++]=str(G[i].x)+","+str(G[i].y);};
  inppts[s++]=str(nt);
  if (flipOrientation) {for (int i=0; i<nt; i++) {inppts[s++]=str(V[3*i])+","+str(V[3*i+2])+","+str(V[3*i+1]);};}
    else {for (int i=0; i<nt; i++) {inppts[s++]=str(V[3*i])+","+str(V[3*i+1])+","+str(V[3*i+2]);};};
  saveStrings("mesh.vts",inppts);  println("saved on file");
  };

public void loadMesh() {
  println("loading fn["+fni+"]: "+fn[fni]); 
  String [] ss = loadStrings(fn[fni]);
  String subpts;
  int s=0;   int comma1, comma2;   float x, y, z;   int a, b, c;
  nv = PApplet.parseInt(ss[s++]);
    print("nv="+nv);
    for(int k=0; k<nv; k++) {int i=k+s; 
      comma1=ss[i].indexOf(',');   
      x=PApplet.parseFloat(ss[i].substring(0, comma1));
      String rest = ss[i].substring(comma1+1);
      y=PApplet.parseFloat(ss[i].substring(comma1+1));
      G[k].setTo(x,y);
    };
  s=nv+1;
  nt = PApplet.parseInt(ss[s]); nc=3*nt;
  println(", nt="+nt);
  s++;
  for(int k=0; k<nt; k++) {int i=k+s;
      comma1=ss[i].indexOf(',');   a=PApplet.parseInt(ss[i].substring(0, comma1));  
      String rest = ss[i].substring(comma1+1, ss[i].length()); comma2=rest.indexOf(',');  
      b=PApplet.parseInt(rest.substring(0, comma2)); c=PApplet.parseInt(rest.substring(comma2+1, rest.length()));
      V[3*k]=a;  V[3*k+1]=b;  V[3*k+2]=c;
    }
  }; 



//  ==========================================================  FLIP ===========================================
public void flipWhenLonger() {for (int c=0; c<nc; c++) if (d(g(n(c)),g(p(c)))>d(g(c),g(o(c)))) flip(c); } 
public void flip() {flip(c);}
public void flip(int c) {      // flip edge opposite to corner c, FIX border cases
  if (b(c)) return;
    V[n(o(c))]=v(c); V[n(c)]=v(o(c));
    int co=o(c); O[co]=r(c); if(!b(p(c))) O[r(c)]=co; if(!b(p(co))) O[c]=r(co); if(!b(p(co))) O[r(co)]=c; O[p(c)]=p(co); O[p(co)]=p(c);  }
 
//  ==========================================================  SIMPLIFICATION  ===========================================
public void collapse() {collapse(c);}
public void collapse(int c) {if (b(c)) return;      // collapse edge opposite to corner c, does not check anything !!! assumes manifold
   int b=n(c), oc=o(c), vpc=v(p(c));
   visible[t(c)]=false; visible[t(oc)]=false;
   for (int a=b; a!=p(oc); a=n(l(a))) V[a]=vpc;
   O[l(c)]=r(c); O[r(c)]=l(c); O[l(oc)]=r(oc); O[r(oc)]=l(oc);  }

// ============================================================= corner stack ============================================================
 int stack[] = new int[10000];
 int stackHeight=1;
 public int pop() {if (stackHeight==0){ println("Stack is empty"); stackHeight=1;}; return(stack[--stackHeight]);}
 public void push(int c) {stack[stackHeight++]=c; }
 public void resetStack() {stackHeight=1;};
 
   //Compute the barycentric coords of each corner with respect to its opposite triangle and fills the
   public void computeBarycentric()
   {
      //For each corner in the mesh
      for(int c=0; c<nc; c++)
      {
         //Only do the calculations if there is an opposite
         if(o(c)!=c)
         {
           //Let vi be the vertex
           pt vi = g(c);
           //Let tk be the opposite triangle
           int tk = t(o(c));
           pt tkA = g(tk);
           pt tkB = g(tk+1);
           pt tkC = g(tk+2);
           //Compute the barycentric coordinates w/respect to the opposite triangle
           float tkArea = abs(area(tkA,tkB,tkC));
           if(abs(tkArea) < 0.001f) //If we leave it without this check, points may end up with Infinity, -Infinity, and NaN for coordinates.
           {
             barycentric[c][0] = 0.0f;
             barycentric[c][1] = 0.0f;
             barycentric[c][2] = 0.0f;
           }
           else
           {
             barycentric[c][0] = abs(area(vi,tkB,tkC))/tkArea;
             barycentric[c][1] = abs(area(tkA,vi,tkC))/tkArea;
             barycentric[c][2] = abs(area(tkA,tkB,vi))/tkArea;
           }
         }
         else
         {
           barycentric[c][0] = 0.0f;
           barycentric[c][1] = 0.0f;
           barycentric[c][2] = 0.0f;
         }
         //println(barycentric[c][0]+" "+barycentric[c][1]+" "+" "+barycentric[c][2]);
       }
    }
    
    public void updateBarycentric(LOOP C)
    {
      // Hard code this size to 100 for now, should be enough
      // This will store all corners whose barycentric coordinates we need to take into account in updating a point
      int[] validCorners = new int[100];
      boolean[] updatedPoints = new boolean[100];
      int numVC = 0;
      // For every corner move the vertex halfway to its average barycentric center
      for(int c=0; c<nc; c++)
      {
        // If we have an opposite
        if(o(c)!=c) {
          validCorners[numVC++] = c;
          // Update its barycentric coordinates according to Method 3
          for(int c2=0; c2<nc; c2++) {
            // If this corner has the same point as the corner we're at and it has an opposite
            if(v(c2) == v(c) && o(c2) != c2 && c != c2) {
              // Then we need to take into account o(c2)'s barycentric coords in the calculations
              validCorners[numVC++] = c2;
            }
          }
          // Make sure we don't hit the same vertex more than once
          if(!updatedPoints[v(c)]) {
            updatedPoints[v(c)] = true;
            // This will hold the final vector between the old point and the averaged target
            vec finalVec = V(0,0);
            pt oldPt = g(c);
            vec[] allVecs = new vec[numVC];
            int realNumVC = 0;
            for (int i = 0; i < numVC; i++) {
              // Compute the new X,Y point from the barycentric coords
              int tk = t(validCorners[i]);
              pt tkA = g(tk);
              pt tkB = g(tk+1);
              pt tkC = g(tk+2);
              float targetX = barycentric[validCorners[i]][0]*tkA.x +
                              barycentric[validCorners[i]][1]*tkB.x +
                              barycentric[validCorners[i]][2]*tkC.x;
              float targetY = barycentric[validCorners[i]][0]*tkA.y +
                              barycentric[validCorners[i]][1]*tkB.y +
                              barycentric[validCorners[i]][2]*tkC.y;
              if(targetX == 0.0f) {
                targetX = oldPt.x;
              } else if(targetY == 0.0f) {
                targetY = oldPt.y;
              } else {
                pt target = new pt(targetX,targetY);
                allVecs[realNumVC++] = V(oldPt, target);
              }
            }
            for(int i=0; i<realNumVC; i++) {
              finalVec = S(finalVec, 1.0f/realNumVC, allVecs[i]);
            }
            finalVec = S(0.5f, finalVec);
            // Move the point halfway towards its barycentric center (in the loop and the mesh)
            C.P[v(c)] = T(C.P[v(c)], finalVec);
            G[v(c)] = T(G[v(c)], finalVec);
          }
          numVC = 0;
        }
      }
      // For every corner move the vertex halfway to its average barycentric center
      for(int c=0; c<nc; c++)
      {
        // If we have an opposite
        if(o(c)!=c) {
          validCorners[numVC++] = c;
          // Update its barycentric coordinates according to Method 3
          for(int c2=0; c2<nc; c2++) {
            // If this corner has the same point as the corner we're at and it has an opposite
            if(v(c2) == v(c) && o(c2) != c2 && c != c2) {
              // Then we need to take into account o(c2)'s barycentric coords in the calculations
              validCorners[numVC++] = c2;
            }
          }
          // Make sure we don't hit the same vertex more than once
          if(!updatedPoints[v(c)]) {
            updatedPoints[v(c)] = true;
            // This will hold the final vector between the old point and the averaged target
            vec finalVec = V(0,0);
            pt oldPt = g(c);
            vec[] allVecs = new vec[numVC];
            int realNumVC = 0;
            for (int i = 0; i < numVC; i++) {
              // Compute the new X,Y point from the barycentric coords
              int tk = t(validCorners[i]);
              pt tkA = g(tk);
              pt tkB = g(tk+1);
              pt tkC = g(tk+2);
              float targetX = barycentric[validCorners[i]][0]*tkA.x +
                              barycentric[validCorners[i]][1]*tkB.x +
                              barycentric[validCorners[i]][2]*tkC.x;
              float targetY = barycentric[validCorners[i]][0]*tkA.y +
                              barycentric[validCorners[i]][1]*tkB.y +
                              barycentric[validCorners[i]][2]*tkC.y;
              if(targetX == 0.0f) {
                targetX = oldPt.x;
              } else if(targetY == 0.0f) {
                targetY = oldPt.y;
              } else {
                pt target = new pt(targetX,targetY);
                allVecs[realNumVC++] = V(oldPt, target);
              }
            }
            for(int i=0; i<realNumVC; i++) {
              finalVec = S(finalVec, 1.0f/realNumVC, allVecs[i]);
            }
            finalVec = S(-0.5f, finalVec);
            // Move the point halfway towards its barycentric center (in the loop and the mesh)
            C.P[v(c)] = T(C.P[v(c)], finalVec);
            G[v(c)] = T(G[v(c)], finalVec);
          }
          numVC = 0;
        }
      }
    }

  } // ==== END OF MESH CLASS
  
public float log2(float x) {float r=0; if (x>0.00001f) { r=log(x) / log(2);} ; return(r);}
vec labelD=new vec(-4,+4);           // offset vector for drawing labels
int mnpt=20000;
pt G[] = new pt [mnpt];
int nv=0;
int Vm[] = new int[3*mnpt];
int E[][] = new int[mnpt][2];
boolean gE[]= new boolean[mnpt];
int ne=0;
int nt=0;
int nc=0;
public void init3D() {for (int i=0; i<mnpt; i++) G[i]=new pt(0,0); }
public void reset3D() {nv=0; nt=0; nv=0; nc=0;}
public int addvertex(pt P) { G[nv].x=P.x; G[nv].y=P.y; nv++; return nv-1;};
public void addTriangle(int i, int j, int k) {Vm[nc++]=i; Vm[nc++]=j; Vm[nc++]=k;nt=nc/3; }
public void triangulate() { // triangulates trimmed loops
   for (int e=0; e<ne; e++) if(gE[e]) { 
     for (int j=0; j<nv; j++)  { 
        if (cw(G[E[e][0]],G[E[e][1]],G[j])) {
            pt CC=CircumCenter(G[E[e][0]],G[E[e][1]],G[j]); 
            float r=G[j].disTo(CC);
            boolean found=false;
            for (int k=0; k<nv; k++) 
                if ((cw(G[E[e][0]],G[E[e][1]],G[k]))&&(G[k].disTo(CC)+0.1f<r)) found=true;
            if (!found) addTriangle(E[e][0],E[e][1],j);
            };    };     };    
     }
            
public void show3DVertices() {for (int i=0; i<nv; i++) G[i].show(2); }
public void showTriangles() { noStroke(); fill(200,200,200);  for (int c=0; c<nt*3; c+=3) 
  { beginShape(); G[Vm[c]].v(); G[Vm[c+1]].v(); G[Vm[c+2]].v();  endShape(CLOSE);};}


// EDGES
public void removeOppositePairs() {for (int i=0; i<ne; i++) if(gE[i]) for (int j=0; j<ne; j++) if(gE[j]) if ((E[i][0]==E[j][1])&&(E[i][1]==E[j][0])) {gE[i]=false; gE[j]=false; }}
public void addEdge(int s, int t) {E[ne][0]=s;  E[ne][1]=t; gE[ne]=true; ne++; };
public void addEdge(pt P, pt Q) {
  int s=0; float d=10000; for(int i=0; i<nv; i++) if (P.disTo(G[i])<d) {d=P.disTo(G[i]); s=i;};
  int t=0;  d=10000; for(int i=0; i<nv; i++) if (Q.disTo(G[i])<d) {d=Q.disTo(G[i]); t=i;}; 
  E[ne][0]=s;  E[ne][1]=t; gE[ne]=true; ne++;
  };

// OTHER
//void add3Dvertex(pt P) { G[nv].x=P.x; G[nv].y=P.y; G[nv].z=H[int(P.x/h)][int(P.y/h)].z; nv++;};
//void add3Dvertex(pt P, float h) { G[nv].x=P.x; G[nv].y=P.y; G[nv].z=h; nv++;};
//*****************************************************************************
// TITLE:         UTILITIES  
// DESCRIPTION:   for GSB to click variables
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
// COUNTER
class COUNTER {  // utilities for tracking a value
  int value=0;
  public void reset() {value=0;};
  public int i() {value++; return value;} // increment
  public int d() {value++; return value;} // decrement
  }
  
// LOADING SVG IMAGE  
PShape bot;
public void loadSVG() {bot = loadShape("bot1.svg"); } // include in MYsetup. The file "bot1.svg" must be in the data folder
public void showSVG() { // include in MYdraw.
  float w=PApplet.parseFloat(width)/612; float h=PApplet.parseFloat(height)/792;
  if (w>h) shape(bot, floor((width-PApplet.parseInt(h/w*width))/2), 0, floor(h/w*width)-1, height-1); 
      else shape(bot, 0, floor((height-PApplet.parseInt(w/h*height))/2), width-1, floor(w/h*height)-1); 
  }
 
 
//*****************************************************************************
// TITLE:         POINT AND VECTOR UTILITIES OF THE GSB TEMPLATE  
// DESCRIPTION:   Classes and functions for manipulating points and vectors in the Geometry SandBox Geometry (GSB)  
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************

/************************* SHORTCUT FUNCTIONS FOR POINTS and VECTORS ********************************************
Names of points: A, B, C, D, P, Q; coordinates (P.x,P.y)
Names of vectors: U, V, W, coordinates <V.x,V.y>
Names of scalars: s, t, a, b, c, d, x, y
The names of functions that return Points or Vectors starts with a an uppercase letter

Create or copy points and vectors 
P(): make point (0,0)
P(x,y): make point (x,y)
P(A): make copy of point A
V(V): make copy of vector V
V(x,y): make vector <x,y>
V(P,Q): PQ (make vector Q-P from P to Q
U(P,Q): PQ/||PQ| (Unit vector : from P towards Q)

Render points and vectors
v(P): next point when drawing polygons between beginShape(); and endShape();
cross(P,r): shows P as cross of length r
cross(P): shows P as small cross
show(P,r): draws circle of center r around P
show(P): draws small circle around point
show(P,Q): draws edge (P,Q)
arrow(P,Q): draws arrow from P to Q
label(P,S): writes string S next to P on the screen ( for example label(P[i],str(i));)
label(P,V,S): writes string S at P+V
show(P,V): show V as line-segment from P 
show(P,s,V): show sV as line-segment from P 
arrow(P,V): show V as arrow from P 
arrow(P,s,V): show sV as arrow from P 
arrow(P,V,S): show V as arrow from P and print string S on its side

Transform: scale, rotate, translate, normalize
U(V): V/||V|| (Unit vector : normalized version of V)
R(V): V turned right 90 degrees (as seen on screen)
R(V,a): V rotated by a radians
S(s,V): sV
S(s,A): sA
S(s,A,B): B+sBA=(1+s)B-sA (scaling of A by s wrt fixed point B)
R(Q,a): Q rotated by angle a around the origin
R(Q,a,P): Q rotated by angle a around fixed point P (center of roatation)
T(P,V): P+V (P transalted by vector V)
T(P,s,V): P+sV (P translated by vector sV)
T(P,s,Q): P+sU(PQ) (translated P by absolute distance (not ratio) s towards Q)

Averages and linear interpolations of points
L(A,s,B): A+sAB (linear interpolation between points)
A(A,B): (A+B)/2 (average)
A(A,B,C): (A+B+C)/3 (average)
S(A,B): A+B
S(A,B,C): A+B+C
S(A,B,C,D): A+B+C+D
S(A,s,B): A+sB (used in summations of center of mass and in circle inversion)
S(a,A,b,B): aA+bB 
S(a,A,b,B,c,C): aA+bB+cC 
S(a,A,b,B,c,C,d,D): aA+bB+cC+dD (used in smoothing and subdivision)
S(U,V): U+V 
S(a,U,b,V): aU+bV (Linear combination)
S(U,s,V): U+sV
A(U,V): (U+V)/2 (average)
L(U,s,V): (1-s)U+sV (Linear interpolation between vectors)
R(U,s,V): interpolation (of angle and length) between U and V

Measures 
isSame(A,B): A==B (Boolean)
isSame(A,B,e): ||A-B||<e (Boolean)
d(A,B): ||AB|| (Distance)
d2(A,B): AB*AB (Distance squared)
dot(U,V): U*V (dot product U*V)
n(V): ||V|| (norm: length of V)
n2(V): V*V (norm squared)
parallel(U,V): U//V (Boolean) 
angle(V): angle between <1,0> and V (between -PI and PI)
angle(U,V): angle <U,V> (between -PI and PI)
angle(A,B,C): angle <BA,BC>
turnAngle(A,B,): angle <AB,BC> (positive when right turn as seen on screen)
toDeg(a): convert radians to degrees
toRad(a): convert degrees to radians 
positive(a): returns angle between 0 and 2PI (adds 2PI if a is negative)
cw(B,C): true if smallest angle turn from OB to OC is cloclwise (cw), where O=(0,0) (defined in TAB tri)
cw(A,B,C): true if A-B-C makes a clockwise turn at B (defined in TAB tri)

GUI mouse & canvas (left part [height,height] of window, assuming width>=height)
Mouse(): returns point at current mouse location
Pmouse(): returns point at previous mouse location
MouseDrag(): vector representing recent mouse displacement
ScreenCenter(): point in center of square canvas
mouseIsInWindow(): if mouse is in square canvas (Boolean)
MouseInWindow(): point in square canvas nearest to mouse (snapped to border if Mouse was out)

Often used methods that transform points or vectors
P.reset(): P=(0,0)
P.set(x,y): P=(x,y)
P.set(Q): P=Q (copy)
P.add(u,v): P+=<u,v>
P.add(V): P+=V
P.add(s,V): P+=sV
P.add(Q): P+=Q
P.scale(s): P*=s
P.scale(s,C): P=L(C,s,P);
P.rotate(a): rotate P around origin by angle a in radians
P.rotate(a,G): rotate P around G by angle a in radians

Intersections of edges or lines
edgesIntersect(A,B,C,D): if edge(A,B) intersects edge(C,D)
edgesIntersect(A,B,C,D,e): if edge(A,B) intersects edge(C,D)or touches it within distance e
pt linesIntersection(A,B,C,D): if line(A,B) intersects line(C,D)

*********************************************************************************************************************************************/

// create or copy points 
public pt P() {return P(0,0); };                                                                            // P(): make point (0,0)
public pt P(float x, float y) {return new pt(x,y); };                                                       // P(x,y): make point (x,y)
public pt P(pt P) {return P(P.x,P.y); };                                                                    // P(A): make copy of point A
// create vectors
public vec V(vec V) {return new vec(V.x,V.y); };                                                             // V(V): make copy of vector V
public vec V(float x, float y) {return new vec(x,y); };                                                      // V(x,y): make vector (x,y)
public vec V(pt P, pt Q) {return new vec(Q.x-P.x,Q.y-P.y);};                                                 // V(P,Q): PQ (make vector Q-P from P to Q

// render points
public void v(pt P) {vertex(P.x,P.y);};                                                                      // v(P): next point when drawing polygons between beginShape(); and endShape();
public void cross(pt P, float r) {line(P.x-r,P.y,P.x+r,P.y); line(P.x,P.y-r,P.x,P.y+r);};                    // cross(P,r): shows P as cross of length r
public void cross(pt P) {cross(P,2);};                                                                       // cross(P): shows P as small cross
public void show(pt P, float r) {ellipse(P.x, P.y, 2*r, 2*r);};                                              // show(P,r): draws circle of center r around P
public void show(pt P) {ellipse(P.x, P.y, 4,4);};                                                            // show(P): draws small circle around point
public void show(pt P, pt Q) {line(P.x,P.y,Q.x,Q.y); };                                                      // show(P,Q): draws edge (P,Q)
public void arrow(pt P, pt Q) {arrow(P,V(P,Q)); }                                                            // arrow(P,Q): draws arrow from P to Q
public void label(pt P, String S) {text(S, P.x-4,P.y+6.5f); }                                                   // label(P,S): writes string S next to P on the screen ( for example label(P[i],str(i));)
public void label(pt P, vec V, String S) {text(S, P.x-3.5f+V.x,P.y+7+V.y); }                                    // label(P,V,S): writes string S at P+V
// render vectors
public void show(pt P, vec V) {line(P.x,P.y,P.x+V.x,P.y+V.y); }                                              // show(P,V): show V as line-segment from P 
public void show(pt P, float s, vec V) {show(P,S(s,V));}                                                     // show(P,s,V): show sV as line-segment from P 
public void arrow(pt P, vec V) {show(P,V);  float n=n(V); if(n<0.01f) return; float s=max(min(0.2f,20.f/n),6.f/n);                  // arrow(P,V): show V as arrow from P 
     pt Q=T(P,V); vec U = S(-s,V); vec W = R(S(.3f,U)); beginShape(); v(T(T(Q,U),W)); v(Q); v(T(T(Q,U),-1,W)); endShape(CLOSE);}; 
public void arrow(pt P, float s, vec V) {arrow(P,S(s,V));}                                                   // arrow(P,s,V): show sV as arrow from P 
public void arrow(pt P, vec V, String S) {arrow(P,V); T(T(P,0.70f,V),15,R(U(V))).showLabel(S,V(-5,4));}      // arrow(P,V,S): show V as arrow from P and print string S on its side

// averages and linear interpolations of points
public pt L(pt A, float s, pt B) {return P(A.x+s*(B.x-A.x),A.y+s*(B.y-A.y)); };                             // L(A,s,B): A+sAB (linear interpolation between points)
public pt A(pt A, pt B) {return P((A.x+B.x)/2.0f,(A.y+B.y)/2.0f); };                                          // A(A,B): (A+B)/2 (average)
public pt A(pt A, pt B, pt C) {return P((A.x+B.x+C.x)/3.0f,(A.y+B.y+C.y)/3.0f); };                            // A(A,B,C): (A+B+C)/3 (average)
// weighted sums of points 
public pt S(pt A, pt B) {return new pt(A.x+B.x,A.y+B.y); };                                                 // S(A,B): A+B
public pt S(pt A, pt B, pt C) {return S(A,S(B,C)); };                                                       // S(A,B,C): A+B+C
public pt S(pt A, pt B, pt C, pt D) {return S(S(A,B),S(C,D)); };                                            // S(A,B,C,D): A+B+C+D
public pt S(pt A, float s, pt B) {return S(A,S(s,B)); };                                                    // S(A,s,B): A+sB (used in summations of center of mass and in circle inversion)
public pt S(float a, pt A, float b, pt B) {return S(S(a,A),S(b,B));}                                        // S(a,A,b,B): aA+bB 
public pt S(float a, pt A, float b, pt B, float c, pt C) {return S(S(a,A),S(b,B),S(c,C));}                  // S(a,A,b,B,c,C): aA+bB+cC 
public pt S(float a, pt A, float b, pt B, float c, pt C, float d, pt D){return A(S(a,A,b,B),S(c,C,d,D));}   // S(a,A,b,B,c,C,d,D): aA+bB+cC+dD (used in smoothing and subdivision)
// combinations of vectors
public vec S(vec U, vec V) {return new vec(U.x+V.x,U.y+V.y);}                                                // S(U,V): U+V 
public vec S(float s,vec V) {return new vec(s*V.x,s*V.y);};                                                  // S(s,V): sV
public vec S(float a, vec U, float b, vec V) {return S(S(a,U),S(b,V));}                                      // S(a,U,b,V): aU+bV )Linear combination)
public vec S(vec U,float s,vec V) {return new vec(U.x+s*V.x,U.y+s*V.y);};                                    // S(U,s,V): U+sV
public vec A(vec U, vec V) {return new vec((U.x+V.x)/2.0f,(U.y+V.y)/2.0f); };                                  // A(U,V): (U+V)/2 (average)
public vec L(vec U,float s,vec V) {return new vec(U.x+s*(V.x-U.x),U.y+s*(V.y-U.y));};                        // L(U,s,V): (1-s)U+sV (Linear interpolation between vectors)
public vec R(vec U, float s, vec V) {float a = angle(U,V); vec W = U.makeRotatedBy(s*a);                     // R(U,s,V): interpolation (of angle and length) between U and V
    float u = n(U); float v=n(V); S((u+s*(v-u))/u,W); return W ; };

// measure points (equality, distance)
public boolean isSame(pt A, pt B) {return (A.x==B.x)&&(A.y==B.y) ;}                                         // isSame(A,B): A==B
public boolean isSame(pt A, pt B, float e) {return ((abs(A.x-B.x)<e)&&(abs(A.y-B.y)<e));}                   // isSame(A,B,e): ||A-B||<e
public float d(pt P, pt Q) {return sqrt(d2(P,Q));  };                                                       // d(A,B): ||AB|| (Distance)
public float d2(pt P, pt Q) {return sq(Q.x-P.x)+sq(Q.y-P.y); };                                             // d2(A,B): AB*AB (Distance squared)
// measure vectors (dot product,  norm, parallel)
public float dot(vec U, vec V) {return U.x*V.x+U.y*V.y; };                                                    // dot(U,V): U*V (dot product U*V)
public float n(vec V) {return sqrt(dot(V,V));};                                                               // n(V): ||V|| (norm: length of V)
public float n2(vec V) {return sq(V.x)+sq(V.y);};                                                             // n2(V): V*V (norm squared)
public boolean parallel (vec U, vec V) {return dot(U,R(V))==0; }; 

// GUI mouse & canvas (left part [height,height] of window, assuming width>=height)
public pt Mouse() {return P(mouseX,mouseY);};                                                                 // Mouse(): returns point at current mouse location
public pt Pmouse() {return P(pmouseX,pmouseY);};                                                              // Pmouse(): returns point at previous mouse location
public vec MouseDrag() {return new vec(mouseX-pmouseX,mouseY-pmouseY);};                                      // MouseDrag(): vector representing recent mouse displacement
public pt MouseInWindow() {float x=mouseX, y=mouseY; x=max(x,0); y=max(y,0); x=min(x,height); y=min(y,height);  return P(x,y);}; // mouseInWindow(): nearest square canvas point to mouse 
public pt ScreenCenter() {return P(height/2,height/2);}                                                       // mouseInWindow(): point in center of square canvas
public boolean mouseIsInWindow() {return(((mouseX>0)&&(mouseX<height)&&(mouseY>0)&&(mouseY<height)));};       // mouseIsInWindow(): if mouse is in square canvas

// Scale, rotate, translate points
public pt S(float s, pt A) {return new pt(s*A.x,s*A.y); };                                                                      // S(s,A): sA
public pt S(float s, pt A, pt B) {return new pt((s+1)*B.x-s*A.x,(s+1)*B.y-s*A.y); };                                            // S(s,A,B): B+sBA=(1+s)B-sA (scaling of A by s wrt fixed point B)
public pt R(pt Q, float a) {float dx=Q.x, dy=Q.y, c=cos(a), s=sin(a); return new pt(c*dx+s*dy,-s*dx+c*dy); };                   // R(Q,a): Q rotated by angle a around the origin
public pt R(pt Q, float a, pt P) {float dx=Q.x-P.x, dy=Q.y-P.y, c=cos(a), s=sin(a); return P(P.x+c*dx-s*dy, P.y+s*dx+c*dy); };  // R(Q,a,P): Q rotated by angle a around point P
public pt T(pt P, vec V) {return P(P.x + V.x, P.y + V.y); }                                                 // T(P,V): P+V (P transalted by vector V)
public pt T(pt P, float s, vec V) {return T(P,S(s,V)); }                                                    // T(P,s,V): P+sV (P transalted by sV)
public pt T(pt P, float s, pt Q) { return T(P,s,U(V(P,Q))); };                                              // T(P,s,Q): P+sU(PQ) (transalted P by distance s towards Q)
// transform vectors
public vec U(vec V) {float n = n(V); if (n==0) return new vec(0,0); else return new vec(V.x/n,V.y/n);};      // U(V): V/||V|| (Unit vector : normalized version of V)
public vec U(pt P, pt Q) {return U(V(P,Q));};                                                                // U(P,Q): PQ/||PQ| (Unit vector : from P towards Q)
public vec R(vec V) {return new vec(-V.y,V.x);};                                                             // R(V): V turned right 90 degrees (as seen on screen)
public vec R(vec U, float a) {vec W = U.makeRotatedBy(a);  return W ; };                                     // R(V,a): V rotated by a radians

//************************************************************************
//**** ANGLES
//************************************************************************
public float angle (vec U, vec V) {return atan2(dot(R(U),V),dot(U,V)); };                                   // angle(U,V): angle <U,V> (between -PI and PI)
public float angle(vec V) {return(atan2(V.y,V.x)); };                                                       // angle(V): angle between <1,0> and V (between -PI and PI)
public float angle(pt A, pt B, pt C) {return  angle(V(B,A),V(B,C)); }                                       // angle(A,B,C): angle <BA,BC>
public float turnAngle(pt A, pt B, pt C) {return  angle(V(A,B),V(B,C)); }                                   // turnAngle(A,B,): angle <AB,BC> (positive when right turn as seen on screen)
public int toDeg(float a) {return PApplet.parseInt(a*180/PI);}                                                           // convert radians to degrees
public float toRad(float a) {return(a*PI/180);}                                                             // convert degrees to radians 
public float positive(float a) { if(a<0) return a+TWO_PI; else return a;}                                   // adds 2PI to make angle positive

//************************************************************************
//**** POINT CLASS
//************************************************************************
class pt { float x=0,y=0; 
  // CREATE
  pt () {}
  pt (float px, float py) {x = px; y = py;};
  pt (pt P) {x = P.x; y = P.y;};
  pt (pt P, vec V) {x = P.x+V.x; y = P.y+V.y;};
  pt (pt P, float s, vec V) {x = P.x+s*V.x; y = P.y+s*V.y;};
  pt (pt A, float s, pt B) {x = A.x+s*(B.x-A.x); y = A.y+s*(B.y-A.y);};

  // MODIFY
  public void reset() {x = 0; y = 0;}                                       // P.reset(): P=(0,0)
  public void set(float px, float py) {x = px; y = py;}                     // P.set(x,y): P=(x,y)
  public void set(pt Q) {x = Q.x; y = Q.y;}                                 // P.set(Q): P=Q (copy)
  public void add(float u, float v) {x += u; y += v;}                       // P.add(u,v): P+=<u,v>
  public void add(vec V) {x += V.x; y += V.y;}                              // P.add(V): P+=V
  public void add(float s, vec V) {x += s*V.x; y += s*V.y;}                 // P.add(s,V): P+=sV
  public void add(pt Q) {x += Q.x; y += Q.y;}                               // P.add(Q): P+=Q
  public void scale(float s) {x*=s; y*=s;}                                  // P.scale(s): P*=s
  public void scale(float s, pt C) {x*=C.x+s*(x-C.x); y*=C.y+s*(y-C.y);}    // P.scale(s,C): P=L(C,s,P);
  public void rotate(float a) {float dx=x, dy=y, c=cos(a), s=sin(a); x=c*dx+s*dy; y=-s*dx+c*dy; };     // P.rotate(a): rotate P around origin by angle a in radians
  public void rotate(float a, pt P) {float dx=x-P.x, dy=y-P.y, c=cos(a), s=sin(a); x=P.x+c*dx+s*dy; y=P.y-s*dx+c*dy; };   // P.rotate(a,G): rotate P around G by angle a in radians
 
  public void setTo(float px, float py) {x = px; y = py;};  
  public void setTo(pt P) {x = P.x; y = P.y;}; 
  public void setToMouse() { x = mouseX; y = mouseY; }; 
  public void moveWithMouse() { x += mouseX-pmouseX; y += mouseY-pmouseY; }; 
  public void addVec(vec V) {x += V.x; y += V.y;};   
  public void translateBy(vec V) {x += V.x; y += V.y;};   
  public void translateBy(float u, float v) {x += u; y += v;};
  public void translateBy(float s, vec V) {x += s*V.x; y += s*V.y;};  
  public void translateToTrack(float s, pt P) {setTo(T(P,s,V(P,this)));};       // translate by distance s towards P
  public void translateTowards(float s, pt P) {x+=s*(P.x-x);  y+=s*(P.y-y); };  // transalte by ratio s towards P
  public void translateTowardsBy(float s, pt P) {vec V = this.makeVecTo(P); V.normalize(); this.translateBy(s,V); };
  public void track(float s, pt P) {setTo(T(P,s,V(P,this)));};
  public void scaleBy(float f) {x*=f; y*=f;};
  public void scaleBy(float u, float v) {x*=u; y*=v;};
  public void addPt(pt P) {x += P.x; y += P.y;};        // incorrect notation, but useful for computing weighted averages
  public void addScaled(float s, vec V) {x += s*V.x; y += s*V.y;};  
  public void addScaled(float s, pt P)   {x += s*P.x; y += s*P.y;};   
  public void addScaledPt(float s, pt P) {x += s*P.x; y += s*P.y;};        // incorrect notation, but useful for computing weighted averages
  public void rotateBy(float a) {float dx=x, dy=y, c=cos(a), s=sin(a); x=c*dx+s*dy; y=-s*dx+c*dy; };     // around origin
  public void rotateBy(float a, pt P) {float dx=x-P.x, dy=y-P.y, c=cos(a), s=sin(a); x=P.x+c*dx+s*dy; y=P.y-s*dx+c*dy; };   // around point P
  public void rotateBy(float s, float t, pt P) {float dx=x-P.x, dy=y-P.y; dx-=dy*t; dy+=dx*s; dx-=dy*t; x=P.x+dx; y=P.y+dy; };   // s=sin(a); t=tan(a/2);
  public void clipToWindow() {x=max(x,0); y=max(y,0); x=min(x,height); y=min(y,height); }
  
  // OUTPUT POINT
  public pt clone() {return new pt(x,y); };
  public pt make() {return new pt(x,y); };
  public pt makeClone() {return new pt(x,y); };
  public pt makeTranslatedBy(vec V) {return(new pt(x + V.x, y + V.y));};
  public pt makeTranslatedBy(float s, vec V) {return(new pt(x + s*V.x, y + s*V.y));};
  public pt makeTransaltedTowards(float s, pt P) {return(new pt(x + s*(P.x-x), y + s*(P.y-y)));};
  public pt makeTranslatedBy(float u, float v) {return(new pt(x + u, y + v));};
  public pt makeRotatedBy(float a, pt P) {float dx=x-P.x, dy=y-P.y, c=cos(a), s=sin(a); return(new pt(P.x+c*dx+s*dy, P.y-s*dx+c*dy)); };
  public pt makeRotatedBy(float a) {float dx=x, dy=y, c=cos(a), s=sin(a); return(new pt(c*dx+s*dy, -s*dx+c*dy)); };
  public pt makeProjectionOnLine(pt P, pt Q) {float a=dot(P.makeVecTo(this),P.makeVecTo(Q)), b=dot(P.makeVecTo(Q),P.makeVecTo(Q)); return(P.makeTransaltedTowards(a/b,Q)); };
  public pt makeOffset(pt P, pt Q, float r) {
    float a = angle(vecTo(P),vecTo(Q))/2;
    float h = r/tan(a); 
    vec T = vecTo(P); T.normalize(); vec N = T.left();
    pt R = new pt(x,y); R.translateBy(h,T); R.translateBy(r,N);
    return R; };

   // OUTPUT VEC
  public vec vecTo(pt P) {return(new vec(P.x-x,P.y-y)); };
  public vec makeVecTo(pt P) {return(new vec(P.x-x,P.y-y)); };
  public vec makeVecToCenter () {return(new vec(x-height/2.f,y-height/2.f)); };
  public vec makeVecToAverage (pt P, pt Q) {return(new vec((P.x+Q.x)/2.0f-x,(P.y+Q.y)/2.0f-y)); };
  public vec makeVecToAverage (pt P, pt Q, pt R) {return(new vec((P.x+Q.x+R.x)/3.0f-x,(P.y+Q.y+R.x)/3.0f-y)); };
  public vec makeVecToMouse () {return(new vec(mouseX-x,mouseY-y)); };
  public vec makeVecToBisectProjection (pt P, pt Q) {float a=this.disTo(P), b=this.disTo(Q);  return(this.makeVecTo(L(P,a/(a+b),Q))); };
  public vec makeVecToNormalProjection (pt P, pt Q) {float a=dot(P.makeVecTo(this),P.makeVecTo(Q)), b=dot(P.makeVecTo(Q),P.makeVecTo(Q)); return(this.makeVecTo(L(P,a/b,Q))); };
  public vec makeVecTowards(pt P, float d) {vec V = makeVecTo(P); float n = V.norm(); V.normalize(); V.scaleBy(d-n); return V; };
 
  // OUTPUT TEST OR MEASURE
  public float disTo(pt P) {return(sqrt(sq(P.x-x)+sq(P.y-y))); };
  public float disToMouse() {return(sqrt(sq(x-mouseX)+sq(y-mouseY))); };
  public boolean isInWindow() {return(((x>0)&&(x<height)&&(y>0)&&(y<height)));};
  public boolean projectsBetween(pt P, pt Q) {float a=dot(P.makeVecTo(this),P.makeVecTo(Q)), b=dot(P.makeVecTo(Q),P.makeVecTo(Q)); return((0<a)&&(a<b)); };
  public float ratioOfProjectionBetween(pt P, pt Q) {float a=dot(P.makeVecTo(this),P.makeVecTo(Q)), b=dot(P.makeVecTo(Q),P.makeVecTo(Q)); return(a/b); };
  public float disToLine(pt P, pt Q) {float a=dot(P.makeVecTo(this),P.makeVecTo(Q).makeUnit().makeTurnedLeft()); return(abs(a)); };
  public boolean isLeftOf(pt P, pt Q) {boolean l=dot(P.makeVecTo(this),P.makeVecTo(Q).makeTurnedLeft())>0; return(l);  };
  public boolean isLeftOf(pt P, pt Q, float e) {boolean l=dot(P.makeVecTo(this),P.makeVecTo(Q).makeTurnedLeft())>e; return(l);  };  public boolean isInTriangle(pt A, pt B, pt C) { boolean a = this.isLeftOf(B,C); boolean b = this.isLeftOf(C,A); boolean c = this.isLeftOf(A,B); return((a&&b&&c)||(!a&&!b&&!c));};
  public boolean isInCircle(pt C, float r) {return d(this,C)<r; }  // returns true if point is in circle C,r
  
  // DRAW , PRINT
  public void show() {ellipse(x, y, height/200, height/200); }; // shows point as small dot
  public void show(float r) {ellipse(x, y, 2*r, 2*r); }; // shows point as disk of radius r
  public void showCross(float r) {line(x-r,y,x+r,y); line(x,y-r,x,y+r);}; 
  public void v() {vertex(x,y);};  // used for drawing polygons between beginShape(); and endShape();
  public void write() {print("("+x+","+y+")");};  // writes point coordinates in text window
  public void showLabel(String s, vec D) {text(s, x+D.x-5,y+D.y+4);  };  // show string displaced by vector D from point
  public void showLabel(String s) {text(s, x+5,y+4);  };
  public void showLabel(int i) {text(str(i), x+5,y+4);  };  // shows integer number next to point
  public void showLabel(String s, float u, float v) {text(s, x+u, y+v);  };
  public void showSegmentTo (pt P) {line(x,y,P.x,P.y); }; // draws edge to another point
  public void to (pt P) {line(x,y,P.x,P.y); }; // draws edge to another point

  } // end of pt class

//************************************************************************
//**** VECTORS
//************************************************************************
class vec { float x=0,y=0; 
 // CREATE
  vec () {};
  vec (vec V) {x = V.x; y = V.y;};
  vec (float s, vec V) {x = s*V.x; y = s*V.y;};
  vec (float px, float py) {x = px; y = py;};
  vec (pt P, pt Q) {x = Q.x-P.x; y = Q.y-P.y;};
 
 // MODIFY
  public void setTo(float px, float py) {x = px; y = py;}; 
  public void setTo(pt P, pt Q) {x = Q.x-P.x; y = Q.y-P.y;}; 
  public void setTo(vec V) {x = V.x; y = V.y;}; 
  public void scaleBy(float f) {x*=f; y*=f;};
  public void back() {x=-x; y=-y;};
  public void mul(float f) {x*=f; y*=f;};
  public void div(float f) {x/=f; y/=f;};
  public void scaleBy(float u, float v) {x*=u; y*=v;};
  public void normalize() {float n=sqrt(sq(x)+sq(y)); if (n>0.000001f) {x/=n; y/=n;};};
  public void add(vec V) {x += V.x; y += V.y;};   
  public void add(float s, vec V) {x += s*V.x; y += s*V.y;};   
  public void addScaled(float s, vec V) {x += s*V.x; y += s*V.y;};  
  public void add(float u, float v) {x += u; y += v;};
  public void turnLeft() {float w=x; x=-y; y=w;};
  public void rotateBy (float a) {float xx=x, yy=y; x=xx*cos(a)-yy*sin(a); y=xx*sin(a)+yy*cos(a); };
  
  // OUTPUT VEC
  public vec make() {return(new vec(x,y));}; 
  public vec clone() {return(new vec(x,y));}; 
  public vec makeClone() {return(new vec(x,y));}; 
  public vec makeUnit() {float n=sqrt(sq(x)+sq(y)); if (n<0.000001f) n=1; return(new vec(x/n,y/n));}; 
  public vec unit() {float n=sqrt(sq(x)+sq(y)); if (n<0.000001f) n=1; return(new vec(x/n,y/n));}; 
  public vec makeScaledBy(float s) {return(new vec(x*s, y*s));};
  public vec makeTurnedLeft() {return(new vec(-y,x));};
  public vec left() {return(new vec(-y,x));};
  public vec makeOffsetVec(vec V) {return(new vec(x + V.x, y + V.y));};
  public vec makeOffsetVec(float s, vec V) {return(new vec(x + s*V.x, y + s*V.y));};
  public vec makeOffsetVec(float u, float v) {return(new vec(x + u, y + v));};
  public vec makeRotatedBy(float a) {float c=cos(a), s=sin(a); return(new vec(x*c-y*s,x*s+y*c)); };
  public vec makeReflectedVec(vec N) { return makeOffsetVec(-2.f*dot(this,N),N);};

  // OUTPUT TEST MEASURE
  public float norm() {return(sqrt(sq(x)+sq(y)));}
  public boolean isNull() {return((abs(x)+abs(y)<0.000001f));}
  public float angle() {return(atan2(y,x)); }

  // DRAW, PRINT
  public void write() {println("("+x+","+y+")");};
  public void show (pt P) {line(P.x,P.y,P.x+x,P.y+y); }; 
  public void showAt (pt P) {line(P.x,P.y,P.x+x,P.y+y); }; 
  public void showArrowAt (pt P) {line(P.x,P.y,P.x+x,P.y+y); 
      float n=min(this.norm()/10.f,height/50.f); 
      pt Q=P.makeTranslatedBy(this); 
      vec U = this.makeUnit().makeScaledBy(-n);
      vec W = U.makeTurnedLeft().makeScaledBy(0.3f);
      beginShape(); Q.makeTranslatedBy(U).makeTranslatedBy(W).v(); Q.v(); 
                    W.scaleBy(-1); Q.makeTranslatedBy(U).makeTranslatedBy(W).v(); endShape(CLOSE); }; 
  public void showLabel(String s, pt P) {pt Q = P.makeTranslatedBy(0.5f,this); 
           vec N = makeUnit().makeTurnedLeft(); Q.makeTranslatedBy(3,N).showLabel(s); };
  } // end vec class
 


//*****************************************************************************
// TITLE:         RAYS  
// DESCRIPTION:   Tools for processing rays
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
public RAY ray(pt A, pt B) {return new RAY(A,B); }
public RAY ray(pt Q, vec T) {return new RAY(Q,T); }
public RAY ray(pt Q, vec T, float d) {return new RAY(Q,T,d); }
public RAY ray(RAY R) {return new RAY(R.Q,R.T,R.r); }
public RAY leftTangentToCircle(pt P, pt C, float r) {return tangentToCircle(P,C,r,-1); }
public RAY rightTangentToCircle(pt P, pt C, float r) {return tangentToCircle(P,C,r,1); }
public RAY tangentToCircle(pt P, pt C, float r, float s) {
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
  public void drag() {pt P=T(Q,r,T); if(P.disToMouse()<Q.disToMouse()) {float dd=d(Q,Mouse()); pt O=T(Q,dd,T); O.moveWithMouse(); Q.track(dd,O); T.setTo(U(V(Q,O)));} else Q.moveWithMouse();}
  public void setTo(pt P, vec V) {Q.setTo(P); T.setTo(U(V)); }
  public void setTo(RAY B) {Q.setTo(B.Q); T.setTo(B.T); d=B.d; T.normalize();}
  public void showArrow() {arrow(Q,r,T); }
  public void showLine() {show(Q,d,T);}
  public pt at(float s) {return new pt(Q,s,T);}         public pt at() {return new pt(Q,d,T);}
  public void turn(float a) {T.rotateBy(a);}            public void turn() {T.rotateBy(PI/180.f);}

  public float disToLine(pt A, vec N) {float n=dot(N,T); float t=0; if(abs(n)>0.000001f) t = -dot(N,V(A,Q))/n; return t;}

  public boolean hitsEdge(pt A, pt B) {boolean hit=isRightOf(A,Q,T)!=isRightOf(B,Q,T); if (cw(A,B,Q)==(dot(T,R(V(A,B)))>0)) hit=false; return hit;} // if hits
  public float disToEdge(pt A, pt B) {vec N = U(R(V(A,B))); float t=0; float n=dot(N,T); if(abs(n)>0.000001f) t=-dot(N,V(A,Q))/n; return t;} // distance to edge along ray if hits
  public pt intersectionWithEdge(pt A, pt B) {return at(disToEdge(A,B));}                                                                   // hit point if hits
  public RAY reflectedOfEdge(pt A, pt B) {pt X=intersectionWithEdge(A,B); vec V =T.makeReflectedVec(R(U(V(A,B)))); float rd=d-disToEdge(A,B); return ray (X,V,rd); } // bounced ray
  public RAY surfelOfEdge(pt A, pt B) {pt X=intersectionWithEdge(A,B); vec V = R(U(V(A,B))); float rd=d-disToEdge(A,B); return ray (X,V,rd); } // bounced ray

  public float disToCircle(pt C, float r) { return rayCircleIntesectionParameter(Q,T,C,r);}  // distance to circle along ray
  public pt intersectionWithCircle(pt C, float r) {return at(disToCircle(C,r));}            // intersection point if hits
  public boolean hitsCircle(pt C, float r) {return disToCircle(C,r)!=-1;}                   // hit test
  public RAY reflectedOfCircle(pt C, float r) {pt X=intersectionWithCircle(C,r); vec V =T.makeReflectedVec(U(V(C,X))); float rd=d-disToCircle(C,r); return ray (X,V,rd); }
  }
     
public boolean isRightOf(pt A, pt Q, vec T) {return dot(R(T),V(Q,A)) > 0 ; };                               // A is on right of ray(Q,T) (as seen on screen)

//*****************************************************************************
// TITLE:         SPIRALS  
// DESCRIPTION:   Tools for animating along spirals
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************

public pt spiralPt(pt A, pt G, float s, float a) {return L(G,s,R(A,a,G));}  
public pt spiralPt(pt A, pt G, float s, float a, float t) {return L(G,pow(s,t),R(A,t*a,G));} 
//pt spiralPt(pt A, pt G, float s, float a, float t) {return L(G,(1.-t)+t*s,R(A,t*a,G));} 
public pt spiralCenter(pt A, pt B, pt C, pt D) { // computes center of spiral that takes A to C and B to D
  float a = spiralAngle(A,B,C,D); 
  float z = spiralScale(A,B,C,D);
  return spiralCenter(a,z,A,C);
  }
public float spiralAngle(pt A, pt B, pt C, pt D) {return angle(V(A,B),V(C,D));}
public float spiralScale(pt A, pt B, pt C, pt D) {return d(C,D)/d(A,B);}
public pt spiralCenter(float a, float z, pt A, pt C) {
  float c=cos(a), s=sin(a);
  float D = sq(c*z-1)+sq(s*z);
  float ex = c*z*A.x - C.x - s*z*A.y;
  float ey = c*z*A.y - C.y + s*z*A.x;
  float x=(ex*(c*z-1) + ey*s*z) / D;
  float y=(ey*(c*z-1) - ex*s*z) / D;
  return P(x,y);
  }
//*****************************************************************************
// TITLE:         J-SPLINES  
// DESCRIPTION:   tols for roducing and processing J-splines
// AUTHOR:        Prof Jarek Rossignac
// DATE CREATED:  September 2009
// EDITS:
//*****************************************************************************
public pt s(pt A, float s, pt B) {return(new pt(A.x+s*(B.x-A.x),A.y+s*(B.y-A.y))); };
public pt b(pt A, pt B, pt C, float s) {return( s(s(B,s/4.f,A),0.5f,s(B,s/4.f,C))); };                          // returns a tucked B towards its neighbors
public pt f(pt A, pt B, pt C, pt D, float s) {return( s(s(A,1.f+(1.f-s)/8.f,B) ,0.5f, s(D,1.f+(1.f-s)/8.f,C))); };    // returns a bulged mid-edge point 
public pt B(pt A, pt B, pt C, float s) {return( s(s(B,s/4.f,A),0.5f,s(B,s/4.f,C))); };                          // returns a tucked B towards its neighbors
public pt F(pt A, pt B, pt C, pt D, float s) {return( s(s(A,1.f+(1.f-s)/8.f,B) ,0.5f, s(D,1.f+(1.f-s)/8.f,C))); };    // returns a bulged mid-edge point 
public pt limit(pt A, pt B, pt C, pt D, pt E, float s, int r) {
  if (r==0) return C.clone();
  else return limit(b(A,B,C,s),f(A,B,C,D,s),b(B,C,D,s),f(B,C,D,E,s),b(C,D,E,s),s,r-1);
  }

//---- biLaplace fit
public vec fitVec (pt B, pt C, pt D) { return A(V(C,B),V(C,D)); }
public pt fitPt (pt B, pt C, pt D) {return A(B,D);};  
public pt fitPt (pt B, pt C, pt D, float s) {return T(C,s,fitVec(B,C,D));};  
public pt fitPt(pt A, pt B, pt C, pt D, pt E, float s) {pt PB = fitPt(A,B,C,s); pt PC = fitPt(B,C,D,s);  pt PD = fitPt(C,D,E,s); return fitPt(PB,PC,PD,-s);}
public pt fitPt(pt A, pt B, pt C, pt D, pt E) {float s=sqrt(2.0f/3.0f); pt PB = fitPt(A,B,C,s); pt PC = fitPt(B,C,D,s);  pt PD = fitPt(C,D,E,s); return fitPt(PB,PC,PD,-s);}
//---- proportional biLaplace fit
public vec proVec (pt B, pt C, pt D) { return S(V(C,B), d(C,B)/(d(C,B)+d(C,D)),V(C,D)); }
public pt proPt (pt B, pt C, pt D) {return T(B,d(C,B)/(d(C,B)+d(C,D)),V(B,D));};  
public pt proPt (pt B, pt C, pt D, float s) {return T(C,s,proVec(B,C,D));};  
public pt proPt(pt A, pt B, pt C, pt D, pt E, float s) {pt PB = proPt(A,B,C,s); pt PC = proPt(B,C,D,s);  pt PD = proPt(C,D,E,s); return proPt(PB,PC,PD,-s);}
public pt proPt(pt A, pt B, pt C, pt D, pt E) {float s=sqrt(2.0f/3.0f); pt PB = proPt(A,B,C,s); pt PC = proPt(B,C,D,s);  pt PD = proPt(C,D,E,s); return proPt(PB,PC,PD,-s);}

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
public boolean cw(pt B, pt C) {return C.y*B.x>C.x*B.y;}  // cw(B,C): Boolean true if smallest angle turn from OB to OC is cloclwise (cw), where O=(0,0)
public boolean cw(pt A, pt B, pt C) {return (C.y-A.y)*(B.x-A.x)>(C.x-A.x)*(B.y-A.y);} // cw(A,B,C): Boolean true if A-B-C makes a clockwise turn at B
public float area(pt A, pt B, pt C) {return 0.5f*((C.y-A.y)*(B.x-A.x)+(A.x-C.x)*(B.y-A.y));} // area(A,B,C): signed Triangle area (positive if A-B-C is clockwise
public float x(pt A, pt B, pt C) {return ((C.x-A.x)*(B.x-A.x)+(C.y-A.y)*(B.y-A.y))/(sq(C.x-A.x)+sq(C.y-A.y));} // x(A,B,C): local x-coordinate of B in system (AC,R(AC),A)
public float y(pt A, pt B, pt C) {return ((C.y-A.y)*(B.x-A.x)+(A.x-C.x)*(B.y-A.y))/(sq(C.x-A.x)+sq(C.y-A.y));} // y(A,B,C): local y-coordinate of B in system (AC,R(AC),A)
public float ay(pt A, pt B, pt C) {return ((C.y-A.y)*(B.x-A.x)+(A.x-C.x)*(B.y-A.y))/sqrt(sq(C.x-A.x)+sq(C.y-A.y));} // ay(A,B,C): signed distance from A to the projection of B onto line(A,R(AC))
public pt projection(pt A, pt B, pt C) {return L(A,y(A,B,C),C);} // returns projection of B on line(A,C)
public float thickness(pt A, pt B, pt C) {return min(abs(ay(A,B,C)),abs(ay(B,C,A)),abs(ay(C,A,B))); } 

// area , moment
public float triangleArea(pt A, pt B, pt C) {return(dot(A.makeVecTo(B).makeTurnedLeft(),A.makeVecTo(C))/2.f); };
public float triangleMoment(pt A, pt B, pt C) {
  float b = A.disTo(B); 
  vec T=A.makeVecTo(B); T.normalize();
  vec N = T.makeTurnedLeft(); 
  vec AC=A.makeVecTo(C); 
  float h = dot(AC,N);
  float a = dot(AC,T);
  return ( b*b*b*h - a*b*b*h + a*a*b*h + b*h*h*h )/36.f; };
 
// render
public void show(pt A, pt B, pt C)  {beginShape();  A.v(); B.v(); C.v(); endShape(CLOSE);}  // show(A,B,C): render triangle
public void show(pt A, pt B, pt C, float r) {
  if (thickness(A,B,C)<abs(2*r)) return;
  float s=r; if (cw(A,B,C)) s=-s; pt AA = A.makeOffset(B,C,s); pt BB = B.makeOffset(C,A,s); pt CC = C.makeOffset(A,B,s); 
  beginShape();  AA.v(); BB.v(); CC.v(); endShape(CLOSE);
    }
public void showAltitude(pt A, pt B, pt C) {vec V=R(V(A,C)); vec U=S(y(A,B,C),V); arrow(B,U); }
   
// centers and their radii: http://www.jimloy.com/geometry/centers.htm
public pt Centroid(pt A, pt B, pt C)  {return A(A,B,C);}        // Centroid(A,B,C): (A+B+C)/3 (center of mass of triangle), intersection of bisectors
public pt CircumCenter (pt A, pt B, pt C) {vec AB = V(A,B); vec AC = R(V(A,C)); return T(A,1.f/2/dot(AB,AC),S(-n2(AC),R(AB),n2(AB),AC)); }; // CircumCenter(A,B,C): center of circumscribing circle, where medians meet)
public float circumRadius (pt A, pt B, pt C) {float a=d(B,C), b=d(C,A), c=d(A,B), s=(a+b+c)/2, d=sqrt(s*(s-a)*(s-b)*(s-c)); return a*b*c/4/d;} // radiusCircum(A,B,C): radius of circumcenter
 
public pt OrthoCenter(pt A, pt B, pt C)  {float a=d2(B,C), b=d2(C,A), c=d2(A,B), x=(a+b-c)*(a-b+c), y=(a+b-c)*(-a+b+c), z=(a-b+c)*(-a+b+c), t=x+y+z; x=x/t; y=y/t; z=z/t; return S(x,A,y,B,z,C);} // OrthoCenter(A,B,C): intersection of altitudes
public pt SpiekerCenter (pt A, pt B, pt C) {float ab=d(A,B), bc=d(B,C), ca=d(C,A), s=ab+bc+ca; return S(ab/s,A(A,B),bc/s,A(B,C),ca/s,A(C,A));  }    // SpiekerCenter(A,B,C): center of mass of the perimeter 
public float spiekerRadius (pt A, pt B, pt C) {float ab=d(A,B), bc=d(B,C), ca=d(C,A), s=ab+bc+ca; return s/(2*PI);  }    // spiekerRadius(A,B,C): radius of circle with same perimeter
public pt InCenter (pt A, pt B, pt C)  {float Z=area(A,B,C), a=B.disTo(C), b=C.disTo(A), c=A.disTo(B), s=a+b+c, r=2*Z/s, R=a*b*c/(2*r*s); return S(a/s,A,b/s,B,c/s,C); } // InCenter(A,B,C): incenter (center of inscribed circle)
public float inRadius (pt A, pt B, pt C)  {float Z=area(A,B,C), a=d(B,C), b=d(C,A), c=d(A,B), s=a+b+c;  return 2*Z/s;} //inRadius(A,B,C): radius of incircle  

public float radiusMonotonic (pt A, pt B, pt C) {    // size of bubble pushed through (A,C) and touching B, >0 when ABC is clockwise
    float a=d(B,C), b=d(C,A), c=d(A,B);
    float s=(a+b+c)/2; float d=sqrt(s*(s-a)*(s-b)*(s-c)); float r=a*b*c/4/d;
    if (abs(angle(A,B,C))>PI/2) r=sq(d(A,C)/2)/r;
    if (abs(angle(C,A,B))>PI/2) r=sq(d(C,B)/2)/r;
    if (abs(angle(B,C,A))>PI/2) r=sq(d(B,A)/2)/r;
    if (cw(A,B,C)) r=-r;
    return r;
   };

       
// constructions of points and offsetrs    
public pt Offset(pt A, pt B, pt C, float r) {  // intersection of r-offsets of lines (A,B) and (B,C)
    float a = angle(V(B,A),V(B,C))/2;
    float d = r/sin(a); 
    vec N = U(A(U(V(B,A)),U(V(B,C)))); 
    return T(B,d,N); };
    
public pt Shadow(pt A, pt B, pt C) {return L(A,x(A,B,C),C); };  // Projection of B on line (B,C)


public void mousePressed() {MYcheckButtons(); if(!ui_picked) MYmousePressed(); } 
public void mouseDragged() {if(!ui_picked) MYmouseDragged(); } 
public void mouseReleased() {ui_picked=false; MYmouseReleased();} 
public void keyReleased() {MYkeyReleased(); } 
public void keyPressed() { 
  int w = PApplet.parseInt(key)-48; if(0<w && w<10) scribe(str(w)); // shows briefly value of numeric key pressed
  if (key==' ') showHelpText=!showHelpText ; 
  if (key=='?') debug=true;  // toggle debug mode
  if (key=='$') picture(); 
  MYkeyPressed();
  };     
  
public void mouseMoved() {if(!ui_picked) MYmouseMoved(); } 

public void showHelp() {
   image(pic, width-(pic.width+pic2.width), 0); 
   image(pic2, width-pic2.width, 0);
   pushMatrix(); translate(20,20);
   fill(black);
   MYshowHelp();
   fill(dblue);
   text("  ",0,0); translate(0,20);
   text("Press SPACE to hide/show this menu",0,0); translate(0,20);
   text("Press '$' to snap a picture (when running Processing, not in browser)",0,0); translate(0,20);
   text("Built using Jarek Rossignac's Geometry SandBox (GSB)",0,0); translate(0,20);
   popMatrix(); noFill();
   }
  


// BUTTONS for toggles and actions
Boolean ui_picked=false; // true when user click on a button
int ui_dy=15, but_hw=5;
class BUTTON {
 String label="not assigned"; Boolean isTrue=false; Boolean click=false;
  BUTTON (String S, Boolean pisTrue) {label=S; isTrue=pisTrue;}
  BUTTON (String S, int x) {label=S; click=true; isTrue=true;}
  public boolean check(int row) {
    boolean clicked=false;
    if (height<mouseX && mouseX<height+but_hw*2 && (row+1)*ui_dy<mouseY+but_hw && mouseY<(row+1)*ui_dy+but_hw) { 
      if(!click) isTrue=!isTrue; else clicked=true;
      ui_picked=true; 
      };
    if(clicked) println(label);
    return clicked;
    }
  public void show(int row) { 
     fill(black); scribe(label,height+but_hw+11,(row+1)*ui_dy+6); 
     if(click) fill(metal); if(!isTrue) fill(white); 
     rect(height+but_hw,(row+1)*ui_dy,but_hw*2,but_hw*2);  
     }
  }  // end BUTTON


   
  static public void main(String args[]) {
    PApplet.main(new String[] { "--bgcolor=#FFFFFF", "GSB" });
  }
}
