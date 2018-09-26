/**
 * Copyright (c) 2018 Joseph A Knapka
 * 
 * This code is released under the terms of the MIT License.
 */
package net.kneuro.layout;

import java.awt.Component;
import java.awt.Container;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JComponent;

/**
 * This class makes Swing's GridBagLayout easier, even a pleasure,
 * to use. It provides an interface to GridBagLayout that resembles
 * that of the Tcl/Tk grid layout manager. It also provides a text-
 * based layout function, with which you can write a string that
 * represents the position and extent of each component in a 2D
 * fashion.
 * <p>
 * This class is merely a thin wrapper around GridBagLayout and
 * GridBagConstraints. It does not perform any layout management
 * itself, it is just a convenient way to specify layout
 * information to GridBagLayout.
 * <p>
 * BASIC USAGE
 * <p>
 * To use Gridder, create a Gridder instance and give it the container
 * you want it to manage, as well as any default constraints as a
 * simple string of "constraintName value" pairs (or as multiple
 * strings of such values, or as constraint names followed by their
 * string, integer, or floating-point values, since the final argument
 * to Gridder() is a varargs array).
 * <pre>
 *    JFrame topLevel = new JFrame();
 *    Gridder gr = new Gridder(topLevel.getContentPane(),
 *          "weightx 1.0 weighty 0.0",
 *    		"inset_top 5", "inset_bottom", 5,
 *          "anchor center","fill","xy");
 * </pre>
 * You can then add components via the Gridder object, which handles
 * all the nastiness of setting up the GridBagConstraints.
 * <pre>
 *    // Uses the default constraints, places label at row 0, column 2.
 *    gr.add(new JLabel("Name:"),0,2);
 * </pre>
 * For any individual component, you can override the default constraints
 * in the add() method (which does not change the defaults). Constraints
 * are specified exactly as they are to the Gridder constructor.
 * <pre>
 *    JTextField nameFld = new JTextField();
 *    gr.add(nameFld,0,3,"weightx", 4.0,"fill horizontal");
 * </pre>
 * You can also call gr.updateConstraints("constraint1 value1 ...") to
 * update the default constraints, which will then be used for all
 * future add() calls.
 * <p>
 * In general, all constraint names are the same as the corresponding
 * member names of the GridBagConstaints class. The exception is the
 * GridBagConstraints.insets member, which is realized here as
 * separate constraints inset_top, inset_bottom, inset_left, and
 * inset_right.
 * <p>
 * Constraint values can be specified as either raw values of the
 * appropriate type, or as strings that can be converted to the
 * appropriate type.
 * <p>
 * Unrecognized constraints or constraints with invalid
 * values cause a RuntimeException. Since the expected usage of
 * this class is to set up a container at UI-construction time,
 * I expect such problems to occur only during development, so
 * throwing a RuntimeException seems reasonable.
 * <p>
 * Constraint names, values, and defaults (if not specified in the
 * Gridder constructor or the add() method) are summarized in the
 * following table (note that the defaults for weights are different
 * when text-based layouts are used - see below).
 * <pre>
 * GBC.member       Gridder name                Possible values         Default
 * ----------------------------------------------------------------------------
 * gridwidth        gridwidth,width             Integer                 1
 * gridheight       gridheight,height           Integer                 1
 * weightx          weightx                     Float                   0.0
 * weighty          weighty                     Float                   0.0
 * anchor           anchor                      center|ctr|c            center
 *                                              north|n|top
 *                                              south|s|bot|bottom
 *                                              east|e|right|r
 *                                              west|w|left|l
 *                                              northeast|ne|topright|tr
 *                                              northwest|nw|topleft|tl
 *                                              southeast|se|bottomright|br
 *                                              southwest|sw|bottomleft|bl
 *                                              Any of the int anchor values
 *                                              defined in GridBagConstraints.
 * fill             fill                        none|neither            none
 *                                              horizontal|h|x
 *                                              vertical|v|y
 *                                              both|all|xy
 *                                              Any of the int fill values
 *                                              defined in GridBagConstraints.
 * ipadx            ipadx                       Integer                 0
 * ipady            ipady                       Integer                 0
 * insets.top       inset_top|insets_top        Integer                 0
 * insets.bottom    inset_bottom|insets_bottom  Integer                 0
 * insets.left      inset_left|insets_left      Integer                 0
 * insets.right     inset_right|insets_right    Integer                 0
 * gridx            None                        Supplied by the Gridder.add() method.
 * gridy            None                        Supplied by the Gridder.add() method.
 * ----------------------------------------------------------------------------
 * </pre>
 * <p>
 * TEXT-BASED LAYOUTS
 * <p>
 * The other, and sometimes more convenient way to use Gridder is
 * to parse a textual layout string, and then add components to the
 * container using identifiers mentioned in the layout string. 
 * You should either use a text-based layout or the plain
 * add(Component,row,column) API for any given container, not both.
 * <p>
 * For example, here is a somewhat complex layout:
 * <pre>
 * String layout =
 *    "    {c1 - - c2}    "+
 *    "    {c3 - c4 -}    "+
 *    "    {|  . . c5}    "+
 *    "    {|  . c6 -}    ";
 * gr.parseLayout(layout);
 * </pre>
 * (Whitespace added to layout string for clarity.) Such a string
 * represents a rectangular array of grid cells and identifies
 * the position and extent of each component. This layout says that:
 * <p>
 * <ul>
 * <li> Component c1 occupies the first three cells of row 0
 *   (- means "extend the previous component into this column").
 * <li> Component c2 occupies the fourth cell of row 0.
 * <li> Component c3 occupies the first two cells of row 1, and
 *   extends two cells downward to row 3 (| means "extend
 *   the component directly above into this row").
 * <li> Component c4 occupies the third and fourth cells of
 *   row 1.
 * <li> Component c5 occupies the fourth cell of row 2.
 * <li> Component c6 occupies the third and fourth cells of
 *   row 3.
 * <li> The cell at row 2, column 2 is empty.
 * </ul>
 * <p>
 * In general, 
 * <ul>
 * <li> Curly brackets { and } delimit each grid row;
 * <li> - causes the gridwidth of the component to the
 *   left to be increased by 1;
 * <li> | causes the gridheight of the component *directly*
 *   above to be increased by 1;
 * <li> . simply occupies space. All grid cells must be
 *   filled with either a component identifier or
 *   one of the characters .<^|-
 * <li> < is a synonym for - and ^ is a synonym for |, for
 *   historical compatibility with an earlier version
 *   of this code.
 * </ul>
 * <p>
 * The - and | characters extending away from a component
 * denote the extent of that component. Dot characters are used
 * to fill in the space occupied by a multi-cell component, and
 * to indicate empty cells. Whitespace within a layout string
 * is ignored except that component identifiers such as "c1"
 * are delimited by either whitespace or one of the other
 * layout characters {.}|^<- . Layout identifiers can be
 * any string that does not contain any whitespace or any
 * of the layout characters. The layout string above is
 * a completely valid example, even with the additional
 * whitespace. It could have been specified without extra
 * whitespace like this:
 * <pre>
 *    String layout="{c1--c2}{c3-c4-}{|..c5}{|.c6-}";
 * </pre>
 * but that would defeat the purpose of making the 2D structure
 * of the layout clear.
 * <p>
 * To add a component to a container based on the last layout
 * string parsed, use the add(String layoutId,Component comp)
 * method, and set the layoutId to a layout identifier from
 * the layout string. For example,
 * <pre>
 *    gr.add("c1",new JLabel("Top left label"));
 * </pre>   
 * adds the JLabel at the position of the "c1" token in the
 * layout string. You can add components in any order, since
 * their grid positions and extents are derived from the
 * layout string.
 * <p>
 * As in the other add() method, you can specify additional
 * constraints:
 * <pre>
 *    JButton btn1(new AbstractAction("Push me") {...});
 *    gr.add("c2", btn1, "weightx 5.0");
 * </pre>
 * <p>
 * There are two things to remember about constraints when using
 * text-based layouts, however:
 * <ol>
 * <li> Any gridwidth or gridheight constraints you supply will
 *    be ignored, since those constraints will be derived from
 *    the layout string.
 * <li> If you do not explicitly supply weightx and weighty
 *    constraints, those constraints will be set to 1/100 of
 *    the gridwidth and gridheight of the component, respectively.
 *    This is because usually, we want components to scale
 *    according to their grid size when their container is
 *    resized. This way, you will get reasonable behavior
 *    from a text-based layout if you don't supply any
 *    weights at all. However, if you need to specify weights
 *    explicitly, you can do that for only the components
 *    that need them, provided you use explicit weights that
 *    are large relative to the gridsize/100 values assigned
 *    to other components by default. I advise using explicit
 *    weights with ranges >= 1.0.
 * </ol>
 * <p>
 * It is, of course, possible to write a nonsensical layout
 * using the simple layout language described above. In the
 * interest of keeping it simple, the code does not defend
 * against this possibility. So don't do that.
 * 
 * @author jk
 */
