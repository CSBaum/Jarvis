/**
 * 
 */
package net.stallbaum.jarvisagent;

import net.stallbaum.jarvis.util.ontologies.AlertConfirmation;
import jade.core.AID;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;

/**
 * @author Administrator
 *
 */
public class CommunucationAgent extends AbsJAgent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2671116117144352375L;
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	
	//-----> COmmunication variables
	protected String conversationId = "";
	protected String alertId = "";
	protected AID sender = null;

	//------> Agent Status
	protected int agentState = AGENT_INITIALIZING;
	protected int previousAgentState = AGENT_INITIALIZING;

	/**
	 * 
	 */
	public CommunucationAgent() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getState()
	 */
	@Override
	public int getState() {
		// TODO Auto-generated method stub
		return agentState;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getConversationId()
	 */
	@Override
	public String getConversationId() {
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		return getAID();
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvisagent.AbsJAgent#getSender()
	 */
	@Override
	public AID getSender() {
		// TODO Auto-generated method stub
		return sender;
	}

	protected void setup(){
		logger.info("Starting JarvisCommAgent: " + getAID().getName());

		// Register with the Container's Yellowbook service
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		
		sd.setType("jarvis-agent-comm");
		sd.setName("jarvis-system");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe){
			fe.printStackTrace();
			logger.severe("Unable to register agent: " + fe.getLocalizedMessage());
		}

		// TODO Implement rest of agent start up code
		//-----> Add Jarvis communication behaviour
		AlerCommunicationBehvior acb = new AlerCommunicationBehvior(this, 200);
		addBehaviour(acb);
		
		//-----> Add Shutdown Communication behavior
		addBehaviour(new ShutdownAgent(this, 250, acb));
	}

	@Override
	public void setSender(AID _sender) {
		// TODO Auto-generated method stub
		this.sender = _sender;
	}

	@Override
	public void setAlertStataus(boolean _flag) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean getAlertStatus() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setAlertConfirmation(AlertConfirmation _confirm) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public AlertConfirmation getAlertConfirmation() {
		// TODO Auto-generated method stub
		return null;
	}

}
