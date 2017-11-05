package nBodyGravitationProblem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class ParallelHandler extends Thread {

    long numObjects, numTimeSteps;
    double mass;

    final double GRAV_CONSTANT = 6.67 * (10^(-11));
    int PLANE_SIZE = 0;

    final double timeStep = 1;
    private Random rand = new Random();
    private Point position[];
    private Point velocity[];
    private Point force[][];
    private double bodySize;
    private double radius;

    private int numWorkers;

    private int index;
    private int round = 0;

    BufferedWriter positionWriter;

    private int collisionCtr = 0;
    private Count countObject;
    private WriteMonitor writer;

    // private int spacingBetweenColumns;

    public ParallelHandler(int numObjects, int numTimeSteps, double bodySize, int index,
	    int numWorkers, Count countObject, WriteMonitor writer, Point position[],
	    Point[][] force, double mass, Point[] velocity) {
	this.numObjects = numObjects;
	this.numTimeSteps = numTimeSteps;
	this.bodySize = bodySize;
	this.index = index;
	this.numWorkers = numWorkers;
	this.countObject = countObject;
	this.writer = writer;
	this.position = position;
	this.force = force;
	this.mass = mass;

	PLANE_SIZE = (int) (numObjects * bodySize * 2);

	this.velocity = velocity;

	this.mass = mass;
	System.out.println("Mass: " + mass);

	try {
	    positionWriter = new BufferedWriter(new FileWriter("nBodyParallel" + index + ".csv"));
	} catch(IOException e) {
	    e.printStackTrace();
	}
	
	String str = "Round,,";

	for (int i = index; i < numObjects; i+= numWorkers) {
	    str += "Object " + i + ",,,";
	}

	str += "\n,,";

	for (int i = index; i < numObjects; i+= numWorkers) {
	    str += "x,y,,";
	}

	try {
	    positionWriter.write(str);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
    }

    public void calculateForces() {
	double dist = 0, magnitude = 0;
	Point direction = new Point(0, 0);

	for (int i = index; i < numObjects - 1; i += numWorkers) {
	    for (int j = i+1; j < numObjects; j++) {
		dist = Math.sqrt(Math.pow((position[i].getX() - position[j].getX()), 2) + 
			Math.pow((position[i].getY() - position[j].getY()), 2));

		if(dist <= bodySize) {
		    System.out.println("");
		    System.out.println("******COLLISION occurred between " + i + " and " + j + " *******");
		    writer.write(round -1, i, j, dist, position[i].getX(), position[j].getX(), position[i].getY(), position[j].getY());

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
		}

		magnitude = (GRAV_CONSTANT * mass * mass) / Math.pow(dist, 2);

		direction.setLocation(position[j].getX() - position[i].getX(),
			position[j].getY() - position[i].getY());

		double forceX = magnitude * direction.getX() / dist;
		double forceY = magnitude * direction.getY() / dist;
		// System.out.println("Magnitude = "+magnitude);
		// System.out.println("Distance = "+dist);
		// System.out.println("Direction = " + direction.toString());

		force[index][i].setLocation(force[index][i].getX() + forceX, force[index][i].getY() + forceY);
		force[index][j].setLocation(force[index][j].getX() - forceX, force[index][j].getY() - forceY);
		// System.out.println("ForceX: "+ forceX + ", ForceY" + forceY);
		// System.out.println("---------------------");
	    }
	}
    }

    public void moveBodies() {
	Point forceTemp = new Point();

	for (int i = index; i < numObjects; i+= numWorkers) {
	    for (int k = 1; k < numWorkers; k++) {
		forceTemp.addToX(force[k][i].getX());
		forceTemp.addToY(force[k][i].getY());

		force[k][i].setLocation(0, 0);
	    }

	    double deltavX = forceTemp.getX() / mass * timeStep;
	    double deltavY = forceTemp.getY() / mass * timeStep;

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
	    } else if (isBreakYPlane) {
		if (isBreakYPlaneHigh && yVelocity > 0) {
		    newYValue = -1*newYValue;
		} else if (isBreakYPlaneLow && yVelocity < 0) {
		    newYValue = -1*newYValue;
		}
		velocity[i].setLocation(newXValue, newYValue);
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

	    force[index][i].setLocation(0, 0);
	}
    }

    /*public void barrier() {
	int stage = 1;
	while(stage < numWorkers) {
	    sems[index].release();
	    while(true) {
		if(stages[(index + stage) % numWorkers] >= stage) {
		    try {
			sems[(index+stage) % numWorkers].acquire();
		    } catch(InterruptedException ie) {
			ie.printStackTrace();
		    }
		    break;
		}
		else
		    continue;
	    }
	    stage *= 2;
	    synchronized(ParallelHandler.class) {
		stages[index] = stage;
	    }
	}
    }*/

    public synchronized void barrier() {
	countObject.increment();
    }

    public void run() {
	for (int i = 0; i < numTimeSteps; i++) {
	    calculateForces();
	    barrier();
	    moveBodies();
	    barrier();

	    round++;

	    String str = "\n" + i + ",,";

	    for (int j = index; j < position.length; j+= numWorkers) {
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
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}
	System.out.println("Number of collisions for worker " + index + ": " + collisionCtr);
	writer.close();
    }
}