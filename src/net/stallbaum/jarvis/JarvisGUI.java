package net.stallbaum.jarvis;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

import jade.gui.*;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.Robot;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

public class JarvisGUI extends JFrame implements ActionListener,
		SecurityVocabulary {
	
	Object[] actions = {"","New Account","Deposit","Withdrawal",
            "Balance","Operations"};
	String columns[] = {"Date","Operation","Debit","Credit","Balance"};
	
	final static int IN_PROCESS = 0;
	final static int WAIT_CONFIRM = 1;
	final static int IN_LINE = 2;
	private int status = IN_PROCESS;
	private java.util.List accounts = new ArrayList();
	private JTextField msg, input, acInfo;
	private JComboBox menu;
	private JList acList;
	private JTable opTable;
	private JButton ok, cancel, quit;

	private Jarvis myAgent;

	public JarvisGUI(Jarvis j){
	
		myAgent = j;
		setTitle("Bank Client Agent - " + myAgent.getLocalName());
		
		JPanel base = new JPanel();
		base.setBorder(new EmptyBorder(15,15,15,15));
		base.setLayout(new BorderLayout(10,10));
		getContentPane().add(base);

		JPanel panel = new JPanel();
		base.add(panel, BorderLayout.WEST);
		panel.setLayout(new BorderLayout(0,16));
		JPanel pane = new JPanel();
		pane.setLayout(new BorderLayout(0,0));
		pane.add(new JLabel("Message"), BorderLayout.NORTH);
		pane.add(msg = new JTextField("Select an action", 15));
		msg.setEditable(false);
		msg.setBackground(Color.black);
		msg.setForeground(Color.white);
		msg.setFont(new Font("Arial", Font.BOLD, 12));
		msg.setHorizontalAlignment(JTextField.CENTER);
		panel.add(pane, BorderLayout.NORTH);
		pane = new JPanel();
		pane.setLayout(new BorderLayout(5,0));

		JPanel p = new JPanel();
		p.setLayout(new BorderLayout(0,0));
		p.add(new JLabel("Accounts"), BorderLayout.NORTH);
		acList = new JList();
		acList.setVisibleRowCount(5);
		acList.setFixedCellHeight(18);
		acList.setFont(new Font("Arial", Font.PLAIN, 12));
		acList.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
		JScrollPane scroll = new JScrollPane(acList);
		scroll.setPreferredSize(new Dimension(160,60));
		p.add(scroll, BorderLayout.SOUTH);
		pane.add(p, BorderLayout.WEST);
		p = new JPanel();
		p.setBorder(new EmptyBorder(0,0,35,0));
		p.setLayout(new BorderLayout(0,0));
		p.add(new JLabel("Menu"), BorderLayout.NORTH);
		p.add(menu = new JComboBox(actions), BorderLayout.SOUTH);
		pane.add(p, BorderLayout.EAST);
		panel.add(pane, BorderLayout.CENTER);
		pane = new JPanel();
		pane.setLayout(new BorderLayout(0,0));
		JPanel p0 = new JPanel();
		p0.setLayout(new BorderLayout(0,3));
		p = new JPanel();
		p.setLayout(new BorderLayout(10,0));
		p.add(new JLabel("Operations"), BorderLayout.WEST);
		p.add(acInfo = new JTextField(25), BorderLayout.EAST);
		acInfo.setEditable(false);
		acInfo.setFont(new Font("Arial", Font.PLAIN, 11));
		msg.setHorizontalAlignment(JTextField.CENTER);
		p0.add(p, BorderLayout.NORTH);
		Object obj[][] = new Object[0][columns.length];
		TableModel model = new TableDataModel(obj, columns);
		opTable = new JTable(model);
		opTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		opTable.setPreferredScrollableViewportSize(new Dimension(320,100));
		opTable.setFont(new Font("Arial", Font.PLAIN, 11));
		p0.add(new JScrollPane(opTable), BorderLayout.SOUTH);
		pane.add(p0, BorderLayout.SOUTH);
		panel.add(pane, BorderLayout.SOUTH);

		panel = new JPanel();
		base.add(panel, BorderLayout.EAST);
		panel.setLayout(new BorderLayout(0,10));
		pane = new JPanel();
		pane.setLayout(new BorderLayout(0,0));
		pane.add(new JLabel("Input"), BorderLayout.NORTH);
		pane.add(input = new JTextField(8));
		panel.add(pane, BorderLayout.NORTH);
		pane = new JPanel();
		panel.add(pane, BorderLayout.SOUTH);
		pane.setBorder(new EmptyBorder(0,0,130,0));
		pane.setLayout(new GridLayout(3,1,0,5));
		pane.add(ok = new JButton("OK"));
		ok.setToolTipText("Submit operation");
		ok.addActionListener(this);
		pane.add(cancel = new JButton("Cancel"));
		cancel.setToolTipText("Submit operation");
		cancel.setEnabled(false);
		cancel.addActionListener(this);
		pane.add(quit = new JButton("QUIT"));
		quit.setToolTipText("Stop agent and exit");
		quit.addActionListener(this);

		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				shutDown();
			}
		});

		setSize(470, 350);
		setResizable(false);
		Rectangle r = getGraphicsConfiguration().getBounds();
		setLocation(r.x + (r.width - getWidth())/2,
				r.y + (r.height - getHeight())/2);

	}	
	
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == quit) {
			shutDown();
		}
	}

	void shutDown() {
		// ----------------- Control the closing of this gui

		int rep = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to exit?", myAgent.getLocalName(),
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (rep == JOptionPane.YES_OPTION) {
			GuiEvent ge = new GuiEvent(this, SHUTDOWN);
			myAgent.postGuiEvent(ge);
		}
	}

	void alertInfo(String s) {
		// --------------------------

		Toolkit.getDefaultToolkit().beep();
		msg.setText(s);
	}

	public void alertResponse(Object o) {
		// -------------------------------------

		String s = "";
		if (o instanceof Problem) {  // This a failure response from server
			s = ((Problem)o).getMsg();
		}
		else if (o instanceof Robot) {
			//Account acc = (Account)o;
			//if (updateAccount(acc)) {  //  <-- Existing account just updated
				//s = acc.getName() + " - Balance: " + acc.getBalance();
			//}
			//else {   //  <-- This is a new account just added
//				java.util.Vector v = getAccountList();
				//acList.setListData(v);
				//acList.setSelectedIndex(accounts.size()-1);
				//s = acc.getName() + " created!";
			//}
		}
		else if (o instanceof jade.util.leap.List) {  //  <-- This is a list of operations
			//displayOperations((jade.util.leap.List)o);
		}
		else if (o instanceof String) {
			s = (String)o;
		}
		msg.setText(s);
	}

	public void resetStatus() {
		// ---------------------------

		status = IN_PROCESS;
	}
}

