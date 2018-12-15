package org.webfreak.plott3r.device;

import lejos.robotics.RegulatedMotor;

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

	public void rotateBy(int deg, double curve) throws InterruptedException {
		rotateBy(deg, curve, false);
	}

	public void rotateBy(int deg, double curve, boolean immediateReturn) throws InterruptedException {
		if (deg == 0) return;

		int start = motor.getTachoCount();
		int maxSpeed = motor.getSpeed();
		if (deg < 0)
			motor.backward();
		else
			motor.forward();
		System.out.println("Submitting job");
		if (immediateReturn)
			workQueue.put(new VariableMotorPackage(start, maxSpeed, deg));
		else
			regulator.observeMotor(start, maxSpeed, deg);
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
}

class VariableMotorPackage {
	private int start, maxSpeed, deg;

	public VariableMotorPackage(int start, int maxSpeed, int deg) {
		this.start = start;
		this.maxSpeed = maxSpeed;
		this.deg = deg;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getMaxSpeed() {
		return maxSpeed;
	}

	public void setMaxSpeed(int maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	public int getDeg() {
		return deg;
	}

	public void setDeg(int deg) {
		this.deg = deg;
	}
}

class VariableMotorRegulator implements Runnable {
	private Thread thread;
	private final VariableMotor motor;
	private boolean running;

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
		motor.workQueue.put(new VariableMotorPackage(0, 0, 0));
	}

	@Override
	public void run() {
		System.out.println("Job started");
		while (running) {
			try {
				VariableMotorPackage pkg = motor.workQueue.take();
				if (pkg == null || !running)
					break;
				observeMotor(pkg.getStart(), pkg.getMaxSpeed(), pkg.getDeg());
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		System.out.println("Job finished");
	}

	private double mapValue(double t) {
		return t;
	}

	private double mapValueDerivation(double t) {
		return 1;
	}

	public void observeMotor(int start, int maxSpeed, int deg) throws InterruptedException {
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

			int expected = (int) (start + deg * mapValue(t));
			double modifier = 1;
			if (current != expected) {
				double error = Math.abs(expected - current);
				if (current > expected)
					modifier = 1 / 0.9;
				else if (current < expected)
					modifier = 0.9;
			}
			int speed = (int) (maxSpeed * modifier * mapValueDerivation(t));
			motor.getMotor().setSpeed(speed);
		}
		motor.getMotor().setSpeed(maxSpeed);
		motor.getMotor().rotateTo(start + deg);
	}

	public void start() {
		System.out.println("Starting thread");
		thread.start();
	}
}
