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
LOOP H = new LOOP(100);
int pC=0;  // counter of which file number the points are saved to (not used here)
int nFrames=9; // not used
void MYsetup() { // executed once at start
  C.declarePoints(); 
  // C.resetPointsOnCircle(); 
  C.loadPts("data/P"+str(pC));
  M.declare(); 
  M.init(); 
  C.makeDelaunayOfPoints(M);
}; 

void MYdraw () { // executed at each frame
  scribeBlack("Project 1 (Constrained Delaunay Triangulation) by Will Mooney",0);
  scribeBlack("The mesh has "+C.n+" points and "+M.nt+" triangles",1);
  H.setToConvexHull(C); 
  strokeWeight(7); 
  noFill(); 
  stroke(cyan);  
  H.drawEdges(); // computes and draws convex hull
  if(showTriangles.isTrue)  C.showDelaunayOfPoints(-2); 
  if(showMesh.isTrue)  M.showMesh(); 
  if(showEdges.isTrue) {
    noFill(); 
    strokeWeight(3); 
    stroke(red); 
    C.drawEdges();
  }
  if(showMYTriangulation.isTrue)
  {
    MYcomputeConstrainedTriangulation();
  }
  if(showLetters.isTrue) {
    fill(blue); 
    C.writePointLetters(); 
    noFill();   
    stroke(blue); 
    noFill(); 
    C.drawPoints(13);
  } // toggle in menu to write letters
  else {
    noStroke(); 
    fill(dred); 
    C.drawPoints(2);
  }; // draws points as small dots
}