//=========================== External class ============================//

/* TableDataModel:
*  --------------
*  External class for the definition of the tables data model, used to
*  control the display of data within the different tables
**/
   class TableDataModel extends AbstractTableModel {
// -------------------------------------------------

   private String[] columns;
   private Object[][] data;

   public TableDataModel(Object[][] data, String[] columns) {
// ----------------------------------------------------------  Constructor
      this.data = data;
      this.columns = columns;
   }

   public int getColumnCount() {
// -----------------------------  Return the number of columns in the table
      return columns.length;
   }

   public int getRowCount() {
// --------------------------  Return the number of rows in the table
      return data.length;
   }

   public String getColumnName(int col) {
// --------------------------------------  Return the name of a column
      return columns[col];
   }

   public Object getValueAt(int row, int col) {
// --------------------------------------------  Return the value at a specific
//                                               row and column
      if ( data.length == 0 ) return null;
      return data[row][col];
   }

   public Class getColumnClass(int col) {
// --------------------------------------  Return the class of the values held
//                                         by a column
      Object o = getValueAt(0, col);
      if (o == null) return columns[col].getClass();
      return getValueAt(0, col).getClass();
   }

   public void setValueAt(Object value, int row, int col){
// ------------------------------------------------------  Set the value at a specific
//                                                         row and column
      data[row][col] = value;
   }

   public void setData(Object[][] data){
// ------------------------------------  Update the entire data in the table
      this.data = data;
   }

   Object[][] getData(){
// ---------------------  Return the entire data of the table
      return data;
   }
}// end TableDataModel
