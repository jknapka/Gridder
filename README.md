# Gridder: Never Deal With GridBagConstraints Again

The Gridder class makes Swing's GridBagLayout easier, even a pleasure,
to use. It provides an interface to GridBagLayout that resembles
that of the Tcl/Tk grid layout manager. It also provides a text-based 
layout function, with which you can write a string that
represents the position and extent of each component in a 2D
fashion.

Gridder is merely a thin wrapper around GridBagLayout and
GridBagConstraints. It does not perform any layout management
itself, it is just a convenient way to specify layout
information to GridBagLayout.

## License

Gridder is offered under the MIT license. See license.txt for details.

## Basic Usage

To use Gridder, create a Gridder instance and give it the container
you want it to manage, as well as any default constraints as a
simple string of "constraintName value" pairs (or as multiple
strings of such values, or as constraint names followed by their
string, integer, or floating-point values, since the final argument
to `Gridder()` is a varargs array).

```
   JFrame topLevel = new JFrame();
   Gridder gr = new Gridder(topLevel.getContentPane(),
         "weightx 1.0 weighty 0.0",
         "inset_top 5", "inset_bottom", 5,
         "anchor center","fill","xy");
```

You can then add components via the Gridder object, which handles
all the nastiness of setting up the GridBagConstraints. Pass the
grid row and column of the top-left cell the component should
occupy as the second and third arguments to `add()`.

```
   // Uses the default constraints, places label at row 0, column 2.
   gr.add(new JLabel("Name:"),0,2);
```

For any individual component, you can override the default constraints
in the `add()` method (which does not change the defaults). Constraints
are specified exactly as they are to the Gridder constructor, as varargs
after the column number.

```
   // Uses the default constraints, except that the weightx and
   // fill constraints are overridden by the trailing arguments
   // to add().
   JTextField nameFld = new JTextField();
   gr.add(nameFld,0,3,"weightx", 4.0,"fill horizontal");
```

You can also call `gr.updateConstraints("constraint1 value1 ...")` to
update the default constraints, which will then be used for all
future `add()` calls. The constraints may be specified as for the
Gridder constructor.

In general, all constraint names are the same as the corresponding
member names of the GridBagConstraints class. The exception is the
GridBagConstraints.insets member, which is realized here as
separate constraints `inset_top`, `inset_bottom`, `inset_left`, and
`inset_right`. Also, short constraint name abbreviations are
supported, as are wildcard constraints such as `weight*` to set
both `weightx` and `weighty` with a single constraint specifier.

Constraint values can be specified as either raw values of the
appropriate type, or as strings that can be converted to the
appropriate type. Gridder also supports many synonyms for the
various anchor and fill constraints (see the table below).

Unrecognized constraints or constraints with invalid
values cause a RuntimeException. Since the expected usage of
this class is to set up a container at UI-construction time,
I expect such problems to occur only during development, so
throwing a RuntimeException seems reasonable.

Constraint names, values, and defaults (if not specified in the
Gridder constructor or the `add()` method) are summarized in the
following table (note that the defaults for weights are different
when text-based layouts are used - see below).

|GBC.member     | Gridder name              | Possible values                |  Default
|---------------|---------------------------|--------------------------------|----------
|gridwidth      | gridwidth, width, wd      | Integer                        |  1
|gridheight     | gridheight, height, ht    | Integer                        |  1
|weightx        | weightx, wx               | Float                          |  0.0
|weighty        | weighty, wy               | Float                          |  0.0
|               | weight*, w* mean "both weights"|                           |
|anchor         | anchor, a                 | center, ctr, c                 |  center
|               |                           | north, n, top                  |
|               |                           | south, s, bot, bottom          |
|               |                           | east, e, right, r              |
|               |                           | west, w, left, l               |
|               |                           | northeast, ne, topright, tr    |
|               |                           | northwest, nw, topleft, tl     |
|               |                           | southeast, se, bottomright, br |
|               |                           | southwest, sw, bottomleft, bl  |
|               |                           | Any of the int anchor values defined in GridBagConstraints  |
|fill           | fill, f                   | none, neither                  |  none
|               |                           | horizontal, h, x               |
|               |                           | vertical, v, y                 |
|               |                           | both, all, xy, yx, hv, vh      |
|               |                           | Any of the int fill values defined in GridBagConstraints    |
|ipadx          | ipadx, px                 | Integer                        |  0
|ipady          | ipady, py                 | Integer                        |  0
|               | ipad*, p* mean "both paddings"|                            |
|insets.top     | inset_top, insets_top, it | Integer                        |  0
|insets.bottom  | inset_bottom, insets_bottom, ib| Integer                   |  0
|insets.left    | inset_left, insets_left, il| Integer                       |  0
|insets.right   | inset_right, insets_right, ir| Integer                     |  0
|               | i*, inset*, insets* mean "all insets"|                     |
|gridx          | None                      | Supplied by the Gridder.add() method. |
|gridy          | None                      | Supplied by the Gridder.add() method. |
----------------------------------------------------------------------------

Constraint names and values are _case insensitive_, so `"ANCHOR NW"` is
a valid constraint.

## Text-Based Layouts

The other, and sometimes more convenient way to use Gridder is
to parse a textual layout string, which allows you to specify your
layout in a 2D fashion. You can then add components to the
container using identifiers mentioned in the layout string. 
You should either use a text-based layout or the plain
`add(Component,row,column)` API for any given container, not both.
(However, it is perfectly OK for nested containers to use
their own Gridder instances, or some other layout manager entirely,
and configure their layouts any way they like.)

