package nBodyGravitationProblem;

public class Point {
    double x, y;
    
    public Point() {
	x = 0;
	y = 0;
    }
    
    public Point(double a, double b) {
	x = a;
	y = b;
    }
    
    public double getX() {
	return x;
    }

    public double getY() {
	return y;
    }
    
    public void setLocation(double a, double b) {
	x = a;
	y = b;
    }
    
    public String toString() {
	return "(" + x + ", " + y + ")";
    }
}
