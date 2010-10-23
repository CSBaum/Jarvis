/**
 * 
 */
package net.stallbaum.jarvis.gui;

import java.awt.Color;
import java.awt.Component;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;

/**
 * @author Administrator
 *
 */
public class RenderStatus extends DefaultTableCellRenderer {

	private Vector problemRows;
	
	/**
	 * 
	 */
	public RenderStatus(Vector _problemRows) {
		this.problemRows = _problemRows;
	}
	
	 public Component getTableCellRendererComponent(JTable aTable,  
			    									Object aNumberValue, 
			    									boolean aIsSelected, 
			    									boolean aHasFocus, 
			    									int aRow, int aColumn) {
		 /*
		  * Implementation Note : It is important that no "new" be present in
		  * this implementation (excluding exceptions): if the table is large,
		  * then a large number of objects would be created during rendering.
		  */;
		 Component renderer = super.getTableCellRendererComponent(aTable,
				aNumberValue, aIsSelected, aHasFocus, aRow, aColumn);
		
		 if (problemRows.size() > 0){
			 for(int inx = 0; inx < problemRows.size();inx++){
				 Integer pRowObj = (Integer)problemRows.get(inx);
				 int pRow = pRowObj.intValue();
				 if (pRow == aRow ) {
					 //renderer.setForeground(Color.red);
					 renderer.setBackground(Color.red);
				 } 
				 else {
					 //renderer.setForeground(fDarkGreen);
					 renderer.setBackground(fDarkGreen);
				 }
			 }		
		 }
		 else
		 {
			 renderer.setBackground(Color.green.brighter());
		 }
		 return this;
	 }
  
	 // PRIVATE //
  
	 //(The default green is too bright and illegible.)
	 private Color fDarkGreen = Color.green.darker();
}

