package org.webfreak.plott3r.functions;

public class CircularPlotFunction implements PlotFunction {
	@Override
	public double map(double t) {
		return 1 - Math.sqrt(1 - t * t);
	}

	@Override
	public double mapDerivative(double t) {
		if (t >= 0.8)
			return 1;
		return t / Math.sqrt(1 - t * t);
	}
}
