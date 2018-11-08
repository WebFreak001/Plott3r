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
		return new SVGPoint(matrix.getA() * x + matrix.getB() * y + matrix.getC(),
				matrix.getD() * x + matrix.getE() * y + matrix.getF());
	}
}