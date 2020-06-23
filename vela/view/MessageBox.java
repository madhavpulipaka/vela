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
import java.awt.*;
import java.awt.event.*;

public class MessageBox extends JDialog implements ActionListener{
	
	private static MessageBox messageBox;
	private JTextArea taMessage;
	private static final int WIDTH = 525;
	private static final int HEIGHT = 200;
	
	private MessageBox(Frame frame, String title, boolean isModel)
	{
		super(frame, title, isModel);
		Container container = getContentPane();
		JButton btnOK = new JButton("Close");
		taMessage = new JTextArea("");
		taMessage.setEnabled(false);
		taMessage.setBackground(Color.LIGHT_GRAY);
		taMessage.setDisabledTextColor(new Color(128,0,0));
		container.add(new JScrollPane(taMessage));
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int)(screenSize.getWidth()/2)-(WIDTH/2), (int)(screenSize.getHeight()/2)-(HEIGHT/2), WIDTH, HEIGHT);
		btnOK.addActionListener(this);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(btnOK);
		container.add(southPanel, BorderLayout.SOUTH);
		taMessage.setFont(new Font("Verdana",Font.BOLD,11));
		btnOK.setFont(new Font("Verdana",Font.BOLD,12));
		//btnOK.setBackground(new Color(0,0,100));
		//btnOK.setForeground(new Color(0,0,128));
	}
	
	public static void showMessage(Frame frame, String title, boolean isModel, String message)
	{
		if(messageBox==null)
			messageBox = new MessageBox(frame, title, isModel);
		messageBox.setMessage(message);
		messageBox.setVisible(true);		
	}
	
	public void setMessage(String message)
	{
		taMessage.setText(message);
	}
	
	public void actionPerformed(ActionEvent arg0){
		setVisible(false);		
	}
	
}
