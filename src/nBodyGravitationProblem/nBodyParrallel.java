package nBodyGravitationProblem;

import java.util.concurrent.Semaphore;

public class nBodyParrallel {
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
		
		Semaphore[] sems = new Semaphore[numWorkers]; 
		for (int i = 0; i < numWorkers; i++) {
		    sems[i] = new Semaphore(0);
		}
		
		int[] workerStage = new int[numWorkers];
		for (int i = 0; i < numWorkers; i++) {
		    workerStage[i] = 1;
		}
		
		Thread[] worker = new Thread[ numWorkers];
		for (int i = 0; i < numWorkers; i++) {
		    worker[i] = new Thread(new ParallelHandler(numBodies, numTimeSteps, bodySize, i, numWorkers, countObject));
		}
		
		for (int i = 0; i < worker.length; i++) {
		    worker[i].start();
		}
		
	}
}
