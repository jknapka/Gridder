/**
 * Copyright (c) 2018 Joseph A Knapka
 * 
 * This code is released under the terms of the MIT License.
 */
package net.kneuro.layout;

import java.awt.GridBagConstraints;
import java.awt.Insets;

/**
 * Holds the constraint parsing methods formerly housed in the
 * Gridder class.
 * @author jk
 *
 */
class ConstraintParser {

	/**
	 * Copy a GridBagConstraints object.
	 * @param from The GBC to copy.
	 * @return A new GBC initialized from the given one.
	 */
	static GridBagConstraints copyGBC(GridBagConstraints from) {
		GridBagConstraints result = new GridBagConstraints();
		result.insets = new Insets(from.insets.top,from.insets.left,
				from.insets.bottom,from.insets.right);
		result.gridx = from.gridx;
		result.gridy = from.gridy;
		result.gridwidth = from.gridwidth;
		result.gridheight = from.gridheight;
		result.weightx = from.weightx;
		result.weighty = from.weighty;
		result.fill = from.fill;
		result.anchor = from.anchor;
		result.ipadx = from.ipadx;
		result.ipady = from.ipady;
		return result;
	}

	/**
	 * Parse a set of constraints and update a GridBagConstraints object.
	 * @param constraints Logically, a list of constraintName value pairs.
	 * @return A GridBagConstraints object with all constraints filled
	 * in as specified, and default values for any unspecified constraint.
	 */
	static void parseConstraints(GridBagConstraints toUpdate,Object... constraints) {
		String bigConstraintString = buildConstraintString(constraints);
		parseStringConstraints(bigConstraintString,toUpdate);
	}

	/**
	 * Take an Object[] array and collapse it down to a single String, with
	 * exactly one space character between adjacent tokens.
	 * @param constraints A constraint array.
	 * @return A string containing the same content as the input.
	 */
	static String buildConstraintString(Object[] constraints) {
		StringBuilder sb = new StringBuilder();
		for (Object obj: constraints) {
			if (obj != null) {
				sb.append(' ');
				sb.append(obj.toString().trim());
			}
		}
		return sb.toString().trim();
	}
	
	/**
	 * Parse a constraint string and update the given GridBagConstraints with
	 * any constraints specified. Do not change any constraint not explicitly
	 * specified in the constraints string.
	 * @param constraints A constraints string containing one or more
	 * "constraintName value" pairs.
	 * @param gbc The GridBagConstraints object to update.
	 */
	static void parseStringConstraints(String constraints,GridBagConstraints gbc) {
		if (constraints.length() == 0) return;
		String[] toks = constraints.split("\\s+");
		for (int ii=0; ii<toks.length; ii += 2) {
			String cname = toks[ii];
			String cval = "";
			if (ii+1 < toks.length) {
				cval = toks[ii+1];
			} else {
				throw new RuntimeException("Odd number of constraint tokens in {"+constraints+"}");
			}
			interpretConstraint(cname,cval,gbc);
		}
	}

	/**
	 * Interpret a single constraint and its value.
	 * @param cname The constraint name.
	 * @param cval The constraint value.
	 * @param gbc The GridBagConstraint object to update.
	 * @throws RuntimeException if the constraint cannot be interpreted.
	 */
	static void interpretConstraint(String cname,String cval,GridBagConstraints gbc) {
		cname = cname.toLowerCase();
		cval = cval.toLowerCase();
		switch (cname) {
		case "gridwidth":
		case "width":
		case "wd":
			gbc.gridwidth = toInt(cname,cval);
			break;
		case "gridheight":
		case "height":
		case "ht":
			gbc.gridheight = toInt(cname,cval);
			break;
		case "weightx":
		case "wx":
			gbc.weightx = toDouble(cname,cval);
			break;
		case "weighty":
		case "wy":
			gbc.weighty = toDouble(cname,cval);
			break;
		case "weight*":
		case "w*":
			// Set both weights to the given cval.
			gbc.weightx = gbc.weighty = toDouble(cname,cval);
			break;
		case "anchor":
		case "a":
			gbc.anchor = toAnchorValue(cval);
			break;
		case "fill":
		case "f":
			gbc.fill = toFillValue(cval);
			break;
		case "ipadx":
		case "px":
			gbc.ipadx = toInt(cname,cval);
			break;
		case "ipady":
		case "py":
			gbc.ipady = toInt(cname,cval);
			break;
		case "p*":
		case "ipad*":
			// Set both internal paddings to the cval.
			gbc.ipadx = gbc.ipady = toInt(cname,cval);
			break;
		case "inset_top":
		case "insets_top":
		case "it":
			// Note: we know gbc.insets is non-null because we create all
			// GBC objects that this code will see.
			gbc.insets.top = toInt(cname,cval);
			break;
		case "inset_bottom":
		case "insets_bottom":
		case "ib":
			gbc.insets.bottom = toInt(cname,cval);
			break;
		case "inset_left":
		case "insets_left":
		case "il":
			gbc.insets.left = toInt(cname,cval);
			break;
		case "inset_right":
		case "insets_right":
		case "ir":
			gbc.insets.right = toInt(cname,cval);
			break;
		case "i*":
		case "inset*":
		case "insets*":
			// Set all insets to the given cval.
			gbc.insets.left = gbc.insets.right
				= gbc.insets.top = gbc.insets.bottom = toInt(cname,cval);
			break;
		default:
			throw new RuntimeException("Unknown constraint: "+cname);
		}
	}

