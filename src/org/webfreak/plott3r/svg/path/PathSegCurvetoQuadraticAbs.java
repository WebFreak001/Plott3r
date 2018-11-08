package org.webfreak.plott3r.svg.path;

public class PathSegCurvetoQuadraticAbs extends PathSeg {
	private double x1, y1;
	private double endX, endY;

	public PathSegCurvetoQuadraticAbs(double x1, double y1, double endX, double endY) {
		this.pathSegType = PathSeg.PATHSEG_CURVETO_QUADRATIC_ABS;
		this.x1 = x1;
		this.y1 = y1;
		this.endX = endX;
		this.endY = endY;
	}

	/**
	 * @return the absolute X coordinate for the first control point.
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * @return the absolute Y coordinate for the first control point.
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * @return the absolute X coordinate for the end point of this path segment.
	 */
	public double getEndX() {
		return endX;
	}

	/**
	 * @return the absolute Y coordinate for the end point of this path segment.
	 */
	public double getEndY() {
		return endY;
	}
}
