/**
 * 
 */
package net.stallbaum.jarvis;

import java.util.Date;

import jade.core.Agent;

import jade.core.behaviours.WakerBehaviour;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sstallbaum
 *
 */
public class SecurityResetBehaviour extends WakerBehaviour implements
		SecurityVocabulary {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7528360941079461920L;
	private Jarvis jarvis = null;
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	
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
	public void onWake() {
		jarvis.lastSecurityLevel = jarvis.securityLevel;
		logger.finer(getBehaviourName() + ": Reseting Security Levels");
	}

}
