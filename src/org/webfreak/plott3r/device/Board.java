package org.webfreak.plott3r.device;

import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorMode;
import lejos.robotics.RegulatedMotor;
import org.webfreak.plott3r.functions.PlotFunction;

public class Board {
	private RegulatedMotor yMotor;
	private EV3ColorSensor sensor;
	private double yCentimeters;
	private double yAbsolute;

	private VariableMotor variableMotor;
	private PlotFunction plotFunction = null;

	private double degreePerCm = 1000.0 / 12.3;
	private double wheelRadius = 2.15;
	private double height = 30;
	private int yScale = -1;
	private int gear1 = 12;
	private int gear2 = 36;

	public Board(RegulatedMotor yMotor, EV3ColorSensor sensor) {
		this.yMotor = yMotor;
		this.sensor = sensor;

		this.variableMotor = new VariableMotor(yMotor);
	}

	public void stop() {
		variableMotor.stop();
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

		if (plotFunction == null) {
			getYMotor().rotate(rounded * yScale, async);
		} else {
			try {
				variableMotor.rotateBy((int) (rounded * yScale), plotFunction, async);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
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

	public PlotFunction getPlotFunction() {
		return plotFunction;
	}

	public void setPlotFunction(PlotFunction plotFunction) {
		this.plotFunction = plotFunction;
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
		pullInPaper(-2.5);
	}

	public void pullInPaper(double offsetCm) {
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
		yCentimeters = -offsetCm;
		yAbsolute = -offsetCm;
		moveY(offsetCm);
		yCentimeters = 0;
		yAbsolute = 0;
	}

	public void complete() {
		variableMotor.complete();
	}
}
