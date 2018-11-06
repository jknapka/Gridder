/**
 * Copyright (c) 2018 Joseph A Knapka
 * 
 * This code is released under the terms of the MIT License.
 */
package net.kneuro.layout;

import static org.junit.Assert.*;

import org.junit.Test;

/**
 * A basic test for the LayoutParser class.
 * 
 * @author jk
 */
public class TestLayoutParser {

	@Test
	public void testLayoutParser() {
		String layout =
				 "    {c1                 +   +     c2}    "+
				 "    {c3:wx1,wy2,i*5,fxy +   c4    + }    "+
				 "    {|                  -   -     c5}    "+
				 "    {|                  -   c6    + }    ";
		LayoutParser lp = new LayoutParser(layout);
		LayoutParser.ComponentPosition cp;
		
		cp = lp.getComponentByName("nobody");
		assertNull(cp);
		
		cp = lp.getComponentByName("c1");
		assertEquals(0,cp.col);
		assertEquals(0,cp.row);
		assertEquals(3,cp.width);
		assertEquals(1,cp.height);
		assertEquals("",cp.constraints);
		
		cp = lp.getComponentByName("c2");
		assertEquals(3,cp.col);
		assertEquals(0,cp.row);
		assertEquals(1,cp.width);
		assertEquals(1,cp.height);
		assertEquals("",cp.constraints);
		
		cp = lp.getComponentByName("c3");
		assertEquals(0,cp.col);
		assertEquals(1,cp.row);
		assertEquals(2,cp.width);
		assertEquals(3,cp.height);
		assertEquals("wx 1 wy 2 i* 5 f xy",cp.constraints);
		
		cp = lp.getComponentByName("c4");
		assertEquals(2,cp.col);
		assertEquals(1,cp.row);
		assertEquals(2,cp.width);
		assertEquals(1,cp.height);
		assertEquals("",cp.constraints);
		
		cp = lp.getComponentByName("c5");
		assertEquals(3,cp.col);
		assertEquals(2,cp.row);
		assertEquals(1,cp.width);
		assertEquals(1,cp.height);
		assertEquals("",cp.constraints);
		
		cp = lp.getComponentByName("c6");
		assertEquals(2,cp.col);
		assertEquals(3,cp.row);
		assertEquals(2,cp.width);
		assertEquals(1,cp.height);
		assertEquals("",cp.constraints);
	}

}
