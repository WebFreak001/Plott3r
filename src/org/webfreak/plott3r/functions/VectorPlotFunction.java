package org.webfreak.plott3r.functions;

import org.webfreak.plott3r.Vector2d;

public abstract class VectorPlotFunction {
	public abstract Vector2d map(double t);
	public abstract Vector2d mapDerivative(double t);

	public PlotFunction getX() {
		final VectorPlotFunction vec = this;
		return new PlotFunction() {
			@Override
			public double map(double t) {
				return vec.map(t).getX();
			}

			@Override
			public double mapDerivative(double t) {
				return vec.mapDerivative(t).getX();
			}
		};
	}

	public PlotFunction getY() {
		final VectorPlotFunction vec = this;
		return new PlotFunction() {
			@Override
			public double map(double t) {
				return vec.map(t).getY();
			}

			@Override
			public double mapDerivative(double t) {
				return vec.mapDerivative(t).getY();
			}
		};
	}
}
