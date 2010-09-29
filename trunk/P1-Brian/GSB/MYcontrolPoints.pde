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

void MYsetup() { // executed once at start
   C.declarePoints(); 
   // C.resetPointsOnCircle(); 
   // C.loadPts("data/P"+str(pC));
   M.declare(); M.init(); C.makeDelaunayOfPoints(M);
   for(int x=0;x<5000;x++)first_run[x]=true;
  }; 

/**
*** Tests to see if the point is behind the curve in the sense that it
*** is on the opposite side of the bulge of the curve from the line AB
**/
boolean behindCurveA(pt P)
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
boolean behindCurveB(pt P,pt center,float radius)
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
boolean behindCurveC(pt P,pt center,float radius)
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


void MYdraw () { // executed at each frame
  scribeBlack("Project 1 (Constrained Delaunay Triangulation) by Brian Ouellette",0);
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
        float distance = (sqrt(radius*original_radius*(radius*original_radius + 2*original_radius*distanceTo[i] + distanceTo[i]*distanceTo[i])) - radius*original_radius)/original_radius;
        /* Method 1 - Skeleton bending */
        // Angle won't scale
        // Take the vector from the center to O and rotate it by the angle to P and then scale it so that the end point is the new P location
        vec tempVec = S(1+distance/radius, R(V(center,O), angleTo[i]));
        C.P[i] = T(center, tempVec);
        /* Method 3 - As Rigid As Possible */
      }
      //C.P[i].showLabel("P"+i);
    }
    C.updatePoints(M);
    for(int i=0; i<C.n;i++) {
      C.P[i].show(3);
    }
    //P.show(3);
    //P.showLabel("P");
  }//End If
  else {for(int x=0;x<5000;x++)first_run[x]=true; }
  }//End Method
 
void MYmousePressed() {
   C.pickClosestPoint(Mouse());
   if (keyPressed && key=='i') C.insert(Mouse()); // add a point
   if (keyPressed && key=='d') C.deletePoint(); // add a point
   if (keyPressed && key=='a') C.appendPoint(Mouse()); // add a point
   }     
void MYmouseReleased() {   }  
void MYmouseMoved() { }
void MYmouseDragged() {     // application-specific actions executed when the mouse is moved while the mouse button is kept pressed
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
   
void MYkeyPressed() {   // application-specific actions executed when a key is pressed (or each time a key-repeat self-activates)
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

void  MYkeyReleased() {  } 
   
void MYshowHelp() {  // Application specific help text to appear in the help pane when user pressed the SPACE BAR
 text("CS 6491 -- Fall 2010 -- Instructor Jarek Rossignac",0,0); translate(0,20);
 text("Project 1: Constrained Delaunay Triangulation",0,0); translate(0,20);
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

void MYshowButtons() {           // shows all my buttons on the right of screen with their status and labels
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
 }

// THE ORDER IN WHICH THE BOTTONS ARE CREATE (ABOVE) AND ACTIVATED (BELOW) MUST BE IDENTICAL

void MYcheckButtons() {          // checks whether any of my buttons was pressed and temporarily disable GUI 
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
  if(subdivide.check(buttonCounter.i())) C.refine(0.5);
  if(coarsen.check(buttonCounter.i())) C.coarsen();
  if(resample.check(buttonCounter.i())) C.resample(200);
  showTriangles.check(buttonCounter.i());
  if(computeMesh.check(buttonCounter.i())) { C.makeDelaunayOfPoints(M); M.classifyTriangles(C); }
  showMesh.check(buttonCounter.i());
  showArc.check(buttonCounter.i());
  }

// PROJECT 1 COMPUTATION
void compute() {
  // Make a new loop which will hold the original loop vertices as well as the sprayed points
  LOOP Cnew = new LOOP(3);
  Cnew.empty();
  // Copy in all the original loop vertices
  Cnew.copyFrom(C);
  // Spray points in and around the curve, adjust float passed in to change how this spray is created
  Cnew.refine(1.1);
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
