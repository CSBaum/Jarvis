/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.AgentAction;

/**
 * @author sean
 *
 */
public class MakeRobotOperation implements SecurityVocabulary, AgentAction {

	//-------> Variables
	private int type;
	private Object operationObj;

	//------> Getters / Setters
	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public Object getObject(){
		return this.getObject();
	}
	
	public void setObject(Object obj){
		this.operationObj = obj;
	}
		
}
