package org.webfreak.plott3r.svg.path;

public class PathSegCurvetoQuadraticSmoothRel extends PathSeg {
	private double x, y;

	public PathSegCurvetoQuadraticSmoothRel(double x, double y) {
		this.pathSegType = PathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL;
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
