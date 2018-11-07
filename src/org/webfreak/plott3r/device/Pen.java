package org.webfreak.plott3r.device;

import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;

public class Pen {
	private RegulatedMotor xMotor;
	private RegulatedMotor zMotor;
	private EV3TouchSensor touch;

	private double xCentimeters;
	private boolean down = false;

	private double degreePerCm = 382.5 / 12.0;
	private int xScale = -1;
	private double width = 18.5;
	private double beltRadiusCentimeters = 1.9;

	public Pen(RegulatedMotor xMotor, RegulatedMotor zMotor, EV3TouchSensor touch) {
		this.xMotor = xMotor;
		this.zMotor = zMotor;
		this.touch = touch;
	}

	public RegulatedMotor getXMotor() {
		return xMotor;
	}

	public void setXMotor(RegulatedMotor xMotor) {
		this.xMotor = xMotor;
	}

	public RegulatedMotor getZMotor() {
		return zMotor;
	}

	public void setZMotor(RegulatedMotor zMotor) {
		this.zMotor = zMotor;
	}

	public void setSpeed(double cmPerSecond) {
		getXMotor().setSpeed((int) (cmPerSecond * 180 / (Math.PI * beltRadiusCentimeters)));
	}

	public void reset() {
		getZMotor().setSpeed(100);
		getXMotor().setSpeed(200);

		getZMotor().rotate(-5);
		getZMotor().rotate(5);
		getZMotor().flt();
		getZMotor().resetTachoCount();
		xCentimeters = 0;
		if (xScale < 0)
			getXMotor().forward();
		else
			getXMotor().backward();

		float[] sample = new float[1];
		while (true) {
			touch.getTouchMode().fetchSample(sample, 0);
			if (sample[0] > 0.5) {
				getXMotor().flt();
				break;
			}
		}

		setSpeed(1.5);
	}

	public void moveX(double cm) {
		moveX(cm, false);
	}

	public void moveX(double cm, boolean async) {
		if (xCentimeters + cm < 0) {
			cm = -xCentimeters;
			xCentimeters = 0;
		} else if (xCentimeters + cm > width) {
			cm = width - xCentimeters;
			xCentimeters = width;
		} else {
			xCentimeters += cm;
		}
		double degrees = cm * degreePerCm;
		int rounded = (int) Math.round(degrees);

		double error = degrees - rounded;
		xCentimeters += error / degreePerCm;

		getXMotor().rotate(rounded * xScale, async);
	}
	
	public void moveToX(double cm) {
		moveToX(cm, false);
	}
	
	public void moveToX(double cm, boolean async) {
		moveX(cm - xCentimeters, async);
	}
	
	public double getX() {
		return xCentimeters;
	}

	public void startWrite() {
		if (down)
			return;
		getZMotor().rotateTo(-30);
		down = true;
	}

	public void stopWrite() {
		if (!down)
			return;
		getZMotor().rotateTo(0);
		down = false;
	}
}
