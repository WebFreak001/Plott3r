package org.webfreak.plott3r.device;

import lejos.hardware.sensor.AnalogSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;

public class Pen {
	private RegulatedMotor xMotor;
	private RegulatedMotor zMotor;
	private EV3TouchSensor touch;

	private double xCentimeters;
	private boolean down = false;

	private int xScale = -1;
	private double width = 17.6;
	private double beltRadiusCentimeters = 2.25;

	public Pen(RegulatedMotor xMotor, RegulatedMotor zMotor, EV3TouchSensor touch) {
		this.xMotor = xMotor;
		this.zMotor = zMotor;
		this.touch = touch;

		xMotor.setSpeed(40);
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

	public void reset() {
		stopWrite();
		xCentimeters = 0;
		if (xScale < 0)
			getXMotor().forward();
		else
			getXMotor().backward();

		while (true) {
			float[] sample = new float[1];
			touch.getTouchMode().fetchSample(sample, 0);
			if (sample[0] > 0.5)
				getXMotor().stop();
		}
	}

	public void moveX(double cm) {
		if (xCentimeters + cm < 0) {
			cm = -xCentimeters;
			xCentimeters = 0;
		} else if (xCentimeters + cm > width) {
			cm = width - xCentimeters;
			xCentimeters = width;
		}
		double degrees = (180.0 / (beltRadiusCentimeters * Math.PI)) * cm;
		int rounded = (int) Math.round(degrees);

		double error = degrees - rounded;
		xCentimeters -= (error / 180.0) * (Math.PI * beltRadiusCentimeters);

		getXMotor().rotate(rounded * xScale);
	}

	public void startWrite() {
		if (down)
			return;
		getZMotor().rotate(45);
	}

	public void stopWrite() {
		if (!down)
			return;
		getZMotor().rotate(-38);
	}
}
