package nBodyGravitationProblem;

import java.math.RoundingMode;
import java.text.DecimalFormat;

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

    public void setX(double a) {
	this.x = a;
    }

    public void setY(double a) {
	this.y = a;
    }
    
    public void setLocation(double a, double b) {
	x = a;
	y = b;
    }
    
    public void addToX(double a) {
	x += a;
    }

    public void addToY(double a) {
	y += a;
    }
    
    public String toString() {
	DecimalFormat df = new DecimalFormat("#.##");
	df.setRoundingMode(RoundingMode.CEILING);
	return df.format(x) + "," + df.format(y) + ",,";
    }
}
