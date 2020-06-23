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
package vela.db;

import java.sql.*;
import java.util.*;
import java.io.*;

import javax.swing.JOptionPane;

import vela.model.*;
import vela.common.*;

public class OraDBManager implements Constants, DBManager{

	private static DBManager dbManager;
	Connection connection;
	
	private OraDBManager()
	{			
	}
	
	public static DBManager getInstance()
	{      
		if(dbManager!=null)
			return dbManager;
		dbManager = new OraDBManager();
		return dbManager;
	}
	
	public void init(ConnectionProperties connProps) throws Exception
	{		
		try{
			Class.forName("oracle.jdbc.driver.OracleDriver");		
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}		
		try{
			if(connection == null)
			{
				connection = DriverManager.getConnection("jdbc:oracle:thin:@"
						+ connProps.getHost() + ":" + connProps.getPort()+":"+connProps.getSid(),
						connProps.getUserName(), connProps.getPassword());
				if(connection != null)
				{
					System.out.println("Successfuly connected to the database.");
					connection.setAutoCommit(false);
				}
			}
		}catch(SQLException sqlEx){
			throw sqlEx;
		}catch(Exception ex){
			ex.printStackTrace();
			throw ex;
		}		
	}
	
	public void cleanup()
	{
		try{
			if(connection != null)
			{
				connection.close();
				connection = null;
				dbManager = null;
				System.out.println("Database Connection is closed successfully.");
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	public Hashtable getDBObjects()
	{
		Hashtable hAllDBObjects = new Hashtable();
		ArrayList listDBObjects = new ArrayList(0);
		ArrayList allObjectNames = new ArrayList(0);
		hAllDBObjects.put("DB_OBJECTS", listDBObjects);
		hAllDBObjects.put("DB_OBJECT_NAMES", allObjectNames);		
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return hAllDBObjects;
			}
			stmt = connection.createStatement();
			String sqlDBObjects = " SELECT OBJECT_ID, OBJECT_TYPE, STATUS, OBJECT_NAME FROM USER_OBJECTS WHERE OBJECT_TYPE!='SYNONYM'";
			sqlDBObjects = sqlDBObjects+" UNION ";
			sqlDBObjects = sqlDBObjects+" SELECT OBJECT_ID, OBJECT_TYPE, STATUS, OBJECT_NAME FROM ALL_OBJECTS WHERE OBJECT_TYPE = 'SYNONYM' AND OBJECT_NAME IN (SELECT OBJECT_NAME FROM USER_OBJECTS) ORDER BY 4";
			rs = stmt.executeQuery(sqlDBObjects);
			while(rs.next())
			{
				DBObject dbObject = new DBObject();
				dbObject.setObjectId(rs.getString("OBJECT_ID"));
				dbObject.setObjectName(rs.getString("OBJECT_NAME"));
				allObjectNames.add(rs.getString("OBJECT_NAME"));
				dbObject.setObjectStatus(rs.getString("STATUS"));
				dbObject.setObjectType(rs.getString("OBJECT_TYPE"));
				listDBObjects.add(dbObject);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		return hAllDBObjects;
	}
	
	public ArrayList getSource(String objectName, String objectType)
	{		
		ArrayList source = new ArrayList(0);
		if(objectName==null || objectName.trim().length()==0)
			return source;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return source;
			}
			stmt = connection.createStatement();
			String sql="";
			if(objectType!=null && objectType.equalsIgnoreCase("TRIGGER"))
				sql="SELECT DESCRIPTION, TRIGGER_BODY FROM USER_TRIGGERS WHERE TRIGGER_NAME = '"+objectName+"'";
			else if(objectType!=null && objectType.equalsIgnoreCase("VIEW"))
				sql="SELECT VIEW_NAME, TEXT FROM USER_VIEWS WHERE VIEW_NAME = '"+objectName+"'";
			else
				sql="SELECT TEXT SOURCE FROM USER_SOURCE WHERE NAME = '"+objectName+"' AND TYPE='"+objectType+"'";
			rs = stmt.executeQuery(sql);
			int rowCount = 0;
			while(rs.next())
			{	
				rowCount++;
				if(objectType!=null && objectType.equalsIgnoreCase("TRIGGER"))
				{
					if(rowCount==1)
					{
						source.add("CREATE OR REPLACE TRIGGER " + rs.getString("DESCRIPTION"));
						source.add(rs.getString("TRIGGER_BODY"));
					}
					else
						source.add(rs.getString("TRIGGER_BODY"));
				}
				else if(objectType!=null && objectType.equalsIgnoreCase("VIEW"))
				{
					if(rowCount==1)
					{
						source.add("CREATE OR REPLACE VIEW " + rs.getString("VIEW_NAME"));
						source.add(" AS ");
						source.add(rs.getString("TEXT"));
						System.out.println("View Source = "+source);
					}
					else
					{
						source.add(rs.getString("TEXT"));
						System.out.println("View Source = "+source);
					}
				}
				else
				{
					if(rowCount==1)
					{
						source.add("CREATE OR REPLACE "+ (String)rs.getString("SOURCE"));						
					}
					else
						source.add((String)rs.getString("SOURCE"));
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		return source;		
	}
	
	public Vector getTabDesc(String selectedObject)
	{
		Vector tabDescVector = new Vector(0,1);
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return tabDescVector;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT COLUMN_NAME, DATA_TYPE, DECODE(DATA_TYPE,'DATE','',DATA_LENGTH) DATA_LENGTH, NULLABLE, DATA_DEFAULT FROM USER_TAB_COLUMNS WHERE TABLE_NAME = '"+selectedObject+"' ORDER BY 1");
			while(rs.next())
			{
				Vector rowVector = new Vector(0,1);
				rowVector.addElement(rs.getString("COLUMN_NAME"));
				rowVector.addElement(rs.getString("DATA_TYPE"));
				rowVector.addElement(rs.getString("DATA_LENGTH"));
				rowVector.addElement(rs.getString("NULLABLE"));
				rowVector.addElement(rs.getString("DATA_DEFAULT"));
				tabDescVector.addElement(rowVector);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		return tabDescVector;
	}
	
	public Vector getSequenceDetails(String sequenceName)
	{
		Vector seqDtls = new Vector(0,1);
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return seqDtls;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT MIN_VALUE, MAX_VALUE, INCREMENT_BY, LAST_NUMBER FROM USER_SEQUENCES WHERE SEQUENCE_NAME = '"+ sequenceName + "'");
			while(rs.next())
			{
				Vector rowVector = new Vector(0,1);
				rowVector.addElement(rs.getString("MIN_VALUE"));
				rowVector.addElement(rs.getString("MAX_VALUE"));
				rowVector.addElement(rs.getString("INCREMENT_BY"));
				rowVector.addElement(rs.getString("LAST_NUMBER"));
				seqDtls.addElement(rowVector);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		return seqDtls;
	}
	
	public Hashtable getIndexDetails(String indxName)
	{
		Hashtable hIndxDtls = new Hashtable();		
		Vector indxDtls = new Vector(0,1);
		String tabName = "";
		hIndxDtls.put("DATA",indxDtls);
		hIndxDtls.put("TABLE_NAME",tabName);
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return hIndxDtls;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT TABLE_NAME, COLUMN_NAME, COLUMN_POSITION FROM USER_IND_COLUMNS WHERE INDEX_NAME = '"+ indxName + "'");
			while(rs.next())
			{
				Vector rowVector = new Vector(0,1);
				tabName = rs.getString("TABLE_NAME");
				rowVector.addElement(rs.getString("COLUMN_NAME"));
				rowVector.addElement(rs.getString("COLUMN_POSITION"));
				indxDtls.addElement(rowVector);
			}
			hIndxDtls.put("TABLE_NAME",tabName);
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		return hIndxDtls;
	}
	
	public Vector getSynDetails(String synName)
	{
		Vector synDtls = new Vector(0,1);
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return synDtls;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT OWNER, TABLE_NAME, TABLE_OWNER FROM ALL_SYNONYMS WHERE SYNONYM_NAME = '"+ synName + "'");
			while(rs.next())
			{
				Vector rowVector = new Vector(0,1);
				rowVector.addElement(rs.getString("OWNER"));
				rowVector.addElement(rs.getString("TABLE_NAME"));
				rowVector.addElement(rs.getString("TABLE_OWNER"));
				synDtls.addElement(rowVector);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		return synDtls;
	}
	
	public Hashtable getTableData(DBObject table, Vector columnNames, int startIndx, int endIndx) throws SQLException
	{
		String tableName = table.getObjectName();
		String filterString = table.getTableFilter();
		String sortString = table.getTableSort();
		if(sortString!=null && sortString.trim().length()>0)
			sortString = "ORDER BY "+sortString;
		else	
			sortString="";
		Hashtable hTabData = new Hashtable();
		Vector tableData = new Vector(0,1);
		String recordCount = "0";
		hTabData.put("DATA", tableData);
		hTabData.put("RECORD_COUNT", recordCount);
		if(columnNames==null || columnNames.size()==0)
			return hTabData;
		String tableDataSQL = "SELECT ";
		int noOfCols = columnNames.size();
		for(int i=0;i<noOfCols;i++)
		{
			if(i==0)
				tableDataSQL = tableDataSQL+columnNames.elementAt(i);
			else
				tableDataSQL = tableDataSQL+", "+columnNames.elementAt(i);
		}
		
		if(filterString!=null && filterString.trim().length()>0)
			tableDataSQL = "SELECT * FROM(("+tableDataSQL+", ROWNUM RN FROM ("+tableDataSQL+" FROM "+tableName+" WHERE "+filterString+" "+sortString+" ) WHERE ROWNUM<="+endIndx+")) WHERE RN>="+startIndx;
		else
			tableDataSQL = "SELECT * FROM(("+tableDataSQL+", ROWNUM RN FROM ("+tableDataSQL+" FROM "+tableName+" "+sortString+" ) WHERE ROWNUM<="+endIndx+")) WHERE RN>="+startIndx;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return null;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery(tableDataSQL);
			while(rs.next())
			{
				Vector rowVector = new Vector(0,1);
				for(int i=1;i<=noOfCols;i++)
				{
					rowVector.addElement(rs.getString(i));								
				}
				tableData.addElement(rowVector);
			}
		}catch(SQLException ex){
			throw ex;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		if(tableData.size()==0)
			return null;
		stmt = null;
		rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return null;
			}
			stmt = connection.createStatement();
			if(filterString!=null && filterString.trim().length()>0)
				rs = stmt.executeQuery("SELECT NVL(COUNT(*),0) RECORD_COUNT FROM "+tableName+" WHERE "+filterString);
			else
				rs = stmt.executeQuery("SELECT NVL(COUNT(*),0) RECORD_COUNT FROM "+tableName);
			if(rs.next())
			{				
				hTabData.put("RECORD_COUNT",rs.getString(1));				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		return hTabData;
	}
	
	public String compile(String script, String objectName)
	{
		String result = "Successfully compiled without errors.";
		Statement stmt = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return null;
			}
			stmt = connection.createStatement();
			int res = stmt.executeUpdate(script);						
		}catch(SQLException ex){
			return ex.getMessage();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{			
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		
		stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return null;
			}
			stmt = connection.createStatement();
			String sqlErrors = "SELECT (TEXT || '. Error at [Line: '|| LINE || ', Position: ' || POSITION ||']') ERROR FROM USER_ERRORS WHERE NAME = '"+objectName+"' AND SEQUENCE = 1";
			rs = stmt.executeQuery(sqlErrors);
			if(rs.next())
				result = rs.getString("ERROR");
		}catch(SQLException ex){
			return ex.getMessage();
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{			
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		
		return result;
	}
	
	public Hashtable getQueryData(String query) throws SQLException
	{
		Hashtable hTabData = new Hashtable();
		Vector vColumns = new Vector(0,1);
		Vector tableData = new Vector(0,1);
		Vector colSizes = new Vector(0,1);
		hTabData.put("DATA", tableData);
		hTabData.put("COLUMNS", vColumns);
		hTabData.put("COLUMN_SIZES", colSizes);
		String sql= "SELECT * FROM ("+query+") WHERE ROWNUM<="+Constants.PAGING_RECORD_COUNT;
		Statement stmt = null;
		ResultSet rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return null;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			ResultSetMetaData rsmd = rs.getMetaData();			
			int szCols = rsmd.getColumnCount();
			for(int i=1;i<=szCols;i++)
			{
				if(rsmd.getColumnLabel(i)!=null)
					vColumns.addElement(rsmd.getColumnLabel(i));
				else
					vColumns.addElement(rsmd.getColumnName(i));
				
				if(rsmd.getColumnDisplaySize(i)>0)
					colSizes.addElement(""+rsmd.getColumnDisplaySize(i));
				else
					colSizes.addElement(""+(rsmd.getPrecision(i)+2));
			}
			while(rs.next())
			{				
				Vector rowVector = new Vector(0,1);
				for(int i=1;i<=szCols;i++)
				{
					rowVector.addElement(rs.getString(i));								
				}
				tableData.addElement(rowVector);
			}
		}catch(SQLException ex){
			throw ex;
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}	
		
		if(tableData.size()==0)
			return null;
		stmt = null;
		rs = null;
		try{
			if(connection == null)
			{
				System.out.println("Error: No valid connection.");
				return null;
			}
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT COUNT(*) FROM ("+query+")");
			
			if(rs.next())
			{					
				hTabData.put("RECORD_COUNT",rs.getString(1));				
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}finally{
			if(rs!=null)
			{
				try{
					rs.close();
				}catch(Exception ex){}
				rs = null;
			}
			if(stmt!=null)
			{
				try{
					stmt.close();
				}catch(Exception ex){}
				stmt = null;
			}
		}
		return hTabData;
	}
	
	public void commit()
	{		
		try {
			connection.commit();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void rollback()
	{
		try {
			connection.rollback();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean isConnectionOpen()
	{
		try {
			if(connection!=null)
			{
				if(connection.isClosed())
					return false;
				else
					return true;
			}			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
		return false;
	}
	
}
