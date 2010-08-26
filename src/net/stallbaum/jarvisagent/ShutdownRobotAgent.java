/**
 * 
 */
package net.stallbaum.jarvisagent;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sean
 *
 */
public class ShutdownRobotAgent extends TickerBehaviour implements
		SecurityVocabulary {
	
	Behaviour behavior;
	PlayerAgent pAgent;

	/**
	 * @param a
	 * @param period
	 */
	public ShutdownRobotAgent(Agent a, long period, Behaviour _b) {
		super(a, period);
		behavior = _b;
		pAgent = (PlayerAgent)a;
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	protected void onTick() {
		
		if (pAgent.agentState == AGENT_HALTING) {
			//------> Remove Agent Behaviours
			System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - Removing the " + behavior + " behavior");
			myAgent.removeBehaviour(behavior);
			
			//------>Remove agent from container
			try {
				ContainerController cc = myAgent.getContainerController();
				AgentController ac;
				ac = cc.getAgent(myAgent.getLocalName());
				ac.kill();
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

}