public class Gridder {
	
	/**
	 * Construct a Gridder instance.
	 * @param container The container the Gridder will manage.
	 * @param constraints Default constraints to apply to added components.
	 */
	public Gridder(Container container,Object... constraints) {
		super();
		this.container = container;
		this.layout = null;
		container.setLayout(new GridBagLayout());
		this.defaultConstraints = this.getDefaultConstraints();
		parseConstraints(this.defaultConstraints,constraints);
	}

	/**
	 * Add a component to the Gridder's managed container
	 * @param comp The component to add.
	 * @param row The first grid row the component occupies.
	 * @param col The first grid column the component occupies.
	 * @param constraints Any additional constraints to apply to the component.
	 * These can be specified any way the caller likes:
	 * <pre>
     * - As one big string, like "gridwidth 2 weightx 3.0"
     * - As a list of strings, like "gridwidth 2","weightx 3.0"
     * - As a list of constraint names and values, like
     *   "gridwidth",2,"weightx",3.0
     * - Or any combination of these.
     * </pre>
	 */
	public void add(Component comp, int row, int col, Object...constraints) {
		GridBagConstraints gbc = copyGBC(defaultConstraints);
		parseConstraints(gbc, constraints);
		gbc.gridx = col;
		gbc.gridy = row;
		this.container.add(comp,gbc);
	}

