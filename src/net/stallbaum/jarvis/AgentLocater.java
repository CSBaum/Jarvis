/**
 * 
 */
package net.stallbaum.jarvis;

import java.util.Arrays;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.util.Logger;
import net.stallbaum.jarvis.util.exceptions.JarvisAgentConfigException;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author Administrator
 *
 */
public class AgentLocater extends TickerBehaviour implements SecurityVocabulary {

	Jarvis jAgent = null;
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	
	DFAgentDescription[] prevResult = null;
	
	/**
	 * @param a
	 * @param period
	 */
	public AgentLocater(Agent a, long period) {
		super(a, period);
		jAgent = (Jarvis)a;
	}

	/* (non-Javadoc)
	 * @see jade.core.behaviours.TickerBehaviour#onTick()
	 */
	@Override
	protected void onTick() {
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		
		//template.addServices(sd);
		try {
			DFAgentDescription[] result = DFService.search(jAgent, template);
			logger.info("Found" + result.length);
			
			for (DFAgentDescription desc: result){
				int index = Arrays.binarySearch(result, desc);
				if (index < 0){
					// we need to process it ..
					
				}
			}
		} catch (FIPAException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Check the DF to see if a new agent has appeared
		
		
		// if yes, determine type and call appropriate behavior to deal with it

	}
	
	private void processAndroidAgent() throws JarvisAgentConfigException{
		
	}
	
	private void processRobotAgent() throws JarvisAgentConfigException{
		
	}
	
	private void processNetworkAgent() throws JarvisAgentConfigException{
		
	}

}
