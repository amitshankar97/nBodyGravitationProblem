package nBodyGravitationProblem;

import java.util.Random;
import java.util.concurrent.Semaphore;

public class nBodyParrallel {

    static Point position[];
    static Point velocity[];

    public static void main(String args[]) {
	int numWorkers = Integer.parseInt(args[0]);
	int numBodies = Integer.parseInt(args[1]);
	double bodySize = Double.parseDouble(args[2]); // diameter
	int numTimeSteps = Integer.parseInt(args[3]);

	System.out.println("************************");
	System.out.println("Parameters:");
	System.out.println("numWorkers: " + numWorkers);
	System.out.println("numBodies: " + numBodies);
	System.out.println("bodySize(diameter): " + bodySize);
	System.out.println("numTimeSteps: " + numTimeSteps);
	System.out.println("radius: " + bodySize/2);
	System.out.println("************************");

	// ParallelHandler p = new ParallelHandler(numBodies, numTimeSteps, bodySize, i);
	Count countObject = new Count(numWorkers);
	WriteMonitor writer = new WriteMonitor(numWorkers);

	Semaphore[] sems = new Semaphore[numWorkers]; 
	for (int i = 0; i < numWorkers; i++) {
	    sems[i] = new Semaphore(0);
	}

	int[] workerStage = new int[numWorkers];
	for (int i = 0; i < numWorkers; i++) {
	    workerStage[i] = 1;
	}

	Thread[] worker = new Thread[ numWorkers];


	position = new Point[numBodies];
	Point[][] force = new Point[numWorkers][numBodies];
	velocity = new Point[numBodies];

	Random rand = new Random();
	double mass;

	do {
	    mass = (double) rand.nextInt(100);
	} while(mass == 0);


	    for (int i = 0; i < numWorkers; i++) {
		for (int j = 0; j < numBodies; j++) {
		    force[i][j] = new Point();
		}
	    }

	initializePosition(numBodies, bodySize);


	for (int i = 0; i < numWorkers; i++) {
	    worker[i] = new Thread(new ParallelHandler(numBodies, numTimeSteps,
		    bodySize, i, numWorkers, countObject, writer, position, force, mass, velocity));
	}

	for (int i = 0; i < worker.length; i++) {
	    worker[i].start();
	}

    }

    private static void initializePosition(int numBodies, double bodySize) {
	int PLANE_SIZE = (int) (numBodies * bodySize * 2);
	double radius = bodySize/2;

	Random rand = new Random();

	for (int i = 0; i < numBodies; i++) {
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
	}

    }
}
