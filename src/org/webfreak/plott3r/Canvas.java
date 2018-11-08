package org.webfreak.plott3r;

import org.webfreak.plott3r.device.Board;
import org.webfreak.plott3r.device.Pen;
import org.webfreak.plott3r.svg.SVGPoint;
import org.webfreak.plott3r.svg.path.Path;
import org.webfreak.plott3r.svg.path.PathParser;
import org.webfreak.plott3r.svg.path.PathSeg;
import org.webfreak.plott3r.svg.path.PathSegArcAbs;
import org.webfreak.plott3r.svg.path.PathSegClosePath;
import org.webfreak.plott3r.svg.path.PathSegCurvetoCubicAbs;
import org.webfreak.plott3r.svg.path.PathSegCurvetoCubicRel;
import org.webfreak.plott3r.svg.path.PathSegLinetoAbs;
import org.webfreak.plott3r.svg.path.PathSegLinetoHorizontalAbs;
import org.webfreak.plott3r.svg.path.PathSegLinetoHorizontalRel;
import org.webfreak.plott3r.svg.path.PathSegLinetoRel;
import org.webfreak.plott3r.svg.path.PathSegLinetoVerticalAbs;
import org.webfreak.plott3r.svg.path.PathSegLinetoVerticalRel;
import org.webfreak.plott3r.svg.path.PathSegMovetoAbs;
import org.webfreak.plott3r.svg.path.PathSegMovetoRel;

import lejos.robotics.RegulatedMotor;

public class Canvas {
	private Pen pen;
	private Board board;

	private double scale = 0.5;

	private double baseSpeed = 5;

	public Canvas(Pen pen, Board board) {
		this.pen = pen;
		this.board = board;
	}

	public void lineTo(double x, double y) {
		pen.startWrite();
		moveToNoChange(x, y);
	}

	/**
	 * Moves the pen to the specified position (only when a draw call follows) in
	 * centimeters before any transformation.
	 *
	 * @param x The x coordinate to move to in centimeters without any
	 *          transformation.
	 * @param y The y coordinate to move to in centimeters without any
	 *          transformation.
	 */
	public void moveTo(double x, double y) {
		moveTo(x, y, false);
	}

	/**
	 * Moves the pen to the specified position (possibly without draw calls) in
	 * centimeters before any transformation.
	 *
	 * @param x      The x coordinate to move to in centimeters without any
	 *               transformation.
	 * @param y      The y coordinate to move to in centimeters without any
	 *               transformation.
	 * @param strict If true, the pen will move even if there is no draw command
	 *               following in the path.
	 */
	public void moveTo(double x, double y, boolean strict) {
		// TODO: implement strict
		pen.stopWrite();
		moveToNoChange(x, y);
	}

	public void bezierTo(double c1x, double c1y, double c2x, double c2y, double endX, double endY) {
		bezierTo(new Vector2d(c1x, c1y), new Vector2d(c2x, c2y), new Vector2d(endX, endY));
	}

	public void bezierTo(Vector2d c1, Vector2d c2, Vector2d end) {
		SVGPoint svgStart = getCurrentPoint();
		Vector2d start = new Vector2d(svgStart.getX(), svgStart.getY());

		int steps = (int) (Math.sqrt(start.subtract(c1).getLengthSquared() + c1.subtract(c2).getLengthSquared()
				+ c2.subtract(end).getLengthSquared()) * scale * 0.2);
		if (steps < 4)
			steps = 4;
		else if (steps > 32)
			steps = 32;

		for (double t = 0; t <= 1; t += 1 / (double) (steps)) {
			double t1 = (1 - t);
			double t2 = t1 * (1 - t);
			double t3 = t2 * (1 - t);
			Vector2d point = start.multiply(t3).add(c1.multiply(3 * t2 * t)).add(c2.multiply(3 * t1 * t * t))
					.add(end.multiply(t * t * t));
			lineTo(point.getX(), point.getY());
		}
	}

	private void moveToNoChange(double x, double y) {
		x *= scale;
		y *= scale;

		double cx = pen.getX();
		double cy = board.getY();

		double dx = x - cx;
		double dy = y - cy;

		moveRelativeNoChangePostTransform(dx, dy);
	}

	private void moveRelativeNoChange(double dx, double dy) {
		moveRelativeNoChangePostTransform(dx * scale, dy * scale);
	}

	private void moveRelativeNoChangePostTransform(double dx, double dy) {
		if (Math.abs(dx) < 0.01 && Math.abs(dy) < 0.01) {
			return;
		} else if (Math.abs(dx) < 0.01) {
			board.setSpeed(baseSpeed);
			board.moveY(dy);
		} else if (Math.abs(dy) < 0.01) {
			pen.setSpeed(baseSpeed);
			pen.moveX(dx);
		} else {
			double length = Math.sqrt(dx * dx + dy * dy);

			pen.setSpeed(dx / length * baseSpeed);
			board.setSpeed(dy / length * baseSpeed);
			board.getYMotor().synchronizeWith(new RegulatedMotor[] { pen.getXMotor() });
			board.getYMotor().startSynchronization();
			board.moveY(dy);
			pen.moveX(dx);
			board.getYMotor().endSynchronization();
			pen.getXMotor().waitComplete();
			board.getYMotor().waitComplete();
		}
	}

