package nBodyGravitationProblem;

public class Count {
    private int count;
    private int numWorkers;
    
    boolean first = true;

    public Count(int numWorkers) {
	count = 0;
	this.numWorkers = numWorkers;
    }
    
    public synchronized void setFalse() {
	first = false;
    }
    
    public synchronized boolean isFirst() {
	return first;
    }

    public synchronized void increment() {
	count++;

	if(count == numWorkers) {
	    notifyAll();
	    reset();
	} else
	    try {
		wait();
	    } catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
    }

    public void reset() {
	count = 0;
    }
}
