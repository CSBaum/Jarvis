/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.Concept;

/**
 * @author sstallbaum
 *
 */
public class SecurityLevel implements Concept, SecurityVocabulary {

	private int level = 0;
	
	
	/**
	 * 
	 */
	public SecurityLevel(int _level) {
		level = _level;
	}


	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}


	/**
	 * @param level the level to set
	 */
	public void setLevel(int level) {
		this.level = level;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SecurityLevel [level=" + level + "]";
	}
}
