package org.webfreak.plott3r.svg.path;

public class PathSegLinetoVerticalAbs extends PathSeg {
	private double y;

	public PathSegLinetoVerticalAbs(double y) {
		this.y = y;
	}

	/**
	 * @return the absolute Y coordinate for the end point of this path segment.
	 */
	public double getY() {
		return y;
	}
}
