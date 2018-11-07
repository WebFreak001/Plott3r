package org.webfreak.plott3r.device;

import lejos.robotics.RegulatedMotor;

public class Board {
	private RegulatedMotor yMotor;
	private double yCentimeters;
	
	private double degreePerCm = 1000.0 / 12.3;
	private double wheelRadius = 2.15;
	private double height = 20;
	private int yScale = -1;
	private int gear1 = 12;
	private int gear2 = 36;

	public Board(RegulatedMotor yMotor) {
		this.yMotor = yMotor;
	}
	
	public void setSpeed(double cmPerSecond) {
		getYMotor().setSpeed((int) ((cmPerSecond * 180.0 / (Math.PI * wheelRadius)) * gear2 / gear1));
	}

	public void moveY(double cm) {
		moveY(cm, false);
	}
	
	public void moveY(double cm, boolean async) {
		if (yCentimeters + cm < 0) {
			cm = -yCentimeters;
			yCentimeters = 0;
		} else if (yCentimeters + cm > height) {
			cm = height - yCentimeters;
			yCentimeters = height;
		} else {
			yCentimeters += cm;
		}
		double degrees = cm * degreePerCm;
		int rounded = (int) Math.round(degrees);

		double error = degrees - rounded;
		yCentimeters += error / degreePerCm;

		getYMotor().rotate(rounded * yScale, async);
	}
	
	public void moveToY(double cm) {
		moveToY(cm, false);
	}
	
	public void moveToY(double cm, boolean async) {
		moveY(cm - yCentimeters, async);
	}
	
	public double getY() {
		return yCentimeters;
	}

	public RegulatedMotor getYMotor() {
		return yMotor;
	}

	public void setYMotor(RegulatedMotor yMotor) {
		this.yMotor = yMotor;
	}

	public void resetCoordinates() {
		this.yCentimeters = 0;
	}
}