### The 2D Layout Language

For example, here is a somewhat complex layout:

```
String layout =
   "    {c1                 + + c2}    "+
   "    {c3:wx1,wy2,i*5,fxy + c4 +}    "+
   "    {|                  - - c5}    "+
   "    {|                  - c6 +}    ";

// We must parse the layout before Gridder can use it.
gr.parseLayout(layout);
```

(Whitespace has been added to the layout string for clarity.) Such a string
represents a rectangular array of grid cells and identifies
the position and extent of each component. This layout says that:

- Component `c1` occupies the first three cells of row 0
  (`+` means "extend the previous component into this column").
- Component `c2` occupies the fourth cell of row 0.
- Component `c3` occupies the first two cells of row 1, and
  extends two cells downward to row 3 (`|` means "extend
  the component directly above into this row"). The
  text following the colon specifies component-specific
  constraints that override the default constraints: `weightx=1`,
  `weighty=2`, all `insets.*=5`, and `fill=xy`.
- Component `c4` occupies the third and fourth cells of
  row 1.
- Component `c5` occupies the fourth cell of row 2.
- Component `c6` occupies the third and fourth cells of
  row 3.
- The cell at row 2, column 2 is empty.

An example presentation built from the layout string above
can be found in GridderTest.java. Here is the window as
it is initially shown:

![Initial packed layout](images/GridderTest-1.png)

And here it is after being resized:
![Resized layout](images/GridderTest-2.png)

The four buttons in the lower left are in a separate JPanel
configured by its own Gridder instance created using the
non-2d API:
```
	// Get a JPanel with some buttons. This is an example
	// of using the basic add(Component,row,col) API.
	private static JComponent getSubPanel() {
		JPanel pnl = new JPanel();
		pnl.setBorder(BorderFactory.createEtchedBorder());
		Gridder gr = new Gridder(pnl);
		gr.add(new JLabel("c3: a subpanel"),0,0,"gridwidth 2");
		gr.add(new JButton("Grow"), 1,0,"anchor nw weightx 1 weighty 1 fill xy");
		gr.add(new JButton("Float NE"), 1,1,"anchor ne weightx 1 weighty 1");
		gr.add(new JButton("Stay"), 2,0,"anchor","e");
		gr.add(new JButton("Together"), 2,1,"anchor",GridBagConstraints.WEST);
		return pnl;
	}
```

In general, 

- Curly brackets `{` and `}` delimit each grid row;
- `+` causes the gridwidth of the component to the
  left to be increased by 1
- `|` causes the gridheight of the component *directly*
  above to be increased by 1
- The `+` and `|` characters extending down and to the right
  from a component denote the extent of that component
- `-` simply occupies space. It is used to fill in the
  space occupied by multi-cell components, and to indicate
  empty grid cells.  All grid cells must be
  filled with either a component identifier or
  one of the characters `|+<^-`
- `<` is a synonym for `+` and `^` is a synonym for `|`, for
  historical compatibility with an earlier version
  of this code
- Whitespace within a layout string is ignored except that
   component identifiers such as "c1" are delimited by either
   whitespace or one of the other layout characters `{}|+<^-`
- Component identifiers are strings that contain
  no whitespace and none of the characters `{}|+^<-`
- If a component identifier contains a colon, the
  characters following the colon are interpreted as a
  comma-separated list of constraints in the format
  `constraintNameValue`, with no space between constraint
  name and value. The constraint names are as described in the
  table above. Thus, `c1:wx1.0` and `c1:weightx1.0` may
  both be used to set the `weightx` constraint of
  component `c1` to `1.0`. Using the short constraint
  names helps to keep a 2D layout compact. If you prefer
  maximally-compact 2D layouts, do not use embedded constraints;
  instead, override constraints in the `add()` method as
  described below.

The layout string above is a completely valid example,
even with the additional whitespace. It could have been 
specified without extra whitespace like this:

```
   String layout="{c1++c2}{c3:wx1,wy2,i*5,fxy+c4+}{|--c5}{|-c6+}";
```

but that would defeat the purpose of making the 2D structure
of the layout clear.

It is, of course, possible to write a nonsensical layout
using the simple layout language described above. In the
interest of keeping it simple, the code does not defend
against this possibility; you will just observe your components
laid out in a nonsensical manner. So don't do that.

### Adding Components to a Text-Based Layout

To add a component to a container based on the last layout
string parsed, use the `add(String layoutId,Component comp)`
method, and set the `layoutId` to a component identifier from
the layout string. For example,

```
   gr.add("c1",new JLabel("Top left label"));
```

adds the JLabel at the position of the `c1` token in the
layout string. You can add components in any order, since
their grid positions and extents are derived from the
layout string.

As in the other `add()` method, you can specify additional
constraints:

```
   JButton btn1(new AbstractAction("Push me") {...}));
   gr.add("c2", btn1, "weightx 5.0");
```
Constraints specified in the add() method will override both
the default constraints supplied to the Gridder constructor
*and* any embedded constraints from the layout string.

When using text-based layouts, keep in mind that any `gridwidth`
or `gridheight` constraints you supply will
be ignored, since those constraints will be derived from
the layout string.

