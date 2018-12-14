package org.webfreak.plott3r.device;

import lejos.robotics.RegulatedMotor;
import org.webfreak.plott3r.functions.PlotFunction;

import java.util.concurrent.LinkedBlockingQueue;

public class VariableMotor {
	private RegulatedMotor motor;
	private final VariableMotorRegulator regulator;
	final LinkedBlockingQueue<VariableMotorPackage> workQueue;

	public VariableMotor(RegulatedMotor motor) {
		this.motor = motor;

		workQueue = new LinkedBlockingQueue<>();
		regulator = new VariableMotorRegulator(this);
		regulator.start();
	}

	@Override
	protected void finalize() throws Throwable {
		regulator.finalize();
		super.finalize();
	}

	public void rotateBy(int deg, PlotFunction curve) throws InterruptedException {
		rotateBy(deg, curve, false);
	}

	public void rotateBy(int deg, PlotFunction curve, boolean immediateReturn) throws InterruptedException {
		if (deg == 0) return;

		int start = motor.getTachoCount();
		int maxSpeed = motor.getSpeed();
		if (deg < 0)
			motor.backward();
		else
			motor.forward();
		System.out.println("Submitting job");
		if (immediateReturn)
			workQueue.put(new VariableMotorPackage(start, maxSpeed, deg, curve));
		else
			regulator.observeMotor(start, maxSpeed, deg, curve);
		System.out.println("Done");
	}

	public RegulatedMotor getMotor() {
		return motor;
	}

	public void stop() {
		try {
			regulator.exit();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void complete() {
		while (workQueue.peek() != null)
			Thread.yield();
		while (regulator.isWorking())
			Thread.yield();
	}
}

class VariableMotorPackage {
	private int start, maxSpeed, deg;
	private PlotFunction function;

	public VariableMotorPackage(int start, int maxSpeed, int deg, PlotFunction function) {
		this.start = start;
		this.maxSpeed = maxSpeed;
		this.deg = deg;
		this.function = function;
	}

	public int getStart() {
		return start;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public int getDeg() {
		return deg;
	}

	public PlotFunction getFunction() {
		return function;
	}
}

class VariableMotorRegulator implements Runnable {
	private Thread thread;
	private final VariableMotor motor;
	private boolean running;
	private boolean working;

	public VariableMotorRegulator(VariableMotor motor) {
		this.motor = motor;
		running = true;
		thread = new Thread(this);
	}

	@Override
	protected void finalize() throws Throwable {
		exit();
		super.finalize();
	}

	public void exit() throws InterruptedException {
		running = false;
		motor.workQueue.put(new VariableMotorPackage(0, 0, 0, null));
	}

	@Override
	public void run() {
		System.out.println("Job started");
		while (running) {
			try {
				working = false;
				VariableMotorPackage pkg = motor.workQueue.take();
				working = true;
				if (pkg == null || !running)
					break;
				observeMotor(pkg.getStart(), pkg.getMaxSpeed(), pkg.getDeg(), pkg.getFunction());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Job finished");
	}

	public void observeMotor(int start, int maxSpeed, int deg, PlotFunction function) throws InterruptedException {
		if (function == null)
			return;
		long startTime = System.nanoTime();
		while (!motor.getMotor().isMoving()) {
			if (System.nanoTime() - startTime > 1000000000)
				throw new InterruptedException("Waited for too long waiting for motor to start");
		}
		long expectedTime = (long) (Math.abs(deg / (double) maxSpeed) * 1000000000);
		while (System.nanoTime() < startTime + expectedTime) {
			int current = motor.getMotor().getTachoCount();

			long now = System.nanoTime();
			double t = (now - startTime) / (double) expectedTime;

			int expected = (int) (start + deg * Math.max(Math.min(1, function.map(t)), 0));
			double modifier = 1;
			if (current != expected) {
				double error = expected - current;
				error *= 0.05;
				error = Math.abs(error);
				if (error > 1)
					error = 1;
				double adjustment = 1 - (1 - 0.3) * error * error * error;
				if (deg > 0 ? current < expected : current > expected)
					modifier = 1 / adjustment;
				else
					modifier = adjustment;
			}
			int speed = (int) (maxSpeed * modifier * Math.max(Math.min(100, function.mapDerivative(t)), -100));
			if (speed == 0)
				motor.getMotor().flt(true);
			else
			{
				if (deg > 0 ? speed > 0 : speed < 0)
					motor.getMotor().forward();
				else
					motor.getMotor().backward();
				motor.getMotor().setSpeed(Math.abs(speed));
			}
		}
		motor.getMotor().setSpeed(maxSpeed);
		motor.getMotor().rotateTo(start + deg);
	}

	public void start() {
		System.out.println("Starting thread");
		thread.start();
	}

	public boolean isWorking() {
		return working;
	}
}
