package nBodyGravitationProblem;

import java.util.Random;

public class SequentialHandler {

    long numObjects, numTimeSteps;
    double mass;

    final double GRAV_CONSTANT = 6.67 * (10^(-11));

    final double timeStep = 1;

    private Point position[];
    private Point velocity[];
    private Point force[];
    private double bodySize;
    private double radius;

    public SequentialHandler(int numObjects, int numTimeSteps, double bodySize) {
	this.numObjects = numObjects;
	this.numTimeSteps = numTimeSteps;
	this.bodySize = bodySize;

	position = new Point[numObjects];
	velocity = new Point[numObjects];
	force = new Point[numObjects];

	Random rand = new Random();
	mass = rand.nextDouble();
	radius = bodySize/2;
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

	// TODO: Write collision point to file

	for (int i = 0; i < numObjects - 1; i++) {
	    for (int j = i+1; j < numObjects; j++) {
		dist = Math.sqrt(Math.pow((position[i].getX() - position[j].getX()), 2) + 
			Math.pow((position[i].getY() - position[j].getY()), 2));

		if(dist <= bodySize) {
		    System.out.println("");
		    System.out.println("******COLLISION occurred*******");

		    System.out.println("");
		}

		magnitude = (GRAV_CONSTANT * mass * mass) / Math.pow(dist, 2);

		direction.setLocation(position[j].getX() - position[i].getX(),
			position[j].getY() - position[i].getY());

		double forceX = magnitude * direction.getX() / dist;
		double forceY = magnitude * direction.getY() / dist;
		System.out.println("Magnitude = "+magnitude);
		System.out.println("Distance = "+dist);
		System.out.println("Direction = " + direction.toString());

		force[i].setLocation(force[i].getX() + forceX, force[i].getY() + forceY);
		force[j].setLocation(force[j].getX() - forceX, force[j].getY() - forceY);
		System.out.println("ForceX: "+ forceX + ", ForceY" + forceY);
		System.out.println("---------------------");
	    }
	}
	System.out.println("##########END###########");
    }

    public void moveBodies() {	

	for (int i = 0; i < numObjects; i++) {
	    double deltavX = force[i].getX() / mass * timeStep;
	    double deltavY = force[i].getY() / mass * timeStep;	
	    
	    System.out.println("Force (x,y): "+ force[i].getX() + "," + force[i].getY());
	    System.out.println("Velocity (x,y): "+ velocity[i].getX() + "," + velocity[i].getY());

	    double xVelocity = velocity[i].getX();
	    double yVelocity = velocity[i].getY();

	    double xPosition = position[i].getX();
	    double yPosition = position[i].getY();

	    boolean isBreakXPlane = xPosition+radius >= 100 || xPosition-radius <= 0;
	    boolean isBreakYPlane = yPosition+radius >= 100 || yPosition-radius <= 0;

	    double newXValue = xVelocity + deltavX;
	    double newYValue = yVelocity + deltavY;

	    //Check if outside of bounds, if so flip direction
	    if (isBreakYPlane && isBreakXPlane) {
		velocity[i].setLocation(-1*newXValue, -1*newYValue);
		System.out.println("Object " +i+ " what is even going on.");
	    } else if (isBreakXPlane) {
		velocity[i].setLocation(-1*newXValue, newYValue);
		System.out.println("Object " +i+ " is past x threshold and has been flipped.");
	    } else if (isBreakYPlane) {
		velocity[i].setLocation(newXValue, -1*newYValue);
		System.out.println("Object " +i+ " is past y threshold and has been flipped.");
	    } else {
		velocity[i].setLocation(newXValue, newYValue);
	    }

	    double deltapX = (velocity[i].getX() + deltavX / 2) * timeStep;
	    double deltapY = (velocity[i].getY() + deltavY / 2) * timeStep;	 

	    position[i].setLocation(position[i].getX() + deltapX,
		    position[i].getY() + deltapY);

	    force[i].setLocation(0, 0);
	}
    }

    public void run() {
	for (int i = 0; i < numTimeSteps; i++) {
	    calculateForces();
	    moveBodies();
	    System.out.println("Location for 0: (" + position[0].getX() + ", " + position[0].getY() + ")");
	    System.out.println("Location for 1: (" + position[1].getX() + ", " + position[1].getY() + ")");
	}
    }
}