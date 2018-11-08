package org.webfreak.plott3r.svg.path;

public class PathSegLinetoHorizontalRel extends PathSeg {
	private double x;

	public PathSegLinetoHorizontalRel(double x) {
		this.x = x;
	}

	/**
	 * @return the relative X coordinate for the end point of this path segment.
	 */
	public double getX() {
		return x;
	}
}
