package org.webfreak.plott3r;

import org.webfreak.plott3r.device.Board;
import org.webfreak.plott3r.device.Pen;
import org.webfreak.plott3r.font.Font;

import lejos.hardware.Sound;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3TouchSensor;
import lejos.utility.Delay;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;


public class Main {
	public static void main(String[] args) {
		EV3TouchSensor debugButton = new EV3TouchSensor(SensorPort.S2);

		float[] sample = new float[1];
		Motor.B.setSpeed(15);
		Motor.B.forward();
		while (true) {
			debugButton.getTouchMode().fetchSample(sample, 0);
			if (sample[0] < 0.5f)
				break;
		}
		Motor.B.flt();

		Pen pen = new Pen(Motor.C, Motor.B, new EV3TouchSensor(SensorPort.S3));
		pen.reset();

		Board board = new Board(Motor.D, new EV3ColorSensor(SensorPort.S4));
		board.pullInPaper();
		board.resetCoordinates();

		Canvas canvas = new Canvas(pen, board);

		Font font = new Font();

		try {
			draw(canvas);
			//canvas.scale(0.1);
			//canvas.drawPath(font.getCharacter('A').getPath());
		} finally {
			pen.stop();
		}

		Sound.twoBeeps();
		Delay.msDelay(5000);

		//Pen pen = new Pen(Motor.C, Motor.B, new EV3TouchSensor(SensorPort.S3));
		//Board board = new Board(Motor.D, new EV3ColorSensor(SensorPort.S4));

		//calibrate(pen, board);
	}

	public static void draw(Canvas canvas) {
		canvas.scale(0.03);
		canvas.drawPath("M305.214,374.779c2.463,0,3.45,0.493,3.45,0.493l1.478-6.241c0,0,1.15,4.763,1.643,9.034\n" +
				"\t\tc0.493,4.271,8.048,1.479,14.454,0.164c6.405-1.314,7.72-11.662,7.72-11.662h59.294c0,0-35.807,10.841-26.772,34.656\n" +
				"\t\tc0,0-52.889-8.048-61.101,24.967h-0.001c-8.212-33.015-61.101-24.967-61.101-24.967c9.034-23.815-26.772-34.656-26.772-34.656\n" +
				"\t\th59.294c0,0,1.314,10.348,7.719,11.662c6.406,1.314,13.962,4.106,14.454-0.164c0.493-4.271,1.643-9.034,1.643-9.034l1.479,6.241\n" +
				"\t\tc0,0,0.985-0.493,3.449-0.493H305.214L305.214,374.779z");
		//canvas.moveTo(0,0);
		//canvas.lineTo(5,5);

	}

	public static void calibrate(final Pen pen, final Board board) {
		Runnable calibrateBoard = new Runnable() {
            @Override
            public void run() {
				System.out.println("Starting Board calibration");
				board.getYMotor().forward();

				System.out.println("Finishing Board calibration");
            }
        };

        ExecutorService executor = Executors.newFixedThreadPool(2);
        ThreadPoolExecutor pool = (ThreadPoolExecutor) executor;

        executor.submit(calibrateBoard);


	}
}
