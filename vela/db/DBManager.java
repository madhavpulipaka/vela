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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Vector;

import vela.model.ConnectionProperties;
import vela.model.DBObject;

public interface DBManager {
	
	public void init(ConnectionProperties connProps) throws Exception;		
	public void cleanup();
	public Hashtable getDBObjects();
	public ArrayList getSource(String objectName, String objectType);
	public Vector getTabDesc(String selectedObject);
	public Vector getSequenceDetails(String sequenceName);
	public Hashtable getIndexDetails(String indxName);	
	public Vector getSynDetails(String synName);	
	public Hashtable getTableData(DBObject table, Vector columnNames, int startIndx, int endIndx) throws SQLException;
	public String compile(String script, String objectName);
	public Hashtable getQueryData(String query) throws SQLException;
	public void commit();
	public void rollback();
	public boolean isConnectionOpen();
}
