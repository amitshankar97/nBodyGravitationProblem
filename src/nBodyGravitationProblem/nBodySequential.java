package nBodyGravitationProblem;

public class nBodySequential {
	public static void main(String args[]) {
		long numWorkers = Long.parseLong(args[0]);
		int numBodies = Integer.parseInt(args[1]);
		double bodySize = Double.parseDouble(args[2]); // diameter
		int numTimeSteps = Integer.parseInt(args[3]);
		
		System.out.println("************************");
		System.out.println("Parameters:");
		System.out.println("numWorkers: " + numWorkers);
		System.out.println("numBodies: " + numBodies);
		System.out.println("bodySize(diameter): " + bodySize);
		System.out.println("numTimeSteps: " + numTimeSteps);
		System.out.println("************************");
		
		SequentialHandler s = new SequentialHandler(numBodies, numTimeSteps, bodySize, 10);
		s.run();
	}
}
