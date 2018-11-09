package org.webfreak.plott3r.device;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;

public class Board {
	private RegulatedMotor yMotor;
	private EV3ColorSensor sensor;
	private double yCentimeters;
	private double yAbsolute;

	private double degreePerCm = 1000.0 / 12.3;
	private double wheelRadius = 2.15;
	private double height = 27;
	private int yScale = -1;
	private int gear1 = 12;
	private int gear2 = 36;

	public Board(RegulatedMotor yMotor, EV3ColorSensor sensor) {
		this.yMotor = yMotor;
		this.sensor = sensor;
	}

	public void setSpeed(double cmPerSecond) {
		getYMotor().setSpeed((int) ((cmPerSecond * 180.0 / (Math.PI * wheelRadius)) * gear2 / gear1));
	}

	public void moveY(double cm) {
		moveY(cm, false);
	}

	public void moveY(double cm, boolean async) {
		yAbsolute = yCentimeters + cm;
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
		yAbsolute += error / degreePerCm;

		getYMotor().rotate(rounded * yScale, async);
	}

	public void moveToY(double cm) {
		moveToY(cm, false);
	}

	public void moveToY(double cm, boolean async) {
		moveY(cm - yAbsolute, async);
		yAbsolute = cm;
	}

	public double getY() {
		return yAbsolute;
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

	public void pullInPaper() {
		getYMotor().setSpeed(200);

		SensorMode mode = sensor.getRedMode();
		sensor.setFloodlight(true);
		float[] sample = new float[mode.sampleSize()];
		int redTime = 0;
		long now = System.currentTimeMillis();
		long prev = now;
		while (true) {
			now = System.currentTimeMillis();
			mode.fetchSample(sample, 0);
			if (sample[0] > 0.35) {
				redTime += now - prev;
				getYMotor().flt();
				if (redTime > 1000)
					break;
			} else {
				redTime = 0;
				if (yScale < 0)
					getYMotor().backward();
				else
					getYMotor().forward();
			}
			prev = now;
		}

		prev = System.currentTimeMillis();
		redTime = 0;
		getYMotor().setSpeed(100);
		while (true) {
			now = System.currentTimeMillis();
			mode.fetchSample(sample, 0);
			if (sample[0] < 0.35) {
				redTime += now - prev;
				getYMotor().flt();
				if (redTime > 1000)
					break;
			} else {
				redTime = 0;
				if (yScale < 0)
					getYMotor().forward();
				else
					getYMotor().backward();
			}
			prev = now;
		}
		
		sensor.setFloodlight(false);
		yCentimeters = 2;
		yAbsolute = 2;
		moveY(-2);
		yCentimeters = 0;
		yAbsolute = 0;
	}
}
