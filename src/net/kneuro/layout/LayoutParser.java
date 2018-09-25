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

	private static class ComponentPosition {
		String name;
		int row;
		int col;
		int width;
		int height;
	}

	private LinkedList<ComponentPosition> components;
	
	LayoutParser(String layout) {
		super();
		components = new LinkedList<>();
		parseLayout(layout);
	}
	
	int[] getComponentPositionAndSize(String cname) {
		if (cname == null) {
			throw new IllegalArgumentException("cname cannot be null");
		}
		for (ComponentPosition cp: components) {
			if (cname.equals(cp.name)) {
				return new int[] {cp.row,cp.col,cp.width,cp.height};
			}
		}
		return null;
	}
	
	public void parseLayout(String layout) {
		int row=0, col=0, idx=0;
		int[] idxHolder = new int[] {idx};
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
			case ".":
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
				cp.name = tok;
				cp.col = col;
				cp.row = row;
				cp.width = 1;
				cp.height = 1;
				components.add(cp);
				++col;
			}
		}
		dumpLayout();
	}

	private void dumpLayout() {
		for (ComponentPosition cp: components) {
			System.out.println(cp.name+" row "+cp.row+" col "+ cp.col +" w "+ cp.width + " h "+cp.height);
		}
	}
	
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
			case '-':
				result = "-";
				++idx;
				break;
			case '.':
				result = ".";
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

	private boolean isTerminatingChar(char c) {
		return Character.isWhitespace(c) ||
				(c == '{') ||
				(c == '}') ||
				(c == '^') ||
				(c == '<') ||
				(c == '|') ||
				(c == '-') ||
				(c == '.');
	}
	
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
}