	/**
	 * Convert a string to the corresponding GBC.anchor value. Integers
	 * will be returned unchanged.
	 * @param value The string to interpret.
	 * @return The corresponding GridBagConstraints.anchor value.
	 */
	static int toAnchorValue(String value) {
		// First, see if it's just an integer, in which case,
		// return its value. In this case we simply assume the
		// supplied value is one of the GridBagConstraints constants.
		try {
			int ival = Integer.parseInt(value);
			return ival;
		} catch (NumberFormatException ex) {
			// Nothing to do.
		}
		// Not an integer, so interpret the string.
		switch (value) {
		case "center":
		case "ctr":
		case "c":
			return GridBagConstraints.CENTER;
		case "north":
		case "n":
		case "top":
			return GridBagConstraints.NORTH;
		case "south":
		case "s":
		case "bottom":
		case "bot":
			return GridBagConstraints.SOUTH;
		case "east":
		case "e":
		case "right":
		case "r":
			return GridBagConstraints.EAST;
		case "west":
		case "w":
		case "left":
		case "l":
			return GridBagConstraints.WEST;
		case "northeast":
		case "ne":
		case "topright":
		case "tr":
			return GridBagConstraints.NORTHEAST;
		case "northwest":
		case "nw":
		case "topleft":
		case "tl":
			return GridBagConstraints.NORTHWEST;
		case "southeast":
		case "se":
		case "bottomright":
		case "br":
			return GridBagConstraints.SOUTHEAST;
		case "southwest":
		case "sw":
		case "bottomleft":
		case "bl":
			return GridBagConstraints.SOUTHWEST;
		default:
			throw new RuntimeException("Unknown anchor value {"+value+"}");
		}
	}

	/**
	 * Convert a string to a GridBagConstraints.fill value. Integers
	 * will be returned unchanged.
	 * @param value The value to interpret.
	 * @return The corresponding GBC.fill value.
	 * @throws RuntimeException if the value cannot be interpreted.
	 */
	static int toFillValue(String value) {
		// First, see if it's just an integer, in which case,
		// return its value. In this case we simply assume the
		// supplied value is one of the GridBagConstraints constants.
		try {
			int ival = Integer.parseInt(value);
			return ival;
		} catch (NumberFormatException ex) {
			// Nothing to do.
		}
		// Not an integer, so interpret the string.
		switch (value) {
		case "none":
		case "neither":
		case "n":
			return GridBagConstraints.NONE;
		case "horizontal":
		case "h":
		case "x":
			return GridBagConstraints.HORIZONTAL;
		case "vertical":
		case "v":
		case "y":
			return GridBagConstraints.VERTICAL;
		case "both":
		case "all":
		case "xy":
		case "yx":
		case "hv":
		case "vh":
			return GridBagConstraints.BOTH;
		default:
			throw new RuntimeException("Unknown fill value {"+value+"}");
		}		
	}