	/**
	 * Parse a layout string as described in the class comment. This allows
	 * components to be added using the add(String,Component) method.
	 * @param layoutStr The layout string to parse.
	 */
	public void parseLayout(String layoutStr) {
		layout = new LayoutParser(layoutStr);
	}
	
	/**
	 * Add a component whose position and extent will be determined based
	 * on the last parsed layout string.
	 * @param layoutName The layout ID of the component within the layout string.
	 * @param comp The component to add.
	 * @param constraints Any additional constraints to apply to the component.
	 * These can be specified any way the caller likes:
	 * <pre>
     * - As one big string, like "gridwidth 2 weightx 3.0"
     * - As a list of strings, like "gridwidth 2","weightx 3.0"
     * - As a list of constraint names and values, like
     *   "gridwidth",2,"weightx",3.0
     * - Or any combination of these.
     * </pre>
	 * Note that gridwidth and gridheight constraints will be ignored since
	 * they are derived from the layout string.
	 */
	public void add(String layoutName,Component comp,Object...constraints) {
		if (layout == null) {
			throw new RuntimeException("No layout string has been parsed");
		}
		int[] rcwh = layout.getComponentPositionAndSize(layoutName);
		constraints = addGridSizeAndWeightConstraints(rcwh[2],rcwh[3],constraints);
		add(comp,rcwh[0],rcwh[1],constraints);
	}

	/**
	 * Get the container managed by this Gridder.
	 * @return the container.
	 */
	public Container getContainer() {
		return this.container;
	}
	
	/**
	 * Get the container managed by this Gridder as a JComponent
	 * instance, if possible. Otherwise, return null.
	 * @return The container cast to JComponent, or null if the
	 * container is not a JComponent.
	 */
	public JComponent getContainerAsJComponent() {
		if (container instanceof JComponent) {
			return (JComponent)container;
		}
		return null;
	}
	
	/**
	 * Update this Gridder's default constraints with the constraints
	 * given in the constraint list.
	 * @param constraints A list of constraint names and values.
	 */
	public void updateConstraints(Object...constraints) {
		parseConstraints(this.defaultConstraints,constraints);
	}
	
