/**
 * 
 */
package net.stallbaum.jarvis;

import java.io.FileReader;
import java.io.IOException;

import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;

import jess.Jesp;
import jess.JessException;
import jess.Rete;

/**
 * @author Sean
 *
 */
public class JessBehavior extends CyclicBehaviour {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2764606525441257913L;

	// the Jess engine
    private Rete jess;

    // maximum number of passes that a run of Jess can execute before giving control to the agent
    private static final int MAX_JESS_PASSES = 1;
    
    JessBehavior(Agent agent, String jessFile) {
    	super(agent);
    	
    	// Create new jess engine
    	jess = new Rete();
    	
    	// Load the jess file
    	try {
    		FileReader fr = new FileReader(jessFile);
    		
    		// Create parser
    		Jesp j = new Jesp(fr, jess);
    		
    		// Parse the file
    		try {
    			j.parse(false);
    		} catch (JessException jex) {
    			jex.printStackTrace();
    		}
    	} catch (IOException ex) {
    		System.err.println("Error loading Jess file - engine is empty");
    	}
    	
    }
    
    /* (non-Javadoc)
	 * @see jade.core.behaviours.Behaviour#action()
	 */
	@Override
	public void action() {
		// counter for number of Jess passes
		int executePasses = -1;
		
		try {
			executePasses = jess.run(MAX_JESS_PASSES);
		} catch (JessException jex) {
			jex.printStackTrace();
		}
		
		// If engine stopped block this behavior
		if (executePasses < MAX_JESS_PASSES)
			block();
	}

	public boolean addFact(String jessFact) {
		// assert the fact into the Jess engine
		try {
			jess.assertString(jessFact);
		} catch (JessException jex) {
			return false;
		}
		
		// if blocked wake up!
		if (!isRunnable()) 
			restart();
		
		return true;
	}
	
	public boolean newMsg(ACLMessage msg) {
		String jf = "";
		
		// TODO build jess fact from ACL Message
		
		return addFact(jf);
	}
}