void MYmousePressed() {
  C.pickClosestPoint(Mouse()); 
  if (keyPressed && key=='i') C.insert(Mouse()); // add a point
  if (keyPressed && key=='d') C.deletePoint(); // add a point
  if (keyPressed && key=='a') C.appendPoint(Mouse()); // add a point
}     
void MYmouseReleased() {
}  
void MYmouseMoved() {
}
void MYmouseDragged() {     // application-specific actions executed when the mouse is moved while the mouse button is kept pressed
  if (!mouseIsInWindow()) return;
  if (!keyPressed||(key=='i'||key=='a')) {
    C.dragPoint(MouseDrag());
  } 
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
  if (key=='f') {
    M.flip(); 
    M.classifyTriangles(C);
  }
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

void  MYkeyReleased() {
} 

void MYshowHelp() {  // Application specific help text to appear in the help pane when user pressed the SPACE BAR
  text("CS 6491 -- Fall 2010 -- Instructor Jarek Rossignac",0,0); 
  translate(0,20);
  text("Project 1: Triangulation",0,0); 
  translate(0,20);
  text("by Will MOONEY",0,0); 
  translate(0,20);
  text("Date submitted: September 9, 2010",0,0); 
  translate(0,20);
  text("  ",0,0); 
  translate(0,20);
  text("USAGE  ",0,0); 
  translate(0,20);
  text("Press the space-bar to hide/show this help page ",0,0); 
  translate(0,20);
  text("Click near a point & drag to move closest point ",0,0); 
  translate(0,20);
  text("Keep 'i', 'd', or 'a', and click to insert, delete, or append a point",0,0); 
  translate(0,20);
  text("Hold 'm', 't', or 'z', and click & drag to move, turn, or zoom ",0,0); 
  translate(0,20);
  text("Press '0' to delete all points",0,0); 
  translate(0,20);
  text("Press 'n', 'p', 'o', 'l', 'r', 's' to change current corner and 'w' to print it ",0,0); 
  translate(0,20);
  text("Press 'f' to flip the edge opposite to the current corner",0,0); 
  translate(0,20);
  text("  ",0,0); 
  translate(0,20);
  text("ADDITIONAL COMMANDS FOR THIS PROJECT:",0,0); 
  translate(0,20);
  text("  ",0,0); 
  translate(0,20);
  text("  ",0,0); 
  translate(0,20);
}

// BOTTONS

COUNTER buttonCounter = new COUNTER();

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
BUTTON showMesh = new BUTTON("show mesh",false); // show triangle mesh
BUTTON showMYTriangulation = new BUTTON("show MY triangulation",false); // show triangle mesh

void MYshowButtons() {           // shows all my buttons on the right of screen with their status and labels
  fill(metal); 
  scribe("Constrained triangulation",height,15); 
  scribe("Menu:",height,35);
  stroke(metal); 
  strokeWeight(2); 
  buttonCounter.reset();  
  buttonCounter.i();
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
  showMYTriangulation.show(buttonCounter.i());
}

// THE ORDER IN WHICH THE BOTTONS ARE CREATE (ABOVE) AND ACTIVATED (BELOW) MUST BE IDENTICAL

void MYcheckButtons() {          // checks whether any of my buttons was pressed and temporarily disable GUI 
  buttonCounter.reset();  
  buttonCounter.i();
  if(savePoints.check(buttonCounter.i())) C.savePts("data/P"+str(pC));
  if(loadPoints.check(buttonCounter.i())) C.loadPts("data/P"+str(pC));
  if(resetPoints.check(buttonCounter.i())) C.resetPoints();
  if(snapPicture.check(buttonCounter.i())) makingPicture=true;  // set makingPicture used not to show the menu
  showEdges.check(buttonCounter.i());  // toggles showEdges
  showLetters.check(buttonCounter.i());  // toggles showLetters
  if(lowpass.check(buttonCounter.i())) C.smoothen();
  if(subdivide.check(buttonCounter.i())) C.refine(0.5);
  if(subdivide.check(buttonCounter.i())) C.coarsen();
  if(resample.check(buttonCounter.i())) C.resample(200);
  showTriangles.check(buttonCounter.i());
  if(computeMesh.check(buttonCounter.i())) C.makeDelaunayOfPoints(M);
  showMesh.check(buttonCounter.i());
  showMYTriangulation.check(buttonCounter.i());
}

/**
 *** Function: MYsamePoints
 *** Author: Will Mooney
 ***
 *** Description: Checks if two edges share a point
 **/
boolean MYsamePoints(pt J,pt K,pt L,pt M)
{
  if(J.x==L.x&&J.y==L.y) return true;
  if(J.x==M.x&&J.y==M.y) return true;
  if(K.x==L.x&&K.y==L.y) return true;
  if(K.x==M.x&&K.y==M.y) return true;
  return false;
}

/**
 *** Function: intersectsPolygonEdge
 *** Author: Will Mooney
 ***
 *** Params: from and to are points that a propsed line segment starts and ends at
 **/
boolean MYintersectsPolygonEdge(pt from,pt to)
{
  //Cycle through all of the polygon edges and make sure this doesn't intersect
  
  for(int j=0;j<C.n-1;j++)
  {
      if(!MYsamePoints(C.P[j],C.P[j+1],from,to)&&edgesIntersect(C.P[j],C.P[j+1],from,to))
      {
        return true;
      }
  }
  //Test the last line in the Polygon
  return (!MYsamePoints(C.P[C.n-1],C.P[0],from,to)&&edgesIntersect(C.P[C.n-1],C.P[0],from,to));
}

/**
 *** Function: MYisInPolygon
 *** Author: Will Mooney
 ***
 *** Params: from and to are the endpoints of the line segment we are testing
 **/
 boolean MYisInPolygon(pt from,pt to)
 {
   //Create a RAY
   float ptX = (from.x+to.x)/2.0;
   float ptY = (from.y+to.y)/2.0;
   pt avgPoint = new pt(ptX,ptY);
   
   //RAY r = new RAY(avgPoint,to);
   RAY r = new RAY(avgPoint,to);
   
   //Cycle through all edges and keep tally of how many are hit
   int edgesHit = 0;
   r.turn(0.1);
   for(int j=0;j<C.n;j++)
   {
     if(r.hitsEdge(C.P[j],C.P[(j+1)%C.n]))//&&!MYsamePoints(C.P[j],C.P[j+1],from,to))
      {
        edgesHit++;
      }
   }
   //If it didn't hit any edges or an even number, then it is outside of the polygon
   //boolean pointIn = (edgesHit!=0&&edgesHit%2!=0);
   //if(pointIn) fill(#00ff00);
   //else fill(#0000ff);
   //noStroke();
   //r.showArrow();
   return (edgesHit!=0&&edgesHit%2!=0);
 }
 

/**
 *** Function: MycomputeConstrainedTriangulation
 *** Author: Will Mooney
 **/
void MYcomputeConstrainedTriangulation()
{
  //Only worthwhile if there are more than 3 points
  if(C.P.length<3) {
    return;
  }
  if(C.P.length==3)
  {
        fill(#FFECE5);
        show(C.P[0],C.P[1],C.P[2]);
  }

  //Make a copy of the points array and the list of edges
  int numPoints = C.n;  //n is the number of points that are actually in the array
  pt [] points = new pt[numPoints];
  for(int i=0;i<numPoints;i++)
  {
    points[i]=C.P[i];
  }

  //Make a list for the edges we add
  pt [] additionalEdges = new pt[4*numPoints];
  int numDiagonals = 0;

  //Initialize the boolean variable that says we are done
  //Done when there are three points or less remaining in the for loop
  boolean done = false;
  int numIter = 0;
  //Begin loop
  while(!done)
  {
    //println("Numpoints is "+numPoints)
    //If there are three points left, we are done (<= just to be safe)
      if(points.length==3)
      {
        stroke(#0000ff);
        line(points[0].x,points[0].y,points[2].x,points[2].y);
        done=true;
        break;
      }
      if(points.length<3)
      {
        done=true;
        break;
      }

    //Create another array of equal size
    pt [] redoPoints = new pt[numPoints];
    int newNumPoints = 0;

    //Loop through all of the vertices
   // println("numpoints before loop: "+numPoints);
    for(int i=0;i<=numPoints-2;i++)
    {
      
      //Initialize the first three points
      println("NumPoints: "+numPoints+" and i is "+i);
      pt previous = points[(i+numPoints-1)%numPoints];    //the last point in the polygon
      pt current = points[i];  //start at the first point
      pt next = points[(i+1)%numPoints];     //the next point in the polygonal curve

    // println("i is "+i+" and numpoints is "+numPoints);
      //println("C: "+current.x+","+current.y+"...P: "+previous.x+","+previous.y+"...N: "+next.x+","+next.y);

      //check if it is in the polygon
      if(MYisInPolygon(previous,next))
      {
        //check if a diagonal from previous to next intersects the polygon
        boolean dontAdd = MYintersectsPolygonEdge(previous,next);

        //check if it intersects any of our new edges within the polygon
        for(int j=0; j<numDiagonals; j++)
        {
          if(!MYsamePoints(additionalEdges[j*2],additionalEdges[j*2+1],previous,next)&&edgesIntersect(additionalEdges[j*2],additionalEdges[j*2+1],previous,next))
          {
            println("Location A");
            dontAdd=true;
          }
        }
        
        if(dontAdd)
        {
            println("Location C");
             redoPoints[newNumPoints]=current;
             newNumPoints++;
        }
        //Else, add the point to our list of points to iterate over again
        else
        {
          println("Location B");
          //Add to the list of new edges
          additionalEdges[numDiagonals*2]=previous;
          additionalEdges[numDiagonals*2+1]=next;
          numDiagonals++;
          stroke(#0000ff);
          line(previous.x,previous.y,next.x,next.y);
          i++;
          redoPoints[newNumPoints]=next;
          newNumPoints++;
        }
      }
      //Else, add the point to our list of points to iterate over again
      else
      {
           redoPoints[newNumPoints]=current;
           newNumPoints++;
      }
      
      println("New NumPoints "+newNumPoints);
     // break;
    }
    numIter++;
    //if(numIter==50)
    //{
    //  done=true;
    //}
    println("NumIter "+numIter);

    //Copy the new series of points over to the old array
    points = new pt[newNumPoints];
    for(int x=0;x<newNumPoints;x++)
    {
      points[x] = redoPoints[x];
    }
    numPoints = newNumPoints;
  }
}
