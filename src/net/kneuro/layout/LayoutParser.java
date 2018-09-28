/**
 * Copyright (c) 2018 Joseph A Knapka
 * 
 * This code is released under the terms of the MIT License.
 */
package net.kneuro.layout;

import java.util.LinkedList;

/**
 * Parses a layout string as documented in the Gridder class comment.
 * Provides methods for Gridder to find the grid cell position and
 * extent of a component based on its ID in a parsed layout string.
 * 
 * @author jk
 */
class LayoutParser {
	
	/**
	 * Parse a layout string.
	 * @param layout The layout string to parse.
	 */
	LayoutParser(String layout) {
		super();
		components = new LinkedList<>();
		parseLayout(layout);
	}

	/**
	 * Get the positioning information associated with a component ID.
	 * @param cname The component ID from the layout string.
	 * @return an int[] containing, in order, the row, column,
	 * grid width, and grid height of the component, or null
	 * if the component was not found.
	 */
	ComponentPosition getComponentByName(String cname) {
		if (cname == null) {
			throw new IllegalArgumentException("cname cannot be null");
		}
		for (ComponentPosition cp: components) {
			if (cname.equals(cp.name)) {
				return cp;
			}
		}
		return null;
	}

	/**
	 * Parse a layout string and update this.components with the
	 * positions and extents of the component IDs in the layout.
	 * @param layout The string to parse.
	 */
	private void parseLayout(String layout) {
		int row=0, col=0;
		int[] idxHolder = new int[] {0};
		ComponentPosition cp = null;
		while (idxHolder[0] < layout.length()) {
			String tok = parseToken(layout,idxHolder);
			switch (tok) {
			case "":
				// End of layout string.
				break;
			case "{":
				// Reset the column number to 0.
				col = 0;
				break;
			case "}":
				// Increment the row number and forget any current
				// component.
				cp = null;
				++row;
				break;
			case "+":
				// Just increment the column number.
				++col;
				break;
			case "<":
			case "-":
				// Extend component rightward.
				if (cp != null) {
					++cp.width;
				}
				++col;
				break;
			case "^":
			case "|":
				// Extend component downward.
				ComponentPosition above = findComponentAbove(row,col);
				if (above != null) {
					++above.height;
				}
				++col;
				break;
			default:
				// It's an identifier. Create a new component.
				cp = new ComponentPosition();
				if (tok.contains(":")) {
					String constraintStr = ConstraintParser.parseConstraintsFromIdentifier(tok);
					cp.constraints = constraintStr;
					tok = tok.split(":")[0];
				} else {
					cp.constraints = "";
				}
				cp.name = tok;
				cp.col = col;
				cp.row = row;
				cp.width = 1;
				cp.height = 1;
				components.add(cp);
				++col;
			}
		}
	}

	/**
	 * Parse a layout token from a layout string at a given position.
	 * @param layout The layout string.
	 * @param idxHolder A one-element int[] array containing the index
	 * 		at which to parse a token. The single element is updated
	 * 		to indicate the next un-parsed position in the layout string.
	 * @return The parsed token, if any, or an empty string if we
	 * 		reach the end of the layout string without finding a
	 * 		valid token.
	 */
	private String parseToken(String layout,int[] idxHolder) {
		String result = "";
		int idx = idxHolder[0];
		while ((idx < layout.length()) && Character.isWhitespace(layout.charAt(idx))) {
			++idx;
		}
		if (idx < layout.length()) {
			switch (layout.charAt(idx)) {
			case '{':
				result = "{";
				++idx;
				break;
			case '}':
				result = "}";
				++idx;
				break;
			case '^':
			case '|':
				result = "|";
				++idx;
				break;
			case '<':
			case '+':
				result = "+";
				++idx;
				break;
			case '-':
				result = "-";
				++idx;
				break;
			default:
				// It's the first character of an identifier.
				char c = layout.charAt(idx);
				StringBuilder sb = new StringBuilder();
				sb.append(c);
				++idx;
				while (idx < layout.length()) {
					c = layout.charAt(idx);
					if (isTerminatingChar(c)) {
						break;
					}
					sb.append(c);
					++idx;
				}
				result = sb.toString();
			}
		}
		idxHolder[0] = idx;
		return result;
	}

	/**
	 * Check whether a character terminates an identifier.
	 * @param c The character to check
	 * @return true if the given character is whitespace or
	 * 		one of the layout structural characters.
	 */
	private boolean isTerminatingChar(char c) {
		return Character.isWhitespace(c) ||
				(c == '{') ||
				(c == '}') ||
				(c == '^') ||
				(c == '<') ||
				(c == '|') ||
				(c == '-') ||
				(c == '+');
	}

	/**
	 * Find the component directly above the given row,col coordinate.
	 * @param row The row above which to search.
	 * @param col The column in which to search.
	 * @return The component found above the given row,col, or null
	 * if no component was found.
	 */
	private ComponentPosition findComponentAbove(int row,int col) {
		for (--row; row >=0; --row) {
			for (ComponentPosition cp: components) {
				if ((cp.row == row) && (cp.col == col)) {
					return cp;
				}
			}
		}
		return null;
	}

	// A structure to hold component positioning information.
	static class ComponentPosition {
		String name;
		int row;
		int col;
		int width;
		int height;
		String constraints;
	}

	// A list of component positions parsed from a layout string.
	private LinkedList<ComponentPosition> components;
}
