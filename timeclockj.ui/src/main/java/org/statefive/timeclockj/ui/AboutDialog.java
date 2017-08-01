/*
 * Adapted from:
 * http://www.eng.auburn.edu/~rayh/java/java/AWT.MenusDialogsWindows.html</a>
 */

package org.statefive.timeclockj.ui;
// Note this class is created hidden
// You'll need to call show() to make it visible

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.Color;
import java.awt.Dialog;
import java.awt.Event;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Panel;

class AboutDialog extends Dialog
{
	static int H_SIZE = 200;
	static int V_SIZE = 200;

	public AboutDialog(Frame parent)
	{
		// Calls the parent telling it this
		// dialog is modal(i.e true)
                super(parent, true);
		setBackground(Color.gray);
		setLayout(new BorderLayout());

		// Two buttons "Close" and "Help"
		Panel p = new Panel();
		p.add(new Button("Close"));
		p.add(new Button("Help"));
                add("South", p);
		resize(H_SIZE, V_SIZE);
	}

        public boolean action(Event evt, Object arg)
        {
		// If action label(i.e arg) equals
		// "Close" then dispose this dialog
                if(arg.equals("Close"))
                {
                        dispose();
                        return true;
                }
                return false;
        }

	public void paint(Graphics g)
	{
		g.setColor(Color.white);
                g.drawString("JTimeClock", H_SIZE/4, V_SIZE/3);
                g.drawString("Version 1.0", H_SIZE/3, V_SIZE/3+20);
	}
}
