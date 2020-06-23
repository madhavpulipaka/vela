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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import java.util.Properties;
import java.util.TreeMap;
import java.util.Enumeration;
import java.util.Iterator;
import java.sql.SQLException;

import vela.model.*;
import vela.common.Constants;
import vela.db.OraDBManager;
import vela.db.DBManager;

public class ConnectionParams extends JDialog implements ActionListener, ItemListener, Constants{

	private static final int WIDTH = 400;
	private static final int HEIGHT = 225;
	TreeMap tmConnections;
	JComboBox databaseConns = new JComboBox();
	JTextField tUserId = new JTextField();
	JTextField tPassword = new JTextField();
	JTextField tHostName = new JTextField();
	JTextField tSid = new JTextField();
	JTextField tPort = new JTextField();
	int retStatus=1;
	ConnectionProperties selectedConnProps;
	Properties connProperties;
	DBManager dbManager = null;	
	
	public ConnectionParams(Frame frame, Properties properties, DBManager dbManager)
	{
		super(frame, "Connection Parameters", true);
		connProperties = properties;
		this.dbManager = dbManager;
		readProperties(properties);
		Container container = getContentPane();
		JButton btnConnect = new JButton("Connect");
		JButton btnCancel = new JButton("Cancel");		
		btnConnect.setActionCommand("Connect");
		btnCancel.setActionCommand("Cancel");
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds((int)(screenSize.getWidth()/2)-(WIDTH/2), (int)(screenSize.getHeight()/2)-(HEIGHT/2), WIDTH, HEIGHT);
		btnConnect.addActionListener(this);
		btnCancel.addActionListener(this);
		JPanel southPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		southPanel.add(btnConnect);
		southPanel.add(btnCancel);		
		JLabel label = null;
		label = new JLabel("Please input your connection parameters.");
		label.setFont(new Font(fontFamily,Font.BOLD,fontBig));		
		container.add(southPanel, BorderLayout.SOUTH);
		southPanel.setBorder(BorderFactory.createEtchedBorder());
		JPanel northPanel = new JPanel(new BorderLayout());
		JPanel centerPanel = new JPanel(new GridLayout());
		centerPanel.setBorder(BorderFactory.createEtchedBorder());
		northPanel.setBorder(BorderFactory.createEtchedBorder());
		northPanel.add(label);
		container.add(northPanel, BorderLayout.NORTH);		
		container.add(centerPanel);
		btnConnect.setFont(new Font("Verdana",Font.BOLD,12));		
		btnCancel.setFont(new Font("Verdana",Font.BOLD,12));
		centerPanel.setLayout(new GridLayout(6,2));
		if(tmConnections!=null && !tmConnections.isEmpty())
		{
			Iterator iter = tmConnections.keySet().iterator();
			while(iter.hasNext())
			{
				databaseConns.addItem(iter.next());	
			}
		}		
		JLabel lConnName = new JLabel(" Connection Name"); 
		JLabel lUserId = new JLabel(" User Id");
		JLabel lPassword = new JLabel(" Password");
		JLabel lHostName = new JLabel(" Host Name/IP Address");
		JLabel lSid = new JLabel(" SID");
		JLabel lPort = new JLabel(" Port");
		 
		centerPanel.add(lConnName);
		centerPanel.add(databaseConns);
		centerPanel.add(lUserId);
		centerPanel.add(tUserId);
		centerPanel.add(lPassword);
		centerPanel.add(tPassword);
		centerPanel.add(lHostName);
		centerPanel.add(tHostName);
		centerPanel.add(lSid);
		centerPanel.add(tSid);
		centerPanel.add(lPort);
		centerPanel.add(tPort);
		lUserId.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		tUserId.setFont(new Font(fontFamily,Font.PLAIN,fontSmall));
		lPassword.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		tPassword.setFont(new Font(fontFamily,Font.PLAIN,fontSmall));
		lHostName.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		tHostName.setFont(new Font(fontFamily,Font.PLAIN,fontSmall));
		lSid.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		tSid.setFont(new Font(fontFamily,Font.PLAIN,fontSmall));
		lPort.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		tPort.setFont(new Font(fontFamily,Font.PLAIN,fontSmall));
		lConnName.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		databaseConns.setFont(new Font(fontFamily,Font.PLAIN,fontSmall));
		databaseConns.setSelectedIndex(-1);
		databaseConns.setEditable(true);
		databaseConns.addItemListener(this);		
		retStatus = 1;
	}
		
