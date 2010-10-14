/**
 * 
 */
package net.stallbaum.jarvisagent;

import jade.content.lang.xml.XMLCodec;
import jade.content.onto.Ontology;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.util.Logger;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.ControllerException;
import net.stallbaum.jarvis.util.ontologies.SecurityOntology;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author sean
 *
 */
public class ShutdownAgent extends TickerBehaviour implements
		SecurityVocabulary {
	
	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	Behaviour[] behaviours = null;
	AbsJAgent jAgent = null;
	
	public ShutdownAgent(Agent a, long period, Behaviour[] _behaviours){
		super(a, period);
		behaviours = _behaviours;
		jAgent = (AbsJAgent)a;
	}
	
	@Override
	public void onTick() {
		if (jAgent.getState() == AGENT_HALTING) {
			
			// send a reply
			// -------> Send security level change message
			Ontology ontology = SecurityOntology.getInstance();
			jAgent.getContentManager().registerOntology(ontology);
			jAgent.getContentManager().registerLanguage(new XMLCodec());
			
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setSender(jAgent.getAID());
			msg.addReceiver(jAgent.getSender());
			msg.setLanguage(XMLCodec.NAME);
			msg.setConversationId(jAgent.getConversationId());
			msg.setPerformative(ACLMessage.AGREE);
			msg.setContent("Shutdown accepted");
			myAgent.send(msg);
			
			//-------> Remove agent from DF
			try{
				DFService.deregister(jAgent);
			}catch(FIPAException fe) {
				System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - Unable to de-register agent: " + fe.getLocalizedMessage());
			}
			
			//------> Remove Agent Behaviours
			for(Behaviour behaviour:behaviours){
				logger.info("Removing behaviour - " + behaviour.getBehaviourName());
				myAgent.removeBehaviour(behaviour);
			}
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
