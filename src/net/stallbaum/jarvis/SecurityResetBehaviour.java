/**
 * 
 */
package net.stallbaum.jarvis;

import java.util.Date;

import jade.core.Agent;

import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.WakerBehaviour;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sstallbaum
 *
 */
public class SecurityResetBehaviour extends WakerBehaviour implements
		SecurityVocabulary {

	private Jarvis jarvis = null;
	
	public SecurityResetBehaviour(Agent a, Date wakeupDate) {
		super(a, wakeupDate);
		// TODO Auto-generated constructor stub
	}

	public SecurityResetBehaviour(Agent a, long timeout) {
		super(a, timeout);
		jarvis = (Jarvis)a;
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void handleElapsedTimeout() {
		jarvis.lastSecurityLevel = jarvis.securityLevel;
		System.out.println(getBehaviourName() + ": Reseting Security Levels");
	}

}
