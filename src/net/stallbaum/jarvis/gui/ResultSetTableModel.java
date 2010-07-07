package net.stallbaum.jarvis.gui;
import java.sql.*;
import javax.swing.table.*;
import javax.swing.event.*;

/**
 * This class takes a JDBC ResultSet object and implements the TableModel
 * interface in terms of it so that a Swing JTable component can display the
 * contents of the ResultSet.  Note that it requires a scrollable JDBC 2.0 
 * ResultSet.  Also note that it provides read-only access to the results
 **/
public class ResultSetTableModel implements TableModel {
    ResultSet results;             // The ResultSet to interpret
    ResultSetMetaData metadata;    // Additional information about the results
    int numcols, numrows;          // How many rows and columns in the table

    /**
     * This constructor creates a TableModel from a ResultSet.  It is package
     * private because it is only intended to be used by 
     * ResultSetTableModelFactory, which is what you should use to obtain a
     * ResultSetTableModel
     **/
    ResultSetTableModel(ResultSet results) throws SQLException {
	this.results = results;                 // Save the results
	metadata = results.getMetaData();       // Get metadata on them
	numcols = metadata.getColumnCount();    // How many columns?
	results.last();                         // Move to last row
	numrows = results.getRow();             // How many rows?
    }
    
    /** 
     * Call this when done with the table model.  It closes the ResultSet and
     * the Statement object used to create it.
     **/
    public void close() {
	try { results.getStatement().close(); }
	catch(SQLException e) {};
    }

    /** Automatically close when we're garbage collected */
    protected void finalize() { close(); }

    // These two TableModel methods return the size of the table
    public int getColumnCount() { return numcols; }
    public int getRowCount() { return numrows; }

    // This TableModel method returns columns names from the ResultSetMetaData
    public String getColumnName(int column) {
	try {
	    return metadata.getColumnLabel(column+1);
	} catch (SQLException e) { return e.toString(); }
    }

    // This TableModel method specifies the data type for each column.  
    // We could map SQL types to Java types, but for this example, we'll just
    // convert all the returned data to strings.
    public Class getColumnClass(int column) { return String.class; }
    
    /**
     * This is the key method of TableModel: it returns the value at each cell
     * of the table.  We use strings in this case.  If anything goes wrong, we
     * return the exception as a string, so it will be displayed in the table.
     * Note that SQL row and column numbers start at 1, but TableModel column
     * numbers start at 0.
     **/
    public Object getValueAt(int row, int column) {
	try {
	    results.absolute(row+1);                // Go to the specified row
	    Object o = results.getObject(column+1); // Get value of the column
	    if (o == null) return null;       
	    else return o.toString();               // Convert it to a string
	} catch (SQLException e) { return e.toString(); }
    }

    // Our table isn't editable
    public boolean isCellEditable(int row, int column) { return false; } 

    // Since its not editable, we don't need to implement these methods
    public void setValueAt(Object value, int row, int column) {}
    public void addTableModelListener(TableModelListener l) {}
    public void removeTableModelListener(TableModelListener l) {}
}
