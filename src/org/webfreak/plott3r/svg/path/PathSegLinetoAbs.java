package org.webfreak.plott3r.svg.path;

public class PathSegLinetoAbs extends PathSeg {
	private double x, y;

	public PathSegLinetoAbs(double x, double y) {
		this.pathSegType = PathSeg.PATHSEG_LINETO_ABS;
		this.x = x;
		this.y = y;
	}

	/**
	 * @return the absolute X coordinate for the end point of this path segment.
	 */
	public double getX() {
		return x;
	}

	/**
	 * @return the absolute Y coordinate for the end point of this path segment.
	 */
	public double getY() {
		return y;
	}
}
