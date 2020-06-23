Overview

Vela is SQL and PL/SQL client with graphical user interface developed as a open source front end tool using JDBC and Java Swing. It supports Oracle Database. Vela is a Front-End tool for a Oracle developer. It supports most of the common developer tasks such as browsing database objects, viewing table definitions, viewing table data. It also supports editing and compiling SQL and PL/SQL scripts.

Note: Java Runtime Environment 1.4 or above should be installed to use Vela.
Click https://sourceforge.net/projects/vela/ to Download Vela

Supported Features

* Connect/Disconnect to Database
* View all Database Object Names
* View Table Definition and Data
* View details of indexes, sequences, synonyms
* View source for Functions, Procedures, Triggers, Views and Packages
* Compile Functions, Procedures, Triggers and Views
* View object status in the status bar for objects other than tables
* Filter/Sort table data
* SQL-PL/SQL Editor
* Commit & Rollback table data changes

Instructions for a quick start
Assuming that the application folder "Vela" is copied to the local drive C:\.

Step 1: Copy appropriate JDBC driver library (jar or zip file) to C:\Vela\lib.

Step 2: Open C:\Vela\vela.properties file in any text editor. Each database connection contains following four properties:

{Database Name}.USERID = {Database User Id}

{Database Name}.HOST = {Database Server's Host Name/IP Address}

{Database Name}.SID = {SID of the database instance}

{Database Name}.PORT = {Database Server's port number}

Please fill in the values (those enclosed in {}) that correspond to your database. Please remember to remove "{" and "}" also. {Database Name} can be any name by which a database connection can be identified by you. Repeat the above four properties for another database connection with a different {Database Name}.

Step 3: Double-click on the file vela.bat in "C:\Vela" directory.

Step 4: Click on the tool bar button "-()-" or menu item Connection->Connect.



Select your database from the Connection Name Drop Down list, enter the password and click on “Connect”.
Click on any table to view its definition.
Select the “Data” tab to view the table data. First 1000 records will be displayed. Use “>>” or “<<” buttons to view next or previous 1000 records.

To filter data
1. Click on the button "::}:" in table data view.
2. Enter SQL "WHERE" clause as Filter Criteria. Please do not include "WHERE" in the filter criteria. Filter criteria is not case sensitive.
3. Click on the button "Apply" to view the filtered table data.
Data after the filter is applied.

To sort data
1. Click on the button "_-_" in table data view.
2. Enter list of columns separated by "," as sort criteria. To sort in descending order of a column include "DESC" after the column name. For example sort criteria "COLUMN1, COLUMN2 DESC" will sort the table data in ascending order for COLUMN1 and in descending order for COLUMN2. Sort criteria is not case sensitive.
3. Click on the button "Apply" to view the sorted table data.
Data after sorting.

List of stored procedures, functions, triggers, views and packages can also be viewed.

Click on a procedure to view its source. Click on “C” to compile the procedure after editing the script. Similarly editing and compilation can be performed for functions, triggers, views and packages.

Click on “SQL” in the tool bar. Click on “[] []” to view the object viewer. User can toggle between these two views using these two tool bar buttons. In SQL Editor enter any SQL (SELECT, INSERT, UPDATE, CREATE, ALTER) query. This view can also be used to create new database objects like tables, stored procedures, functions, views, triggers and packages. Click on “R” to run the SQLs. If the SQL Editor has more than one SQL script, select the script to be executed before clicking on "R".


Copyright 2007 Madhav Pulipaka

Vela is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 2 of the License, or (at your option) any later version. Vela is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You should have received a copy of the GNU General Public License along with Vela; if not, write to the Free Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
