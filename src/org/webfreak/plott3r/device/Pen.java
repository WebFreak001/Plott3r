package org.webfreak.plott3r.device;

import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;

public class Pen {
	private RegulatedMotor xMotor;
	private RegulatedMotor zMotor;
	private EV3TouchSensor touch;

	private VariableMotor variableMotor;

	private double xCentimeters, xAbsolute;
	private boolean down = false;

	private double degreePerCm = 382.5 / 4.0;
	private int xScale = -1;
	private double width = 16.8;
	private double beltRadiusCentimeters = 1.8; // was 1.9
	private double translation = 1 / 3.0;

	public Pen(RegulatedMotor xMotor, RegulatedMotor zMotor, EV3TouchSensor touch) {
		this.xMotor = xMotor;
		this.zMotor = zMotor;
		this.touch = touch;
		xMotor.setAcceleration(Integer.MAX_VALUE);
		variableMotor = new VariableMotor(xMotor);
	}

	public void stop() {
		variableMotor.stop();
		stopWrite();
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
		getXMotor().setSpeed((int) (cmPerSecond * 180 / (Math.PI * beltRadiusCentimeters * translation)));
	}

	public void reset() {
		getZMotor().setSpeed(100);
		getXMotor().setSpeed(240);

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
		xAbsolute = xCentimeters + cm;
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
		xAbsolute += error / degreePerCm;

		getXMotor().rotate((int)(rounded * xScale), async);
		//try {
		//	variableMotor.rotateBy((int)(rounded * xScale), 1, async);
		//} catch (InterruptedException e) {
		//	e.printStackTrace();
		//}
	}
	
	public void moveToX(double cm) {
		moveToX(cm, false);
	}
	
	public void moveToX(double cm, boolean async) {
		moveX(cm - xAbsolute, async);
		xAbsolute = cm;
	}
	
	public double getX() {
		return xAbsolute;
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
