/**
 * 
 */
package net.stallbaum.jarvis.util.exceptions;

import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author Administrator
 *
 */
public class JarvisAgentConfigException extends Exception implements
		SecurityVocabulary {

	/**
	 * 
	 */
	public JarvisAgentConfigException() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public JarvisAgentConfigException(String arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 */
	public JarvisAgentConfigException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public JarvisAgentConfigException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

}
