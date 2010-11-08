/**
 * 
 */
package net.stallbaum.jarvis;

import net.stallbaum.jarvis.util.ontologies.AlertConfirmation;
import net.stallbaum.jarvisagent.AbsJAgent;
import net.stallbaum.jarvisagent.ShutdownAgent;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;

/**
 * @author Administrator
 *
 */
public class JessAgent extends AbsJAgent {
	
	//-----> COmmunication variables
	protected String conversationId = "";
	protected String alertId = "";
	protected AID sender = null;

	//------> Agent Status
	protected int agentState = AGENT_INITIALIZING;
	protected int previousAgentState = AGENT_INITIALIZING;
	protected boolean alertFound = false;
	protected boolean alertStatus = false;
	private AlertConfirmation aConfirmation;
	
	//-----> Agent configuration information
	protected int agentType;
	/**
	 * adds the JessBehaviour and that's all.
	 */
	  protected void setup() {
		  // add the behaviour
		  // 1 is the number of steps that must be executed at each run of
		  // the Jess engine before giving back the control to the Java code
		  BasicJessBehaviour bjb = new BasicJessBehaviour(this,"examples/jess/JadeAgent.clp",1);
		  addBehaviour(bjb);
		  Behaviour[] behaviours = {bjb};
		  addBehaviour(new ShutdownAgent(this, 250, behaviours));
	  }

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getState()
	 */
	@Override
	public int getState() {
		return agentState;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getConversationId()
	 */
	@Override
	public String getConversationId() {
		return conversationId;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getAlertId()
	 */
	@Override
	public String getAlertId() {
		// TODO Auto-generated method stub
		return alertId;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getReceiver()
	 */
	@Override
	public AID getReceiver() {
		return getAID();
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getSender()
	 */
	@Override
	public AID getSender() {
		return sender;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#setSender(jade.core.AID)
	 */
	@Override
	public void setSender(AID _sender) {
		this.sender = _sender;
		
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#setAlertStataus(boolean)
	 */
	@Override
	public void setAlertStataus(boolean _flag) {
		this.alertStatus = _flag;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getAlertStatus()
	 */
	@Override
	public boolean getAlertStatus() {
		return alertStatus;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#setAlertConfirmation(net.stallbaum.jarvis.util.ontologies.AlertConfirmation)
	 */
	@Override
	public void setAlertConfirmation(AlertConfirmation _confirm) {
		this.aConfirmation = _confirm;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getAlertConfirmation()
	 */
	@Override
	public AlertConfirmation getAlertConfirmation() {
		return aConfirmation;
	}
}
