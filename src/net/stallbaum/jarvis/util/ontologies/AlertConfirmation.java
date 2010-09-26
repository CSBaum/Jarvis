/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.Concept;
import jade.core.AID;

/**
 * @author Administrator
 *
 */
public class AlertConfirmation implements Concept, SecurityVocabulary {

	//-----> Class Variables
	AID agnet;
	Problem problem;
	
	boolean status;
	
	/**
	 * 
	 */
	public AlertConfirmation() {
		// TODO Auto-generated constructor stub
	}
	
	public AlertConfirmation(AID _agent){
		this.agnet = _agent;
		this.status = true;
	}
	
	public AlertConfirmation(AID _agent, Problem _problem) {
		this.agnet = _agent;
		this.problem = _problem;
		this.status = false;
	}

	public AID getAgent(){
		return this.agnet;
	}
	public boolean getStatus(){
		return this.status;
	}
	
	public Problem getProblem(){
		return this.problem;
	}
}
