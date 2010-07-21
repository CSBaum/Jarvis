/**
 * 
 */
package net.stallbaum.jarvisagent;

import java.io.IOException;
import net.stallbaum.jarvis.util.ontologies.MakeRobotOperation;
import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.Robot;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;

/**
 * @author Sean
 *
 */
public class JarvisAgent extends Agent implements SecurityVocabulary {

	/**
	 * Unique id suggested by Eclipse :)
	 */
	private static final long serialVersionUID = -6400372757964918253L;

	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	
	protected String[] serverCommands;
	protected String[] playerCommands;

	protected String conversationId = "";
	protected AID sender = null;

	//------> Agent Status
	protected int agentState = AGENT_INITIALIZING;
	protected int previousAgentState = AGENT_INITIALIZING;
	
	protected int agentType;

	/**
	 * 
	 */
	public JarvisAgent() {
		// TODO Auto-generated constructor stub");
	}

	/**
	 * Method invoked when Agent adds behavior
	 */
	protected void setup(){
		logger.info("Starting JarvisAgent: " + getAID().getName());

		// Register with the Container's Yellowbook service
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("jarvis-agent-robot");
		sd.setName("jarvis-system");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe){
			fe.printStackTrace();
			logger.severe("Unable to register agent: " + fe.getLocalizedMessage());
		}

		// TODO Implement rest of agent start up code

		// Add JarvisCommBehahvior
		ServerCommunicationBehavior sbc = new ServerCommunicationBehavior(this, 500);
		addBehaviour(sbc);
		
		addBehaviour(new ShutdownAgent(this, 250, sbc));

		// Add PlayerBehavior (start up local instance of Player instance OR initialize a blank instance)
	}

	/**
	 * Method invoked when Agent is shutting down
	 */
	protected void takedown() {
		// Unregister from YB Service
		try {
			DFService.deregister(this);
		} catch (FIPAException fe) {
			fe.printStackTrace();
		}
		logger.info("JarvisAgent " + getLocalName() + " terminating");
	}
	
	/**
	 * Method invoked prior to the agent being moved from 1 container to another.
	 * In this case, we are de-registering the agent from the DF so that Jarvis
	 * does not use incorrect AID information to talk to the agent.
	 */
	protected void beforeMove() {
		 try { DFService.deregister(this); }
         catch (Exception e) {}
	}
	
	/**
	 * Method invoked after doMove() is called on teh agent.
	 * This specifically registers the agent with the DF with the correct AID
	 */
	protected void afterMove() {
		// Register with the Container's Yellowbook service
		DFAgentDescription dfd = new DFAgentDescription();
		dfd.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("jarvis-agent-robot");
		sd.setName("jarvis-system");
		dfd.addServices(sd);
		try {
			DFService.register(this, dfd);
		} catch (FIPAException fe){
			fe.printStackTrace();
			logger.severe(getLocalName() + " - Unable to register agent: " + fe.getLocalizedMessage());
		}
	}
	
	protected void checkSecurityLevel(int securityLevel){
		if ((securityLevel == SECURITY_LEVEL_NETWORK_AGENTS_ONLY) &&
				(agentType == NETWORK_AGENT)) {
			// Change to active if not already
			if (agentState != AGENT_ACTIVE){
				previousAgentState = agentState;
				agentState = AGENT_ACTIVE;
				logger.fine(getLocalName()+ ": Setting agent statue to " + getAgentStateTxt());
			}
			
		}
		else if ((securityLevel == SECURITY_LEVEL_ROBOT_AGENTS_ONLY) && agentType == NETWORK_AGENT) {
			// Change status to hold
			if (agentState != AGENT_STANDBY)  {
				previousAgentState = agentState;
				agentState = AGENT_STANDBY;
				logger.fine(getLocalName()+ ": Setting agent statue to " + getAgentStateTxt());
			}
		}
		else if ((securityLevel == SECURITY_LEVEL_NETWORK_AGENTS_ONLY) && agentType == ROBOT_AGENT) {
			// Change status to hold
			if (agentState != AGENT_STANDBY)  {
				previousAgentState = agentState;
				agentState = AGENT_STANDBY;
				logger.fine(getLocalName()+ ": Setting agent statue to " + getAgentStateTxt());
			}
		}
		else if ((securityLevel == SECURITY_LEVEL_ROBOT_AGENTS_ONLY) && agentType == ROBOT_AGENT) {
			// Change status to hold
			if (agentState != AGENT_ACTIVE)  {
				previousAgentState = agentState;
				agentState = AGENT_ACTIVE;
				logger.fine(getLocalName()+ ": Setting agent statue to " + getAgentStateTxt());
			}
		}
		else if (securityLevel == SECURITY_LEVEL_ALL_ON){
			previousAgentState = agentState;
			agentState = AGENT_ACTIVE;
			logger.fine(getLocalName()+ ": Setting agent statue to " + getAgentStateTxt());
		}
	}
	
	/**
	 * 
	 * @return Sting version of Agent state
	 */
	public String getAgentStateTxt() {
		String state = "";
		
		switch(agentState){
			case AGENT_INITIALIZING: 
				state = "Agent Initalizing";
				break;
			case AGENT_STANDBY: 
				state = "Standy";
				break;
			case AGENT_STAND_BY_SENSORS: 
				state = "Standy - Sensors Only";
				break;
			case AGENT_ACTIVE: 
				state = "Active";
				break;
			case AGENT_HALTING: 
				state = "Halting";
				break;
			default: 
				state = "Invalid State ... " + agentState;
				logger.warning("Detected an invalid agent state: " + agentState);
		}
		
		return state;
	}
}