	public void actionPerformed(ActionEvent ae){
		if(ae.getActionCommand().equalsIgnoreCase("Connect"))
		{				
			if(!validateParams())
				return;
			selectedConnProps.setPassword(tPassword.getText());
			try{
				dbManager.init(selectedConnProps);
			}catch(SQLException sqlEx){
				JOptionPane.showMessageDialog(this, sqlEx.getMessage());
			}catch(Exception ex){
				JOptionPane.showMessageDialog(this, "Failed to connect to the database.");
			}
			if(dbManager.isConnectionOpen())
			{
				retStatus = 0;	
				setVisible(false);
				dispose();
			}
		}
		else if(ae.getActionCommand().equalsIgnoreCase("Cancel"))
		{				
			retStatus = 1;
			setVisible(false);	
			dispose();
		}		
	}	
	    
    public void itemStateChanged(ItemEvent e)
    {
    	if(e.getStateChange()==ItemEvent.SELECTED)
    	{
    		String connName = (String)databaseConns.getSelectedItem();
    		ConnectionProperties connProps = (ConnectionProperties)tmConnections.get(connName);
    		tUserId.setText(connProps.getUserName());
    		tHostName.setText(connProps.getHost());
    		tSid.setText(connProps.getSid());
    		tPort.setText(connProps.getPort());
    		selectedConnProps = connProps;
    	}
    }
    
    public void readProperties(Properties props)
    {
    	tmConnections = new TreeMap();
    	Enumeration enum = props.keys();
    	while(enum.hasMoreElements())
    	{   
    		String key = (String)enum.nextElement();
    		if(key.indexOf(".")==-1)
    			continue;
    		String connName = key.substring(0, key.indexOf("."));
    		ConnectionProperties connProps;
    		if(tmConnections.containsKey(connName))
    			connProps = (ConnectionProperties)tmConnections.get(connName);
    		else
    		{
    			connProps = new ConnectionProperties();
    			tmConnections.put(connName, connProps);
    		}
    		connProps.setDbName(connName);
    		String connParam = key.substring(key.indexOf(".")+1);
    		if(connParam.equalsIgnoreCase("USERID"))
    			connProps.setUserName(props.getProperty(key));
   			else if(connParam.equalsIgnoreCase("HOST"))
   				connProps.setHost(props.getProperty(key));
			else if(connParam.equalsIgnoreCase("SID"))
				connProps.setSid(props.getProperty(key));
			else if(connParam.equalsIgnoreCase("PORT"))
				connProps.setPort(props.getProperty(key));
    	}    	
    }

	public int getRetStatus() {
		return retStatus;
	}

	public void setRetStatus(int retStatus) {
		this.retStatus = retStatus;
	}

	public ConnectionProperties getSelectedConnProps() {
		return selectedConnProps;
	}

	public void setSelectedConnProps(ConnectionProperties selectedConnProps) {
		this.selectedConnProps = selectedConnProps;
	}
	
	public boolean validateParams()
	{
		String dbName = (String)databaseConns.getSelectedItem();
		String user = tUserId.getText();
		String passwd = tPassword.getText();
		String host = tHostName.getText();
		String sid = tSid.getText();
		String port = tPort.getText();
		if(dbName==null || dbName.trim().length()==0)
		{
			JOptionPane.showMessageDialog(this, "Please select a database name or enter database name.");
			databaseConns.requestFocus();
			return false;
		}
		return true;
	}
	
}
