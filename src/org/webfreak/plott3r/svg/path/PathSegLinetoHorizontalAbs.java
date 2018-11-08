package org.webfreak.plott3r.svg.path;

public class PathSegLinetoHorizontalAbs extends PathSeg {
	private double x;

	public PathSegLinetoHorizontalAbs(double x) {
		this.pathSegType = PathSeg.PATHSEG_LINETO_HORIZONTAL_ABS;
		this.x = x;
	}

	/**
	 * @return the absolute X coordinate for the end point of this path segment.
	 */
	public double getX() {
		return x;
	}
}
