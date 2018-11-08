package org.webfreak.plott3r.svg.path;

public class PathSegLinetoVerticalRel extends PathSeg {
	private double y;

	public PathSegLinetoVerticalRel(double y) {
		this.y = y;
	}

	/**
	 * @return the relative Y coordinate for the end point of this path segment.
	 */
	public double getY() {
		return y;
	}
}
