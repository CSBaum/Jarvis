/**
 * 
 */
package net.stallbaum.jarvisagent;

import java.util.Random;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import net.stallbaum.jarvis.util.ontologies.Problem;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sean
 *
 */
public class PlayerCommunicationBehavior extends CyclicBehaviour implements
		SecurityVocabulary {

	JarvisAgent jAgent = null;

	protected int cidCnt = 0;
	String cidBase ;
	
	private int behaviorState = AGENT_INITIALIZING;
	private boolean communicationAchieved = false;
	
	String playerConversationId = "";
	
	protected PlayerCommunicationBehavior(Agent a) {
		super();
		jAgent = (JarvisAgent)a;
	}
	
	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		// Generate Player conversation Id
		playerConversationId = genCID();
		
		// Based on agent state do something :)
		switch (jAgent.agentState) {
			case AGENT_INITIALIZING:
				// Either spawn a new player agent OR
				//       keep attempted to start a conversation with existing agent
				break;
			case AGENT_HALTING:
				// Send shutdown command to player agent
				break;
			case AGENT_STANDBY:
				// Send Pause Command to agent
				break;
			case AGENT_ACTIVE:
				break;
			default:
				Problem problem = new Problem();
				// 
		}

	}

//  --- generating Conversation IDs -------------------
	private String genCID() 
	{ 
		if (cidBase==null) {
			cidBase = myAgent.getLocalName() + hashCode() +
			System.currentTimeMillis()%10000 + "_";
		}
		return  cidBase + (cidCnt++); 
	}

	//  --- generating distinct Random generator -------------------

	private Random newRandom() 
	{	return  new Random( hashCode() + System.currentTimeMillis()); }
}
