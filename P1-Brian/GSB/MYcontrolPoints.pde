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

void MYsetup() { // executed once at start
   C.declarePoints(); 
   // C.resetPointsOnCircle(); 
   C.loadPts("data/P"+str(pC));
   M.declare(); M.init(); C.makeDelaunayOfPoints(M);
  }; 
 
void MYdraw () { // executed at each frame
  scribeBlack("Project 1 (Constrained Delaunay Triangulation) by Brian Ouellette and Jonathan Brownsworth",0);
  scribeBlack("The mesh has "+C.n+" points and "+M.nt+" triangles",1);
  if(showTriangles.isTrue)  C.showDelaunayOfPoints(-2); 
  if(showMesh.isTrue)  M.showMesh(); 
  if(showEdges.isTrue) {noFill(); strokeWeight(3); stroke(red); C.drawEdges(); }
  if(showLetters.isTrue) {fill(blue); C.writePointLetters(); noFill();   stroke(blue); noFill(); C.drawPoints(13); } // toggle in menu to write letters
  else {stroke(blue); fill(blue); C.drawPoints(3); }; // draws points as small dots
  }
 
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
     if (key=='t') C.rotatePointsAroundCenterOfMass(Mouse(),Pmouse()); // rotate all points around their center of mass
     if (key=='m') C.translatePoints(MouseDrag()); // rotate all points around their center of mass
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
 text("Student: Brian Ouellette and Jonathan Brownsworth",0,0); translate(0,20);
 text("Date submitted: September 9, 2010",0,0); translate(0,20);
  text("  ",0,0); translate(0,20);
 text("USAGE  ",0,0); translate(0,20);
 text("Press the space-bar to hide/show this help page ",0,0); translate(0,20);
 text("Click near a point & drag to move closest point ",0,0); translate(0,20);
 text("Keep 'i', 'd', or 'a', and clic to insert, delete, or append a point",0,0); translate(0,20);
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

void MYshowButtons() {           // shows all my buttons on the right of screen with their status and labels
 fill(metal); scribe("Constrained triangulation",height,15); scribe("Menu:",height,35);
 stroke(metal); strokeWeight(2); 
 buttonCounter.reset();  buttonCounter.i();
 computeTriangulation.show(buttonCounter.i());
 savePoints.show(buttonCounter.i());
 loadPoints.show(buttonCounter.i());
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
 }

// THE ORDER IN WHICH THE BOTTONS ARE CREATE (ABOVE) AND ACTIVATED (BELOW) MUST BE IDENTICAL

void MYcheckButtons() {          // checks whether any of my buttons was pressed and temporarily disable GUI 
  buttonCounter.reset();  buttonCounter.i();
  if(computeTriangulation.check(buttonCounter.i())) compute();
  if(savePoints.check(buttonCounter.i())) C.savePts("data/P"+str(pC));
  if(loadPoints.check(buttonCounter.i())) C.loadPts("data/P"+str(pC));
  if(resetPoints.check(buttonCounter.i())) C.resetPoints();
  if(snapPicture.check(buttonCounter.i())) makingPicture=true;  // set makingPicture used not to show the menu
  showEdges.check(buttonCounter.i());  // toggles showEdges
  showLetters.check(buttonCounter.i());  // toggles showLetters
  if(lowpass.check(buttonCounter.i())) C.smoothen();
  if(subdivide.check(buttonCounter.i())) C.refine(0.5);
  if(coarsen.check(buttonCounter.i())) C.coarsen();
  if(resample.check(buttonCounter.i())) C.resample(200);
  showTriangles.check(buttonCounter.i());
  if(computeMesh.check(buttonCounter.i())) C.makeDelaunayOfPoints(M);
  showMesh.check(buttonCounter.i());
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
  }*/
  // Add back in the loop vertices, these were removed during the refine() operation
  for (int i=0; i<C.n; i++) {
    Cnew.appendPoint(C.P[i]);
  }
  Cnew.makeDelaunayOfPoints(M);
  M.classifyTriangles(C);
}
