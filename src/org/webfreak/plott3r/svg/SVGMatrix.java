package org.webfreak.plott3r.svg;

/**
 * Many of SVG's graphics operations utilize 2x3 matrices of the form:
 * 
 * [a c e]<br>
 * [b d f]
 * 
 * which, when expanded into a 3x3 matrix for the purposes of matrix arithmetic,
 * become:
 * 
 * [a c e]<br>
 * [b d f]<br>
 * [0 0 1]
 */
public class SVGMatrix {
	private double a, b, c, d, e, f;

	public SVGMatrix(double a, double b, double c, double d, double e, double f) {
		this.a = a;
		this.b = b;
		this.c = c;
		this.d = d;
		this.e = e;
		this.f = f;
	}

	public double getA() {
		return a;
	}

	public double getB() {
		return b;
	}

	public double getC() {
		return c;
	}

	public double getD() {
		return d;
	}

	public double getE() {
		return e;
	}

	public double getF() {
		return f;
	}

	/**
	 * Performs matrix multiplication. This matrix is post-multiplied by another
	 * matrix, returning the resulting new matrix.
	 * 
	 * @param secondMatrix the matrix which is post-multiplied to this matrix.
	 * @return the resulting matrix.
	 */
	public SVGMatrix multiply(SVGMatrix secondMatrix) {
		return new SVGMatrix(a * secondMatrix.a + c * secondMatrix.b, a * secondMatrix.c + c * secondMatrix.d,
				a * secondMatrix.e + c * secondMatrix.f + e, b * secondMatrix.a + d * secondMatrix.b,
				b * secondMatrix.c + d * secondMatrix.d, b * secondMatrix.e + d * secondMatrix.f + f);
	}

	/**
	 * @return The inverse matrix.
	 */
	public SVGMatrix inverse() {
		return new SVGMatrix(d, b, 0, c * f - e * d, a * f - e * b, a * d - c * b);
	}

	/**
	 * Post-multiplies a translation transformation on the current matrix and returns the resulting matrix.
	 * @param x The distance to translate along the x-axis.
	 * @param y The distance to translate along the y-axis.
	 * @return The resulting matrix.
	 */
	public SVGMatrix translate(double x, double y) {
		return multiply(new SVGMatrix(1, 0, x, 0, 1, y));
	}

	/**
	 * Post-multiplies a uniform scale transformation on the current matrix and returns the resulting matrix.
	 * @param scaleFactor Scale factor in both X and Y.
	 * @return The resulting matrix.
	 */
	public SVGMatrix scale(double scaleFactor) {
		return multiply(new SVGMatrix(scaleFactor, 0, 0, 0, scaleFactor, 0));
	}

	/**
	 * Post-multiplies a non-uniform scale transformation on the current matrix and returns the resulting matrix.
	 * @param scaleFactorX Scale factor in X.
	 * @param scaleFactorY Scale factor in Y.
	 * @return The resulting matrix.
	 */
	public SVGMatrix scaleNonUniform(double scaleFactorX, double scaleFactorY) {
		return multiply(new SVGMatrix(scaleFactorX, 0, 0, 0, scaleFactorY, 0));
	}

	/**
	 * Post-multiplies a rotation transformation on the current matrix and returns the resulting matrix.
	 * @param angle Rotation angle.
	 * @return The resulting matrix.
	 */
	public SVGMatrix rotate(double angle) {
		double cos = Math.cos(angle);
		double sin = Math.cos(angle);
		return multiply(new SVGMatrix(cos, -sin, 0, sin, cos, 0));
	}

	/**
	 * Post-multiplies a rotation transformation on the current matrix and returns the resulting matrix. The rotation angle is determined by taking (+/-) atan(y/x). The direction of the vector (x, y) determines whether the positive or negative angle value is used.
	 * @param x The X coordinate of the vector (x,y). Must not be zero.
	 * @param y The Y coordinate of the vector (x,y). Must not be zero.
	 * @throws IllegalArgumentException Raised if one of the parameters has an invalid value.
	 * @return The resulting matrix.
	 */
	public SVGMatrix rotateFromVector(double x, double y) throws IllegalArgumentException {
		if (x == 0)
			throw new IllegalArgumentException("x is 0");
		if (y == 0)
			throw new IllegalArgumentException("y is 0");
		return rotate(Math.atan(y / x));
	}

	/**
	 * Post-multiplies the transformation [-1 0 0 1 0 0] and returns the resulting matrix.
	 * @return The resulting matrix.
	 */
	public SVGMatrix flipX() {
		return multiply(new SVGMatrix(-1, 0, 0, 1, 0, 0));
	}

	/**
	 * Post-multiplies the transformation [1 0 0 -1 0 0] and returns the resulting matrix.
	 * @return The resulting matrix.
	 */
	public SVGMatrix flipY() {
		return multiply(new SVGMatrix(1, 0, 0, -1, 0, 0));
	}

	/**
	 * Post-multiplies a skewX transformation on the current matrix and returns the resulting matrix.
	 * @param angle Skew angle.
	 * @return The resulting matrix.
	 */
	public SVGMatrix skewX(double angle) {
		return multiply(new SVGMatrix(1, angle, 0, 1, 0, 0));
	}

	/**
	 * Post-multiplies a skewY transformation on the current matrix and returns the resulting matrix.
	 * @param angle Skew angle.
	 * @return The resulting matrix.
	 */
	public SVGMatrix skewY(double angle) {
		return multiply(new SVGMatrix(1, 0, angle, 1, 0, 0));
	}
}