/**
 * Copyright (c) 2018 Joseph A Knapka
 * 
 * This code is released under the terms of the MIT License.
 */
package net.kneuro.layout;

import static org.junit.Assert.*;

import org.junit.Test;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Tests for the ConstraintParser class.
 *
 * @author jk
 */
public class TestConstraintParser {

	@Test
	public void testCopyGBC() {
		GridBagConstraints gbc = new GridBagConstraints(1, 2, 3, 4, 5.0, 6.0, 7, 8, new Insets(9, 10, 11, 12), 13, 14);
		GridBagConstraints gbcCopy = ConstraintParser.copyGBC(gbc);
		assertEquals(gbc.gridx,gbcCopy.gridx);
		assertEquals(gbc.gridy,gbcCopy.gridy);
		assertEquals(gbc.gridwidth,gbcCopy.gridwidth);
		assertEquals(gbc.gridheight,gbcCopy.gridheight);
		assertEquals(gbc.anchor,gbcCopy.anchor);
		assertEquals(gbc.fill,gbcCopy.fill);
		assertEquals(gbc.weightx,gbcCopy.weightx,0.001);
		assertEquals(gbc.weighty,gbcCopy.weighty,0.001);
		assertEquals(gbc.ipadx,gbcCopy.ipadx);
		assertEquals(gbc.ipady,gbcCopy.ipady);
		assertEquals(gbc.insets.top,gbcCopy.insets.top);
		assertEquals(gbc.insets.bottom,gbcCopy.insets.bottom);
		assertEquals(gbc.insets.left,gbcCopy.insets.left);
		assertEquals(gbc.insets.right,gbcCopy.insets.right);
		gbc.insets = null;
		gbcCopy = ConstraintParser.copyGBC(gbc);
		assertEquals(0,gbcCopy.insets.top);
		assertEquals(0,gbcCopy.insets.bottom);
		assertEquals(0,gbcCopy.insets.left);
		assertEquals(0,gbcCopy.insets.right);
		assertEquals(gbc.gridx,gbcCopy.gridx);
		assertEquals(gbc.gridy,gbcCopy.gridy);
		assertEquals(gbc.gridwidth,gbcCopy.gridwidth);
		assertEquals(gbc.gridheight,gbcCopy.gridheight);
		assertEquals(gbc.anchor,gbcCopy.anchor);
		assertEquals(gbc.fill,gbcCopy.fill);
		assertEquals(gbc.weightx,gbcCopy.weightx,0.001);
		assertEquals(gbc.weighty,gbcCopy.weighty,0.001);
		assertEquals(gbc.ipadx,gbcCopy.ipadx);
		assertEquals(gbc.ipady,gbcCopy.ipady);
	}

