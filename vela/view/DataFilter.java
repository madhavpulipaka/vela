package vela.view;

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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JEditorPane;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.JToolTip;
import javax.swing.border.Border;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Position;
import vela.model.*;
import vela.common.Constants;

public class DataFilter extends JDialog implements ActionListener, KeyListener, ItemListener, Constants{

	private static final int WIDTH = 400;
	private static final int HEIGHT = 200;
	JComboBox colNames;
	JEditorPane tpFilterString;
	DBObject tableObj;
	Frame parentFrame;
	String title;
		
	public DataFilter(Frame frame, DBObject tableObj, String title)
	{
		super(frame, title, true);
		this.tableObj = tableObj;
		this.title = title;
		parentFrame = frame;
		Container container = getContentPane();
		JButton btnApply = new JButton("Apply");
		JButton btnCancel = new JButton("Cancel");		
		JButton btnClear = new JButton("Clear");
		btnApply.setActionCommand("Apply");
		btnCancel.setActionCommand("Cancel");
		btnClear.setActionCommand("Clear");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int)(screenSize.getWidth()/2)-(WIDTH/2), (int)(screenSize.getHeight()/2)-(HEIGHT/2), WIDTH, HEIGHT);
		btnApply.addActionListener(this);
		btnClear.addActionListener(this);
		btnCancel.addActionListener(this);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(btnApply);
		southPanel.add(btnClear);
		southPanel.add(btnCancel);
		
		JLabel help = new JLabel(" ? ");
		help.setBorder(BorderFactory.createEtchedBorder());
		help.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		JLabel label = null;
		if(title.startsWith("Filter"))
		{
			label = new JLabel("Please enter the filter criteria.");
			help.setToolTipText("<HTML><FONT FACE=\"Verdana, sans-serif\" size=\"3\">Enter SQL \"WHERE\" clause as Filter Criteria.<BR>Please do not include \"WHERE\" in the filter criteria.<BR>Filter criteria is not case sensitive.</Font></HTML>");
		}
		else
		{
			label = new JLabel("Please enter the sorting criteria.");
			label.setToolTipText("");
			help.setToolTipText("<HTML><FONT FACE=\"Verdana, sans-serif\" size=\"3\">Enter list of columns separated by \",\" as sort criteria.<BR>To sort in descending order of a column include \"DESC\" after the column name.<BR>For example sort criteria \"COLUMN1, COLUMN2 DESC\" will sort the table data<BR>in ascending order for COLUMN1 and in descending order for COLUMN2.<BR>Sort criteria is not case sensitive.</Font></HTML>");
		}
		label.setFont(new Font(fontFamily,Font.BOLD,fontBig));		
		container.add(southPanel, BorderLayout.SOUTH);
		JPanel northPanel = new JPanel(new BorderLayout());
		northPanel.add(label);
		northPanel.add(help, BorderLayout.EAST);
		container.add(northPanel, BorderLayout.NORTH);
		
		btnApply.setFont(new Font("Verdana",Font.BOLD,12));
		//btnApply.setBackground(new Color(0,0,100));
		//btnApply.setForeground(new Color(0,0,128));
		
		btnCancel.setFont(new Font("Verdana",Font.BOLD,12));
		//btnCancel.setBackground(new Color(0,0,100));
		//btnCancel.setForeground(new Color(0,0,128));
		
		btnClear.setFont(new Font("Verdana",Font.BOLD,12));
		//btnClear.setBackground(new Color(0,0,100));
		//btnClear.setForeground(new Color(0,0,128));
		
		//JPanel centerPanel = new JPanel();
		//centerPanel.setBorder(BorderFactory.createEtchedBorder(1));
		//add(centerPanel);
		tpFilterString = new JEditorPane();
		tpFilterString.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		tpFilterString.setLayout(null);
		tpFilterString.addKeyListener(this);
		container.add(new JScrollPane(tpFilterString));
		colNames = new JComboBox();		
		tpFilterString.add(colNames);
		colNames.setVisible(false);		
		colNames.addItemListener(this);
		colNames.setSize(new Dimension(100,0));		
		colNames.setBorder(null);
		colNames.setOpaque(false);
		tpFilterString.setText("");
		if(tableObj.getTableFilter()!=null && tableObj.getTableFilter().trim().length()>0 && title.startsWith("Filter"))
			tpFilterString.setText(tableObj.getTableFilter());
		else if(tableObj.getTableSort()!=null && tableObj.getTableSort().trim().length()>0 && title.startsWith("Sort"))
			tpFilterString.setText(tableObj.getTableSort());
	}
		
	public void actionPerformed(ActionEvent ae){
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try{
			if(ae.getActionCommand().equalsIgnoreCase("Apply"))
			{	
				if(title.startsWith("Filter"))
					tableObj.setTableFilter(tpFilterString.getText().trim());
				else
					tableObj.setTableSort(tpFilterString.getText().trim());
				setVisible(false);	
				dispose();
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Cancel"))
			{				
				setVisible(false);	
				dispose();
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Clear"))
			{			
				tpFilterString.setText("");			
			}
		}finally{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public void keyPressed(KeyEvent e)
	{
		
	}
	
    public void keyReleased(KeyEvent e)
    {
    	if(e.getKeyCode()==KeyEvent.VK_SPACE && e.isControlDown())
    	{
    		/*System.out.println("Key Released.");
    		colNames.setVisible(true);
    		colNames.setLocation(tpFilterString.getCaret().getMagicCaretPosition());*/   		    		
    	}
    }
     
    public void keyTyped(KeyEvent e)
    {
    	
    }
     
    public void itemStateChanged(ItemEvent e)
    {
    	setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	try{
	    	if(e.getStateChange()==ItemEvent.SELECTED)
	    	{
	    		int caretPos = tpFilterString.getCaretPosition();
	    		String currentText = tpFilterString.getText();
	    		String textBefore = currentText.substring(0,caretPos);
	    		String textAfter = currentText.substring(caretPos);
	    		tpFilterString.setText(textBefore+colNames.getSelectedItem()+textAfter);
	    		colNames.setVisible(false);
	    	}
	    }finally{
	    	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	     }
    }
}
