package org.webfreak.plott3r.test;

import static org.junit.Assert.*;

import org.junit.Test;
import org.webfreak.plott3r.svg.SVGMatrix;
import org.webfreak.plott3r.svg.SVGPoint;

public class TestMath {

	@Test
	public void testMatrix() {
		/*
		 * [a c e]<br> [b d f]<br> [0 0 1]
		 */

		SVGMatrix m = SVGMatrix.identity();
		m = m.scale(2);
		assertEquals(m.getA(), 2, 0.01);
		assertEquals(m.getC(), 0, 0.01);
		assertEquals(m.getE(), 0, 0.01);
		assertEquals(m.getB(), 0, 0.01);
		assertEquals(m.getD(), 2, 0.01);
		assertEquals(m.getF(), 0, 0.01);
		m = m.inverse();
		assertEquals(m.getA(), 0.5, 0.01);
		assertEquals(m.getC(), 0, 0.01);
		assertEquals(m.getE(), 0, 0.01);
		assertEquals(m.getB(), 0, 0.01);
		assertEquals(m.getD(), 0.5, 0.01);
		assertEquals(m.getF(), 0, 0.01);

		assertEquals(SVGMatrix.byRows(4, 6, 3, 7, 4, 2).getDeterminant(), -26, 0.01);
		SVGMatrix weird = SVGMatrix.byRows(1, 2, 3, 4, 5, 6);
		m = weird.adjoined();
		assertEquals(m.getA(), 5, 0.01);
		assertEquals(m.getC(), -2, 0.01);
		assertEquals(m.getE(), -3, 0.01);
		assertEquals(m.getB(), -4, 0.01);
		assertEquals(m.getD(), 1, 0.01);
		assertEquals(m.getF(), 6, 0.01);
		m = weird.inverse();
		
		SVGPoint p = new SVGPoint(3, 5);
		SVGPoint t = p.matrixTransform(weird).matrixTransform(m);
		assertEquals(p.getX(), t.getX(), 0.01);
		assertEquals(p.getY(), t.getY(), 0.01);
	}
}
