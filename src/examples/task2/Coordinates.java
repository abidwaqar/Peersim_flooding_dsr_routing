package examples.task2;

import peersim.core.Protocol;

public class Coordinates implements Protocol {

	private double x, y;

	public Coordinates(String prefix) {
		/* Un-initialized coordinates are -1. */
		x = y = -1;
	}

	public Object clone() {
		Coordinates inp = null;
		try {
			inp = (Coordinates) super.clone();
		} catch (CloneNotSupportedException e) {
		}
		return inp;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double sqrDist(Coordinates other) {
		double x_dist = this.x - other.x;
		double y_dist = this.y - other.y;
		return x_dist * x_dist + y_dist * y_dist;
	}
	
	public double distance(Coordinates other) {
		return Math.sqrt(sqrDist(other));
	}
	
}