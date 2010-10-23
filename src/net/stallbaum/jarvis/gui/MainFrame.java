package net.stallbaum.jarvis.gui;
import jade.gui.GuiEvent;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.HeadlessException;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.LayoutStyle;
import javax.swing.SpinnerListModel;
import javax.swing.SwingConstants;

import javax.swing.WindowConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.SwingUtilities;

import net.stallbaum.jarvis.Jarvis;
import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;


/**
* This code was edited or generated using CloudGarden's Jigloo
* SWT/Swing GUI Builder, which is free for non-commercial
* use. If Jigloo is being used commercially (ie, by a corporation,
* company or business for any purpose whatever) then you
* should purchase a license for each developer using Jigloo.
* Please visit www.cloudgarden.com for details.
* Use of Jigloo implies acceptance of these licensing terms.
* A COMMERCIAL LICENSE HAS NOT BEEN PURCHASED FOR
* THIS MACHINE, SO JIGLOO OR THIS CODE CANNOT BE USED
* LEGALLY FOR ANY CORPORATE OR COMMERCIAL PURPOSE.
*/
public class MainFrame extends JFrame implements ActionListener, SecurityVocabulary {
	private Vector<Integer> problemRows = new Vector<Integer>(1);
	private JButton cancelButton;
	private JTable agentTable;
	private JScrollPane agentScrollPane;
	private JMenuItem pauseStatusMenuItem;
	private JMenu securityLevelMenu;
	private JMenuItem resetStatusMenuItem;
	private JMenu changeStatusMenu;
	private JMenu dataMenu;
	private JMenu statusMenu;
	private AbstractAction closeAboutAction;
	private JButton oK;
	private JDialog AboutDialog;
	private AbstractAction aboutAction;
	private JMenuItem jMenuItem1;
	private JMenu helpMenu;
	private JMenu agentMenu;
	private JMenuItem removeAgentMenuItem;
	private JSeparator agentjSeparator1;
	private AbstractAction resetAction;
	private AbstractAction setSecurityAllAction;
	private AbstractAction setSecurityRobotsOnlyAction;
	private AbstractAction setSecurityNetOnlytAction;
	private AbstractAction SetSecurityOffAction;
	private JMenuItem securityAllMenuItem;
	private JMenuItem securityRobotsOnlyMenuItem;
	private JMenuItem securityNetOnlyMenuItem;
	private JMenuItem securityOffMenuItem;
	private JLabel robotLabel;
	private JLabel locationLabel;
	private JComboBox agentTypeCombo;
	private JLabel agentTypeLabel;
	private JLabel agentNameLabel;
	private JTextField agentName;
	private AbstractAction cloaseAgentAction;
	private JButton agentAddButton;
	private JButton agentCancelButton;
	private JDialog AgentDialog;
	private AbstractAction createRobotbstractAction;
	private AbstractAction shutdownJarvisButtonabstractAction;
	private JTextField statusjTextField;
	private JLabel statusjLabel;
	private JTextPane aboutTextPane;
	private JMenuItem roboticAgentMenuItem;
	private JMenuItem networkAgentMenuItem;
	private JMenu addAgentMenu;
	private JMenuItem shutdownJarvisMenuItem;
	private JSeparator agentSeparator2;
	private JMenuItem pauseAgentMenuItem;
	private JMenuItem stopAgentMenuItem;
	private JMenuItem startAgentMenuItem;
	private JMenuItem agentPropertiesMenuItem;
	private JMenuBar jarvisMenuBar;

	private Jarvis jarvisAgent;
	private boolean localConnection = true;
	private Connection guiConnection;
	private Statement guiStmt;
	private ResultSet agentRS;
	
	public MainFrame() throws HeadlessException{
		
	}
	
	public MainFrame(Jarvis _jarvis, ResultSet _agentRS) {
		super();
		jarvisAgent = _jarvis;
		agentRS = _agentRS;
		
		try {
			// Load MySQL Driver
			Class.forName("com.mysql.jdbc.Driver").newInstance();

			// Create connection (Should be based on property file)
			guiConnection = DriverManager.getConnection("jdbc:mysql://localhost/jarvis",
					"jarvis", "stark");

			guiStmt = guiConnection.createStatement();
		} catch (SQLException sex) {
			// handle any errors
			System.out.println("SQL Error occured while setting up Jarvis: ");
			System.out.println("         SQLException: " + sex.getMessage());
			System.out.println("         SQLState: " + sex.getSQLState());
			System.out.println("         VendorError: " + sex.getErrorCode());
		} catch (Exception ex) {
			System.out.println("Unable to setup database: ");
			System.out.println("        " + ex.getMessage());
			System.out.println("        " + ex.getStackTrace());
		} 
		initGUI();
	}
	
