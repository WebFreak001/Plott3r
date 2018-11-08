package org.webfreak.plott3r.svg.path;

public class PathSegLinetoHorizontalAbs extends PathSeg {
	private double x;

	public PathSegLinetoHorizontalAbs(double x) {
		this.x = x;
	}

	/**
	 * @return the absolute X coordinate for the end point of this path segment.
	 */
	public double getX() {
		return x;
	}
}