	public SVGPoint getCurrentPoint() {
		return new SVGPoint(pen.getX() / scale, board.getY() / scale);
	}

	/**
	 * Implements the Path defintion according to the w3 SVG 1.1 (Second Edition)
	 * Specification Chapter 8.3 (Paths)
	 * 
	 * @see https://www.w3.org/TR/SVG11/paths.html
	 */
	public void drawPath(String path) {
		Path draw = new PathParser().feed(path).build();
		SVGPoint pathStart = getCurrentPoint();
		SVGPoint currentPoint = pathStart;
		for (PathSeg segment : draw.getPathSegList()) {
			switch (segment.getPathSegType()) {
			case PathSeg.PATHSEG_UNKNOWN:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_UNKNOWN.");
			case PathSeg.PATHSEG_CLOSEPATH:
				lineTo(pathStart.getX(), pathStart.getY());
				currentPoint = pathStart;
			case PathSeg.PATHSEG_MOVETO_ABS:
				PathSegMovetoAbs ma = (PathSegMovetoAbs) segment;
				pathStart = currentPoint = new SVGPoint(ma.getX(), ma.getY());
				moveTo(currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_MOVETO_REL:
				PathSegMovetoRel mr = (PathSegMovetoRel) segment;
				pathStart = currentPoint = currentPoint.offset(mr.getX(), mr.getY());
				moveTo(currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_LINETO_ABS:
				PathSegLinetoAbs la = (PathSegLinetoAbs) segment;
				currentPoint = new SVGPoint(la.getX(), la.getY());
				lineTo(currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_LINETO_REL:
				PathSegLinetoRel lr = (PathSegLinetoRel) segment;
				currentPoint = currentPoint.offset(lr.getX(), lr.getY());
				lineTo(currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_CURVETO_CUBIC_ABS:
				PathSegCurvetoCubicAbs cca = (PathSegCurvetoCubicAbs) segment;
				SVGPoint cca1 = new SVGPoint(cca.getX1(), cca.getY1());
				SVGPoint cca2 = new SVGPoint(cca.getX2(), cca.getY2());
				currentPoint = new SVGPoint(cca.getEndX(), cca.getEndY());
				bezierTo(cca1.getX(), cca1.getY(), cca2.getX(), cca2.getY(), currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_CURVETO_CUBIC_REL:
				PathSegCurvetoCubicRel ccr = (PathSegCurvetoCubicRel) segment;
				SVGPoint ccr1 = currentPoint.offset(ccr.getX1(), ccr.getY1());
				SVGPoint ccr2 = currentPoint.offset(ccr.getX2(), ccr.getY2());
				currentPoint = currentPoint.offset(ccr.getEndX(), ccr.getEndY());
				bezierTo(ccr1.getX(), ccr1.getY(), ccr2.getX(), ccr2.getY(), currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_CURVETO_QUADRATIC_ABS:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_CURVETO_QUADRATIC_ABS.");
			case PathSeg.PATHSEG_CURVETO_QUADRATIC_REL:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_CURVETO_QUADRATIC_REL.");
			case PathSeg.PATHSEG_ARC_ABS:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_ARC_ABS.");
			case PathSeg.PATHSEG_ARC_REL:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_ARC_REL.");
			case PathSeg.PATHSEG_LINETO_HORIZONTAL_ABS:
				PathSegLinetoHorizontalAbs ha = (PathSegLinetoHorizontalAbs) segment;
				currentPoint = new SVGPoint(ha.getX(), currentPoint.getY());
				lineTo(currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_LINETO_HORIZONTAL_REL:
				PathSegLinetoHorizontalRel hr = (PathSegLinetoHorizontalRel) segment;
				currentPoint = currentPoint.offset(hr.getX(), 0);
				lineTo(currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_LINETO_VERTICAL_ABS:
				PathSegLinetoVerticalAbs va = (PathSegLinetoVerticalAbs) segment;
				currentPoint = new SVGPoint(currentPoint.getX(), va.getY());
				lineTo(currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_LINETO_VERTICAL_REL:
				PathSegLinetoVerticalRel vr = (PathSegLinetoVerticalRel) segment;
				currentPoint = currentPoint.offset(0, vr.getY());
				lineTo(currentPoint.getX(), currentPoint.getY());
			case PathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_ABS:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_CURVETO_CUBIC_SMOOTH_ABS.");
			case PathSeg.PATHSEG_CURVETO_CUBIC_SMOOTH_REL:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_CURVETO_CUBIC_SMOOTH_REL.");
			case PathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_CURVETO_QUADRATIC_SMOOTH_ABS.");
			case PathSeg.PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL:
				throw new IllegalStateException("Unimplemented pathseg type PATHSEG_CURVETO_QUADRATIC_SMOOTH_REL.");
			}
		}
	}
}
