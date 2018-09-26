package net.kneuro.layout;

import java.awt.GridBagConstraints;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;

public class GridderTest {
	public static void main(String[] argv) throws Exception {
		JFrame top = new JFrame("Gridder test");

		Gridder gr = new Gridder(top.getContentPane());
		String layout =
				 "    {c1 - - c2}    "+
				 "    {c3 - c4 -}    "+
				 "    {|  . . c5}    "+
				 "    {|  . c6 -}    ";
		gr.parseLayout(layout);
		
		gr.add("c1", new JLabel("Top left"));
		gr.add("c4", new JLabel("Kinda middle"));
		gr.add("c2", new JButton("Top right"),"anchor e");
		gr.add("c5", new JCheckBox("Check me"),"anchor w");
		gr.add("c3", getSubPanel(), "weightx 1.0","weighty",1.0,"fill both");
		gr.add("c6", new JLabel("Bottom right label with very long string"), "anchor w");
		
		top.pack();
		top.addWindowListener(new WindowListener() {

			@Override
			public void windowOpened(WindowEvent e) {
			}

			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}

			@Override
			public void windowClosed(WindowEvent e) {
			}

			@Override
			public void windowIconified(WindowEvent e) {
			}

			@Override
			public void windowDeiconified(WindowEvent e) {
			}

			@Override
			public void windowActivated(WindowEvent e) {
			}

			@Override
			public void windowDeactivated(WindowEvent e) {
			}
			
		});
		
		top.setVisible(true);
		
		while (true) Thread.sleep(1000);
	}
	
	private static JComponent getSubPanel() {
		JPanel pnl = new JPanel();
		pnl.setBorder(BorderFactory.createEtchedBorder());
		Gridder gr = new Gridder(pnl);
		gr.add(new JButton("Grow"), 0,0,"anchor nw weightx 1 weighty 1 fill xy");
		gr.add(new JButton("Float NE"), 0,1,"anchor ne weightx 1 weighty 1");
		gr.add(new JButton("Stay"), 1,0,"anchor","e");
		gr.add(new JButton("Together"), 1,1,"anchor",GridBagConstraints.WEST);
		return pnl;
	}
}