	/**
	 * Convert a string to an integer value, and throw a RuntimeException
	 * if this cannot be done.
	 * @param value The string to convert.
	 * @return The corresponding integer value.
	 * @throws RuntimeException if conversion fails.
	 */
	static int toInt(String cname,String value) {
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Bad int constraint value {"+value+"} for constraint "+cname);
		}
	}
	
	/**
	 * Convert a string to a double value, and throw a RuntimeException
	 * if this cannot be done.
	 * @param value The string to convert.
	 * @return The corresponding double value.
	 * @throws RuntimeException if conversion fails.
	 */
	static double toDouble(String cname,String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Bad float constraint value {"+value+"} for constraint "+cname);
		}
	}
	
	/**
	 * A layout identifier may be followed by a colon and a comma-separated
	 * list of constraint names and values, with no gap between constraint
	 * names and values, eg:
	 * 
	 *   comp1:wx1,wy2,p*5
	 * 
	 * which sets weightx to 1.0, weighty to 2.0, and all ipad values to 5
	 * for component comp1 (using default constraints for all other values).
	 * This method parses those overridden constraints from an identifier
	 * string and updates the given GBC object.
	 * 
	 * @param id A layout ID string.
	 * @param gbc The GBC object to update.
	 */
	static String parseConstraintsFromIdentifier(String id) {
		StringBuilder sb = new StringBuilder();
		String[] constraints = id.split(":")[1].split(",");
		for (String cons: constraints) {
			String[] cnameAndVal = getConstraintNameAndValue(cons);
			sb.append(' ').append(cnameAndVal[0]).append(' ').append(cnameAndVal[1]);
		}
		return sb.toString().trim();
	}
	
	/**
	 * Split a single constraintValue token into the constraint name and value.
	 * @param constraint A constraint and its value run together in a single string,
	 *        like "wx1.0".
	 * @return A string with the constraint name and value separated by a space,
	 *        like "wx 1.0".
	 */
	static String[] getConstraintNameAndValue(String constraint) {
		if (constraint.startsWith("gridwidth")) {
			return new String[] {"gridwidth",constraint.substring(9)};
		}
		if (constraint.startsWith("width")) {
			return new String[]{"width",constraint.substring(5)};
		}
		if (constraint.startsWith("wd")) {
			return new String[]{"wd",constraint.substring(2)};
		}
		if (constraint.startsWith("gridheight")) {
			return new String[]{"gridheight",constraint.substring(10)};
		}
		if (constraint.startsWith("height")) {
			return new String[]{"height",constraint.substring(6)};
		}
		if (constraint.startsWith("ht")) {
			return new String[]{"ht",constraint.substring(2)};
		}
		if (constraint.startsWith("weightx")) {
			return new String[]{"weightx",constraint.substring(7)};
		}
		if (constraint.startsWith("wx")) {
			return new String[]{"wx",constraint.substring(2)};
		}
		if (constraint.startsWith("weighty")) {
			return new String[]{"weighty",constraint.substring(7)};
		}
		if (constraint.startsWith("wy")) {
			return new String[]{"wy",constraint.substring(2)};
		}
		if (constraint.startsWith("w*")) {
			return new String[]{"w*",constraint.substring(2)};
		}
		if (constraint.startsWith("weight*")) {
			return new String[]{"weight*",constraint.substring(2)};
		}
		if (constraint.startsWith("anchor")) {
			return new String[]{"anchor",constraint.substring(6)};
		}
		if (constraint.startsWith("a")) {
			return new String[]{"a",constraint.substring(1)};
		}
		if (constraint.startsWith("fill")) {
			return new String[]{"fill",constraint.substring(4)};
		}
		if (constraint.startsWith("f")) {
			return new String[]{"f",constraint.substring(1)};
		}
		if (constraint.startsWith("ipadx")) {
			return new String[]{"ipadx",constraint.substring(5)};
		}
		if (constraint.startsWith("px")) {
			return new String[]{"px",constraint.substring(2)};
		}
		if (constraint.startsWith("ipady")) {
			return new String[]{"ipady",constraint.substring(5)};
		}
		if (constraint.startsWith("py")) {
			return new String[]{"py",constraint.substring(2)};
		}
		if (constraint.startsWith("ipad*")) {
			return new String[]{"ipad*",constraint.substring(5)};
		}
		if (constraint.startsWith("p*")) {
			return new String[]{"p*",constraint.substring(2)};
		}
		if (constraint.startsWith("inset_top")) {
			return new String[]{"inset_top",constraint.substring(9)};
		}
		if (constraint.startsWith("insets_top")) {
			return new String[]{"insets_top",constraint.substring(10)};
		}
		if (constraint.startsWith("it")) {
			return new String[]{"it",constraint.substring(2)};
		}
		if (constraint.startsWith("inset_bottom")) {
			return new String[]{"inset_bottom",constraint.substring(12)};
		}
		if (constraint.startsWith("insets_bottom")) {
			return new String[]{"insets_bottom",constraint.substring(13)};
		}
		if (constraint.startsWith("ib")) {
			return new String[]{"ib",constraint.substring(2)};
		}
		if (constraint.startsWith("inset_left")) {
			return new String[]{"inset_left",constraint.substring(10)};
		}
		if (constraint.startsWith("insets_left")) {
			return new String[]{"insets_left",constraint.substring(11)};
		}
		if (constraint.startsWith("il")) {
			return new String[]{"il",constraint.substring(2)};
		}
		if (constraint.startsWith("inset_right")) {
			return new String[]{"inset_right",constraint.substring(11)};
		}
		if (constraint.startsWith("insets_right")) {
			return new String[]{"insets_right",constraint.substring(12)};
		}
		if (constraint.startsWith("ir")) {
			return new String[]{"ir",constraint.substring(2)};
		}
		if (constraint.startsWith("insets*")) {
			return new String[]{"insets*",constraint.substring(7)};
		}
		if (constraint.startsWith("inset*")) {
			return new String[]{"inset*",constraint.substring(6)};
		}
		if (constraint.startsWith("i*")) {
			return new String[]{"i*",constraint.substring(2)};
		}
		throw new RuntimeException("Could not intepret embedded constraint "+constraint);
	}
}
