/*
 * Copyright 2007 Madhav Pulipaka
 * 
 * This file is part of Vela.

 * Vela is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Vela is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Vela; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
*/
package vela.view;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;
import javax.swing.event.HyperlinkListener;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLFrameHyperlinkEvent;

import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;

public class License extends JDialog implements ActionListener{
	
	private static License messageBox;
	private JEditorPane taMessage;
	private static final int WIDTH = 500;
	private static final int HEIGHT = 300;
	
	private License(Frame frame, String title, boolean isModel)
	{
		super(frame, title, isModel);
		Container container = getContentPane();
		JButton btnOK = new JButton("Close");
		taMessage = new JEditorPane();
		taMessage.setEditable(false);
		taMessage.setEnabled(false);
		container.add(new JScrollPane(taMessage));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int)(screenSize.getWidth()/2)-(WIDTH/2), (int)(screenSize.getHeight()/2)-(HEIGHT/2), WIDTH, HEIGHT);
		btnOK.addActionListener(this);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(btnOK);
		container.add(southPanel, BorderLayout.SOUTH);
		taMessage.setFont(new Font("Verdana",Font.BOLD,11));
		taMessage.setDisabledTextColor(Color.BLACK);
		//taMessage.setBackground(Color.LIGHT_GRAY);
		btnOK.setFont(new Font("Verdana",Font.BOLD,12));
		//btnOK.setBackground(new Color(0,0,100));
		//btnOK.setForeground(Color.YELLOW);
	}
	
	public static void showMessage(Frame frame, String title, boolean isModel)
	{
		if(messageBox==null)
			messageBox = new License(frame, title, isModel);
		messageBox.setMessage();		
		messageBox.setVisible(true);		
	}
	
	public void setMessage()
	{		
		taMessage.setText("Copyright 2007 Madhav Pulipaka\n\nVela is free software; you can redistribute it and/or modify\n"+
							"it under the terms of the GNU General Public License as published by\n"+
							"the Free Software Foundation; either version 2 of the License, or\n"+
							"(at your option) any later version.\n\n"+							
							"Vela is distributed in the hope that it will be useful,\n"+
							"but WITHOUT ANY WARRANTY; without even the implied warranty of\n"+
							"MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the\n"+
							"GNU General Public License for more details.\n\n"+							
							"You should have received a copy of the GNU General Public License\n"+
							"along with Vela; if not, write to the Free Software\n"+
							"Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA");
	}
	
	public void actionPerformed(ActionEvent arg0){
		setVisible(false);		
	}
	
}
