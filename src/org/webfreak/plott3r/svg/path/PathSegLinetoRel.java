package org.webfreak.plott3r.svg.path;

public class PathSegLinetoRel extends PathSeg {
	private double x, y;

	public PathSegLinetoRel(double x, double y) {
		this.pathSegType = PathSeg.PATHSEG_LINETO_REL;
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
