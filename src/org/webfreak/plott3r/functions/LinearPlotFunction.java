package org.webfreak.plott3r.functions;

public class LinearPlotFunction implements PlotFunction {
	@Override
	public double map(double t) {
		return t;
	}

	@Override
	public double mapDerivative(double t) {
		return 1;
	}
}
