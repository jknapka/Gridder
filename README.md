# Gridder, a convenient interface to GridBagLayout

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
   JTextField nameFld = new JTextField();
   gr.add(nameFld,0,3,"weightx", 4.0,"fill horizontal");
```

You can also call `gr.updateConstraints("constraint1 value1 ...")` to
update the default constraints, which will then be used for all
future `add()` calls.

In general, all constraint names are the same as the corresponding
member names of the GridBagConstaints class. The exception is the
GridBagConstraints.insets member, which is realized here as
separate constraints `inset_top`, `inset_bottom`, `inset_left`, and
`inset_right`.

Constraint values can be specified as either raw values of the
appropriate type, or as strings that can be converted to the
appropriate type.

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
|gridwidth      | gridwidth,width           | Integer                        |  1
|gridheight     | gridheight,height         | Integer                        |  1
|weightx        | weightx                   | Float                          |  0.0
|weighty        | weighty                   | Float                          |  0.0
|anchor         | anchor                    | center,ctr,c                   |  center
|               |                           | north,n,top                    |
|               |                           | south,s,bot,bottom             |
|               |                           | east,e,right,r                 |
|               |                           | west,w,left,l                  |
|               |                           | northeast,ne,topright,tr       |
|               |                           | northwest,nw,topleft,tl        |
|               |                           | southeast,se,bottomright,br    |
|               |                           | southwest,sw,bottomleft,bl     |
|               |                           | Any of the int anchor values defined in GridBagConstraints  |
|fill           | fill                      | none,neither                   |  none
|               |                           | horizontal,h,x                 |
|               |                           | vertical,v,y                   |
|               |                           | both,all,xy                    |
|               |                           | Any of the int fill values defined in GridBagConstraints    |
|ipadx          | ipadx                     | Integer                        |  0
|ipady          | ipady                     | Integer                        |  0
|insets.top     | inset_top,insets_top      | Integer                        |  0
|insets.bottom  | inset_bottom,insets_bottom| Integer                        |  0
|insets.left    | inset_left,insets_left    | Integer                        |  0
|insets.right   | inset_right,insets_right  | Integer                        |  0
|gridx          | None                      | Supplied by the Gridder.add() method. |
|gridy          | None                      | Supplied by the Gridder.add() method. |
----------------------------------------------------------------------------

## Text-Based Layouts

The other, and sometimes more convenient way to use Gridder is
to parse a textual layout string, which allows you to specify your
layout in a 2D fashion. You can then add components to the
container using identifiers mentioned in the layout string. 
You should either use a text-based layout or the plain
`add(Component,row,column)` API for any given container, not both.

### The 2D Layout Language

For example, here is a somewhat complex layout:

```
String layout =
   "    {c1 - - c2}    "+
   "    {c3 - c4 -}    "+
   "    {|  . . c5}    "+
   "    {|  . c6 -}    ";

// We must parse the layout before Gridder can use it.
gr.parseLayout(layout);
```

(Whitespace has been added to the layout string for clarity.) Such a string
represents a rectangular array of grid cells and identifies
the position and extent of each component. This layout says that:

- Component `c1` occupies the first three cells of row 0
  (`-` means "extend the previous component into this column").
- Component `c2` occupies the fourth cell of row 0.
- Component `c3` occupies the first two cells of row 1, and
  extends two cells downward to row 3 (`|` means "extend
  the component directly above into this row").
- Component `c4` occupies the third and fourth cells of
  row 1.
- Component `c5` occupies the fourth cell of row 2.
- Component `c6` occupies the third and fourth cells of
  row 3.
- The cell at row 2, column 2 is empty.

In general, 

- Curly brackets `{` and `}` delimit each grid row;
- `-` causes the gridwidth of the component to the
  left to be increased by 1;
- `|` causes the gridheight of the component *directly*
  above to be increased by 1;
- `.` simply occupies space. All grid cells must be
  filled with either a component identifier or
  one of the characters `.<^|-`;
- `<` is a synonym for `-` and `^` is a synonym for `|`, for
  historical compatibility with an earlier version
  of this code.

The `-` and `|` characters extending away from a component
denote the extent of that component. Dot characters are used
to fill in the space occupied by a multi-cell component, and
to indicate empty cells. Whitespace within a layout string
is ignored except that component identifiers such as "c1"
are delimited by either whitespace or one of the other
layout characters `{.}|^<-` . Component identifiers can be
any string that does not contain any whitespace or any
of the layout characters. The layout string above is
a completely valid example, even with the additional
whitespace. It could have been specified without extra
whitespace like this:

```
   String layout="{c1--c2}{c3-c4-}{|..c5}{|.c6-}";
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
method, and set the layoutId to a layout identifier from
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

There are two things to remember about constraints when using
text-based layouts, however:

1. Any `gridwidth` or `gridheight` constraints you supply will
   be ignored, since those constraints will be derived from
   the layout string.
2. If you do not explicitly supply `weightx` and `weighty`
   constraints in the `add()` method, those constraints will be set to 1/100 of
   the gridwidth and gridheight of the component, respectively.
   The default `weightx` and `weighty` constraints supplied to
   the Gridder constructor are ignored.
   This is because usually, we want components to scale
   according to their grid size when their container is
   resized. This way, you will get reasonable behavior
   from a text-based layout if you don't supply any
   weights at all. However, if you need to specify weights
   explicitly, you can do that for only the components
   that need them, provided you use explicit weights that
   are large relative to the gridsize/100 values assigned
   to other components by default. I advise using explicit
   weights with ranges >= 1.0.

