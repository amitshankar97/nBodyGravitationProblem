package nBodyGravitationProblem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

public class SequentialHandler {

    long numObjects, numTimeSteps;
    double mass;

    final double GRAV_CONSTANT = 6.67 * (10^(-11));
    int PLANE_SIZE = 0;


    final double timeStep = .1;
    private Random rand = new Random();
    private Point position[];
    private Point velocity[];
    private Point force[];
    private double bodySize;
    private double radius;
    
    private ArrayList<Integer> collisions;

    private int round = 0;

    BufferedWriter positionWriter;
    BufferedWriter collisionWriter;
    
    nBodyVisual artist;
    
    private int collisionCtr = 0;

    public SequentialHandler(int numObjects, int numTimeSteps, double bodySize) {
	this.numObjects = numObjects;
	this.numTimeSteps = numTimeSteps;
	this.bodySize = bodySize;
	this.collisions = new ArrayList<Integer>();

	PLANE_SIZE = (int) (numObjects * bodySize * 2);
	System.out.println("PLANE_SIZE: " + PLANE_SIZE);

	position = new Point[numObjects];
	velocity = new Point[numObjects];
	force = new Point[numObjects];

	mass = (double) rand.nextInt(100);
	System.out.println("Mass: " + mass);
	
	radius = bodySize/2;

	for (int i = 0; i < numObjects; i++) {
	    while (true) {
		int roundedRadius = (int) Math.round(radius);
		roundedRadius++;

		int x = rand.nextInt(PLANE_SIZE-roundedRadius);
		int y = rand.nextInt(PLANE_SIZE-roundedRadius);


		boolean acceptable = true;
		for (int j = 0; j < i; j++) {
		    boolean isBetweenX = (x <= position[j].getX()+roundedRadius && x >= position[j].getX()-roundedRadius);
		    boolean isBetweenY = (y <= position[j].getY()+roundedRadius && y >= position[j].getY()-roundedRadius);
		    boolean isOnBorderX = (x + roundedRadius >= PLANE_SIZE || x - roundedRadius <= 0);
		    boolean isOnBorderY = (y + roundedRadius >= PLANE_SIZE || y - roundedRadius <= 0);


		    if (isBetweenX || isBetweenY || isOnBorderX || isOnBorderY)
			acceptable = false;
		}
		if (acceptable) {
		    position[i] = new Point(x, y);
		    break;
		} else {
		    continue;
		}
	    }
	    
	    int x = rand.nextInt(15);
	    int y = rand.nextInt(15);
	    
	    int xDir = rand.nextInt(3);
	    int yDir = rand.nextInt(3);
	    
	    if(xDir % 2 == 0)
		x = 0 - x;
	    if(yDir % 2 == 0)
		y = 0 - y;
	    
	    velocity[i] = new Point(x, y);
	    
	    force[i] = new Point();
	}
	

	artist = new nBodyVisual(1, bodySize, numObjects, position);

	try {
	    positionWriter = new BufferedWriter(new FileWriter("nBodySeq.csv"));
	    collisionWriter = new BufferedWriter(new FileWriter("collisionLog.csv"));
	} catch(IOException e) {
	    e.printStackTrace();
	}

	try {
	    collisionWriter.write("Round, Object 1, Object 2, Distance, x1, x2, y1, y2");
	} catch (IOException e1) {
	    // TODO Auto-generated catch block
	    e1.printStackTrace();
	}
	
	String str = "Round,,";

	for (int i = 0; i < position.length; i++) {
	    str += "Object " + i + ",,,";
	}

	str += "\n,,";

	for (int i = 0; i < position.length; i++) {
	    str += "x,y,,";
	}

	try {
	    positionWriter.write(str);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

    }

    public SequentialHandler(int numObjects, int numTimeSteps, double bodySize, double mass) {
	this.numObjects = numObjects;
	this.numTimeSteps = numTimeSteps;
	this.bodySize = bodySize;

	position = new Point[numObjects];
	velocity = new Point[numObjects];
	force = new Point[numObjects];

	this.mass = mass;

	for (int i = 0; i < position.length; i++) {
	    position[i] = new Point();
	    force[i] = new Point();
	    velocity[i] = new Point();
	}

	position[0].setLocation(20, 30);
	position[1].setLocation(80, 30);

	double magnitude = (GRAV_CONSTANT * mass * mass) / 3600;

	Point direction0 = new Point();
	Point direction1 = new Point();

	direction0.setLocation(position[1].getX() - position[0].getX(),
		position[1].getY() - position[0].getY());


	direction1.setLocation(position[0].getX() - position[1].getX(),
		position[0].getY() - position[1].getY());

	force[0].setLocation(magnitude * direction0.getX() / 60,
		magnitude * direction0.getY() / 60);

	force[1].setLocation(magnitude * direction1.getX() / 60,
		magnitude * direction1.getY() / 60);

	velocity[0].setLocation(5, 0);
	velocity[1].setLocation(-5, 0);
    }

    public void calculateForces() {
	double dist = 0, magnitude = 0;
	Point direction = new Point(0, 0);

	for (int i = 0; i < numObjects - 1; i++) {
	    for (int j = i+1; j < numObjects; j++) {
		dist = Math.sqrt(Math.pow((position[i].getX() - position[j].getX()), 2) + 
			Math.pow((position[i].getY() - position[j].getY()), 2));

		if(dist <= bodySize) {
		    System.out.println("");
		    System.out.println("******COLLISION occurred between " + i + " and " + j + " *******");
		    collisions.add(i);
		    collisions.add(j);
		    try {
			collisionWriter.write("\n" + round + "," + i + "," + j + "," + dist + "," + position[i].getX() + "," + 
		    position[j].getX() + "," + position[i].getY() + "," + position[j].getY());
		    } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		    }
		    collisionCtr++;

		    double deltax = position[j].getX() - position[i].getX();
		    double deltay = position[j].getY() - position[i].getY();

		    double deltaxSquared = Math.pow(deltax, 2);
		    double deltaySquared = Math.pow(deltay, 2);

		    double oneXVelocity = position[i].getX();
		    double oneYVelocity = position[i].getY();

		    double twoXVelocity = position[j].getX();
		    double twoYVelocity = position[j].getY();

		    double v1fx; // Object 1's post-collision x
		    double v1fy; // Object 1's post-collision y

		    // v1fx
		    v1fx = (twoXVelocity * deltaxSquared + twoYVelocity * deltax * deltay +
			    oneXVelocity * deltaySquared - oneYVelocity * deltax * deltay);

		    v1fx /= deltaxSquared + deltaySquared;

		    // v1fy
		    v1fy = (twoXVelocity * deltax * deltay + twoYVelocity * deltaySquared - oneXVelocity *
			    deltay * deltax + oneYVelocity * deltaxSquared);

		    v1fy /= deltaxSquared + deltaySquared;


		    double v2fx; // Object 2's post-collision x
		    double v2fy; // Object 2's post-collision y

		    // v2fx
		    v2fx = (oneXVelocity * deltaxSquared + oneYVelocity * deltax * deltay +
			    twoXVelocity * deltaySquared - twoYVelocity * deltax * deltay);

		    v2fx /= deltaxSquared + deltaySquared;

		    // v2fy
		    v2fy = (oneXVelocity * deltax * deltay + oneYVelocity * deltaySquared
			    - twoXVelocity * deltax * deltay + twoYVelocity * deltaxSquared);

		    v2fy /= deltaxSquared + deltaySquared;
		    
		    velocity[i].setLocation(v1fx, v1fy);
		    velocity[j].setLocation(v2fx, v2fy);
		    
		    System.out.println("");
		}

		magnitude = (GRAV_CONSTANT * mass * mass) / Math.pow(dist, 2);

		direction.setLocation(position[j].getX() - position[i].getX(),
			position[j].getY() - position[i].getY());

		double forceX = magnitude * direction.getX() / dist;
		double forceY = magnitude * direction.getY() / dist;
		// System.out.println("Magnitude = "+magnitude);
		// System.out.println("Distance = "+dist);
		// System.out.println("Direction = " + direction.toString());

		force[i].setLocation(force[i].getX() + forceX, force[i].getY() + forceY);
		force[j].setLocation(force[j].getX() - forceX, force[j].getY() - forceY);
		// System.out.println("ForceX: "+ forceX + ", ForceY" + forceY);
		// System.out.println("---------------------");
	    }
	}
	System.out.println("##########END###########");
    }

