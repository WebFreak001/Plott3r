package org.webfreak.plott3r.svg.path;

public class PathSegArcAbs extends PathSeg {
	private double x, y;
	private double rX, rY;
	private double angle;
	private boolean largeArcFlag;
	private boolean sweepFlag;

	public PathSegArcAbs(double x, double y, double rX, double rY, double angle, boolean largeArcFlag,
			boolean sweepFlag) {
		this.x = x;
		this.y = y;
		this.rX = rX;
		this.rY = rY;
		this.angle = angle;
		this.largeArcFlag = largeArcFlag;
		this.sweepFlag = sweepFlag;
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

	/**
	 * @return the x-axis radius for the ellipse.
	 */
	public double getRX() {
		return rX;
	}

	/**
	 * @return the y-axis radius for the ellipse.
	 */
	public double getRY() {
		return rY;
	}

	/**
	 * @return the rotation angle in degrees for the ellipse's x-axis relative to
	 *         the x-axis of the user coordinate system.
	 */
	public double getAngle() {
		return angle;
	}

	/**
	 * Flag if the larger sweep of an arc should be drawn.
	 * <p>
	 * Of the four candidate arc sweeps, two will represent an arc sweep of greater
	 * than or equal to 180 degrees (the "large-arc"), and two will represent an arc
	 * sweep of less than or equal to 180 degrees (the "small-arc"). If
	 * large-arc-flag is '1', then one of the two larger arc sweeps will be chosen;
	 * otherwise, if large-arc-flag is '0', one of the smaller arc sweeps will be
	 * chosen.
	 * </p>
	 *
	 * @see https://www.w3.org/TR/SVG11/images/paths/arcs02.png
	 * @return the large arc flag attribute.
	 */
	public boolean hasLargeArcFlag() {
		return largeArcFlag;
	}

	/**
	 * Flag if the arc should be drawn in a "positive-angle" direction.
	 * <p>
	 * If sweep-flag is '1', then the arc will be drawn in a "positive-angle"
	 * direction (i.e., the ellipse formula x=cx+rx*cos(theta) and
	 * y=cy+ry*sin(theta) is evaluated such that theta starts at an angle
	 * corresponding to the current point and increases positively until the arc
	 * reaches (x,y)). A value of 0 causes the arc to be drawn in a "negative-angle"
	 * direction (i.e., theta starts at an angle value corresponding to the current
	 * point and decreases until the arc reaches (x,y)).
	 * </p>
	 *
	 * @see https://www.w3.org/TR/SVG11/images/paths/arcs02.png
	 * @return the sweep flag attribute.
	 */
	public boolean hasSweepFlag() {
		return sweepFlag;
	}
}
