package org.webfreak.plott3r.svg.path;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.webfreak.plott3r.svg.SVGPoint;

public class PathParser {
	private StringBuilder pathData;
	private Path path;

	public PathParser() {
		pathData = new StringBuilder();
	}

	public PathParser feed(String data) {
		pathData.append(data);
		return this;
	}

	public Path build() {
		path = new Path();
		skipWhitespaces();
		parseMovetoDrawtoCommandGroups();
		if (pathData.length() != 0)
			throw new IllegalStateException("Provided path data was not valid (garbage after content)");
		return path;
	}

	private boolean parseMovetoDrawtoCommandGroups() {
		if (pathData.length() == 0)
			return false;
		while (pathData.length() > 0) {
			enforce(parseMovetoDrawtoCommandGroup(), "moveto-drawto-command-group");
			skipWhitespaces();
		}
		return true;
	}

	private boolean parseMovetoDrawtoCommandGroup() {
		if (!parseMoveto())
			return false;
		skipWhitespaces();
		parseDrawtoCommands();
		return true;
	}

	private boolean parseDrawtoCommands() {
		if (!parseDrawtoCommand())
			return false;
		do {
			skipWhitespaces();
		} while (parseDrawtoCommand());
		return true;
	}

	private boolean parseDrawtoCommand() {
		if (parseClosepath())
			return true;
		else if (parseLineto())
			return true;
		else if (parseHorizontalLineto())
			return true;
		else if (parseVerticalLineto())
			return true;
		else if (parseCurveto())
			return true;
		else if (parseSmoothCurveto())
			return true;
		else if (parseQuadraticBezierCurveto())
			return true;
		else if (parseSmoothQuadraticBezierCurveto())
			return true;
		else if (parseEllipticalArc())
			return true;
		else
			return false;
	}

