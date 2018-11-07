package org.webfreak.plott3r;

import org.webfreak.plott3r.device.Board;
import org.webfreak.plott3r.device.Pen;

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
		moveTo(x, y);
	}

	public void bezierTo(double c1x, double c1y, double c2x, double c2y, double endX, double endY) {
		bezierTo(new Vector2d(c1x, c1y), new Vector2d(c2x, c2y), new Vector2d(endX, endY));
	}

	public void bezierTo(Vector2d c1, Vector2d c2, Vector2d end) {
		Vector2d start = new Vector2d(pen.getX(), board.getY());

		for (double t = 0; t <= 1; t += 1 / 8.0) {
			double t1 = (1 - t);
			double t2 = t1 * (1 - t);
			double t3 = t2 * (1 - t);
			Vector2d point = start.multiply(t3)
					.add(c1.multiply(3 * t2 * t))
					.add(c2.multiply(3 * t1 * t * t))
					.add(end.multiply(t * t * t));
			lineTo(point.getX(), point.getY());
		}
	}
	
	public void moveTo(double x, double y) {
		pen.stopWrite();
		moveToNoChange(x,y);
	}

	public void moveToNoChange(double x, double y) {
		x *= scale;
		y *= scale;
		
		double cx = pen.getX();
		double cy = board.getY();

		double dx = x - cx;
		double dy = y - cy;

		if (Math.abs(dx) < 0.01) {
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

	public void drawPath(String path) {
	}
}
