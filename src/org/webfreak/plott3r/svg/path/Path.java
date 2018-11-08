package org.webfreak.plott3r.svg.path;

import java.util.ArrayList;
import java.util.List;

import org.webfreak.plott3r.svg.SVGPoint;

public class Path {
	private List<PathSeg> pathSegList;

	public Path() {
		pathSegList = new ArrayList<>();
	}

	public List<PathSeg> getPathSegList() {
		return pathSegList;
	}

	public int getPathSegCount() {
		return pathSegList.size();
	}

	public double getTotalLength() {
		throw new UnsupportedOperationException();
	}

	public SVGPoint getPointAtLength(double distance) {
		throw new UnsupportedOperationException();
	}

	public double getPathSegAtLength(double distance) {
		throw new UnsupportedOperationException();
	}
}