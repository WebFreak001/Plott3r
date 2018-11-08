package org.webfreak.plott3r.svg.path;

public class PathSegCurvetoCubicRel extends PathSeg {
	private double x1, y1;
	private double x2, y2;
	private double endX, endY;

	public PathSegCurvetoCubicRel(double x1, double y1, double x2, double y2, double endX, double endY) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.endX = endX;
		this.endY = endY;
	}

	/**
	 * @return the relative X coordinate for the first control point.
	 */
	public double getX1() {
		return x1;
	}

	/**
	 * @return the relative Y coordinate for the first control point.
	 */
	public double getY1() {
		return y1;
	}

	/**
	 * @return the relative X coordinate for the second control point.
	 */
	public double getX2() {
		return x2;
	}

	/**
	 * @return the relative Y coordinate for the second control point.
	 */
	public double getY2() {
		return y2;
	}

	/**
	 * @return the relative X coordinate for the end point of this path segment.
	 */
	public double getEndX() {
		return endX;
	}

	/**
	 * @return the relative Y coordinate for the end point of this path segment.
	 */
	public double getEndY() {
		return endY;
	}
}
