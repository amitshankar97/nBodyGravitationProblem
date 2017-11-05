package nBodyGravitationProblem;

import java.awt.Color;
import java.util.ArrayList;

public class nBodyVisual {

    int count;
    int numWorkers;
    double conversionUnit;
    private int numBodies;
    double newBodySize;

    public nBodyVisual(int numWorkers, double bodySize, int numBodies, Point[] position) {
	this.numWorkers = numWorkers;
	this.numBodies = numBodies;
	count = 0;
	conversionUnit = 500 / ( (bodySize * numBodies) * 2);
	StdDraw.setCanvasSize(600, 600);
	StdDraw.setXscale(0, 600);
	StdDraw.setYscale(0, 600);
	
	StdDraw.setPenColor(StdDraw.WHITE);
	StdDraw.filledSquare(300, 300, 300);

	StdDraw.setPenColor(StdDraw.BLACK);
	StdDraw.square(300, 300, 250);
	
	System.out.println("conversionUnit: " + conversionUnit);
	newBodySize = conversionUnit * bodySize;
	System.out.println("newBodySize: " + newBodySize);

	StdDraw.setPenColor(StdDraw.BLACK);
	for (int i = 0; i < position.length; i++) {
	    StdDraw.circle(50+(position[i].getX() * conversionUnit), (position[i].getY() * conversionUnit)+50, newBodySize / 2);
	}
    }

    public synchronized void draw(Point[] position, ArrayList<Integer> collisions) {
	count++;
	if(count == numWorkers) {
	    count = 0;
	    StdDraw.clear(StdDraw.WHITE);
	    StdDraw.setPenColor(StdDraw.BLACK);
	    StdDraw.square(300, 300, 250);
	    for (int i = 0; i < position.length; i++) {
		if(collisions.contains(i))
		    StdDraw.setPenColor(StdDraw.RED);
		else
		    StdDraw.setPenColor(StdDraw.BLACK);
		StdDraw.circle(50+(position[i].getX() * conversionUnit), (position[i].getY() * conversionUnit)+50, newBodySize / 2);
	    }
	}
    }

}