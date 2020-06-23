package vela.view.util;

import javax.swing.table.*;
import java.util.*;

public class ReadOnlyTableModel extends DefaultTableModel {
    
    public ReadOnlyTableModel()
    {
    	super();    	
    }
    
    public ReadOnlyTableModel(Vector dat, Vector colNames)
    {
    	super(dat,colNames);
    }  

    public boolean isCellEditable(int row, int col) {
        return false;
    }
    
}
