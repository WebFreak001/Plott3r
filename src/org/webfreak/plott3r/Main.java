package org.webfreak.plott3r;

import org.webfreak.plott3r.device.Pen;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;

public class Main {
	public static void main(String[] args) {
		Pen pen = new Pen(Motor.C, Motor.D, new EV3TouchSensor(SensorPort.S3));
		pen.reset();
	}
}
