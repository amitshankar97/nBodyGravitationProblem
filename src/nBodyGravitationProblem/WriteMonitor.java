package nBodyGravitationProblem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteMonitor {
    boolean isWriting;
    BufferedWriter collisionWriter;
    BufferedWriter resultWriter;

    boolean start = false;

    int numWorkers = 0;
    int numCloses = 0;
    int printResultsCount = 0;
    int finished = 0;

    long startTime, endTime;
    private int collisionCtr;

    public WriteMonitor(int numWorkers) {
	this.numWorkers = numWorkers;
	isWriting = false;
	try {
	    collisionWriter = new BufferedWriter(new FileWriter("collisionLog.csv"));
	    resultWriter = new BufferedWriter(new FileWriter("nBodyParallelResults.csv"));
	} catch (IOException e) {
	    e.printStackTrace();
	}

	try {
	    collisionWriter.write("Round, Object 1, Object 2, Distance, x1, x2, y1, y2");
	    resultWriter.write("Object, Position, Velocity");
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }

    public synchronized void printResults(long numObjects, Point[] position, Point[] velocity) {
	printResultsCount++;
	if(printResultsCount == numWorkers) {
	    try {
		for (int i = 0; i < numObjects; i++) {
		    try {
			resultWriter.write("\n" + i + "," + position[i].fileToString() + "," + velocity[i].fileToString());
		    } catch (IOException e) {
			e.printStackTrace();
		    }
		}
		resultWriter.close();
	    } catch(IOException e) {
		e.printStackTrace();
	    }
	}
    }

    public synchronized void write(int round, int obj1, int obj2, double dist, double x1,
	    double x2, double y1, double y2) {

	try {
	    collisionWriter.write("\n" + round + "," + obj1 + "," + obj2 + ","
		    + dist + "," +x1 + "," + x2 + "," + y1 + "," + y2);
	} catch (IOException e) {
	    e.printStackTrace();
	}

	if(isWriting) {
	    try {
		wait();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}

	isWriting = true;

	isWriting = false;
	notify();
    }

    public synchronized void start() {
	if(!start) {
	    startTime = System.nanoTime();
	    start = true;
	}
    }

    public synchronized void finish(int numCollisions) {
	finished++;
	collisionCtr += numCollisions;
	if(finished == numWorkers) {
	    endTime = System.nanoTime();

	    //Calculate Collisions and runtime
	    long programTime = endTime - startTime;
	    int seconds = 0;
	    long milliseconds = programTime/1000000;

	    while (milliseconds >= 1000) {
		seconds++;
		milliseconds = milliseconds - 1000;
	    }

	    System.out.println("Number of collisions: " + collisionCtr);
	    System.out.format("computation time: %d seconds, %d milliseconds", seconds, milliseconds);
	}
    }

    public synchronized void close() {
	numCloses++;
	if(numCloses == numWorkers)
	    try {
		collisionWriter.close();
	    } catch (IOException e) {
		e.printStackTrace();
	    }
    }
}
