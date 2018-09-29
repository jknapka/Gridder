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
 * <h1>Gridder: Never Deal With GridBagConstraints Again</h1>
 * 
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
 * 
 * <h2>Basic Usage</h2>
 * 
 * To use Gridder, create a Gridder instance and give it the container
 * you want it to manage, as well as any default constraints as a
 * simple string of "constraintName value" pairs (or as multiple
 * strings of such values, or as constraint names followed by their
 * string, integer, or floating-point values, since the final argument
 * to Gridder() is a varargs array).
 * <br><br> 
 * <pre>
 *    JFrame topLevel = new JFrame();
 *    Gridder gr = new Gridder(topLevel.getContentPane(),
 *          "weightx 1.0 weighty 0.0",
 *    		"inset_top 5", "inset_bottom", 5,
 *          "anchor center","fill","xy");
 * </pre>
 * 
 * You can then add components via the Gridder object, which handles
 * all the nastiness of setting up the GridBagConstraints.
 * <br><br> 
 * <pre>
 *    // Uses the default constraints, places label at row 0, column 2.
 *    gr.add(new JLabel("Name:"),0,2);
 * </pre>
 * 
 * For any individual component, you can override the default constraints
 * in the add() method (which does not change the defaults). Constraints
 * are specified exactly as they are to the Gridder constructor.
 * <br><br>
 * <pre>
 *    JTextField nameFld = new JTextField();
 *    gr.add(nameFld,0,3,"weightx", 4.0,"fill horizontal");
 * </pre>
 * 
 * You can also call gr.updateConstraints("constraint1 value1 ...") to
 * update the default constraints, which will then be used for all
 * future add() calls.
 * <p>
 * In general, all constraint names are the same as the corresponding
 * member names of the GridBagConstraints class. The exception is the
 * GridBagConstraints.insets member, which is realized here as
 * separate constraints inset_top, inset_bottom, inset_left, and
 * inset_right. Gridder also supports constraint name abbreviations,
 * as well as many alternative values for the anchor and fill
 * constraints (see the table below).
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
 * <br><br>
 * <pre>
 * <b>GBC.member    Gridder name                  Possible values         Default</b>
 * ----------------------------------------------------------------------------
 * gridwidth        gridwidth,width,wd            Integer                 1
 * ----------------------------------------------------------------------------
 * gridheight       gridheight,height,ht          Integer                 1
 * ----------------------------------------------------------------------------
 * weightx          weightx,wx                    Float                   0.0
 * ----------------------------------------------------------------------------
 * weighty          weighty,wy                    Float                   0.0
 * ----------------------------------------------------------------------------
 *                  w* and weight* mean           Float
 *                 "both weights"
 * ----------------------------------------------------------------------------
 * anchor           anchor,a                      center, ctr, c          center
 *                                                north, n, top
 *                                                south, s, bot, bottom
 *                                                east, e, right, r
 *                                                west, w, left, l
 *                                                northeast, ne, 
 *                                                topright, tr
 *                                                northwest, nw, 
 *                                                topleft, tl
 *                                                southeast, se, 
 *                                                bottomright, br
 *                                                southwest, sw, 
 *                                                bottomleft, bl
 *                                                Any of the int anchor
 *                                                values defined in
 *                                                GridBagConstraints.
 * ----------------------------------------------------------------------------
 * fill             fill,f                        none, neither            none
 *                                                horizontal, h, x
 *                                                vertical, v, y
 *                                                both, all, xy, yx
 *                                                hv, vh
 *                                                Any of the int fill 
 *                                                values defined in 
 *                                                GridBagConstraints.
 * ----------------------------------------------------------------------------
 * ipadx            ipadx,px                      Integer                 0
 * ----------------------------------------------------------------------------
 * ipady            ipady,py                      Integer                 0
 * ----------------------------------------------------------------------------
 *                  p* or ipad* mean              Integer 
 *                  "both paddings"
 * ----------------------------------------------------------------------------
 * insets.top       inset_top,insets_top,it       Integer                 0
 * ----------------------------------------------------------------------------
 * insets.bottom    inset_bottom,insets_bottom,ib Integer                 0
 * ----------------------------------------------------------------------------
 * insets.left      inset_left,insets_left,il     Integer                 0
 * ----------------------------------------------------------------------------
 * insets.right     inset_right,insets_right,ir   Integer                 0
 * ----------------------------------------------------------------------------
 *                  i* or inset* or insets*       Integer
 *                  mean "all insets"
 * ----------------------------------------------------------------------------
 * gridx            None                        Supplied by the Gridder.add() method.
 * ----------------------------------------------------------------------------
 * gridy            None                        Supplied by the Gridder.add() method.
 * ----------------------------------------------------------------------------
 * </pre>
 *
 * Constraint names and values are <em>case insensitive</em>, so "ANCHOR NW" is
 * a valid constraint.
 * 
 * <h2>2D Text-Based Layouts</h2>
 * 
 * The other, and sometimes more convenient way to use Gridder is
 * to parse a textual layout string, and then add components to the
 * container using identifiers mentioned in the layout string. 
 * You should either use a text-based layout or the plain
 * add(Component,row,column) API for any given container, not both.
 * 
 * <h3>The 2D Layout Language</h3>
 * 
 * For example, here is a somewhat complex layout:
 * <br><br>
 * <pre>
 * String layout =
 *    "    {c1                 + + c2}    "+
 *    "    {c3:wx1,wy2,i*5,fxy + c4 -}    "+
 *    "    {|                  - - c5}    "+
 *    "    {|                  - c6 +}    ";
 * gr.parseLayout(layout);
 * </pre>
 * 
 * (Whitespace added to layout string for clarity.) Such a string
 * represents a rectangular array of grid cells and identifies
 * the position and extent of each component. This layout says that:
 * 
 * <ul>
 * <li> Component c1 occupies the first three cells of row 0
 *   (+ means "extend the previous component into this column").
 * <li> Component c2 occupies the fourth cell of row 0.
 * <li> Component c3 occupies the first two cells of row 1, and
 *   extends two cells downward to row 3 (| means "extend
 *   the component directly above into this row"). This component
 *   also contains embedded constraints that will be used to
 *   override the default constraints supplied to the gridder
 *   constructor: weightx = 1.0, weighty = 1.0, all inset values
 *   set to 5, and fill set to xy. The syntax of those constraints
 *   is described below.
 * <li> Component c4 occupies the third and fourth cells of
 *   row 1.
 * <li> Component c5 occupies the fourth cell of row 2.
 * <li> Component c6 occupies the third and fourth cells of
 *   row 3.
 * <li> The cell at row 2, column 2 is empty.
 * </ul>
 * <p>
 * 
 * In general, 
 * 
 * <ul>
 * <li> Curly brackets { and } delimit each grid row
 * <li> + causes the gridwidth of the component to the
 *   left to be increased by 1
 * <li> | causes the gridheight of the component *directly*
 *   above to be increased by 1
 * <li> The + and | characters extending down and to the right of a component
 * denote the extent of that component
 * <li> - simply occupies space. It is used to fill in the space
 *   occupied by multi-cell components, and to fill empty cells.
 *   All grid cells must be filled with either a component identifier or
 *   one of the characters |-^&lt;+
 * <li> &lt; is a synonym for + and ^ is a synonym for |, for
 *   historical compatibility with an earlier version
 *   of this code
 * <li> Whitespace within a layout string is ignored except that
 *   component identifiers such as "c1" are delimited by either
 *   whitespace or one of the other layout characters {}|-^&lt;+
 * <li> Component identifiers are any string that contains
 *   no whitespace and none of the characters {}|-^&lt;+
 * <li> If a component identifier contains a colon, the
 *   characters following the colon are interpreted as a
 *   comma-separated list of constraints in the format
 *   constraintNameValue, with no space between constraint
 *   name and value. The constraint names are as described in the
 *   table above. Thus, "c1:wx1.0" and "c1:weightx1.0" may
 *   both be used to set the "weightx" constraint of
 *   component "c1" to 1.0. Using the short constraint
 *   names helps to keep a 2D layout compact. If you prefer
 *   maximally-compact 2D layouts, do not use embedded
 *   constraints; instead, specify override constraints in
 *   the add() method as described below.
 * </ul>
 * <p>
 * The layout string above is a completely valid example, even
 * with the additional whitespace. It could have been specified
 * without extra whitespace like this:
 * <br><br>
 * <pre>
 *    String layout="{c1++c2}{c3:wx1,wy2,i*5,fxy+c4+}{|--c5}{|-c6+}";
 * </pre>
 * 
 * but that would defeat the purpose of making the 2D structure
 * of the layout clear.
 *
 * <h3>Adding Components to a Text-Based Layout</h3>
 *
 * To add a component to a container based on the last layout
 * string parsed, use the add(String layoutId,Component comp)
 * method, and set the layoutId to a layout identifier from
 * the layout string. For example,
 * <br><br>
 * <pre>
 *    gr.add("c1",new JLabel("Top left label"));
 * </pre>   
 * 
 * adds the JLabel at the position of the "c1" token in the
 * layout string. You can add components in any order, since
 * their grid positions and extents are derived from the
 * layout string.
 * <p>
 * As in the other add() method, you can specify additional
 * constraints:
 * <br><br>
 * <pre>
 *    JButton btn1 = new JButton("Push me");
 *    gr.add("c2", btn1, "weightx 5.0 fill horizontal");
 * </pre>
 * 
 * Any constraints specified in the add() method will override
 * both the default constraints supplied to the Gridder constructor
 * and any embedded constraints from the layout string.
 * <p>
 * There are two more things to remember about constraints when using
 * text-based layouts:
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
 *    weights with ranges &gt;= 1.0.
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
	 * These can be specified any way the caller likes:
	 * <pre>
	 * - As one big string, like "gridwidth 2 weightx 3.0"
	 * - As a list of strings, like "gridwidth 2","weightx 3.0"
	 * - As a list of constraint names and values, like
	 *   "gridwidth",2,"weightx",3.0
	 * - Or any combination of these.
	 * </pre>
	 */
	public Gridder(Container container,Object... constraints) {
		super();
		this.container = container;
		this.layout = null;
		container.setLayout(new GridBagLayout());
		this.defaultConstraints = this.getDefaultConstraints();
		ConstraintParser.parseConstraints(this.defaultConstraints,constraints);
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
		GridBagConstraints gbc = ConstraintParser.copyGBC(defaultConstraints);
		ConstraintParser.parseConstraints(gbc, constraints);
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
		LayoutParser.ComponentPosition cp = layout.getComponentByName(layoutName);
		if (cp != null) {
			String embeddedConstraints = cp.constraints;
			Object[] augmentedConstraints = new Object[constraints.length + 1];
			augmentedConstraints[0] = embeddedConstraints;
			System.arraycopy(constraints,0,augmentedConstraints,1,constraints.length);
			constraints = addGridSizeAndWeightConstraints(cp.width,cp.height,augmentedConstraints);
			add(comp,cp.row,cp.col,constraints);
		} else {
			throw new RuntimeException("No component named "+layoutName+" in layout string.");
		}
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
		ConstraintParser.parseConstraints(this.defaultConstraints,constraints);
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
		String constraintString = ConstraintParser.buildConstraintString(constraints);
		StringBuilder sb = new StringBuilder(constraintString);
		String[] constraintToks = constraintString.split(" ");
		sb.append(" gridwidth ").append(gridwidth);
		sb.append(" gridheight ").append(gridheight);
		if (!arrayContains("weightx",constraintToks) &&
				!arrayContains("wx",constraintToks) &&
				!arrayContains("w*",constraintToks)) {
			sb.append(" weightx ").append((double)gridwidth/100.0);
		}
		if (!arrayContains("weighty",constraintToks) &&
				!arrayContains("wy",constraintToks) &&
				!arrayContains("w*",constraintToks)) {
			sb.append(" weighty ").append((double)gridheight/100.0);
		}
		return new Object[] {sb.toString()};
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

	// Return the LayoutParser instance. For test and internal use only.
	LayoutParser getLayoutParser() { return layout; }
	
	// The container being managed.
	private Container container;

	// The default constraints.
	private GridBagConstraints defaultConstraints;

	// The LayoutParser for the last parsed layout string,
	// if any. If null, the add(String,Component,Object...) method
	// will fail with a RuntimeException.
	private LayoutParser layout;	
}