    public void moveBodies() {	

	for (int i = 0; i < numObjects; i++) {
	    double deltavX = force[i].getX() / mass * timeStep;
	    double deltavY = force[i].getY() / mass * timeStep;	

	    //System.out.println("Force (x,y): "+ force[i].getX() + "," + force[i].getY());
	    //System.out.println("Velocity (x,y): "+ velocity[i].getX() + "," + velocity[i].getY());

	    double xVelocity = velocity[i].getX();
	    double yVelocity = velocity[i].getY();

	    double xPosition = position[i].getX();
	    double yPosition = position[i].getY();

	    boolean isBreakXPlane = xPosition+radius >= PLANE_SIZE || xPosition-radius <= 0;
	    boolean isBreakYPlane = yPosition+radius >= PLANE_SIZE || yPosition-radius <= 0;

	    boolean isBreakXPlaneHigh = xPosition+radius >= PLANE_SIZE; 
	    boolean isBreakXPlaneLow  = xPosition-radius <= 0;
	    boolean isBreakYPlaneHigh = yPosition+radius >= PLANE_SIZE; 
	    boolean isBreakYPlaneLow =  yPosition-radius <= 0;
		    
	    double newXValue = xVelocity + deltavX;
	    double newYValue = yVelocity + deltavY;

	    //Check if outside of bounds, if so flip direction
	    if (isBreakYPlane && isBreakXPlane) {
		if (isBreakXPlaneHigh && xVelocity > 0) {
		    newXValue = -1*newXValue;
		} else if (isBreakXPlaneLow && xVelocity < 0) {
		    newXValue = -1*newXValue;
		}
		
		if (isBreakYPlaneHigh && yVelocity > 0) {
		    newYValue = -1*newYValue;
		} else if (isBreakYPlaneLow && yVelocity < 0) {
		    newYValue = -1*newYValue;
		}
		
		velocity[i].setLocation(newXValue, newYValue);
	    } else if (isBreakXPlane) {
		if (isBreakXPlaneHigh && xVelocity > 0) {
		    newXValue = -1*newXValue;
		} else if (isBreakXPlaneLow && xVelocity < 0) {
		    newXValue = -1*newXValue;
		}
		
		velocity[i].setLocation(newXValue, newYValue);
		System.out.println("Object " +i+ " is past x threshold and has been flipped.");
	    } else if (isBreakYPlane) {
		if (isBreakYPlaneHigh && yVelocity > 0) {
		    newYValue = -1*newYValue;
		} else if (isBreakYPlaneLow && yVelocity < 0) {
		    newYValue = -1*newYValue;
		}
		velocity[i].setLocation(newXValue, newYValue);
		System.out.println("Object " +i+ " is past y threshold and has been flipped.");
	    } else {
		velocity[i].setLocation(newXValue, newYValue);
	    }

	    double deltapX = (velocity[i].getX() + deltavX / 2) * timeStep;
	    double deltapY = (velocity[i].getY() + deltavY / 2) * timeStep;
	    //System.out.println("DeltavX = "+ deltavX);
	    //System.out.println("DeltvpX = "+ deltavY);
	    //System.out.println("DeltapX = "+ deltapX);
	    //System.out.println("DeltapX = "+ deltapY);
	    position[i].setLocation(position[i].getX() + deltapX,
		    position[i].getY() + deltapY);

	    force[i].setLocation(0, 0);
	}
    }

    public void run() {
	for (int i = 0; i < numTimeSteps; i++) {
	    calculateForces();
	    moveBodies();
	    artist.draw(position, collisions);
	    collisions.clear();
	    try {
		Thread.sleep(100);
	    } catch(InterruptedException e) {
		e.printStackTrace();
	    }
	    
	    round++;

	    String str = "\n" + i + ",,";

	    for (int j = 0; j < position.length; j++) {
		str += position[j].toString();
		// System.out.println("Location for " + j + ": (" + position[j].getX() + ", " + position[j].getY() + ")");
	    }
	    try {
		positionWriter.write(str);
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	    // System.out.println("Location for 0: (" + position[0].getX() + ", " + position[0].getY() + ")");
	    // System.out.println("Location for 1: (" + position[1].getX() + ", " + position[1].getY() + ")");
	}
	try {
	    positionWriter.close();
	    collisionWriter.close();
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("Number of collisions: " + collisionCtr);
    }
}