package nBodyGravitationProblem;

public class nBodySequential {
	public static void main(String args[]) {
		long numWorkers = Long.parseLong(args[0]);
		int numBodies = Integer.parseInt(args[1]);
		double bodySize = Double.parseDouble(args[2]);
		int numTimeSteps = Integer.parseInt(args[3]);
		
		SequentialHandler s = new SequentialHandler(numBodies, numTimeSteps, bodySize);
		s.run();
	}
}
