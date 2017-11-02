package nBodyGravitationProblem;

import java.awt.Point;

public class SequentialHandler {
	
	long numObjects, timeStep;
	double bodySize;
	
	final double GRAV_CONSTANT = 6.67 * (10^(-11));
	
	private Point position[];
	private Point velocity[];
	private Point force[];
	private double mass[];
	
	public SequentialHandler(int numObjects, int timeStep, double bodySize) {
		this.numObjects = numObjects;
		this.timeStep = timeStep;
		this.bodySize = bodySize;
		
		position = new Point[numObjects];
		velocity = new Point[numObjects];
		force = new Point[numObjects];
		mass = new double[numObjects];
	}
	
	public void calculateForces() {
		double dist = 0, magnitude = 0;
		Point direction = new Point(0, 0);
		
		for (int i = 0; i < numObjects - 1; i++) {
			for (int j = i+1; j < numObjects; j++) {
				dist = Math.sqrt(Math.pow((position[i].getX() - position[j].getX()), 2) + 
						Math.pow((position[i].getY() - position[j].getY()), 2));
				
				magnitude = (GRAV_CONSTANT * mass[i] * mass[j]) / Math.pow(dist, 2);
				
				direction.setLocation(position[j].getX() - position[i].getX(),
						position[j].getY() - position[i].getY());
				
				double forceX = magnitude * direction.getX() / dist;
				double forceY = magnitude * direction.getY() / dist;
				
				force[i].setLocation(force[i].getX() + forceX, force[i].getY() + forceY);
				force[j].setLocation(force[j].getX() - forceX, force[j].getY() - forceY);
			}
		}
	}
	
	public void 

	public void run() {
		// TODO Auto-generated method stub
		
	}
}