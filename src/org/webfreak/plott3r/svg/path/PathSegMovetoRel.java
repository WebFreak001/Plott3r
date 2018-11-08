package org.webfreak.plott3r.svg.path;

public class PathSegMovetoRel extends PathSeg {
	private double x, y;

	public PathSegMovetoRel(double x, double y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the relative X coordinate for the end point of this path segment.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the relative Y coordinate for the end point of this path segment.
	 */
	public double getY() {
		return y;
	}
}
