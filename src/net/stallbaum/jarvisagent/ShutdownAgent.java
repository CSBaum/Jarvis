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
			XMLCodec codex = new XMLCodec();
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
			
			System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - Removing ServerCommBehaviour");
			myAgent.removeBehaviour(behaviour);
			try {
				ContainerController cc = myAgent.getContainerController();
				AgentController ac;
				ac = cc.getAgent(myAgent.getLocalName());
				//System.out.println(myAgent.getLocalName() + ":" + getBehaviourName() + " - About to kill myself");
				ac.kill();
			} catch (ControllerException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
		}
	}
}