	/**
	 * When adding a component based on a layout string, add the necessary
	 * grid size and weight parameters to the constraints.
	 * @param gridwidth The gridwidth of the component being added.
	 * @param gridheight The gridheight of the component being added.
	 * @param constraints A list of constraints.
	 * @return A new constraint list with the gridwidth, gridheight, and any
	 * unspecified weight parameters added.
	 */
	private Object[] addGridSizeAndWeightConstraints(int gridwidth,int gridheight,Object[] constraints) {
		String constraintString = buildConstraintString(constraints);
		StringBuilder sb = new StringBuilder(constraintString);
		String[] constraintToks = constraintString.split(" ");
		sb.append(" gridwidth ").append(gridwidth);
		sb.append(" gridheight ").append(gridheight);
		if (!arrayContains("weightx",constraintToks)) {
			sb.append(" weightx ").append((double)gridwidth/100.0);
		}
		if (!arrayContains("weighty",constraintToks)) {
			sb.append(" weighty ").append((double)gridheight/100.0);
		}
		return new Object[] {sb.toString()};
	}
	
	/**
	 * Parse a set of constraints and update a GridBagConstraints object.
	 * @param constraints Logically, a list of constraintName value pairs.
	 * @return A GridBagConstraints object with all constraints filled
	 * in as specified, and default values for any unspecified constraint.
	 */
	private void parseConstraints(GridBagConstraints toUpdate,Object... constraints) {
		String bigConstraintString = buildConstraintString(constraints);
		parseStringConstraints(bigConstraintString,toUpdate);
	}

	/**
	 * Take an Object[] array and collapse it down to a single String, with
	 * exactly one space character between adjacent tokens.
	 * @param constraints A constraint array.
	 * @return A string containing the same content as the input.
	 */
	private String buildConstraintString(Object[] constraints) {
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
	private void parseStringConstraints(String constraints,GridBagConstraints gbc) {
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
	private void interpretConstraint(String cname,String cval,GridBagConstraints gbc) {
		switch (cname) {
		case "gridwidth":
		case "width":
			gbc.gridwidth = toInt(cname,cval);
			break;
		case "gridheight":
		case "height":
			gbc.gridheight = toInt(cname,cval);
			break;
		case "weightx":
			gbc.weightx = toDouble(cname,cval);
			break;
		case "weighty":
			gbc.weighty = toDouble(cname,cval);
			break;
		case "anchor":
			gbc.anchor = toAnchorValue(cval);
			break;
		case "fill":
			gbc.fill = toFillValue(cval);
			break;
		case "ipadx":
			gbc.ipadx = toInt(cname,cval);
			break;
		case "ipady":
			gbc.ipady = toInt(cname,cval);
			break;
		case "inset_top":
		case "insets_top":
			// Note: we know gbc.insets is non-null because we create all
			// GBC objects that this code will see.
			gbc.insets.top = toInt(cname,cval);
			break;
		case "inset_bottom":
		case "insets_bottom":
			gbc.insets.bottom = toInt(cname,cval);
			break;
		case "inset_left":
		case "insets_left":
			gbc.insets.left = toInt(cname,cval);
			break;
		case "inset_right":
		case "insets_right":
			gbc.insets.right = toInt(cname,cval);
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
	private int toAnchorValue(String value) {
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
	private int toFillValue(String value) {
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
	private int toInt(String cname,String value) {
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
	private double toDouble(String cname,String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException ex) {
			throw new RuntimeException("Bad float constraint value {"+value+"} for constraint "+cname);
		}
	}
	
	/**
	 * @return a GridBagConstraints filled with default values.
	 */
	private GridBagConstraints getDefaultConstraints() {
		return new GridBagConstraints(0, 0,
				1, 1,
				0.0, 0.0,
				GridBagConstraints.CENTER,
				GridBagConstraints.NONE,
				new Insets(0, 0, 0, 0),
				0, 0);
	}

	/**
	 * Copy a GridBagConstraints object.
	 * @param from The GBC to copy.
	 * @return A new GBC initialized from the given one.
	 */
	private GridBagConstraints copyGBC(GridBagConstraints from) {
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
	 * Check whether a String array contains a given String,
	 * using the .equals() test.
	 * @param target String to search for.
	 * @param strs Array to search in.
	 * @return true if target is in objects.
	 */
	private boolean arrayContains(String target,String...strs) {
		if (target == null) {
			return false;
		}
		for (String str: strs) {
			if (target.equals(str)) {
				return true;
			}
		}
		return false;
	}

	// The container being managed.
	private Container container;

	// The default constraints.
	private GridBagConstraints defaultConstraints;

	// The LayoutParser for the last parsed layout string,
	// if any. If null, the add(String,Component,Object...) method
	// will fail with a RuntimeException.
	private LayoutParser layout;	
}