	@Test
	public void testParseConstraints() {
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0, 0, 0, new Insets(0,0,0,0), 0, 0);
		ConstraintParser.parseConstraints(gbc,
				"width 2 gridheight 3",
				"wx",2.0,"weighty 1",
				"fill","both",
				"anchor CENTER",
				"insets* 4",
				"px",3,"py 4"
				);
		assertEquals(2,gbc.gridwidth);
		assertEquals(3,gbc.gridheight);
		assertEquals(2.0,gbc.weightx,0.001);
		assertEquals(1.0,gbc.weighty,0.001);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.CENTER,gbc.anchor);
		assertEquals(4,gbc.insets.top);
		assertEquals(4,gbc.insets.bottom);
		assertEquals(4,gbc.insets.left);
		assertEquals(4,gbc.insets.right);
		assertEquals(3,gbc.ipadx);
		assertEquals(4,gbc.ipady);
	}

	@Test
	public void testBuildConstraintString() {
		String constraints = ConstraintParser.buildConstraintString(
				new Object[] {
				"one 1 two 2",
				"three",3,"four   4.0 "
				}
				);
		assertEquals("one 1 two 2 three 3 four 4.0",constraints);
	}

	@Test
	public void testInterpretConstraint() {
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0, 0, 0, new Insets(0,0,0,0), 0, 0);
		
		ConstraintParser.interpretConstraint("gridwidth","1",gbc);
		assertEquals(1,gbc.gridwidth);
		assertEquals(0,gbc.gridheight);

		ConstraintParser.interpretConstraint("width","2",gbc);
		assertEquals(2,gbc.gridwidth);
		assertEquals(0,gbc.gridheight);

		ConstraintParser.interpretConstraint("wd","3",gbc);
		assertEquals(3,gbc.gridwidth);
		assertEquals(0,gbc.gridheight);
				
		ConstraintParser.interpretConstraint("gridheight","4",gbc);
		assertEquals(4,gbc.gridheight);
		assertEquals(3, gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("height","5",gbc);
		assertEquals(5,gbc.gridheight);
		assertEquals(3, gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("ht","6",gbc);
		assertEquals(6,gbc.gridheight);
		assertEquals(3, gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("weightx","7.0",gbc);
		assertEquals(7.0,gbc.weightx,0.001);
		assertEquals(0.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("wx","8",gbc);
		assertEquals(8.0,gbc.weightx,0.001);
		assertEquals(0.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("weighty","9.00",gbc);
		assertEquals(9.0,gbc.weighty,0.001);
		assertEquals(8.0,gbc.weightx,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("wy","10.0",gbc);
		assertEquals(10.0,gbc.weighty,0.001);
		assertEquals(8.0,gbc.weightx,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("w*","11.0",gbc);
		assertEquals(11.0,gbc.weightx,0.001);
		assertEquals(11.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("weight*","12.0",gbc);
		assertEquals(12.0,gbc.weightx,0.001);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("anchor","E",gbc);
		assertEquals(GridBagConstraints.EAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("a","tr",gbc);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("fill","neither",gbc);
		assertEquals(GridBagConstraints.NONE,gbc.fill);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("f","both",gbc);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("ipadx","13",gbc);
		assertEquals(13,gbc.ipadx);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("px","14",gbc);
		assertEquals(14,gbc.ipadx);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("ipady","15",gbc);
		assertEquals(15,gbc.ipady);
		assertEquals(14,gbc.ipadx);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("py","16",gbc);
		assertEquals(16,gbc.ipady);
		assertEquals(14,gbc.ipadx);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("ipad*","17",gbc);
		assertEquals(17,gbc.ipady);
		assertEquals(17,gbc.ipadx);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("p*","18",gbc);
		assertEquals(18,gbc.ipady);
		assertEquals(18,gbc.ipadx);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.NORTHEAST,gbc.anchor);
		assertEquals(12.0,gbc.weighty,0.001);
		assertEquals(3,gbc.gridwidth);
		
		ConstraintParser.interpretConstraint("inset_top","1",gbc);
		assertEquals(1,gbc.insets.top);
		
		ConstraintParser.interpretConstraint("insets_top","2",gbc);
		assertEquals(2,gbc.insets.top);
		
		ConstraintParser.interpretConstraint("it","3",gbc);
		assertEquals(3,gbc.insets.top);
		
		ConstraintParser.interpretConstraint("inset_bottom","4",gbc);
		assertEquals(4,gbc.insets.bottom);
		
		ConstraintParser.interpretConstraint("insets_bottom","5",gbc);
		assertEquals(5,gbc.insets.bottom);
		
		ConstraintParser.interpretConstraint("ib","6",gbc);
		assertEquals(6,gbc.insets.bottom);
		
		ConstraintParser.interpretConstraint("inset_left","7",gbc);
		assertEquals(7,gbc.insets.left);
		
		ConstraintParser.interpretConstraint("insets_left","8",gbc);
		assertEquals(8,gbc.insets.left);
		
		ConstraintParser.interpretConstraint("il","9",gbc);
		assertEquals(9,gbc.insets.left);
		
		ConstraintParser.interpretConstraint("inset_right","10",gbc);
		assertEquals(10,gbc.insets.right);
		
		ConstraintParser.interpretConstraint("insets_right","11",gbc);
		assertEquals(11,gbc.insets.right);
		
		ConstraintParser.interpretConstraint("ir","12",gbc);
		assertEquals(12,gbc.insets.right);
		
		ConstraintParser.interpretConstraint("insets*","8",gbc);
		assertEquals(8,gbc.insets.top);
		assertEquals(8,gbc.insets.bottom);
		assertEquals(8,gbc.insets.left);
		assertEquals(8,gbc.insets.right);
		
		ConstraintParser.interpretConstraint("inset*","7",gbc);
		assertEquals(7,gbc.insets.top);
		assertEquals(7,gbc.insets.bottom);
		assertEquals(7,gbc.insets.left);
		assertEquals(7,gbc.insets.right);
		
		ConstraintParser.interpretConstraint("i*","9",gbc);
		assertEquals(9,gbc.insets.top);
		assertEquals(9,gbc.insets.bottom);
		assertEquals(9,gbc.insets.left);
		assertEquals(9,gbc.insets.right);

	}

	@Test
	public void testToAnchorValue() {
		int val = ConstraintParser.toAnchorValue("42");
		assertEquals(42,val);

		// Center
		for (String s: new String[]{
				"center","CtR","c"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.CENTER,val);
		}

		// North
		for (String s: new String[]{
				"noRTh","n","TOP"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.NORTH,val);
		}

		// South
		for (String s: new String[]{
				"SOUTH","s","bot","botTOM"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.SOUTH,val);
		}

		// East
		for (String s: new String[]{
				"east","E","right","r"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.EAST,val);
		}

		// West
		for (String s: new String[]{
				"West","w","LEFT","l"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.WEST,val);
		}
		
		// Northeast
		for (String s: new String[]{
				"northeast","ne","topright","tr"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.NORTHEAST,val);
		}

		// Southeast
		for (String s: new String[]{
				"southeast","se","br","bottomright"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.SOUTHEAST,val);
		}

		// Northwest
		for (String s: new String[]{
				"northwest","nw","topleft","tl"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.NORTHWEST,val);
		}

		// Southwest
		for (String s: new String[]{
				"SOUTHWEST","SW","BOTTOMLEFT","BL"
				}) {
			val = ConstraintParser.toAnchorValue(s);
			assertEquals(GridBagConstraints.SOUTHWEST,val);
		}
		
	}

	@Test
	public void testToFillValue() {
		int val = ConstraintParser.toFillValue("99");
		assertEquals(99,val);

		// No fill
		for (String s: new String[] {
				"none","neither","NONE"
				}) {
			val = ConstraintParser.toFillValue(s);
			assertEquals(GridBagConstraints.NONE,val);
		}

		// Horizontal
		for (String s: new String[] {
				"horizontal","h","x","HORIZONTAL"
				}) {
			val = ConstraintParser.toFillValue(s);
			assertEquals(GridBagConstraints.HORIZONTAL,val);
		}

		// Vertical
		for (String s: new String[] {
				"vertical","v","y","VERTICAL"
				}) {
			val = ConstraintParser.toFillValue(s);
			assertEquals(GridBagConstraints.VERTICAL,val);
		}

		// Both
		for (String s: new String[] {
				"both","all","xy","yx","hv","vh","BOTH","ALL"
				}) {
			val = ConstraintParser.toFillValue(s);
			assertEquals(GridBagConstraints.BOTH,val);
		}
	}

	@Test
	public void testToInt() {
		int val = ConstraintParser.toInt("test", "14");
		assertEquals(14,val);
	}

	@Test
	public void testToDouble() {
		double val = ConstraintParser.toDouble("test", "1.2345");
		assertEquals(1.2345,val,0.001);
	}

	@Test
	public void testParseConstraintsFromIdentifier() {
		String layoutId = "comp:wd2,gridheight3,wx2.0,weighty1,fxy,ac,i*4,px3,py4";
		String constraints = ConstraintParser.parseConstraintsFromIdentifier(layoutId);
		assertEquals("wd 2 gridheight 3 wx 2.0 weighty 1 f xy a c i* 4 px 3 py 4",constraints);
		
		GridBagConstraints gbc = new GridBagConstraints(0, 0, 0, 0, 0.0, 0.0, 0, 0, new Insets(0,0,0,0), 0, 0);
		ConstraintParser.parseConstraints(gbc,constraints);
		assertEquals(2,gbc.gridwidth);
		assertEquals(3,gbc.gridheight);
		assertEquals(2.0,gbc.weightx,0.001);
		assertEquals(1.0,gbc.weighty,0.001);
		assertEquals(GridBagConstraints.BOTH,gbc.fill);
		assertEquals(GridBagConstraints.CENTER,gbc.anchor);
		assertEquals(4,gbc.insets.top);
		assertEquals(4,gbc.insets.bottom);
		assertEquals(4,gbc.insets.left);
		assertEquals(4,gbc.insets.right);
		assertEquals(3,gbc.ipadx);
		assertEquals(4,gbc.ipady);
	}

	@Test
	public void testGetConstraintNameAndValue() {
		String[] cval;
		cval = ConstraintParser.getConstraintNameAndValue("gridwidth2");
		assertEquals(cval[0],"gridwidth");
		assertEquals(cval[1],"2");
		
		cval = ConstraintParser.getConstraintNameAndValue("width42");
		assertEquals(cval[0],"width");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("wd42");
		assertEquals(cval[0],"wd");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("gridheight42");
		assertEquals(cval[0],"gridheight");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("height42");
		assertEquals(cval[0],"height");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("ht42");
		assertEquals(cval[0],"ht");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("weightx42");
		assertEquals(cval[0],"weightx");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("wx42");
		assertEquals(cval[0],"wx");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("weighty42");
		assertEquals(cval[0],"weighty");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("wy42");
		assertEquals(cval[0],"wy");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("w*42");
		assertEquals(cval[0],"w*");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("weight*42");
		assertEquals(cval[0],"weight*");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("anchor42");
		assertEquals(cval[0],"anchor");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("a42");
		assertEquals(cval[0],"a");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("fill42");
		assertEquals(cval[0],"fill");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("f42");
		assertEquals(cval[0],"f");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("ipadx42");
		assertEquals(cval[0],"ipadx");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("px42");
		assertEquals(cval[0],"px");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("ipady42");
		assertEquals(cval[0],"ipady");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("py42");
		assertEquals(cval[0],"py");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("ipad*42");
		assertEquals(cval[0],"ipad*");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("p*42");
		assertEquals(cval[0],"p*");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("inset_top42");
		assertEquals(cval[0],"inset_top");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("insets_top42");
		assertEquals(cval[0],"insets_top");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("it42");
		assertEquals(cval[0],"it");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("inset_bottom42");
		assertEquals(cval[0],"inset_bottom");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("insets_bottom42");
		assertEquals(cval[0],"insets_bottom");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("ib42");
		assertEquals(cval[0],"ib");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("inset_left42");
		assertEquals(cval[0],"inset_left");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("insets_left42");
		assertEquals(cval[0],"insets_left");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("il42");
		assertEquals(cval[0],"il");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("inset_right42");
		assertEquals(cval[0],"inset_right");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("insets_right42");
		assertEquals(cval[0],"insets_right");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("ir42");
		assertEquals(cval[0],"ir");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("insets*42");
		assertEquals(cval[0],"insets*");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("inset*42");
		assertEquals(cval[0],"inset*");
		assertEquals(cval[1],"42");
		
		cval = ConstraintParser.getConstraintNameAndValue("i*42");
		assertEquals(cval[0],"i*");
		assertEquals(cval[1],"42");
	}

}
