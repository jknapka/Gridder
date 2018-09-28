package net.kneuro.layout;

import java.awt.GridBagConstraints;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;

/**
 * A class that exercises the functionality of Gridder. Start
 * this as the main class to see a window laid out using the
 * 2D layout language example from the Gridder class comment.
 * 
 * @author jk
 *
 */
public class GridderTest {
	public static void main(String[] argv) throws Exception {
		JFrame top = new JFrame("Gridder test");

		// Build a grid using the 2D layout example from README.md. The
		// c3 component will be a JPanel with its own layout.
		Gridder gr = new Gridder(top.getContentPane());
		String layout =
				 "    {c1                 +   +     c2}    "+
				 "    {c3:wx1,wy2,i*5,fxy +   c4    - }    "+
				 "    {|                  -   -     c5}    "+
				 "    {|                  -   c6    + }    ";
		gr.parseLayout(layout);
		
		String[] cnames = {"c1","c2","c3","c4","c5","c6"};
		for (String cname: cnames) {
			LayoutParser.ComponentPosition comp = gr.getLayoutParser().getComponentByName(cname);
			System.out.println(comp.name+" @ "+comp.row+","+comp.col+" w="+comp.width+" h="+comp.height+" cons="+comp.constraints);
		}
		
		gr.add("c1", new JLabel("c1: Top left"),"anchor w");
		gr.add("c4", new JLabel("c4: Kinda middle"));
		gr.add("c2", new JButton("c2: Top right"),"anchor e");
		gr.add("c5", new JCheckBox("c5: Check me"));
		gr.add("c3", getSubPanel());
		gr.add("c6", new JLabel("c6: Bottom right label with very long string"));
		
		top.pack();
		top.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});
		
		top.setVisible(true);
		
		while (true) Thread.sleep(1000);
	}

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
}
