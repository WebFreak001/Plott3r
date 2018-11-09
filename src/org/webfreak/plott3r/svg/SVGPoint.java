package org.webfreak.plott3r.svg;

public class SVGPoint {
	private double x, y;

	public SVGPoint(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public SVGPoint matrixTransform(SVGMatrix matrix) {
		/*
		 * [a c e]<br>
		 * [b d f]<br>
		 * [0 0 1]
		 */
		
		return new SVGPoint(matrix.getA() * x + matrix.getC() * y + matrix.getE(),
				matrix.getB() * x + matrix.getD() * y + matrix.getF());
	}

	public SVGPoint offset(double x, double y) {
		return new SVGPoint(this.x + x, this.y + y);
	}

	public SVGPoint clone() {
		return new SVGPoint(x, y);
	}
}