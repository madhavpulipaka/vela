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
import javax.swing.text.*;

import java.awt.*;
import java.awt.event.*;
import java.io.FileInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.sql.SQLException;
import java.util.*;

import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.event.*;
import javax.swing.table.*;

import vela.common.Constants;
import vela.db.*;
import vela.model.*;
import vela.view.util.*;
import java.util.regex.*;
import java.awt.image.BufferedImage;

public class MainFrame extends JFrame implements ActionListener,
		WindowListener, ItemListener, ListSelectionListener, DocumentListener, Constants, CaretListener {
	
	JMenuBar menu;
	Container cont;
	JTextField statusBar;
	JEditorPane editor;
	JEditorPane sqlEditor;
	JScrollPane spEditor;
	JList list;	
	Color toolbarColor = new Color(250,255,255);
	JButton btnConn;		
	JButton btnCommit;
	JButton btnRollback;
	JButton btnDisc;
	JButton btnHelp;
	JButton btnAbout;	
	JButton btnLicense;
	JButton btnSQLEd;
	JButton btnObjViewer;
	JButton btnRun;
	JButton btnMetalLF;
	JButton btnSystemLF;
	JButton btnMotifLF;
	JPanel leftPanel;
	JPanel rightPanel;
	JComboBox cbDBObjects;
	ArrayList dbObjects = new ArrayList(0);
	JMenuItem miConn = new JMenuItem("Connect");
	JMenuItem miCommit = new JMenuItem("Commit");
	JMenuItem miRollback = new JMenuItem("Rollback");
	JMenuItem miDisc = new JMenuItem("Disconnect");
	JMenuItem miHelp = new JMenuItem("Help");
	JMenuItem miAbout = new JMenuItem("About");
	JMenuItem miLicense = new JMenuItem("License");
	JMenuItem miSystem = new JMenuItem("System");
	JMenuItem miMetal = new JMenuItem("Metal");
	JMenuItem miMotif = new JMenuItem("Motif");
	JMenuItem miSQLEd = new JMenuItem("SQL Editor");
	JMenuItem miDBViewer = new JMenuItem("Object Viewer");
	DefaultComboBoxModel cb;
	DBStructure dbStructure;
	String objectTypes[] = {"FUNCTION","INDEX","PROCEDURE","SEQUENCE","SYNONYM","TABLE","TRIGGER","VIEW","PACKAGE"};
	DefaultListModel listModel;
	JTable tableDetails;
	JTable seqDetails;
	JTable indxDetails;
	JTable synDetails;
	//JTable tableData;
	Vector tabDescVector;
	Vector colHdr;
	Vector colHdrSeq;
	Vector colHdrIndx;
	Vector colHdrSyn;
	Vector tableColumns;
	ReadOnlyTableModel tabModel;
	ReadOnlyTableModel tabModelSeq;
	ReadOnlyTableModel tabModelIndx;
	ReadOnlyTableModel tabModelSyn;
	ReadOnlyTableModel tabDataModel;
	ReadOnlyTableModel tabDataModelSE;
	JTabbedPane tabbedPaneTable;
	int tableDataStartRowNum;
	int tableDataEndRowNum;
	JButton btnNextRecords = new JButton(">>");
	JButton btnPrevRecords = new JButton("<<");
	JButton btnDataFilter = new JButton("::}:");
	JButton btnDataSort = new JButton("_-_");
	//JButton btnDataFilter = new JButton("Y");
	ArrayList lstKeyWords = new ArrayList(0);
	ArrayList lstSQLCommands = new ArrayList(0);
	ArrayList allDBObjectNames = new ArrayList(0);	
	JDesktopPane desktop = new JDesktopPane();
	JPanel panPLSQLEditorTop;
	JButton btnCompile;
	Vector dataDesc; 
	int currentTabSelection;
	int currTabRowCount;
	JSplitPane splitPane;
	JInternalFrame objViewerFrame;
	JInternalFrame sqlEditorFrame;
	JTextField positionBar = new JTextField(""); 
	JPanel seSouthPanel;
	JPanel seNorthPanel;
	JEditorPane textSEResult;
	DBManager dbManager;
	Properties properties;
					
	public MainFrame()
	{
		init();
	}
	
	public void init()
	{
		cont = getContentPane();
		statusBar = new JTextField("Welcome - Click on Connect to start.");		
		//getContentPane().add(desktop);
		/*frameDBViewer.moveToFront();
		frameDBViewer.setVisible(true);
		cont = frameDBViewer.getContentPane();
		JPanel panDBViewer = new JPanel(new BorderLayout());
		cont.add(panDBViewer);*/
		menu = new JMenuBar();
		JMenu menuConn = new JMenu("Connection");
		JMenu menuHelp = new JMenu("Help");
		JMenu menuTools = new JMenu("Tools");
		JMenu menuLF = new JMenu("Look & Feel");
		
		menuConn.setFont(new Font(fontFamily,Font.BOLD,fontBig));		
		menuHelp.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		menuLF.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		menuTools.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		menuConn.add(miConn);
		menuConn.add(miDisc);
		menuConn.add(miCommit);
		menuConn.add(miRollback);
		menuHelp.add(miHelp);		
		menuHelp.add(miAbout);
		menuHelp.add(miLicense);
		menuLF.add(miSystem);
		menuLF.add(miMetal);
		menuLF.add(miMotif);
		menuTools.add(miSQLEd);
		menuTools.add(miDBViewer);		
		
		miConn.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miDisc.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miCommit.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miRollback.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miAbout.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miLicense.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miHelp.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miSystem.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miMetal.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miMotif.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miSQLEd.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		miDBViewer.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		menu.add(menuConn);
		menu.add(menuTools);
		menu.add(menuLF);
		menu.add(menuHelp);		
		//menu.setBackground(toolbarColor);
		//miConn.setBackground(toolbarColor);
		//miDisc.setBackground(toolbarColor);
		//menuConn.setBackground(toolbarColor);
		menu.setForeground(Color.BLACK);
		miConn.setForeground(Color.BLACK);
		miDisc.setForeground(Color.BLACK);
		miCommit.setForeground(Color.BLACK);
		miRollback.setForeground(Color.BLACK);
		menuConn.setForeground(Color.BLACK);
		menuHelp.setForeground(Color.BLACK);
		menuLF.setForeground(Color.BLACK);
		menuTools.setForeground(Color.BLACK);
		JToolBar toolbar = new JToolBar();
		btnConn = new JButton("-( )-");
		btnCommit = new JButton("OO>");
		btnRollback = new JButton("<OO");
		//btnConn.setRolloverEnabled(true);		
		btnConn.setToolTipText("Connect");
		btnCommit.setToolTipText("Commit");
		btnRollback.setToolTipText("Rollback");
		btnConn.setActionCommand("Connect");
		btnCommit.setActionCommand("Commit");
		btnRollback.setActionCommand("Rollback");
		btnDisc = new JButton("-( )-");
		btnAbout = new JButton("?");
		btnLicense = new JButton("License");
		btnHelp =  new JButton("!!");
		btnSQLEd = new JButton("SQL");
		btnObjViewer = new JButton("[][]");
		btnSQLEd.setEnabled(false);
		btnObjViewer.setEnabled(false);
		btnRun = new JButton("R");
		btnMetalLF = new JButton("Metal Look");
		btnMotifLF = new JButton("Motif Look");
		btnSystemLF = new JButton("System Look");
		btnAbout.setToolTipText("About");
		btnLicense.setToolTipText("License");
		btnHelp.setToolTipText("Help");
		btnSQLEd.setToolTipText("SQL Editor");
		btnObjViewer.setToolTipText("Object Viewer");
		btnRun.setToolTipText("Run Script");
		btnMetalLF.setToolTipText("Metal Look");
		btnMotifLF.setToolTipText("Motif Look");
		btnSystemLF.setToolTipText("System Look");
		btnDisc.setActionCommand("Disconnect");
		btnAbout.setActionCommand("About");
		btnLicense.setActionCommand("License");
		btnHelp.setActionCommand("Help");
		btnSQLEd.setActionCommand("SQLEditor");
		btnObjViewer.setActionCommand("ShowObjectViewer");
		btnRun.setActionCommand("RunSQL");
		btnMetalLF.setActionCommand("MetalLF");
		btnMotifLF.setActionCommand("MotifLF");
		btnSystemLF.setActionCommand("SystemLF");
		btnConn.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnCommit.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnRollback.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnDisc.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnAbout.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnLicense.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnHelp.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnSQLEd.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnObjViewer.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnRun.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnMetalLF.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnMotifLF.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		btnSystemLF.setFont(new Font("Lucida Console",Font.BOLD,fontBig));
		toolbar.add(btnConn);
		toolbar.add(btnCommit);
		toolbar.add(btnRollback);
		toolbar.add(btnSQLEd);
		toolbar.add(btnObjViewer);
		toolbar.add(btnSystemLF);
		toolbar.add(btnMetalLF);
		toolbar.add(btnMotifLF);
		toolbar.add(btnHelp);
		toolbar.add(btnAbout);
		toolbar.add(btnLicense);
		//toolbar.add(btnDisc);
		btnConn.setForeground(new Color(128,0,0));
		btnCommit.setForeground(new Color(0,128,0));
		btnRollback.setForeground(new Color(128,0,0));
		//btnDisc.setForeground(Color.RED);		
		//btnConn.setBackground(new Color(0,0,100));
		//btnAbout.setForeground(Color.YELLOW);
		//btnAbout.setBackground(new Color(0,0,100));
		//btnDisc.setBackground(new Color(0,0,100));
		toolbar.setForeground(Color.BLACK);
		//toolbar.setBackground(toolbarColor);
		JPanel topPan = new JPanel();
		topPan.setLayout(new BorderLayout());
		topPan.add(menu);
		topPan.add(toolbar, BorderLayout.SOUTH);
		cont.add(topPan, BorderLayout.NORTH);
		
		statusBar.setOpaque(true);
		statusBar.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		statusBar.setEnabled(false);
		statusBar.setDisabledTextColor(Color.BLACK);
		
		positionBar.setOpaque(true);
		positionBar.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		positionBar.setEnabled(false);
		positionBar.setDisabledTextColor(Color.BLACK);
		positionBar.setPreferredSize(new Dimension(200,20));
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(statusBar);
		southPanel.add(positionBar, BorderLayout.EAST);
		
		cont.add(southPanel, BorderLayout.SOUTH);
		editor = new JEditorPane("text/rtf", "");
		sqlEditor = new JEditorPane();
		spEditor = new JScrollPane(editor);
		DefaultStyledDocument editorDocument = new DefaultStyledDocument();
		editor.setDocument(editorDocument);
		editorDocument.addDocumentListener(this);
		listModel = new DefaultListModel();
		leftPanel = new JPanel();
		rightPanel = new JPanel();
		cb = new DefaultComboBoxModel();
		cbDBObjects = new JComboBox(cb);
		cbDBObjects.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		list = new JList(listModel);
		leftPanel.setLayout(new BorderLayout());
		cbDBObjects.setBackground(Color.WHITE);
		cbDBObjects.setForeground(Color.BLACK);
		leftPanel.add(cbDBObjects, BorderLayout.NORTH);
		list.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		editor.setFont(new Font("Courier New",Font.PLAIN,fontBig));
		sqlEditor.setFont(new Font("Courier New",Font.PLAIN,fontBig));
		editor.setForeground(Color.BLACK);
		sqlEditor.setForeground(Color.BLACK);
		editor.addCaretListener(this);
		list.addListSelectionListener(this);
		leftPanel.add(new JScrollPane(list));
		rightPanel.setLayout(new BorderLayout());
		//rightPanel.add(editor);		
		miConn.addActionListener(this);
		miCommit.addActionListener(this);
		miRollback.addActionListener(this);
		miAbout.addActionListener(this);
		miLicense.addActionListener(this);
		miHelp.addActionListener(this);
		miSystem.addActionListener(this);
		miMetal.addActionListener(this);
		miMotif.addActionListener(this);
		miHelp.setActionCommand("Help");
		miSystem.setActionCommand("SystemLF");
		miMetal.setActionCommand("MetalLF");
		miMotif.setActionCommand("MotifLF");
		miSQLEd.addActionListener(this);
		miSQLEd.setActionCommand("SQLEditor");
		miDBViewer.addActionListener(this);
		miDBViewer.setActionCommand("ShowObjectViewer");
		btnConn.addActionListener(this);
		btnCommit.addActionListener(this);
		btnRollback.addActionListener(this);
		addWindowListener(this);
		miDisc.addActionListener(this);
		btnDisc.addActionListener(this);
		btnAbout.addActionListener(this);
		btnLicense.addActionListener(this);
		btnHelp.addActionListener(this);
		btnSQLEd.addActionListener(this);
		btnObjViewer.addActionListener(this);
		btnRun.addActionListener(this);
		btnMetalLF.addActionListener(this);
		btnMotifLF.addActionListener(this);
		btnSystemLF.addActionListener(this);
		dbObjects = null;
		cbDBObjects.addItemListener(this);
		
		tabDescVector = new Vector(0,1);
		colHdr = new Vector(0,1);
		colHdr.addElement("COLUMN NAME");
		colHdr.addElement("DATA TYPE");
		colHdr.addElement("LENGTH");
		colHdr.addElement("NULL");
		colHdr.addElement("DEFAULT");
		tableColumns = new Vector(0,1); 
		tableColumns.addElement("Col1");
		tableColumns.addElement("Col1");
		tableColumns.addElement("Col1");
		tableColumns.addElement("Col1");
		tabModel = new ReadOnlyTableModel(null, colHdr);
		tableDetails = new JTable(tabModel);
		tableDetails.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		JTableHeader tabDescTabHdr = tableDetails.getTableHeader();
		tabDescTabHdr.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		tabDescTabHdr.setForeground(Color.WHITE);
		tabDescTabHdr.setBackground(new Color(0,0,100));		
		tableDetails.setShowGrid(false);
		tableDetails.setCellSelectionEnabled(true);
		JTextField cellLabel = new JTextField();
		cellLabel.setEnabled(false);
		rightPanel.setOpaque(true);
		DefaultTableCellRenderer centerColumn = new DefaultTableCellRenderer();
		centerColumn.setHorizontalAlignment(JTextField.CENTER);
		tableDetails.getColumnModel().getColumn(2).setCellRenderer(centerColumn);
		tableDetails.getColumnModel().getColumn(3).setCellRenderer(centerColumn);
		tableDetails.getColumnModel().getColumn(4).setCellRenderer(centerColumn);
		tableDetails.getColumnModel().getColumn(0).setPreferredWidth((int)(0.600*tableDetails.getPreferredSize().getWidth()));
		tableDetails.getColumnModel().getColumn(1).setPreferredWidth((int)(0.080*tableDetails.getPreferredSize().getWidth()));
		tableDetails.getColumnModel().getColumn(2).setPreferredWidth((int)(0.010*tableDetails.getPreferredSize().getWidth()));
		tableDetails.getColumnModel().getColumn(3).setPreferredWidth((int)(0.010*tableDetails.getPreferredSize().getWidth()));
		tableDetails.getColumnModel().getColumn(4).setPreferredWidth((int)(0.300*tableDetails.getPreferredSize().getWidth()));		
		
		colHdrSeq = new Vector(0,1);
		colHdrSeq.addElement("MINIMUM VALUE");
		colHdrSeq.addElement("MAXIMUM VALUE");
		colHdrSeq.addElement("INCREMENT BY");
		colHdrSeq.addElement("LAST NUMBER");
		tabModelSeq = new ReadOnlyTableModel(null, colHdrSeq);
		seqDetails = new JTable(tabModelSeq);				
		seqDetails.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		JTableHeader seqTabHdr = seqDetails.getTableHeader();
		seqTabHdr.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		seqTabHdr.setForeground(Color.WHITE);
		seqTabHdr.setBackground(new Color(0,0,100));		
		seqDetails.setShowGrid(false);
		seqDetails.setCellSelectionEnabled(true);
		seqDetails.getColumnModel().getColumn(0).setCellRenderer(centerColumn);
		seqDetails.getColumnModel().getColumn(1).setCellRenderer(centerColumn);
		seqDetails.getColumnModel().getColumn(2).setCellRenderer(centerColumn);
		seqDetails.getColumnModel().getColumn(3).setCellRenderer(centerColumn);
		
		colHdrIndx = new Vector(0,1);
		colHdrIndx.addElement("COLUMN NAME");
		colHdrIndx.addElement("COLUMN POSITION");
		tabModelIndx = new ReadOnlyTableModel(null, colHdrIndx);
		indxDetails = new JTable(tabModelIndx);			
		indxDetails.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		JTableHeader indxTabHdr = indxDetails.getTableHeader();
		indxTabHdr.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		indxTabHdr.setForeground(Color.WHITE);
		indxTabHdr.setBackground(new Color(0,0,100));		
		indxDetails.setShowGrid(false);
		indxDetails.setCellSelectionEnabled(true);
		indxDetails.getColumnModel().getColumn(1).setCellRenderer(centerColumn);
		indxDetails.getColumnModel().getColumn(1).setPreferredWidth((int)(0.95*indxDetails.getPreferredSize().getWidth()));
		
		colHdrSyn = new Vector(0,1);
		colHdrSyn.addElement("OWNER");
		colHdrSyn.addElement("OBJECT NAME");
		colHdrSyn.addElement("OBJECT OWNER");
		tabModelSyn = new ReadOnlyTableModel(null, colHdrSyn);
		synDetails = new JTable(tabModelSyn);			
		synDetails.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		JTableHeader synTabHdr = synDetails.getTableHeader();
		synTabHdr.setFont(new Font(fontFamily,Font.BOLD,fontSmall));
		synTabHdr.setForeground(Color.WHITE);
		synTabHdr.setBackground(new Color(0,0,100));		
		synDetails.setShowGrid(false);
		synDetails.setCellSelectionEnabled(true);
		
		btnNextRecords.setFont(new Font("Lucida Console",Font.BOLD,14));
		btnPrevRecords.setFont(new Font("Lucida Console",Font.BOLD,14));
		btnDataFilter.setFont(new Font("Lucida Console",Font.BOLD,14));
		btnDataSort.setFont(new Font("Lucida Console",Font.BOLD,14));
		btnNextRecords.setForeground(new Color(0,0,128));
		btnPrevRecords.setForeground(new Color(0,0,128));
		btnDataFilter.setForeground(new Color(0,0,128));
		btnDataSort.setForeground(new Color(0,0,128));
		//btnNextRecords.setBackground(new Color(0,0,100));
		//btnPrevRecords.setBackground(new Color(0,0,100));
		//btnDataFilter.setBackground(new Color(0,0,100));
		//btnDataSort.setBackground(new Color(0,0,100));
		btnNextRecords.setToolTipText("Next "+Constants.PAGING_RECORD_COUNT);
		btnPrevRecords.setToolTipText("Previous "+Constants.PAGING_RECORD_COUNT);
		btnDataFilter.setToolTipText("Data Filter");
		btnDataSort.setToolTipText("Sort Data");
		btnNextRecords.setActionCommand("Next");
		btnPrevRecords.setActionCommand("Previous");
		btnDataFilter.setActionCommand("FilterData");
		btnDataSort.setActionCommand("SortData");
		btnNextRecords.addActionListener(this);
		btnPrevRecords.addActionListener(this);
		btnDataFilter.addActionListener(this);
		btnDataSort.addActionListener(this);
		
		populateKeyWords();
		
		//JScrollPane rightSP = new JScrollPane(rightPanel);		
		splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
		splitPane.setOneTouchExpandable(true);
		//cont.add(splitPane);
		
		panPLSQLEditorTop = new JPanel(new FlowLayout(FlowLayout.LEFT));
		btnCompile = new JButton("C");
		btnCompile.setFont(new Font("Lucida Console",Font.BOLD,14));
		btnCompile.setForeground(new Color(0,0,128));
		//btnCompile.setBackground(new Color(0,0,100));
		panPLSQLEditorTop.add(btnCompile);
		btnCompile.setActionCommand("Compile");
		btnCompile.addActionListener(this);
		btnCompile.setToolTipText("Compile");
		
		currentTabSelection = 0;
		
		currTabRowCount = 0;
		
		objViewerFrame = new JInternalFrame("Object Viewer", true, false, true, false); 
		objViewerFrame.getContentPane().add(splitPane);
		objViewerFrame.setBounds(5, 5, 1000, 625);
		objViewerFrame.setVisible(true);		
		objViewerFrame.isMaximizable();
		objViewerFrame.isResizable();		
		cont.add(desktop);
		
		miConn.setEnabled(true);
		miDisc.setEnabled(false);
		miCommit.setEnabled(false);
		miRollback.setEnabled(false);
		/*ImageIcon icon = new ImageIcon("vela.jpg");
	    JLabel l = new JLabel(icon);
	    l.setBounds(0,-300,icon.getIconWidth(),icon.getIconHeight());
	    desktop.add(l, new Integer(Integer.MIN_VALUE));	*/
		seSouthPanel = new JPanel(new BorderLayout());
		seNorthPanel = new JPanel(new BorderLayout());
		JToolBar seToolbar = new JToolBar();
		seToolbar.add(btnRun);
		seNorthPanel.add(seToolbar, BorderLayout.NORTH);
		seNorthPanel.add(new JScrollPane(sqlEditor));
		textSEResult = new JEditorPane();
		textSEResult.setEnabled(false);
		seSouthPanel.add(textSEResult);
		textSEResult.setFont(new Font(fontFamily,Font.BOLD,fontBig));
		seSouthPanel.setBorder(BorderFactory.createTitledBorder("Output"));	
		btnSystemLF.setEnabled(false);
		miSystem.setEnabled(false);
	}
	
	public static void main(String[] args)
	{		
		/*System.setProperty("java.ext.dirs","C:\\VenLak\\lib");
		try {
			System.out.println("Loding Driver... = ");
			System.out.println("URL = "+new URL("file:///C:/VenLak/lib/classes12.zip"));
			ClassLoader a1 = ClassLoader.getSystemClassLoader();
			System.out.println("ClassLoader = "+a1);
			Class c1 = a1.loadClass("oracle.jdbc.driver.OracleDriver");
			Class.forName("oracle.jdbc.driver.OracleDriver");
			System.out.println("DriverClass = "+c1);			
		//} catch (MalformedURLException e) {
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	*/	
		String laf = UIManager.getSystemLookAndFeelClassName();
		//String laf = "com.sun.java.swing.plaf.gtk.GTKLookAndFeel";
		//String laf = "javax.swing.plaf.metal.MetalLookAndFeel";
		//String laf = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
		//String laf = "javax.swing.plaf.metal.MetalTheme";
		//String laf = "javax.swing.plaf.metal.OceanTheme";
		/*LookAndFeelInfo lf[] = UIManager.getInstalledLookAndFeels();
		boolean metalLFInstalled = false;
		for(int i=0; i<lf.length;i++)
		{
			if(lf[i].getClassName()!=null && lf[i].getClassName().equals("javax.swing.plaf.metal.MetalLookAndFeel"))
			{
				metalLFInstalled = true;
				break;
			}
		}	
		if(metalLFInstalled)
			laf = "javax.swing.plaf.metal.MetalLookAndFeel";*/
		try{			
			UIManager.setLookAndFeel(laf);
			//UIManager.setLookAndFeel(new SynthLookAndFeel());
		}catch (UnsupportedLookAndFeelException exc){
			System.out.println("Unsupported: " + laf);			
		}catch (Exception exc){
			System.out.println("Error loading: "+laf);
		}		
		MainFrame frame = new MainFrame();
		frame.setResizable(true);
		frame.setBounds(50,25,900,700);
		frame.setTitle("Vela - Database Objects");
		frame.setVisible(true);	
		frame.setIconImage(new ImageIcon("vela.jpg").getImage());
	}

	public void actionPerformed(ActionEvent ae)
	{
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try{
			statusBar.setText("");
			positionBar.setText("");
			if(ae.getActionCommand().equalsIgnoreCase("Connect"))
			{	
				properties = new Properties();			
				try{
					properties.load(new FileInputStream("Vela.properties"));
				}catch(Exception ex){
					ex.printStackTrace();
				}
				String dbms = (String)properties.get("DBMS");			
				if(dbms!=null && dbms.equalsIgnoreCase("Oracle"))
					dbManager = OraDBManager.getInstance();
				ConnectionParams connParams = new ConnectionParams(this, properties, dbManager);
				connParams.setVisible(true);
				int retStatus = connParams.getRetStatus();
				if(retStatus==1)
					return;
				ConnectionProperties currConnProps = connParams.getSelectedConnProps();
				setTitle("Vela - Connected to "+currConnProps.getDbName());
				Hashtable hDBObjects = dbManager.getDBObjects();			
				dbObjects = (ArrayList)hDBObjects.get("DB_OBJECTS");
				allDBObjectNames = (ArrayList)hDBObjects.get("DB_OBJECT_NAMES");
				initDBStructure();
				if(dbObjects!=null && dbObjects.size()>0)
				{
					desktop.add(objViewerFrame);
					initUI();
					statusBar.setDisabledTextColor(Color.BLACK);
					statusBar.setText("Connected to the database.");
					btnConn.setActionCommand("Disconnect");
					btnConn.setText("-()-");
					btnConn.setToolTipText("Disconnect");
					btnConn.setForeground(new Color(0,128,0));
					miConn.setEnabled(false);
					miDisc.setEnabled(true);
					miCommit.setEnabled(true);
					miRollback.setEnabled(true);
					btnSQLEd.setEnabled(true);
					btnObjViewer.setEnabled(true);
				}
				repaint();
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Disconnect"))
			{			
				dbManager.cleanup();
				dbObjects = new ArrayList(0);
				statusBar.setDisabledTextColor(Color.BLUE);
				statusBar.setText("Not Connected to the database.");
				setTitle("Vela - Database objects");
				rightPanel.removeAll();
				cbDBObjects.setSelectedItem(null);
				cbDBObjects.removeAllItems();
				rightPanel.repaint();
				listModel.clear();
				btnConn.setActionCommand("Connect");
				btnConn.setText("-( )-");
				btnConn.setToolTipText("Connect");
				btnConn.setForeground(new Color(128,0,0));
				desktop.remove(objViewerFrame);
				miConn.setEnabled(true);
				miDisc.setEnabled(false);
				miCommit.setEnabled(false);
				miRollback.setEnabled(false);
				btnSQLEd.setEnabled(false);
				btnObjViewer.setEnabled(false);
				if(sqlEditorFrame!=null)
					sqlEditorFrame.hide();
				repaint();
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Compile"))
			{			
				Document doc = editor.getDocument();
				String result = "Compilation unsucessful.";
				try{
					result = dbManager.compile(doc.getText(0, doc.getLength()), (String)list.getSelectedValue());
				}catch(BadLocationException ex){
					//Never Mind
				}
				DBObject dbObject = dbStructure.getDBObject((String)list.getSelectedValue());
				if(!result.startsWith("Successfully"))
				{
					dbObject.setObjectStatus("INVALID");
					statusBar.setDisabledTextColor(Color.RED);
					statusBar.setText("Compilation failed.");
					MessageBox.showMessage(this, "Compilation Error", true, result);				
				}
				else
				{
					dbObject.setObjectStatus("VALID");
					statusBar.setDisabledTextColor(Color.BLACK);
					statusBar.setText(result);				
				}		
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Next"))
			{			
				String selectedObject = (String)list.getSelectedValue();
				tableDataStartRowNum = tableDataStartRowNum+Constants.PAGING_RECORD_COUNT;
				tableDataEndRowNum = tableDataEndRowNum+Constants.PAGING_RECORD_COUNT;
	    		setTableData(selectedObject, dataDesc, tableDataStartRowNum, tableDataEndRowNum);
	    		tabbedPaneTable.setSelectedIndex(1);
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Previous"))
			{
				String selectedObject = (String)list.getSelectedValue();
				tableDataStartRowNum = tableDataStartRowNum-Constants.PAGING_RECORD_COUNT;
				tableDataEndRowNum = tableDataEndRowNum-Constants.PAGING_RECORD_COUNT;
	    		setTableData(selectedObject, dataDesc, tableDataStartRowNum, tableDataEndRowNum);
	    		tabbedPaneTable.setSelectedIndex(1);
			}
			else if(ae.getActionCommand().equalsIgnoreCase("FilterData"))
			{			
				DBObject tableObj = dbStructure.getTable((String)list.getSelectedValue());
				String dataFilterOld = tableObj.getTableFilter();
				DataFilter df = new DataFilter(this, tableObj, "Filter Data");
				df.setVisible(true);
				String dataFilterNew = tableObj.getTableFilter();
				if((dataFilterOld!=null && !dataFilterOld.trim().equalsIgnoreCase(dataFilterNew)) 
						|| (dataFilterNew!=null && !dataFilterNew.trim().equalsIgnoreCase(dataFilterOld)))
				{
					tableDataStartRowNum = 1;
	    			tableDataEndRowNum = Constants.PAGING_RECORD_COUNT;    			    			
	    			setTableData((String)list.getSelectedValue(), dataDesc, tableDataStartRowNum, tableDataEndRowNum);
	    			tabbedPaneTable.setSelectedIndex(1);
				}
			}
			else if(ae.getActionCommand().equalsIgnoreCase("SortData"))
			{			
				DBObject tableObj = dbStructure.getTable((String)list.getSelectedValue());
				String dataSortOld = tableObj.getTableSort();
				DataFilter df = new DataFilter(this, tableObj, "Sort Data");
				df.setVisible(true);
				String dataSortNew = tableObj.getTableSort();
				if((dataSortOld!=null && !dataSortOld.trim().equalsIgnoreCase(dataSortNew)) 
						|| (dataSortNew!=null && !dataSortNew.trim().equalsIgnoreCase(dataSortOld)))
				{
					tableDataStartRowNum = 1;
	    			tableDataEndRowNum = Constants.PAGING_RECORD_COUNT;    			    			
	    			setTableData((String)list.getSelectedValue(), dataDesc, tableDataStartRowNum, tableDataEndRowNum);
	    			tabbedPaneTable.setSelectedIndex(1);
				}
			}		
			else if(ae.getActionCommand().equalsIgnoreCase("About"))
			{
				About.showMessage(this,"About Vela",true);
			}
			else if(ae.getActionCommand().equalsIgnoreCase("License"))
			{
				License.showMessage(this,"Vela - License",true);
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Help"))
			{
				Help.showMessage(this,"Vela - Help",true);
			}
			else if(ae.getActionCommand().equalsIgnoreCase("SQLEditor"))
			{
				if(sqlEditorFrame!=null)
				{
					sqlEditorFrame.setVisible(true);
					sqlEditorFrame.moveToFront();
				}
				else
				{
					sqlEditorFrame = new JInternalFrame("SQL Editor", true, false, true, false);				
					JSplitPane seSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, seNorthPanel, seSouthPanel);
					seSplitPane.setOneTouchExpandable(true);
					sqlEditorFrame.getContentPane().add(seSplitPane);
					sqlEditor.setPreferredSize(new Dimension(1000, 400));
					sqlEditorFrame.setBounds(5, 5, 1000, 800);
					sqlEditorFrame.setVisible(true);
					desktop.add(sqlEditorFrame);
					sqlEditorFrame.toFront();
				}
			}
			else if(ae.getActionCommand().equalsIgnoreCase("RunSQL"))
			{	
				Document doc = sqlEditor.getDocument();
				String script = null;
				try {
					script = sqlEditor.getSelectedText();
					if(script == null || script.trim().length() == 0)
						script = doc.getText(0, doc.getLength());
				} catch (BadLocationException e) {
					e.printStackTrace();
				}	
				System.out.println("script = "+script);
				if(script==null || script.trim().length()==0)
				{
					JOptionPane.showMessageDialog(this, "Please key-in your SQL or PL/SQL script in the editor to run the script.");
				}
				else if(!script.trim().toUpperCase().startsWith("CREATE") 
						&& !script.trim().toUpperCase().startsWith("ALTER")
						&& !script.trim().toUpperCase().startsWith("DROP")
						&& !script.trim().toUpperCase().startsWith("INSERT")
						&& !script.trim().toUpperCase().startsWith("UPDATE")
						&& !script.trim().toUpperCase().startsWith("DELETE")
						&& !script.trim().toUpperCase().startsWith("SELECT"))
				{
					JOptionPane.showMessageDialog(this, "Not a valid SQL or PL/SQL statement.");
				}
				else if(script.trim().toUpperCase().startsWith("SELECT"))
				{
					Hashtable hTabData = null;
					try{
						hTabData = dbManager.getQueryData(script);
					}catch(SQLException sqlEx){
						JOptionPane.showMessageDialog(this, sqlEx.getMessage());
					}catch(Exception ex){
						JOptionPane.showMessageDialog(this, "System is unable to execute the script.");
						ex.printStackTrace();
					}
					if(hTabData!=null)
					{
						Vector tableDataVectorSE = (Vector)hTabData.get("DATA");
						Vector vColumns = (Vector)hTabData.get("COLUMNS");
						Vector colSizes = (Vector)hTabData.get("COLUMN_SIZES");
						String recCount = (String)hTabData.get("RECORD_COUNT");
						int retrievedRecCount = tableDataVectorSE.size();
						int totRecCount = 0;
						try{
							totRecCount = new Integer(recCount).intValue();
						}catch(Exception ex){
							//Its OK
						}
						tabDataModelSE = new ReadOnlyTableModel(null, vColumns);
						tabDataModelSE.setDataVector(tableDataVectorSE,vColumns);
						JTable tableDataSE = new JTable(tabDataModelSE);
						tableDataSE.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
						setDataTableColSizes(tableDataSE, colSizes, vColumns);
						JTableHeader tabDataTabHdrSE = tableDataSE.getTableHeader();
						tabDataTabHdrSE.setFont(new Font(fontFamily,Font.BOLD,9));
						tabDataTabHdrSE.setForeground(Color.WHITE);
						tabDataTabHdrSE.setBackground(new Color(0,0,100));
						tableDataSE.setBackground(new Color(255,255,235));
						seSouthPanel.removeAll();
						seSouthPanel.add(new JScrollPane(tableDataSE));
						statusBar.setDisabledTextColor(Color.BLACK);
						if(retrievedRecCount<totRecCount)
							statusBar.setText(retrievedRecCount+" records retrieved out of a total of "+totRecCount+" records.");
						else
							statusBar.setText(retrievedRecCount+" records found.");					
					}
					else
					{
						statusBar.setDisabledTextColor(Color.RED);
						statusBar.setText("No data found.");
					}
					seSouthPanel.setVisible(false);
					seSouthPanel.setVisible(true);
				}
				else
				{
					seSouthPanel.removeAll();
					seSouthPanel.add(textSEResult);
					String result = "Script is executed successfully.";
					try{
						result = dbManager.compile(script, (String)list.getSelectedValue());
					}catch(Exception ex){
						//Never Mind
					}
					if(!result.startsWith("Successfully"))
					{
						statusBar.setDisabledTextColor(Color.RED);
						statusBar.setText("Compilation failed.");
						textSEResult.setDisabledTextColor(new Color(128,0,0));
						textSEResult.setText(result);
					}
					else
					{
						statusBar.setDisabledTextColor(Color.BLACK);
						textSEResult.setText("Script is executed successfully.");
						statusBar.setText("Script is executed successfully.");
						textSEResult.setDisabledTextColor(Color.BLACK);
					}
					seSouthPanel.setVisible(false);
					seSouthPanel.setVisible(true);
				}				
			}
			else if(ae.getActionCommand().equalsIgnoreCase("ShowObjectViewer"))
			{
				objViewerFrame.toFront();
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Commit"))
			{
				dbManager.commit();
			}
			else if(ae.getActionCommand().equalsIgnoreCase("Rollback"))
			{
				dbManager.rollback();
			}
			else if(ae.getActionCommand().equalsIgnoreCase("MetalLF"))
			{
				try{
					UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
					SwingUtilities.updateComponentTreeUI(this);
					btnMetalLF.setEnabled(false);				
					btnMotifLF.setEnabled(true);
					btnSystemLF.setEnabled(true);
					miMetal.setEnabled(false);
					miMotif.setEnabled(true);
					miSystem.setEnabled(true);
				}catch(Exception ex){
					ex.printStackTrace();
				}			
			}
			else if(ae.getActionCommand().equalsIgnoreCase("SystemLF"))
			{
				try{
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					SwingUtilities.updateComponentTreeUI(this);
					btnMetalLF.setEnabled(true);
					btnMotifLF.setEnabled(true);
					btnSystemLF.setEnabled(false);
					miMetal.setEnabled(true);
					miMotif.setEnabled(true);
					miSystem.setEnabled(false);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
			else if(ae.getActionCommand().equalsIgnoreCase("MotifLF"))
			{
				try{
					UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
					SwingUtilities.updateComponentTreeUI(this);
					btnMetalLF.setEnabled(true);
					btnMotifLF.setEnabled(false);
					btnSystemLF.setEnabled(true);
					miMetal.setEnabled(true);
					miMotif.setEnabled(false);
					miSystem.setEnabled(true);
				}catch(Exception ex){
					ex.printStackTrace();
				}
			}
		}finally{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
    /**
     * Invoked the first time a window is made visible.
     */
    public void windowOpened(WindowEvent e)
    {    	
    }

    /**
     * Invoked when the user attempts to close the window
     * from the window's system menu.  If the program does not 
     * explicitly hide or dispose the window while processing 
     * this event, the window close operation will be cancelled.
     */
    public void windowClosing(WindowEvent e)
    {    	
		setCursor(new Cursor(Cursor.WAIT_CURSOR));
		try{
	    	if(dbManager!=null && dbManager.isConnectionOpen())
	    	{
			    Object options[]={"Commit","Rollback"};
			    int retStatus = JOptionPane.showOptionDialog(this,
								"Click on \"Commit\" to save the changes done to the table data in this session.\nOtherwise click on \"Rollback\".\nClick on \"Rollback\" if no changes are done to the table data in this session.",
								"Save/Undo Table Data Changes", JOptionPane.DEFAULT_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			    if(retStatus==0)
			    {
					dbManager.commit();
			    }
			    else
			    {
			    	dbManager.rollback();
			    }
			    dbManager.cleanup();
	    	}	    
	    	System.exit(0);
		}finally{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
    }

    /**
     * Invoked when a window has been closed as the result
     * of calling dispose on the window.
     */
    public void windowClosed(WindowEvent e)
    {    	
    }

    /**
     * Invoked when a window is changed from a normal to a
     * minimized state. For many platforms, a minimized window 
     * is displayed as the icon specified in the window's 
     * iconImage property.
     * @see java.awt.Frame#setIconImage
     */
    public void windowIconified(WindowEvent e)
    {    	
    }

    /**
     * Invoked when a window is changed from a minimized
     * to a normal state.
     */
    public void windowDeiconified(WindowEvent e)
    {    	
    }

    /**
     * Invoked when the Window is set to be the active Window. Only a Frame or
     * a Dialog can be the active Window. The native windowing system may
     * denote the active Window or its children with special decorations, such
     * as a highlighted title bar. The active Window is always either the
     * focused Window, or the first Frame or Dialog that is an owner of the
     * focused Window.
     */
    public void windowActivated(WindowEvent e)
    {    	
    }

    /**
     * Invoked when a Window is no longer the active Window. Only a Frame or a
     * Dialog can be the active Window. The native windowing system may denote
     * the active Window or its children with special decorations, such as a
     * highlighted title bar. The active Window is always either the focused
     * Window, or the first Frame or Dialog that is an owner of the focused
     * Window.
     */
    public void windowDeactivated(WindowEvent e)
    {    	
    }
    
    public void initDBStructure()
    {
    	if(dbObjects==null || dbObjects.size()==0)
    		return;
    	TreeMap tables = new TreeMap();
    	TreeMap indexes = new TreeMap();
    	TreeMap functions = new TreeMap();
    	TreeMap sequences = new TreeMap();
    	TreeMap procedures = new TreeMap();
    	TreeMap triggers = new TreeMap();
    	TreeMap views = new TreeMap();
    	TreeMap synonyms = new TreeMap();
    	TreeMap packages = new TreeMap();    	
    	TreeMap allDBObjects = new TreeMap();
    	int szDBObjects = dbObjects.size();
    	for(int i=0;i<szDBObjects;i++)
    	{
    		DBObject dbObject = (DBObject)dbObjects.get(i);
    		String objectName = dbObject.getObjectName();
    		String objectType = dbObject.getObjectType();
    		if(objectType.equalsIgnoreCase("TABLE"))
    			tables.put(objectName, dbObject);
    		if(objectType.equalsIgnoreCase("FUNCTION"))
    			functions.put(objectName, dbObject);
    		if(objectType.equalsIgnoreCase("INDEX"))
    			indexes.put(objectName, dbObject);
    		if(objectType.equalsIgnoreCase("SEQUENCE"))
    			sequences.put(objectName, dbObject);
    		if(objectType.equalsIgnoreCase("PROCEDURE"))
    			procedures.put(objectName, dbObject);
    		if(objectType.equalsIgnoreCase("TRIGGER"))
    			triggers.put(objectName, dbObject);
    		if(objectType.equalsIgnoreCase("VIEW"))
    			views.put(objectName, dbObject);
    		if(objectType.equalsIgnoreCase("SYNONYM"))
    			synonyms.put(objectName, dbObject);
    		if(objectType.equalsIgnoreCase("PACKAGE"))
    			packages.put(objectName, dbObject);    		
    		allDBObjects.put(objectName, dbObject);
    	}
    	dbStructure = new DBStructure();
    	dbStructure.setFunctions(functions);
    	dbStructure.setTables(tables);
    	dbStructure.setIndexes(indexes);
    	dbStructure.setSequences(sequences);
    	dbStructure.setProcedures(procedures);
    	dbStructure.setTriggers(triggers);
    	dbStructure.setViews(views);
    	dbStructure.setSynonyms(synonyms);
    	dbStructure.setPackages(packages);
    	dbStructure.setAllDBObjectsMap(allDBObjects);
    }
    
    public void initUI()
    {       	
    	//desktop.add(frameDBViewer);
		//frameDBViewer.moveToFront();
		//frameDBViewer.setVisible(true);		
		//cont = frameDBViewer.getContentPane();
		//JPanel panDBViewer = new JPanel(new BorderLayout());
		//frameDBViewer.add(panDBViewer);		
		//panDBViewer.add(splitPane);
    	cb.removeAllElements();
    	for(int i=0;i<objectTypes.length;i++)
    	{
	    	cb.addElement(objectTypes[i]);	    	
    	}
    	cb.setSelectedItem("TABLE");
    }
    
    public void itemStateChanged(ItemEvent e)
    {
    	setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	try{
	    	statusBar.setText("");
	    	positionBar.setText("");
	    	if(e.getSource().equals(cbDBObjects) && e.getStateChange() == ItemEvent.SELECTED)
	    	{
	    		rightPanel.removeAll();
	       		listModel.removeAllElements();
	       		String objectType = (String)cbDBObjects.getSelectedItem();
	       		System.out.println("objectType = "+objectType);
	    		TreeMap dbObjects = dbStructure.getDBObjects(objectType);
	    		System.out.println("dbObjects = "+dbObjects);
	    		Iterator iter = dbObjects.keySet().iterator();
	    		while(iter.hasNext())
	    		{
	    			String objectName = (String)iter.next();
	    			listModel.addElement(objectName);    			
	    		}    		
	    	}
	    }finally{
	    	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    }
    }
    
    public void valueChanged(ListSelectionEvent e)
    {
    	setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	try{
	    	statusBar.setText("");
	    	positionBar.setText("");
	    	statusBar.setDisabledTextColor(Color.BLACK);
	    	if(e.getSource().equals(list) && !e.getValueIsAdjusting())
	    	{    		
	    		String selectedObject = (String)list.getSelectedValue();
	    		String objType = (String)cbDBObjects.getSelectedItem();
	    		if(objType==null || selectedObject==null)
	    			return;
	    		if(objType.equalsIgnoreCase("FUNCTION") || objType.equalsIgnoreCase("PROCEDURE") 
	    				|| objType.equalsIgnoreCase("PACKAGE"))
	    		{
		    		ArrayList source = dbManager.getSource(selectedObject, objType);
		    		rightPanel.removeAll();
		    		rightPanel.add(panPLSQLEditorTop, BorderLayout.NORTH);
		    		rightPanel.add(spEditor);
		    		setSourceToEditor(editor, source);
		    		splitPane.setVisible(false);
		            splitPane.setVisible(true);
		            String objectStatus = "";
		            if(objType.equalsIgnoreCase("FUNCTION"))
		            	objectStatus = dbStructure.getFunction(selectedObject).getObjectStatus();
		            if(objType.equalsIgnoreCase("PROCEDURE"))
		            	objectStatus = dbStructure.getProcedure(selectedObject).getObjectStatus();
		            if(objType.equalsIgnoreCase("PACKAGE"))
		            	objectStatus = dbStructure.getPackage(selectedObject).getObjectStatus();	            
		            if(objectStatus.equalsIgnoreCase("VALID"))
		            {	            	
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.BLACK);
		            }
		            else
		            {
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.RED);
		            }
		            editor.requestFocus(true);
	    		}
	    		if(objType!=null && objType.equalsIgnoreCase("TABLE"))
	    		{     	
	    			if(tabbedPaneTable!=null)
	    				currentTabSelection = tabbedPaneTable.getSelectedIndex();
	    			tableDataStartRowNum = 1;
	    			tableDataEndRowNum = Constants.PAGING_RECORD_COUNT;
	    			rightPanel.removeAll();
	    			tableColumns = new Vector(0,1);    			
	    			JScrollPane rightSP = new JScrollPane(tableDetails);
	    			tabbedPaneTable = new JTabbedPane();    			
	    			tabbedPaneTable.setFont(new Font(fontFamily,Font.BOLD,fontBig));
	    			dataDesc = dbManager.getTabDesc(selectedObject);
	    			tabModel.setDataVector(dataDesc, colHdr);
	    			tabbedPaneTable.addTab("Definition", rightSP);    			
	    			setTableData(selectedObject, dataDesc, tableDataStartRowNum, tableDataEndRowNum);
	    			tabbedPaneTable.setSelectedIndex(currentTabSelection);
	    			String objectStatus = dbStructure.getTable(selectedObject).getObjectStatus();
	    			/*if(objectStatus.equalsIgnoreCase("VALID"))
		            {	            	
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.BLACK);
		            }
		            else
		            {
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.RED);
		            }*/
	    		}
	    		if(objType!=null && objType.equalsIgnoreCase("SEQUENCE"))
	    		{   
	    			rightPanel.removeAll();
	    			JScrollPane rightSP = new JScrollPane(seqDetails);
	    			rightPanel.add(rightSP, BorderLayout.CENTER);
	    			rightPanel.setVisible(false);
	    			rightPanel.setVisible(true);
	    			Vector dtlsSeq = dbManager.getSequenceDetails(selectedObject);
	    			tabModelSeq.setDataVector(dtlsSeq, colHdrSeq);
	    			String objectStatus = dbStructure.getSequence(selectedObject).getObjectStatus();
	    			if(objectStatus.equalsIgnoreCase("VALID"))
		            {	            	
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.BLACK);
		            }
		            else
		            {
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.RED);
		            }
	    		}
	    		if(objType!=null && objType.equalsIgnoreCase("INDEX"))
	    		{   
	    			rightPanel.removeAll();
	    			rightPanel.setLayout(new BorderLayout());
	    			JScrollPane rightSP = new JScrollPane(indxDetails);
	    			rightPanel.add(rightSP, BorderLayout.CENTER);
	    			JLabel tabName = new JLabel();
	    			tabName.setOpaque(true);
	    			tabName.setBackground(new Color(0,0,100));
	    			tabName.setForeground(Color.WHITE);
	    			tabName.setFont(new Font(fontFamily,Font.BOLD,fontBig));
	    			Hashtable hIndxDtls = dbManager.getIndexDetails(selectedObject);
	    			tabName.setText("TABLE NAME: "+hIndxDtls.get("TABLE_NAME"));
	    			Vector dtlsIndx = (Vector)hIndxDtls.get("DATA");
	    			tabModelIndx.setDataVector(dtlsIndx, colHdrIndx);
	    			rightPanel.add(tabName, BorderLayout.NORTH);
	    			tabName.setPreferredSize(new Dimension(indxDetails.getWidth(), 15));
	    			rightPanel.setVisible(false);
	    			rightPanel.setVisible(true);    			
	    			String objectStatus = dbStructure.getIndex(selectedObject).getObjectStatus();
	    			if(objectStatus.equalsIgnoreCase("VALID"))
		            {	            	
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.BLACK);
		            }
		            else
		            {
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.RED);
		            }
	    		}
	    		if(objType!=null && objType.equalsIgnoreCase("TRIGGER"))
	    		{
	    			ArrayList source = dbManager.getSource(selectedObject, objType);
		    		rightPanel.removeAll();
		    		rightPanel.add(panPLSQLEditorTop, BorderLayout.NORTH);
		    		rightPanel.add(spEditor);
		    		setSourceToEditor(editor, source);
		    		splitPane.setVisible(false);
		            splitPane.setVisible(true);
		            statusBar.setText(dbStructure.getTrigger(selectedObject).getObjectStatus());
		            String objectStatus = dbStructure.getTrigger(selectedObject).getObjectStatus();
		            if(objectStatus.equalsIgnoreCase("VALID"))
		            {	            	
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.BLACK);
		            }
		            else
		            {
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.RED);
		            }
		            editor.requestFocus(true);
	    		}
	    		if(objType!=null && objType.equalsIgnoreCase("VIEW"))
	    		{
	    			ArrayList source = dbManager.getSource(selectedObject, objType);
		    		rightPanel.removeAll();
		    		rightPanel.add(panPLSQLEditorTop, BorderLayout.NORTH);
		    		rightPanel.add(spEditor);
		    		setSourceToEditor(editor, source);
		    		splitPane.setVisible(false);
		            splitPane.setVisible(true);
		            String objectStatus = dbStructure.getView(selectedObject).getObjectStatus();
		            if(objectStatus.equalsIgnoreCase("VALID"))
		            {	            	
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.BLACK);
		            }
		            else
		            {
		            	statusBar.setText(objectStatus);
		            	statusBar.setDisabledTextColor(Color.RED);
		            }
		            editor.requestFocus(true);
	    		}
	    		if(objType!=null && objType.equalsIgnoreCase("SYNONYM"))
	    		{   
	    			rightPanel.removeAll();
	    			JScrollPane rightSP = new JScrollPane(synDetails);
	    			rightPanel.add(rightSP, BorderLayout.CENTER);
	    			JLabel synName = new JLabel();
	    			synName.setOpaque(true);
	    			synName.setBackground(new Color(0,0,100));
	    			synName.setForeground(Color.WHITE);
	    			synName.setFont(new Font(fontFamily,Font.BOLD,fontBig));    			
	    			Vector dtlsSyn = dbManager.getSynDetails(selectedObject);
	    			synName.setText(selectedObject);    			   			
	    			tabModelSyn.setDataVector(dtlsSyn, colHdrSyn);
	    			//rightPanel.add(synName, BorderLayout.NORTH);
	    			rightPanel.setVisible(false);
	    			rightPanel.setVisible(true);
	    		}
	    	}
	    }finally{
	    	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    }
    }
    
    public void setDataTableColSizes(JTable tableData, Vector datTabColSizes, Vector columnNames)
    {
    	if(datTabColSizes==null)
    		return;
    	int noOfCols = datTabColSizes.size();
    	for(int i=0;i<noOfCols;i++)
    	{
    		int colSz = 0;
    		try{
    			colSz = new Integer((String)datTabColSizes.elementAt(i)).intValue();
    		}catch(Exception ex){}
    		String colName = (String)columnNames.elementAt(i);
    		if(colSz<colName.trim().length())
    			colSz = colName.trim().length();
    		int colWidth = colSz*DATA_TABLE_COL_SIZE_MULTIPLE;
    		if(colWidth>500)
    			colWidth = 500;
    		tableData.getColumn(colName).setPreferredWidth(colWidth);
    	}
    }
    
    public void changedUpdate(DocumentEvent e)
    {    
    }
    
    public void insertUpdate(DocumentEvent e)
    {    
    }
    
    public void removeUpdate(DocumentEvent e)
    {    	
    }
    
    public void setSourceToEditor(JEditorPane editor, ArrayList sourceList)
    {
    	//if(properties.get("HIGHLIGHT_SYNTAX")==null || properties.get("HIGHLIGHT_SYNTAX").toString().trim().equalsIgnoreCase("NO"))
    	//{
    		setSourceToEditorPlainText(editor, sourceList);
    		//return;
    	//}
    	/*editor.setText("");
    	SimpleAttributeSet keyWordsText = new SimpleAttributeSet();
        StyleConstants.setForeground(keyWordsText, Color.BLUE);
        StyleConstants.setBold(keyWordsText, true);
        
        SimpleAttributeSet sqlCommandsText = new SimpleAttributeSet();
        StyleConstants.setForeground(sqlCommandsText, Color.RED);
        StyleConstants.setBold(sqlCommandsText, true);
        
        SimpleAttributeSet sqlDBObjectsText = new SimpleAttributeSet();
        StyleConstants.setForeground(sqlDBObjectsText, new Color(0,128,0));
        StyleConstants.setBold(sqlDBObjectsText, true);
        
        SimpleAttributeSet commentedText = new SimpleAttributeSet();
        StyleConstants.setForeground(commentedText, Color.GRAY);
        
        SimpleAttributeSet nonAlphaNumericText = new SimpleAttributeSet();
        StyleConstants.setForeground(nonAlphaNumericText, new Color(128,0,0));
        StyleConstants.setBold(nonAlphaNumericText,true);
        
        SimpleAttributeSet defaultBlackText = new SimpleAttributeSet();
        StyleConstants.setForeground(defaultBlackText, new Color(0,0,100)); 
        //StyleConstants.setBold(defaultBlackText,true);
        int nols = sourceList.size();
        Document doc = editor.getDocument();
        for(int sl = 0; sl<nols; sl++)
        {
        	try{
	        	String source = (String)sourceList.get(sl);
	        	if(source!=null && source.trim().length()==0)
	        	{
	        		doc.insertString(doc.getLength(), "\n", defaultBlackText);
	        		continue;
	        	}
	        	boolean commentedLine = false;
	        	if(source!=null && source.trim().startsWith("--"))
	        	{
	        		commentedLine = true;
	        	}	
	        	ArrayList othToks = new ArrayList(0);
	        	Pattern pattern = Pattern.compile("(\\W+)\\w*?");
	        	Matcher matcher = pattern.matcher(source);
	        	while(matcher.find()) 
	        	{			
	        		othToks.add(matcher.group());
	        	}
	        	
	        	ArrayList anToks = new ArrayList(0);
	        	pattern = Pattern.compile("(\\w+)\\W*?");
	        	matcher = pattern.matcher(source);
	        	while(matcher.find()) 
	        	{
	        		anToks.add(matcher.group());
	        	}	        	
	        	int loopSz = 0;
	        	loopSz = anToks.size();
	        	String firstAN = null;
	        	if(anToks!=null && !anToks.isEmpty())
	        		firstAN = anToks.get(0).toString();
	        	String firstOth = null;
	        	if(othToks!=null && !othToks.isEmpty())
	        		firstOth = othToks.get(0).toString();
	            for(int i=0;i<loopSz;i++)
	            {
	            	if(i>0 && source.startsWith(firstAN))
	            	{		            		
	            		doc.insertString(doc.getLength(), ((String)othToks.get(i-1)), nonAlphaNumericText);	            		
	            	}
	            	else if(source.startsWith(firstOth))
	            	{	            		
	            		doc.insertString(doc.getLength(), ((String)othToks.get(i)), nonAlphaNumericText);
	            	}
	            	if(commentedLine)
	            	{
	            		doc.insertString(doc.getLength(), (String)anToks.get(i), commentedText);
	            	}
	            	else if(isSQLCommand((String)anToks.get(i)))
	            	{
	            		doc.insertString(doc.getLength(), (String)anToks.get(i), sqlCommandsText);
	            	}
	            	else if(isDBObject((String)anToks.get(i)))
	            	{
	            		doc.insertString(doc.getLength(), (String)anToks.get(i), sqlDBObjectsText);
	            	}else if(isKeyWord((String)anToks.get(i)))
	            	{
	            		doc.insertString(doc.getLength(), (String)anToks.get(i), keyWordsText);
	            	}	            	
	            	else
	            	{	           
	            		doc.insertString(doc.getLength(), (String)anToks.get(i), defaultBlackText);
	            	}
	            }
            	try{
            		doc.insertString(doc.getLength(), (String)othToks.get(othToks.size()-1), nonAlphaNumericText);
	            }catch(Exception ex){
            		//Its OK
            	}
	            editor.setCaretPosition(0);	            
	        }catch(BadLocationException exp) {
	            exp.printStackTrace();
	        }
        }*/        
    }
    
    public void setSourceToEditorPlainText(JEditorPane editor, ArrayList sourceList)
    {
    	editor.setText("");
    	int nols = sourceList.size();
        Document doc = editor.getDocument();
        SimpleAttributeSet defaultBlueText = new SimpleAttributeSet();
        StyleConstants.setFontFamily(defaultBlueText, "Courier New");
        StyleConstants.setForeground(defaultBlueText, new Color(0,0,0)); 
        StyleConstants.setFontSize(defaultBlueText, fontBig);
        //StyleConstants.setBold(defaultBlueText,true);
        for(int sl = 0; sl<nols; sl++)
        {
        	try{
	        	String source = (String)sourceList.get(sl);	 
	        	doc.insertString(doc.getLength(), source, defaultBlueText);	            	            
	        }catch(BadLocationException exp) {
	            exp.printStackTrace();
	        }
        }    	
        editor.setCaretPosition(0);
    }
    
    public boolean isKeyWord(String str)
    {
    	if(lstKeyWords.contains(str.toUpperCase()))
    		return true;
    	return false;
    }
    
    public boolean isSQLCommand(String str)
    {
    	if(lstSQLCommands.contains(str.toUpperCase()))
    		return true;
    	return false;
    }
    
    public boolean isDBObject(String str)
    {
    	if(allDBObjectNames.contains(str.toUpperCase()))
    		return true;
    	return false;
    }
    
    public void populateKeyWords()
    {
    	lstSQLCommands.add("CREATE");
    	lstKeyWords.add("OR");
    	lstSQLCommands.add("REPLACE");
    	lstSQLCommands.add("SELECT");
    	lstSQLCommands.add("INSERT");
    	lstSQLCommands.add("DELETE");
    	lstSQLCommands.add("UPDATE");
    	lstKeyWords.add("FROM");
    	lstKeyWords.add("WHERE");
    	lstKeyWords.add("HAVING");
    	lstKeyWords.add("IN");
    	lstKeyWords.add("IN(");
    	lstKeyWords.add("AND");
    	lstKeyWords.add("SET");
    	lstKeyWords.add("VALUES");
    	lstSQLCommands.add("COMMIT");
    	lstSQLCommands.add("ROLLBACK");
    	lstKeyWords.add("AS");
    	lstKeyWords.add("DECLARE");
    	lstKeyWords.add("BEGIN");
    	lstKeyWords.add("END");
    	lstKeyWords.add("IF");
    	lstKeyWords.add("FOR");
    	lstKeyWords.add("LOOP");
    	lstSQLCommands.add("PROCEDURE");
    	lstSQLCommands.add("VIEW");
    	lstSQLCommands.add("FUNCTION");
    	lstSQLCommands.add("TRIGGER");
    	lstKeyWords.add("ON");
    	lstKeyWords.add("AFTER");
    	lstKeyWords.add("BEFORE");
    	lstKeyWords.add("CURSOR");
    	lstKeyWords.add("VARCHAR");
    	lstKeyWords.add("CHAR");
    	lstKeyWords.add("VARCHAR2");
    	lstKeyWords.add("DATE");
    	lstKeyWords.add("NUMBER");
    	lstKeyWords.add("SYSDATE");
    	lstKeyWords.add("DUAL");
    	lstKeyWords.add("END;");
    	lstKeyWords.add("RETURN");
    	lstKeyWords.add("INTO");
    	lstKeyWords.add("IF;");
    	lstKeyWords.add("LOOP;");
    	lstKeyWords.add("THEN");
    	lstKeyWords.add("IF(");
    	lstKeyWords.add("ORDER");
    	lstKeyWords.add("GROUP");
    	lstKeyWords.add("BY");
    	lstKeyWords.add("ROWNUM");
    	lstKeyWords.add("ROWNUM;");
    	lstKeyWords.add("IS");
    	lstKeyWords.add("DUAL;");
    	lstKeyWords.add("VARCHAR;");
    	lstKeyWords.add("CHAR;");
    	lstKeyWords.add("VARCHAR2;");
    	lstKeyWords.add("DATE;");
    	lstKeyWords.add("NUMBER;");
    	
    	lstKeyWords.add("VARCHAR(");
    	lstKeyWords.add("CHAR(");
    	lstKeyWords.add("VARCHAR2(");
    	lstKeyWords.add("DATE(");
    	lstKeyWords.add("NUMBER(");
    	
    	lstKeyWords.add("VARCHAR)");
    	lstKeyWords.add("CHAR)");
    	lstKeyWords.add("VARCHAR2)");
    	lstKeyWords.add("DATE)");
    	lstKeyWords.add("NUMBER)");
    	
    	lstKeyWords.add("ELSE");
    	lstKeyWords.add("TO_DATE");
    	lstKeyWords.add("TO_NUMBER");
    	lstKeyWords.add("TO_CHAR");
    	lstKeyWords.add("TRIM");
    	lstKeyWords.add("LTRIM");
    	lstKeyWords.add("RTRIM");
    	lstKeyWords.add("SUBSTR");
    	lstKeyWords.add("COUNT");
    	lstKeyWords.add("MAX");
    	lstKeyWords.add("MIN");
    	lstKeyWords.add("SUM");
    	lstKeyWords.add("TO_DATE(");
    	lstKeyWords.add("TO_NUMBER(");
    	lstKeyWords.add("TO_CHAR(");
    	lstKeyWords.add("TRIM(");
    	lstKeyWords.add("LTRIM(");
    	lstKeyWords.add("RTRIM(");
    	lstKeyWords.add("SUBSTR(");
    	lstKeyWords.add("COUNT(");
    	lstKeyWords.add("MAX(");
    	lstKeyWords.add("MIN(");
    	lstKeyWords.add("SUM(");
    	lstKeyWords.add("ABS(");
    	lstKeyWords.add("ABS");
    	lstKeyWords.add("BETWEEN");
    	lstKeyWords.add("OLD:");
    	lstKeyWords.add("NEW:");
    }
    
    public void setTableData(String selectedObject, Vector dataDesc, int startIndx, int endIndx)
    {    	
    	btnPrevRecords.setEnabled(true);
    	btnNextRecords.setEnabled(true);
    	if(tabbedPaneTable.getTabCount()>1)
    		tabbedPaneTable.removeTabAt(1);
    	tableColumns = new Vector(0,1);
    	int szDataDesc = 0;
		if(dataDesc!=null)
		{
			szDataDesc = dataDesc.size();    				
		}
    	Vector datTabColSizes = new Vector(0,1);
		for(int i=0;i<szDataDesc;i++)
		{
			tableColumns.addElement(((Vector)dataDesc.elementAt(i)).elementAt(0));
			datTabColSizes.addElement(((Vector)dataDesc.elementAt(i)).elementAt(2));
		}
		Hashtable hTabData = null;
		try{
			hTabData = dbManager.getTableData(dbStructure.getTable(selectedObject), tableColumns, tableDataStartRowNum, tableDataEndRowNum);
		}catch(SQLException ex){			
			JOptionPane.showMessageDialog(this, "Error occured while retrieving table data. Please check your data filter/sorting criteria.");
			ex.printStackTrace();
		}
		//Vector tableDataVector = dbManager.getTableData(selectedObject, tableColumns, tableDataStartRowNum, tableDataEndRowNum);
		Vector tableDataVector = null;
		String strRowCount = "0";
		if(hTabData!=null)
		{
			tableDataVector = (Vector)hTabData.get("DATA");
			strRowCount = (String)hTabData.get("RECORD_COUNT");
		}
		currTabRowCount = 0;
		try{
			currTabRowCount = new Integer(strRowCount).intValue();
		}catch(Exception ex){
			System.out.println("Record count is not valid.");
		}
		tabDataModel = new ReadOnlyTableModel(null, tableColumns);
		tabDataModel.setDataVector(tableDataVector,tableColumns);
		JTable tableData = new JTable(tabDataModel);
		tableData.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		JTableHeader tabDataTabHdr = tableData.getTableHeader();
		tabDataTabHdr.setFont(new Font(fontFamily,Font.BOLD,9));
		tabDataTabHdr.setForeground(Color.WHITE);
		tabDataTabHdr.setBackground(new Color(0,0,100));
		rightPanel.add(tabbedPaneTable, BorderLayout.CENTER);
		setDataTableColSizes(tableData, datTabColSizes, tableColumns);
		tableData.setBackground(new Color(255,255,235));
		JScrollPane rightSPTabData = new JScrollPane(tableData);
		JPanel panTabData = new JPanel();
		panTabData.setLayout(new BorderLayout());
		JPanel panTabDataTop = new JPanel(new FlowLayout(FlowLayout.LEFT)); 
		panTabData.add(panTabDataTop, BorderLayout.NORTH);
		panTabData.add(rightSPTabData);
		panTabDataTop.add(btnNextRecords);
		panTabDataTop.add(btnPrevRecords);
		panTabDataTop.add(btnDataFilter);
		panTabDataTop.add(btnDataSort);		
		tabbedPaneTable.addTab("Data", panTabData);		
		rightPanel.setVisible(false);
		rightPanel.setVisible(true);
		if(startIndx==1 || currTabRowCount == 0)
			btnPrevRecords.setEnabled(false);
		if(endIndx>=currTabRowCount || currTabRowCount == 0)
			btnNextRecords.setEnabled(false);	
		//if(tabbedPaneTable.getSelectedIndex()==1)
		//{
		//statusBar.setDisabledTextColor(Color.BLACK);
		DBObject tableObj = dbStructure.getTable(selectedObject);
		String filterInd = "";
		if(tableObj.getTableFilter()!=null && tableObj.getTableFilter().trim().length()>0)
			filterInd = " [Filter Applied]";
		if(tableDataVector==null || tableDataVector.size()==0)
			statusBar.setText("No records found in this table"+filterInd+".");
		else if(currTabRowCount>Constants.PAGING_RECORD_COUNT)
			statusBar.setText(tableDataVector.size()+" records retrieved from this table out of a total of "+currTabRowCount+" records"+filterInd+".");
		else
			statusBar.setText(tableDataVector.size()+" records found in this table"+filterInd+".");
		//}
    }     
    
    public ArrayList getKeyWordsInfo(String source)
    {
    	//String sourceTemp = new String(source); 
    	ArrayList tempLstKeyWords = new ArrayList(0);
    	if(source == null)
    		return tempLstKeyWords;
    	int length = source.length(); 
    	if(length == 0)
    		return tempLstKeyWords;
    	for(int i=length-1;i>=0;i--)
    	{
    		String sourceTemp = source.substring(i);    		
    		int keyWordsSz = lstKeyWords.size();    		
        	for(int j=0;j<keyWordsSz;j++)
        	{   
        		if(sourceTemp.toUpperCase().startsWith((String)lstKeyWords.get(j)))
        		{
	        		KeyWord keyWord = new KeyWord();
	        		keyWord.setWord((String)lstKeyWords.get(j));
	        		keyWord.setPosition(i);
	        		//keyWord.setLength(((String)lstKeyWords.get(j)).length());
	        		tempLstKeyWords.add(keyWord);        		
	        		break;
        		}
        	}        	
    	}
    	return tempLstKeyWords;
    }       
	
    public void caretUpdate(CaretEvent e)
    {
    	setCursor(new Cursor(Cursor.WAIT_CURSOR));
    	try{
	    	int caretPos = e.getDot();
	    	if(caretPos==0)
	    	{
	    		positionBar.setText("[Line: 1, Position: 1]");
	    		return;
	    	}
	    	String editorText = null;
	    	
	    	try {
				editorText = editor.getText(0, caretPos);
			} catch (BadLocationException e1) {		
				e1.printStackTrace();
			}
			if(editorText==null || editorText.length()==0)
				return;
			editorText = editorText+" ";
			int lineNo = 1;
			int posNo = 1;
			String lines[] = editorText.split("(\\n)");
			if(lines==null || lines.length>0)
			{
				lineNo = lines.length;
				String lastLine = lines[lineNo-1];
				if((caretPos-lastLine.length())<0)
					posNo = caretPos+1;
				else
					posNo = editor.getText(0, caretPos).length()-editor.getText(0, caretPos-lastLine.length()).length();
			}		
			positionBar.setText("[Line: "+lineNo+", Position: "+posNo+"]");
    	}catch(Exception ex){
    		//Its OK
    		ex.printStackTrace();
    	}finally{
	    	setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
	    }
    }
    
}