	private boolean parseMoveto() {
		int command = parseTagStart('m');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			SVGPoint pair = parseCoordinatePair();
			if (!first && pair == null)
				break;
			SVGPoint point = enforce(pair, "moveto-coordinate-pair");
			double x = point.getX();
			double y = point.getY();
			path.getPathSegList().add(relative ? new PathSegMovetoRel(x, y) : new PathSegMovetoAbs(x, y));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private boolean parseClosepath() {
		if (pathData.length() == 0 || Character.toLowerCase(pathData.charAt(0)) != 'z')
			return false;
		pathData.deleteCharAt(0);
		path.getPathSegList().add(new PathSegClosePath());
		return true;
	}

	private boolean parseLineto() {
		int command = parseTagStart('l');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			SVGPoint pair = parseCoordinatePair();
			if (!first && pair == null)
				break;
			SVGPoint point = enforce(pair, "lineto-coordinate-pair");
			double x = point.getX();
			double y = point.getY();
			path.getPathSegList().add(relative ? new PathSegLinetoRel(x, y) : new PathSegLinetoAbs(x, y));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private boolean parseHorizontalLineto() {
		int command = parseTagStart('h');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			double num = parseCoordinate();
			if (!first && Double.isNaN(num))
				break;
			double x = enforce(num, "lineto-coordinate-pair");
			path.getPathSegList().add(relative ? new PathSegLinetoHorizontalRel(x) : new PathSegLinetoHorizontalAbs(x));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private boolean parseVerticalLineto() {
		int command = parseTagStart('v');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			double num = parseCoordinate();
			if (!first && Double.isNaN(num))
				break;
			double y = enforce(num, "lineto-coordinate-pair");
			path.getPathSegList().add(relative ? new PathSegLinetoVerticalRel(y) : new PathSegLinetoVerticalAbs(y));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private boolean parseCurveto() {
		int command = parseTagStart('c');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			SVGPoint pair = parseCoordinatePair();
			if (!first && pair == null)
				break;
			SVGPoint c1 = enforce(pair, "curveto-c1-coordinate-pair");
			parseCommaWhitespace();
			SVGPoint c2 = enforce(parseCoordinatePair(), "curveto-c2-coordinate-pair");
			parseCommaWhitespace();
			SVGPoint end = enforce(parseCoordinatePair(), "curveto-end-coordinate-pair");
			path.getPathSegList()
					.add(relative ? new PathSegCurvetoCubicRel(c1.getX(), c1.getY(), c2.getX(), c2.getY(), end.getX(), end.getY())
							: new PathSegCurvetoCubicAbs(c1.getX(), c1.getY(), c2.getX(), c2.getY(), end.getX(), end.getY()));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private boolean parseSmoothCurveto() {
		int command = parseTagStart('s');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			SVGPoint pair = parseCoordinatePair();
			if (!first && pair == null)
				break;
			SVGPoint c2 = enforce(pair, "smooth-curveto-c2-coordinate-pair");
			parseCommaWhitespace();
			SVGPoint end = enforce(parseCoordinatePair(), "smooth-curveto-end-coordinate-pair");
			path.getPathSegList()
					.add(relative ? new PathSegCurvetoCubicSmoothRel(c2.getX(), c2.getY(), end.getX(), end.getY())
							: new PathSegCurvetoCubicSmoothAbs(c2.getX(), c2.getY(), end.getX(), end.getY()));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private boolean parseQuadraticBezierCurveto() {
		int command = parseTagStart('q');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			SVGPoint pair = parseCoordinatePair();
			if (!first && pair == null)
				break;
			SVGPoint c1 = enforce(pair, "quadratic-bezier-curve-c1-coordinate-pair");
			parseCommaWhitespace();
			SVGPoint end = enforce(parseCoordinatePair(), "quadratic-bezier-curve-end-coordinate-pair");
			path.getPathSegList().add(relative ? new PathSegCurvetoQuadraticRel(c1.getX(), c1.getY(), end.getX(), end.getY())
					: new PathSegCurvetoQuadraticAbs(c1.getX(), c1.getY(), end.getX(), end.getY()));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private boolean parseSmoothQuadraticBezierCurveto() {
		int command = parseTagStart('t');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			SVGPoint pair = parseCoordinatePair();
			if (!first && pair == null)
				break;
			SVGPoint end = enforce(pair, "quadratic-bezier-curve-end-coordinate-pair");
			path.getPathSegList().add(relative ? new PathSegCurvetoQuadraticSmoothRel(end.getX(), end.getY())
					: new PathSegCurvetoQuadraticSmoothAbs(end.getX(), end.getY()));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private boolean parseEllipticalArc() {
		int command = parseTagStart('a');
		if (command < 0)
			return false;
		boolean relative = command == 1;
		boolean first = true;
		do {
			double num = parseNonnegativeNumber();
			if (!first && Double.isNaN(num))
				break;
			double radiusX = enforce(num, "elliptical-arc-radius-x");
			parseCommaWhitespace();
			double radiusY = enforce(parseNonnegativeNumber(), "elliptical-arc-radius-y");
			parseCommaWhitespace();
			double rotation = enforce(parseNumber(), "elliptical-arc-x-axis-rotation");
			enforce(parseCommaWhitespace(), "elliptical-arc-comma-before-flags");
			boolean largeArcFlag = enforceFlag(parseFlag(), "elliptical-arc-large-arc-flag");
			parseCommaWhitespace();
			boolean sweepFlag = enforceFlag(parseFlag(), "elliptical-arc-sweep-flag");
			parseCommaWhitespace();
			SVGPoint end = enforce(parseCoordinatePair(), "elliptical-arc-end");
			path.getPathSegList()
					.add(relative ? new PathSegArcRel(end.getX(), end.getY(), radiusX, radiusY, rotation, largeArcFlag, sweepFlag)
							: new PathSegArcAbs(end.getX(), end.getY(), radiusX, radiusY, rotation, largeArcFlag, sweepFlag));
			first = false;
		} while (parseCommaWhitespace());
		return true;
	}

	private int parseTagStart(char lowerTag) {
		if (pathData.length() == 0 || Character.toLowerCase(pathData.charAt(0)) != lowerTag)
			return -1;
		boolean relative = pathData.charAt(0) == lowerTag;
		pathData.deleteCharAt(0);
		skipWhitespaces();
		return relative ? 1 : 0;
	}

	private boolean parseCommaWhitespace() {
		if (pathData.length() > 0) {
			if (pathData.charAt(0) == ',') {
				pathData.deleteCharAt(0);
				skipWhitespaces();
				return true;
			} else if (skipWhitespaces()) {
				if (pathData.length() > 0 && pathData.charAt(0) == ',')
					pathData.deleteCharAt(0);
				skipWhitespaces();
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private boolean skipWhitespaces() {
		boolean ret = false;
		while (pathData.length() > 0 && isWhite(pathData.charAt(0))) {
			pathData.deleteCharAt(0);
			ret = true;
		}
		return ret;
	}

	private int parseFlag() {
		if (pathData.length() == 0 || (pathData.charAt(0) != '0' && pathData.charAt(0) != '1'))
			return -1;
		return pathData.charAt(0) != '0' ? 1 : 0;
	}

	public double parseCoordinate() {
		return parseNumber();
	}

	public SVGPoint parseCoordinatePair() {
		double x = parseCoordinate();
		if (Double.isNaN(x))
			return null;
		parseCommaWhitespace();
		double y = enforce(parseCoordinate(), "coordinate-y");
		return new SVGPoint(x, y);
	}

	public double parseNumber() {
		if (pathData.length() > 0) {
			if (pathData.charAt(0) == '+') {
				pathData.deleteCharAt(0);
				return parseNonnegativeNumber();
			} else if (pathData.charAt(0) == '-') {
				pathData.deleteCharAt(0);
				return -parseNonnegativeNumber();
			} else {
				return parseNonnegativeNumber();
			}
		} else
			return Double.NaN;
	}

	private Pattern nonNegativePattern = Pattern.compile("^([0-9]*\\.[0-9]+|[0-9]+\\.?)([eE][+-]?[0-9]+)?");
	public double parseNonnegativeNumber() {
		Matcher matcher = nonNegativePattern.matcher(pathData);
		if (!matcher.find())
			return Double.NaN;
		String s = matcher.group();
		pathData.delete(0, matcher.group().length());
		// java handles it all
		return Double.parseDouble(s);
	}

	private void enforce(boolean b, String what) {
		if (!b)
			throw new IllegalStateException("Expected " + what + " to parse here");
	}

	private double enforce(double b, String what) {
		if (Double.isNaN(b))
			throw new IllegalStateException("Expected " + what + " to parse here");
		return b;
	}

	private <T> T enforce(T b, String what) {
		if (b == null)
			throw new IllegalStateException("Expected " + what + " to parse here");
		return b;
	}

	private boolean enforceFlag(int flag, String what) {
		if (flag < 0)
			throw new IllegalStateException("Expected " + what + " to parse here");
		return flag > 0;
	}

	private static boolean isWhite(char c) {
		return c == ' ' || c == '\t' || c == '\r' || c == '\n';
	}
}
