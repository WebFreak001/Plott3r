package org.webfreak.plott3r;

import lejos.hardware.lcd.LCD;
import org.webfreak.plott3r.device.Board;
import org.webfreak.plott3r.device.Pen;

import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.robotics.RegulatedMotor;
import lejos.utility.Delay;
import org.webfreak.plott3r.device.VariableMotor;
import org.webfreak.plott3r.functions.CircularPlotFunction;
import org.webfreak.plott3r.functions.PlotFunction;

public class Main {
	public static void main(String[] args) {
		EV3TouchSensor debugButton = new EV3TouchSensor(SensorPort.S1);

		float[] sample = new float[1];
		Motor.D.setSpeed(15);
		Motor.D.forward();
		while (true) {
			debugButton.getTouchMode().fetchSample(sample, 0);
			if (sample[0] < 0.5f)
				break;
		}
		Motor.D.flt();

		Pen pen = new Pen(Motor.C, Motor.D, new EV3TouchSensor(SensorPort.S3));
		pen.reset();

		Board board = new Board(Motor.A, new EV3ColorSensor(SensorPort.S2));
		board.pullInPaper();
		board.resetCoordinates();

		//VariableMotor test = new VariableMotor(Motor.C);
		//test.rotateBy(100, 1);

		Canvas canvas = new Canvas(pen, board);
		try {
			draw(canvas);
		} finally {
			board.stop();
			pen.stop();
		}

		Sound.twoBeeps();
		Delay.msDelay(5000);
	}

	public static void draw(Canvas canvas) {
		// canvas.drawPath(
		// "M12,21.35L10.55,20.03C5.4,15.36 2,12.27 2,8.5C2,5.41 4.42,37.5,3C9.24,3
		// 10.91,3.81 12,5.08C13.09,3.81 14.76,3 16.5,3C19.58,3 22,5.4122,8.5C22,12.27
		// 18.6,15.36 13.45,20.03L12,21.35Z");

		canvas.moveTo(4, 0);
		canvas.bezierTo(8, 4, 0, 8, 4, 12);

		//canvas.translate(1, 2);
		//canvas.scale(2);
		//canvas.moveTo(1, 1);
		//canvas.lineTo(8, 1);
		//canvas.moveTo(1, 1);
		//canvas.lineTo(1, 8);
		//canvas.moveTo(1, 1);
		//canvas.lineTo(6, 6);

		// translate + scale = move, then scale
		// scale + translate = scale coordinate system, then move
		// scale/rotate order doesn't matter

		//canvas.translate(3, 3);
		/*for (int i = 0; i < 8; i++) {
			canvas.loadIdentity();
			canvas.rotate(Math.PI);
			canvas.translate(-8.6, -10);
			canvas.rotate(i / 4.0 * Math.PI);
			canvas.scale(0.25, 0.25);

			canvas.moveTo(12 - 12, -21.35);
			canvas.lineTo(10.55 - 12, -20.03);
			canvas.bezierTo(5.4 - 12, -15.36, 2 - 12, -12.27, 2 - 12, -8.5);
			canvas.bezierTo(2 - 12, -5.41, 4.42 - 12, -3, 7.5 - 12, -3);
			canvas.bezierTo(9.24 - 12, -3, 10.91 - 12, -3.81, 12 - 12, -5.08);
			canvas.bezierTo(13.09 - 12, -3.81, 14.76 - 12, -3, 16.5 - 12, -3);
			canvas.bezierTo(19.58 - 12, -3, 22 - 12, -5.41, 22 - 12, -8.5);
			canvas.bezierTo(22 - 12, -12.27, 18.6 - 12, -15.36, 13.45 - 12, -20.03);
			canvas.lineTo(12 - 12, -21.35);
		}*/

		//canvas.loadIdentity();
		//canvas.translate(0, -291.70835);
		//canvas.drawPath("M 10.75186,6.96671 H 9.9847153 V 6.09815 H 10.75186 m -3.8357249,0.86856 H 6.14899 v -0.86856 h 0.7671451 m 4.2423109,-1.59815 1.004962,-1.13781 c 0.145757,-0.16504 0.145757,-0.44297 0,-0.61668 -0.15343,-0.16503 -0.398916,-0.16503 -0.544674,0 l -1.135374,1.28547 c -0.6137167,-0.33874 -1.3041469,-0.53851 -2.0329347,-0.53851 -0.7364593,0 -1.4268898,0.19977 -2.040606,0.54718 l -1.1430462,-1.29414 c -0.1457574,-0.16503 -0.3912439,-0.16503 -0.5370015,0 -0.153429,0.17371 -0.153429,0.45164 0,0.61668 l 1.00496,1.13781 c -1.1430461,0.95541 -1.8871769,2.46671 -1.8871769,4.20384 h 9.2057413 c 0,-1.73713 -0.767145,-3.25711 -1.89485,-4.20384 m 3.812712,5.07238 c -0.635527,0 -1.150721,0.58331 -1.150717,1.30285 v 6.07993 c 10e-7,0.71954 0.515194,1.30284 1.150717,1.30284 0.635524,0 1.150717,-0.5833 1.150718,-1.30284 v -6.07993 c 4e-6,-0.71954 -0.515191,-1.30285 -1.150718,-1.30285 m -13.041466,0 c -0.6355268,0 -1.15072193,0.58331 -1.15071764,1.30285 v 6.07993 c 5.9e-7,0.71954 0.51519424,1.30284 1.15071764,1.30284 0.6355234,0 1.1507171,-0.5833 1.1507177,-1.30284 v -6.07993 c 4.3e-6,-0.71954 -0.5151908,-1.30285 -1.1507177,-1.30285 m 1.9178627,8.68562 c 4e-7,0.47969 0.3434628,0.86856 0.7671451,0.86856 H 5.381845 v 3.03996 c -4.3e-6,0.71954 0.5151907,1.30285 1.1507175,1.30285 0.6355269,0 1.150722,-0.58331 1.1507177,-1.30285 v -3.03996 h 1.5342902 v 3.03996 c -4.3e-6,0.71954 0.5151902,1.30285 1.1507166,1.30285 0.635527,0 1.150723,-0.58331 1.150719,-1.30285 v -3.03996 h 0.767145 c 0.423682,0 0.767145,-0.38887 0.767145,-0.86856 v -8.68562 H 3.8475547 Z");

	}
}
