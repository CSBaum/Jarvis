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
	
	Behaviour behaviour = null;
	JarvisAgent jAgent = null;
	
	public ShutdownAgent(Agent a, long period, Behaviour _behaviour){
		super(a, period);
		behaviour = _behaviour;
		jAgent = (JarvisAgent)a;
	}
	
	@Override
	public void onTick() {
		if (jAgent.agentState == AGENT_HALTING) {
			
			// send a reply
			// -------> Send security level change message
			Ontology ontology = SecurityOntology.getInstance();
			jAgent.getContentManager().registerOntology(ontology);
			jAgent.getContentManager().registerLanguage(new XMLCodec());
			
			ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
			msg.setSender(jAgent.getAID());
			msg.addReceiver(jAgent.sender);
			msg.setLanguage(XMLCodec.NAME);
			msg.setConversationId(jAgent.conversationId);
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
			System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - Removing ServerCommBehaviour");
			myAgent.removeBehaviour(behaviour);
			
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
