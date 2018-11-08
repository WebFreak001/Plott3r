package org.webfreak.plott3r.svg.path;

public class PathSegLinetoHorizontalRel extends PathSeg {
	private double x;

	public PathSegLinetoHorizontalRel(double x) {
		this.pathSegType = PathSeg.PATHSEG_LINETO_HORIZONTAL_REL;
		this.x = x;
	}

	/**
	 * @return the relative X coordinate for the end point of this path segment.
	 */
	public double getX() {
		return x;
	}
}
