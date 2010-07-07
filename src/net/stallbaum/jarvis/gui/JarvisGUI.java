/**
 * 
 */
package net.stallbaum.jarvis.gui;

import jade.gui.GuiEvent;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GraphicsConfiguration;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
import javax.swing.table.TableModel;

import net.stallbaum.jarvis.Jarvis;

/**
 * @author sstallbaum
 *
 */
public class JarvisGUI extends JFrame implements ActionListener {
	
	final static int IN_PROCESS = 0;
	final static int WAIT_CONFIRM = 1;
	final static int IN_LINE = 2;
	private int status = IN_PROCESS;
	
	
	Object[] actions = {"","New Account","Deposit","Withdrawal",
            "Balance","Operations"};
	private JTextField msg, input, acInfo;
	private JComboBox menu;
	private JList acList;
	private JTable opTable;
	private JButton ok, cancel, quit;
	
	private Jarvis jarvisAgent;
	
	/**
	 * @throws HeadlessException
	 */
	public JarvisGUI() throws HeadlessException {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param gc
	 */
	public JarvisGUI(GraphicsConfiguration gc) {
		super(gc);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @throws HeadlessException
	 */
	public JarvisGUI(String title) throws HeadlessException {
		super(title);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param title
	 * @param gc
	 */
	public JarvisGUI(String title, GraphicsConfiguration gc) {
		super(title, gc);
		// TODO Auto-generated constructor stub
	}

	public JarvisGUI(Jarvis j){
		jarvisAgent = j;
		setTitle("Jarvis GUI");
		
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
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent ae) {
		if (ae.getSource() == quit) {
	         shutDown();
	      }
	}
	
	void shutDown() {
		// ----------------- Control the closing of this gui

		int rep = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to exit?", jarvisAgent.getLocalName(),
				JOptionPane.YES_NO_CANCEL_OPTION);
		//if (rep == JOptionPane.YES_OPTION) {
		//	GuiEvent ge = new GuiEvent(this,);
		//	jarvisAgent.postGuiEvent(ge);
		//}
	}

	void alertInfo(String s) {
		// --------------------------

		Toolkit.getDefaultToolkit().beep();
		msg.setText(s);
	}

	public void alertResponse(Object o) {
		// -------------------------------------

		String s = "";
		if (o instanceof String) {
			s = (String) o;
		}
		msg.setText(s);
	}
}
