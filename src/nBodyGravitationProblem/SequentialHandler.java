package nBodyGravitationProblem;

import java.awt.Point;
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

	public SequentialHandler(int numObjects, int numTimeSteps, double bodySize) {
		this.numObjects = numObjects;
		this.numTimeSteps = numTimeSteps;
		this.bodySize = bodySize;

		position = new Point[numObjects];
		velocity = new Point[numObjects];
		force = new Point[numObjects];

		Random rand = new Random();
		mass = rand.nextDouble();
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

		velocity[0].setLocation(15, 0);
		velocity[1].setLocation(-15, 0);
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
					System.out.println("******COLLISION occurred*******");
					System.out.println("");
				}

				magnitude = (GRAV_CONSTANT * mass * mass) / Math.pow(dist, 2);

				direction.setLocation(position[j].getX() - position[i].getX(),
						position[j].getY() - position[i].getY());

				double forceX = magnitude * direction.getX() / dist;
				double forceY = magnitude * direction.getY() / dist;

				force[i].setLocation(force[i].getX() + forceX, force[i].getY() + forceY);
				force[j].setLocation(force[j].getX() - forceX, force[j].getY() - forceY);
			}
		}
	}

	public void moveBodies() {
		Point deltav = new Point();
		Point deltap = new Point();

		for (int i = 0; i < numObjects; i++) {
			deltav.setLocation(force[i].getX() / mass * timeStep, 
					force[i].getY() / mass * timeStep); // try with parentheses

			deltap.setLocation((velocity[i].getX() + deltav.getX() / 2) * timeStep,
					(velocity[i].getY() + deltav.getY() / 2) * timeStep);

			double xVelocity = velocity[i].getX();
			double yVelocity = velocity[i].getY();

			//Check if outside of bounds, if so flip direction
			if((yVelocity <= 0 || yVelocity >= 100) && 
					(xVelocity <= 0 && xVelocity >= 100)) {
				velocity[i].setLocation(-1*(xVelocity + deltav.getX()),
						-1*(yVelocity + deltav.getY()));
				System.out.println("Object " +i+ " what is even going on.");
			} else if (xVelocity >= 100 || xVelocity <= 0) {
				velocity[i].setLocation(-1*(xVelocity + deltav.getX()),
						(yVelocity + deltav.getY()));
				System.out.println("Object " +i+ " is past x threshold and has been flipped.");
			} else if (yVelocity >= 100 || yVelocity <= 0) {
				velocity[i].setLocation((xVelocity + deltav.getX()),
						-1*(yVelocity + deltav.getY()));
				System.out.println("Object " +i+ " is past y threshold and has been flipped.");
			} else {
				velocity[i].setLocation(velocity[i].getX() + deltav.getX(),
						velocity[i].getY() + deltav.getY());
				System.out.println("Object " +i+ " shit.");
			}

			position[i].setLocation(position[i].getX() + deltap.getX(),
					position[i].getY() + deltap.getY());

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