package org.webfreak.plott3r;

import org.webfreak.plott3r.device.Board;
import org.webfreak.plott3r.device.Pen;

import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;

public class Main {
	public static void main(String[] args) {
		Pen pen = new Pen(Motor.C, Motor.D, new EV3TouchSensor(SensorPort.S3));
		pen.reset();

		EV3TouchSensor debugButton = new EV3TouchSensor(SensorPort.S1);
		Motor.C.setSpeed(15);

		float[] sample = new float[1];
		while (true) {
			debugButton.getTouchMode().fetchSample(sample, 0);
			if (sample[0] < 0.5f)
				break;
			Motor.D.rotate(15);
		}

		Board board = new Board(Motor.A);
		board.moveY(1);
		board.resetCoordinates();

		Canvas canvas = new Canvas(pen, board);
		// canvas.drawPath("M12,21.35L10.55,20.03C5.4,15.36 2,12.27 2,8.5C2,5.41 4.42,3
		// 7.5,3C9.24,3 10.91,3.81 12,5.08C13.09,3.81 14.76,3 16.5,3C19.58,3 22,5.41
		// 22,8.5C22,12.27 18.6,15.36 13.45,20.03L12,21.35Z");

		canvas.moveTo(12, 21.35);
		canvas.lineTo(10.55, 20.03);
		canvas.bezierTo(5.4, 15.36, 2, 12.27, 2, 8.5);
		canvas.bezierTo(2, 5.41, 4.42, 3, 7.5, 3);
		canvas.bezierTo(9.24, 3, 10.91, 3.81, 12, 5.08);
		canvas.bezierTo(13.09, 3.81, 14.76, 3, 16.5, 3);
		canvas.bezierTo(19.58, 3, 22, 5.41, 22, 8.5);
		canvas.bezierTo(22, 12.27, 18.6, 15.36, 13.45, 20.03);
		canvas.lineTo(12, 21.35);

		pen.stopWrite();
	}
}
