/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.Concept;

/**
 * @author sean
 *
 */
public class RobotOperation implements Concept, SecurityVocabulary {

	private int type;

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "RobotOperation [type=" + type + "]";
	}
	
	
}