	public MainFrame(Jarvis _jarvis, Connection _guiConnection, ResultSet _agentRS) {
		super();
		jarvisAgent = _jarvis;
		guiConnection = _guiConnection;
		agentRS = _agentRS;
		localConnection = false;
		initGUI();
	}
	
	private void initGUI() {
		try {
			GroupLayout thisLayout = new GroupLayout((JComponent)getContentPane());
			setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
			getContentPane().setLayout(thisLayout);
			this.setTitle("Jarvis Console");
			{
				jarvisMenuBar = new JMenuBar();
				setJMenuBar(jarvisMenuBar);
				{
					agentMenu = new JMenu();
					jarvisMenuBar.add(agentMenu);
					jarvisMenuBar.add(getStatusMenu());
					jarvisMenuBar.add(getDataMenu());
					agentMenu.setText("Agents");
					agentMenu.add(getAddAgentMenu());
					agentMenu.add(getRemoveAgentMenuItem());
					agentMenu.add(getAgentPropertiesMenuItem());
					agentMenu.add(getAgentjSeparator1());
					agentMenu.add(getStartAgentMenuItem());
					agentMenu.add(getPauseAgentMenuItem());
					agentMenu.add(getStopAgentMenuItem());
					agentMenu.add(getAgentSeparator2());
					agentMenu.add(getShutdownJarvisMenuItem());
				}
				{
					helpMenu = new JMenu();
					jarvisMenuBar.add(helpMenu);
					helpMenu.setText("Help");
					{
						jMenuItem1 = new JMenuItem();
						helpMenu.add(jMenuItem1);
						jMenuItem1.setText("jMenuItem1");
						jMenuItem1.setAction(getAboutAction());
					}
				}
			}
			{
				cancelButton = new JButton();
				cancelButton.setText("Shutdown Jarvis");
				cancelButton.setFont(new java.awt.Font("Segoe UI",1,15));
				cancelButton.setAction(getShutdownJarvisButtonabstractAction());
			}
			{
				agentScrollPane = new JScrollPane();
				{
					ResultSetTableModel jResultSet = new ResultSetTableModel(agentRS);
					agentTable = new JTable();
					agentTable.setFillsViewportHeight(true);
					agentScrollPane.setViewportView(agentTable);
					agentTable.setModel(jResultSet);
					agentTable.setPreferredSize(new java.awt.Dimension(342, 113));
					int columnCount = agentTable.getColumnCount();
					for (int inx = 0; inx < columnCount; inx++){
						agentTable.getColumnModel().getColumn(inx).setCellRenderer(new RenderStatus(problemRows));
					}
				}
			}
			thisLayout.setHorizontalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addGroup(thisLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(getStatusjLabel(), GroupLayout.PREFERRED_SIZE, 106, GroupLayout.PREFERRED_SIZE)
				        .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				        .addComponent(getStatusjTextField(), GroupLayout.PREFERRED_SIZE, 240, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 35, Short.MAX_VALUE))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addComponent(agentScrollPane, 0, 358, Short.MAX_VALUE)
				        .addGap(35))
				    .addGroup(GroupLayout.Alignment.LEADING, thisLayout.createSequentialGroup()
				        .addGap(0, 235, Short.MAX_VALUE)
				        .addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, 158, GroupLayout.PREFERRED_SIZE)))
				.addContainerGap());
			thisLayout.setVerticalGroup(thisLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(agentScrollPane, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
				.addGap(24)
				.addGroup(thisLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(getStatusjLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				    .addComponent(getStatusjTextField(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 21, GroupLayout.PREFERRED_SIZE))
				.addGap(0, 84, Short.MAX_VALUE)
				.addComponent(cancelButton, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap());
			pack();
			this.setSize(434, 350);
		} catch (Exception e) {
		    //add your error handling code here
			e.printStackTrace();
		}
	}
	
	private AbstractAction getAboutAction() {
		if(aboutAction == null) {
			aboutAction = new AbstractAction("About", null) {
				public void actionPerformed(ActionEvent evt) {
					getAboutDialog().pack();
					getAboutDialog().setLocationRelativeTo(null);
					getAboutDialog().setVisible(true);
				}
			};
		}
		return aboutAction;
	}
	
	private JDialog getAboutDialog() {
		if(AboutDialog == null) {
			AboutDialog = new JDialog(this);
			GroupLayout AboutDialogLayout = new GroupLayout((JComponent)AboutDialog.getContentPane());
			AboutDialog.getContentPane().setLayout(AboutDialogLayout);
			AboutDialog.setPreferredSize(new java.awt.Dimension(409, 262));
			AboutDialog.setSize(409, 262);
			AboutDialogLayout.setHorizontalGroup(AboutDialogLayout.createSequentialGroup()
				.addContainerGap(147, 147)
				.addGroup(AboutDialogLayout.createParallelGroup()
				    .addGroup(AboutDialogLayout.createSequentialGroup()
				        .addGap(0, 0, Short.MAX_VALUE)
				        .addComponent(getAboutTextPane(), GroupLayout.PREFERRED_SIZE, 232, GroupLayout.PREFERRED_SIZE))
				    .addGroup(GroupLayout.Alignment.LEADING, AboutDialogLayout.createSequentialGroup()
				        .addGap(162)
				        .addComponent(getOK(), GroupLayout.PREFERRED_SIZE, 70, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 0, Short.MAX_VALUE)))
				.addContainerGap());
			AboutDialogLayout.setVerticalGroup(AboutDialogLayout.createSequentialGroup()
				.addContainerGap()
				.addComponent(getAboutTextPane(), GroupLayout.PREFERRED_SIZE, 132, GroupLayout.PREFERRED_SIZE)
				.addGap(34)
				.addComponent(getOK(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE));
		}
		return AboutDialog;
	}
	
	private JButton getOK() {
		if(oK == null) {
			oK = new JButton();
			oK.setText("Ok");
			oK.setAction(getCloseAboutAction());
		}
		return oK;
	}

	private AbstractAction getCloseAboutAction() {
		if(closeAboutAction == null) {
			closeAboutAction = new AbstractAction("Ok", null) {
				public void actionPerformed(ActionEvent evt) {
					getAboutDialog().dispose();
				}
			};
		}
		return closeAboutAction;
	}
	
	private JMenu getStatusMenu() {
		if(statusMenu == null) {
			statusMenu = new JMenu();
			statusMenu.setText("System Status");
			statusMenu.add(getChangeStatusMenu());
		}
		return statusMenu;
	}
	
	private JMenu getDataMenu() {
		if(dataMenu == null) {
			dataMenu = new JMenu();
			dataMenu.setText("Data");
		}
		return dataMenu;
	}

	/**
	* Auto-generated method for setting the popup menu for a component
	*/
	private void setComponentPopupMenu(final java.awt.Component parent, final javax.swing.JPopupMenu menu) {
		parent.addMouseListener(new java.awt.event.MouseAdapter() {
			public void mousePressed(java.awt.event.MouseEvent e) {
				if(e.isPopupTrigger())
					menu.show(parent, e.getX(), e.getY());
			}
			public void mouseReleased(java.awt.event.MouseEvent e) {
				if(e.isPopupTrigger())
					menu.show(parent, e.getX(), e.getY());
			}
		});
	}
	
	private JMenu getChangeStatusMenu() {
		if(changeStatusMenu == null) {
			changeStatusMenu = new JMenu();
			changeStatusMenu.setText("Change");
			changeStatusMenu.add(getSecurityLevelMenu());
			changeStatusMenu.add(getResetStatusMenuItem());
			changeStatusMenu.add(getPauseStatusMenuItem());
		}
		return changeStatusMenu;
	}
	
	private JMenuItem getResetStatusMenuItem() {
		if(resetStatusMenuItem == null) {
			resetStatusMenuItem = new JMenuItem();
			resetStatusMenuItem.setText("Reset");
			resetStatusMenuItem.setAction(getResetAction());
		}
		return resetStatusMenuItem;
	}
	
	private JMenu getSecurityLevelMenu() {
		if(securityLevelMenu == null) {
			securityLevelMenu = new JMenu();
			securityLevelMenu.setText("Set Security Level");
			securityLevelMenu.setBounds(0, 26, 137, 26);
			securityLevelMenu.add(getSecurityOffMenuItem());
			securityLevelMenu.add(getSecurityNetOnlyMenuItem());
			securityLevelMenu.add(getSecurityRobotsOnlyMenuItem());
			securityLevelMenu.add(getSecurityAllMenuItem());
		}
		return securityLevelMenu;
	}
	
	private JMenuItem getPauseStatusMenuItem() {
		if(pauseStatusMenuItem == null) {
			pauseStatusMenuItem = new JMenuItem();
			pauseStatusMenuItem.setText("Pause System");
		}
		return pauseStatusMenuItem;
	}
	
	private JMenuItem getRemoveAgentMenuItem() {
		if(removeAgentMenuItem == null) {
			removeAgentMenuItem = new JMenuItem();
			removeAgentMenuItem.setText("Remove Agent");
		}
		return removeAgentMenuItem;
	}
	
	private JMenuItem getAgentPropertiesMenuItem() {
		if(agentPropertiesMenuItem == null) {
			agentPropertiesMenuItem = new JMenuItem();
			agentPropertiesMenuItem.setText("Agent Properties");
		}
		return agentPropertiesMenuItem;
	}
	
	private JSeparator getAgentjSeparator1() {
		if(agentjSeparator1 == null) {
			agentjSeparator1 = new JSeparator();
		}
		return agentjSeparator1;
	}
	
	private JMenuItem getStartAgentMenuItem() {
		if(startAgentMenuItem == null) {
			startAgentMenuItem = new JMenuItem();
			startAgentMenuItem.setText("Start Agent");
		}
		return startAgentMenuItem;
	}
	
	private JMenuItem getStopAgentMenuItem() {
		if(stopAgentMenuItem == null) {
			stopAgentMenuItem = new JMenuItem();
			stopAgentMenuItem.setText("Stop Agent");
		}
		return stopAgentMenuItem;
	}
	
	private JMenuItem getPauseAgentMenuItem() {
		if(pauseAgentMenuItem == null) {
			pauseAgentMenuItem = new JMenuItem();
			pauseAgentMenuItem.setText("Pause Agent");
		}
		return pauseAgentMenuItem;
	}
	
	private JSeparator getAgentSeparator2() {
		if(agentSeparator2 == null) {
			agentSeparator2 = new JSeparator();
		}
		return agentSeparator2;
	}
	
	private JMenuItem getShutdownJarvisMenuItem() {
		if(shutdownJarvisMenuItem == null) {
			shutdownJarvisMenuItem = new JMenuItem();
			shutdownJarvisMenuItem.setText("Shutdown Jarvis");
		}
		return shutdownJarvisMenuItem;
	}
	
	private JMenu getAddAgentMenu() {
		if(addAgentMenu == null) {
			addAgentMenu = new JMenu();
			addAgentMenu.setText("Add Agent");
			addAgentMenu.add(getNetworkAgentMenuItem());
			addAgentMenu.add(getRoboticAgentMenuItem());
		}
		return addAgentMenu;
	}
	
	private JMenuItem getNetworkAgentMenuItem() {
		if(networkAgentMenuItem == null) {
			networkAgentMenuItem = new JMenuItem();
			networkAgentMenuItem.setText("Network Agent");
		}
		return networkAgentMenuItem;
	}
	
	private JMenuItem getRoboticAgentMenuItem() {
		if(roboticAgentMenuItem == null) {
			roboticAgentMenuItem = new JMenuItem();
			roboticAgentMenuItem.setText("Robotic Agent");
			roboticAgentMenuItem.setBounds(0, 198, 123, 26);
			roboticAgentMenuItem.setAction(getCreateRobotbstractAction());
		}
		return roboticAgentMenuItem;
	}
	
	private JTextPane getAboutTextPane() {
		if(aboutTextPane == null) {
			aboutTextPane = new JTextPane();
			aboutTextPane.setText("Jarvis System .1.0 by Sean Stallbaum");
			aboutTextPane.setEditable(false);
			aboutTextPane.setFont(new java.awt.Font("Cambria",0,16));
		}
		return aboutTextPane;
	}

	private JLabel getStatusjLabel() {
		if(statusjLabel == null) {
			statusjLabel = new JLabel();
			statusjLabel.setText("System Status:");
		}
		return statusjLabel;
	}
	
	private JTextField getStatusjTextField() {
		if(statusjTextField == null) {
			statusjTextField = new JTextField();
			statusjTextField.setEditable(false);
			statusjTextField.setText(jarvisAgent.getSystemStateTxt());
			statusjTextField.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
		}
		return statusjTextField;
	}

	@Override
	public void actionPerformed(ActionEvent ae) {
		// TODO stuff
	}
	
	void shutDown() {
		// ----------------- Control the closing of this gui

		int rep = JOptionPane.showConfirmDialog(this,
				"Are you sure you want to exit?", jarvisAgent.getLocalName(),
				JOptionPane.YES_NO_CANCEL_OPTION);
		if (rep == JOptionPane.YES_OPTION) {
			GuiEvent ge = new GuiEvent(this, GUI_SHUTDOWN);
			jarvisAgent.postGuiEvent(ge);
		}
		
		if (guiStmt != null){
			try {
				guiStmt.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if (agentRS != null) {
			try {
				agentRS.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private AbstractAction getShutdownJarvisButtonabstractAction() {
		if(shutdownJarvisButtonabstractAction == null) {
			shutdownJarvisButtonabstractAction = new AbstractAction("Shutdown Jarvis", null) {
				public void actionPerformed(ActionEvent evt) {
					shutDown();
				}
			};
		}
		return shutdownJarvisButtonabstractAction;
	}

	public void alertResponse(Object o) {
		// -------------------------------------

		if (o instanceof String){
			statusjTextField.setText(o.toString());
		}
		else if (o instanceof Problem){
			Problem problem = (Problem)o;
			String agent = problem.getAgentName();
			for (int inx = 0; inx < agentTable.getRowCount(); inx++){
				String value = (String)agentTable.getValueAt(inx, 1);
				if (value.equalsIgnoreCase(agent)){
					problemRows.add(new Integer(inx));
				}
			}
		}
		agentTable.repaint();
	}

	private ResultSetTableModel generateTable(String whereClause){
		ResultSetTableModel tableModel = null;
		
		try {
			guiStmt = guiConnection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			agentRS = guiStmt.executeQuery(whereClause);
			tableModel = new ResultSetTableModel(agentRS);
		} catch (SQLException ex) {
			// handle any errors
			System.out.println("SQL Error occured while querying: " + whereClause);
			System.out.println("         SQLException: " + ex.getMessage());
			System.out.println("         SQLState: " + ex.getSQLState());
			System.out.println("         VendorError: " + ex.getErrorCode());
		}
		return tableModel;
	}
	
	private AbstractAction getCreateRobotbstractAction() {
		if(createRobotbstractAction == null) {
			createRobotbstractAction = new AbstractAction("Robot Agent", null) {
				public void actionPerformed(ActionEvent evt) {
					getAgentDialog().pack();
					getAgentDialog().setLocationRelativeTo(null);
					getAgentDialog().setModal(true);
					getAgentDialog().setVisible(true);
				}
			};
		}
		return createRobotbstractAction;
	}
	
	private JDialog getAgentDialog() {
		if(AgentDialog == null) {
			AgentDialog = new JDialog(this);
			GroupLayout AgentDialogLayout = new GroupLayout((JComponent)AgentDialog.getContentPane());
			AgentDialog.getContentPane().setLayout(AgentDialogLayout);
			AgentDialog.setTitle("Create New Robot Agent");
			AgentDialogLayout.setHorizontalGroup(AgentDialogLayout.createSequentialGroup()
				.addContainerGap(52, 52)
				.addGroup(AgentDialogLayout.createParallelGroup()
				    .addGroup(AgentDialogLayout.createSequentialGroup()
				        .addComponent(getAgentNameLabel(), GroupLayout.PREFERRED_SIZE, 98, GroupLayout.PREFERRED_SIZE)
				        .addGap(0, 0, Short.MAX_VALUE))
				    .addGroup(AgentDialogLayout.createSequentialGroup()
				        .addGap(28)
				        .addGroup(AgentDialogLayout.createParallelGroup()
				            .addComponent(getLocationLabel(), GroupLayout.Alignment.LEADING, 0, 70, Short.MAX_VALUE)
				            .addGroup(AgentDialogLayout.createSequentialGroup()
				                .addGap(17)
				                .addGroup(AgentDialogLayout.createParallelGroup()
				                    .addGroup(AgentDialogLayout.createSequentialGroup()
				                        .addComponent(getRobotLabel(), GroupLayout.PREFERRED_SIZE, 53, GroupLayout.PREFERRED_SIZE)
				                        .addGap(0, 0, Short.MAX_VALUE))
				                    .addComponent(getAgentTypeLabel(), GroupLayout.Alignment.LEADING, 0, 53, Short.MAX_VALUE))))))
				.addGap(7)
				.addGroup(AgentDialogLayout.createParallelGroup()
				    .addGroup(GroupLayout.Alignment.LEADING, AgentDialogLayout.createSequentialGroup()
				        .addComponent(getAgentTypeCombo(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				        .addGap(84))
				    .addComponent(getAgentName(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, 163, GroupLayout.PREFERRED_SIZE)
				    .addGroup(GroupLayout.Alignment.LEADING, AgentDialogLayout.createSequentialGroup()
				        .addGap(62)
				        .addComponent(getAgentAddButton(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				        .addGap(15)))
				.addComponent(getAgentCancelButton(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addContainerGap(43, 43));
			AgentDialogLayout.setVerticalGroup(AgentDialogLayout.createSequentialGroup()
				.addContainerGap(53, 53)
				.addGroup(AgentDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(getAgentName(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				    .addComponent(getAgentNameLabel(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, 23, GroupLayout.PREFERRED_SIZE))
				.addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
				.addGroup(AgentDialogLayout.createParallelGroup()
				    .addComponent(getAgentTypeCombo(), GroupLayout.Alignment.LEADING, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				    .addGroup(GroupLayout.Alignment.LEADING, AgentDialogLayout.createSequentialGroup()
				        .addComponent(getAgentTypeLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				        .addGap(7)))
				.addComponent(getLocationLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
				.addComponent(getRobotLabel(), GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				.addGap(24)
				.addGroup(AgentDialogLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
				    .addComponent(getAgentAddButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
				    .addComponent(getAgentCancelButton(), GroupLayout.Alignment.BASELINE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
				.addContainerGap(29, 29));
		}
		return AgentDialog;
	}
	
	private JButton getAgentCancelButton() {
		if(agentCancelButton == null) {
			agentCancelButton = new JButton();
			agentCancelButton.setText("Cancel");
			agentCancelButton.setAction(getCloaseAgentAction());
		}
		return agentCancelButton;
	}
	
	private JButton getAgentAddButton() {
		if(agentAddButton == null) {
			agentAddButton = new JButton();
			agentAddButton.setText("Add Agent");
		}
		return agentAddButton;
	}
	
	private AbstractAction getCloaseAgentAction() {
		if(cloaseAgentAction == null) {
			cloaseAgentAction = new AbstractAction("Close", null) {
				public void actionPerformed(ActionEvent evt) {
					getAgentDialog().dispose();
				}
			};
		}
		return cloaseAgentAction;
	}
	
	private JTextField getAgentName() {
		if(agentName == null) {
			agentName = new JTextField();
			agentName.setText("JAgent-");
		}
		return agentName;
	}
	
	private JLabel getAgentNameLabel() {
		if(agentNameLabel == null) {
			agentNameLabel = new JLabel();
			agentNameLabel.setText("Agent Name:");
		}
		return agentNameLabel;
	}
	
	private JLabel getAgentTypeLabel() {
		if(agentTypeLabel == null) {
			agentTypeLabel = new JLabel();
			agentTypeLabel.setText("Type:");
		}
		return agentTypeLabel;
	}
	
	private JComboBox getAgentTypeCombo() {
		if(agentTypeCombo == null) {
			ComboBoxModel agentTypeComboModel = 
				new DefaultComboBoxModel(
						new String[] { "Network", "Robotic" });
			agentTypeCombo = new JComboBox();
			agentTypeCombo.setModel(agentTypeComboModel);
			agentTypeCombo.setSelectedIndex(1);
		}
		return agentTypeCombo;
	}
	
	private JLabel getLocationLabel() {
		if(locationLabel == null) {
			locationLabel = new JLabel();
			locationLabel.setText("Location:");
		}
		return locationLabel;
	}
	
	private JLabel getRobotLabel() {
		if(robotLabel == null) {
			robotLabel = new JLabel();
			robotLabel.setText("Robot:");
		}
		return robotLabel;
	}
	
	private JMenuItem getSecurityOffMenuItem() {
		if(securityOffMenuItem == null) {
			securityOffMenuItem = new JMenuItem();
			securityOffMenuItem.setText("Off");
			securityOffMenuItem.setAction(getSetSecurityOffAction());
		}
		return securityOffMenuItem;
	}
	
	private JMenuItem getSecurityNetOnlyMenuItem() {
		if(securityNetOnlyMenuItem == null) {
			securityNetOnlyMenuItem = new JMenuItem();
			securityNetOnlyMenuItem.setText("Network Agents Only");
			securityNetOnlyMenuItem.setAction(getSetSecurityNetOnlytAction());
		}
		return securityNetOnlyMenuItem;
	}
	
	private JMenuItem getSecurityRobotsOnlyMenuItem() {
		if(securityRobotsOnlyMenuItem == null) {
			securityRobotsOnlyMenuItem = new JMenuItem();
			securityRobotsOnlyMenuItem.setText("Robots Only");
			securityRobotsOnlyMenuItem.setAction(getSetSecurityRobotsOnlyAction());
		}
		return securityRobotsOnlyMenuItem;
	}
	
	private JMenuItem getSecurityAllMenuItem() {
		if(securityAllMenuItem == null) {
			securityAllMenuItem = new JMenuItem();
			securityAllMenuItem.setText("On");
			securityAllMenuItem.setAction(getSetSecurityAllAction());
		}
		return securityAllMenuItem;
	}
	
	private AbstractAction getSetSecurityOffAction() {
		if(SetSecurityOffAction == null) {
			SetSecurityOffAction = new AbstractAction("Off", null) {
				public void actionPerformed(ActionEvent evt) {
					GuiEvent ge = new GuiEvent(this, GUI_SEC_OFF);
					jarvisAgent.postGuiEvent(ge);
				}
			};
		}
		return SetSecurityOffAction;
	}
	
	private AbstractAction getSetSecurityNetOnlytAction() {
		if(setSecurityNetOnlytAction == null) {
			setSecurityNetOnlytAction = new AbstractAction("Network Agents Only", null) {
				public void actionPerformed(ActionEvent evt) {
					GuiEvent ge = new GuiEvent(this, GUI_SEC_NET);
					jarvisAgent.postGuiEvent(ge);
				}
			};
		}
		return setSecurityNetOnlytAction;
	}
	
	private AbstractAction getSetSecurityRobotsOnlyAction() {
		if(setSecurityRobotsOnlyAction == null) {
			setSecurityRobotsOnlyAction = new AbstractAction("Robots Only", null) {
				public void actionPerformed(ActionEvent evt) {
					GuiEvent ge = new GuiEvent(this, GUI_SEC_BOT);
					jarvisAgent.postGuiEvent(ge);
				}
			};
		}
		return setSecurityRobotsOnlyAction;
	}
	
	private AbstractAction getSetSecurityAllAction() {
		if(setSecurityAllAction == null) {
			setSecurityAllAction = new AbstractAction("On", null) {
				public void actionPerformed(ActionEvent evt) {
					GuiEvent ge = new GuiEvent(this, GUI_SEC_ON);
					jarvisAgent.postGuiEvent(ge);
				}
			};
		}
		return setSecurityAllAction;
	}
	
	private AbstractAction getResetAction() {
		if(resetAction == null) {
			resetAction = new AbstractAction("Reset", null) {
				public void actionPerformed(ActionEvent evt) {
					GuiEvent ge = new GuiEvent(this, GUI_RESET);
					jarvisAgent.postGuiEvent(ge);
				}
			};
		}
		return resetAction;
	}

}
