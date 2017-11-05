package nBodyGravitationProblem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class WriteMonitor {
    boolean isWriting;
    BufferedWriter collisionWriter;

    int numWorkers = 0;
    int numCloses = 0;

    public WriteMonitor(int numWorkers) {
	this.numWorkers = numWorkers;
	isWriting = false;
	try {
	    collisionWriter = new BufferedWriter(new FileWriter("collisionLog.csv"));
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	}

	try {
	    collisionWriter.write("Round, Object 1, Object 2, Distance, x1, x2, y1, y2");
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }


    public synchronized void write(int round, int obj1, int obj2, double dist, double x1,
	    double x2, double y1, double y2) {
	/*while(isWriting) {
	    try {
		wait();
	    } catch (InterruptedException e) {
		e.printStackTrace();
	    }
	}*/

	try {
	    collisionWriter.write("\n" + round + "," + obj1 + "," + obj2 + ","
		    + dist + "," +x1 + "," + x2 + "," + y1 + "," + y2);
	} catch (IOException e) {
	    // TODO Auto-generated catch block
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

    public synchronized void close() {
	numCloses++;
	if(numCloses == numWorkers)
	    try {
		collisionWriter.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
    }
}
