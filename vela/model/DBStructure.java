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
package vela.model;

import java.io.*;
import java.util.*;

public class DBStructure implements Serializable{

	ArrayList allObjects = new ArrayList(0);
	TreeMap tables = new TreeMap();
	TreeMap functions = new TreeMap();
	TreeMap indexes = new TreeMap();
	TreeMap sequences = new TreeMap();
	TreeMap procedures = new TreeMap();
	TreeMap triggers = new TreeMap();
	TreeMap views = new TreeMap();
	TreeMap synonyms = new TreeMap();
	TreeMap packages = new TreeMap();
	ArrayList allObjectNames = new ArrayList(0);
	TreeMap allDBObjectsMap = new TreeMap();
	
	public ArrayList getAllObjects() {
		return allObjects;
	}
	public void setAllObjects(ArrayList allObjects) {
		this.allObjects = allObjects;
	}
	public TreeMap getFunctions() {
		return functions;
	}
	public void setFunctions(TreeMap functions) {
		this.functions = functions;
	}
	public TreeMap getIndexes() {
		return indexes;
	}
	public void setIndexes(TreeMap indexes) {
		this.indexes = indexes;
	}
	public TreeMap getSequences() {
		return sequences;
	}
	public void setSequences(TreeMap sequences) {
		this.sequences = sequences;
	}
	public TreeMap getTables() {
		return tables;
	}
	public void setTables(TreeMap tables) {
		this.tables = tables;
	}
	
	public TreeMap getProcedures() {
		return procedures;
	}
	public void setProcedures(TreeMap procedures) {
		this.procedures = procedures;
	}
	
	public TreeMap getTriggers() {
		return triggers;
	}
	public void setTriggers(TreeMap triggers) {
		this.triggers = triggers;
	}
	
	public TreeMap getViews() {
		return views;
	}
	public void setViews(TreeMap views) {
		this.views = views;
	}
	
	public TreeMap getSynonyms() {
		return synonyms;
	}
	public void setSynonyms(TreeMap synonyms) {
		this.synonyms = synonyms;
	}
	
	public TreeMap getDBObjects(String objectType)
	{
		if(objectType==null)
			return new TreeMap();
		if(objectType.equalsIgnoreCase("FUNCTION"))
			return functions;
		if(objectType.equalsIgnoreCase("TABLE"))
			return tables;
		if(objectType.equalsIgnoreCase("SEQUENCE"))
			return sequences;
		if(objectType.equalsIgnoreCase("INDEX"))
			return indexes;
		if(objectType.equalsIgnoreCase("PROCEDURE"))
			return procedures;
		if(objectType.equalsIgnoreCase("TRIGGER"))
			return triggers;
		if(objectType.equalsIgnoreCase("VIEW"))
			return views;
		if(objectType.equalsIgnoreCase("SYNONYM"))
			return synonyms;
		if(objectType.equalsIgnoreCase("PACKAGE"))
			return packages;		
		return new TreeMap();
	}	
	
	public DBObject getFunction(String functionName)
	{
		return (DBObject)functions.get(functionName);
	}
	
	public DBObject getPackage(String packageName)
	{
		return (DBObject)packages.get(packageName);
	}
	
	public DBObject getTable(String tableName)
	{		
		return (DBObject)tables.get(tableName);
	}
	
	public DBObject getSequence(String sequenceName)
	{
		return (DBObject)sequences.get(sequenceName);
	}
	
	public DBObject getIndex(String indexName)
	{
		return (DBObject)indexes.get(indexName);
	}
	
	public DBObject getProcedure(String procedureName)
	{
		return (DBObject)procedures.get(procedureName);
	}
	
	public DBObject getTrigger(String triggerName)
	{
		return (DBObject)triggers.get(triggerName);
	}
	
	public DBObject getView(String viewName)
	{
		return (DBObject)views.get(viewName);
	}
	
	public DBObject getSynonym(String synonymName)
	{
		return (DBObject)synonyms.get(synonymName);
	}
	
	public TreeMap getAllDBObjectsMap() {
		return allDBObjectsMap;
	}
	
	public void setAllDBObjectsMap(TreeMap allDBObjectsMap) {
		this.allDBObjectsMap = allDBObjectsMap;
	}
	
	public DBObject getDBObject(String objectName)
	{
		return (DBObject)allDBObjectsMap.get(objectName);
	}
	
	public TreeMap getPackages() {
		return packages;
	}
	
	public void setPackages(TreeMap packages) {
		this.packages = packages;
	}
	
}




