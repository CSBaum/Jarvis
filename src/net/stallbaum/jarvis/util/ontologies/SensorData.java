/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import java.util.Date;

import jade.content.Concept;
import jade.core.AID;

/**
 * @author Administrator
 *
 */
public abstract class SensorData implements Concept, SecurityVocabulary {

	Date timeStamp;
	boolean isArchived;
	int type;
	AID agent;
	
	/**
	 * 
	 */
	public SensorData() {
		// TODO Auto-generated constructor stub
	}
	
	public SensorData(AID _agent, int _type){
		this.agent = _agent;
		this.type = _type;
		this.isArchived = false;
		//this.timeStamp = 
	}
	
	public boolean getIsArchived(){
		return this.isArchived;
	}
	
	public void setIsArchived(boolean _flag){
		this.isArchived = _flag;
	}
	
	public AID getAgent(){
		return this.agent;
	}
	
	public int getType(){
		return this.type;
	}
	
	public Date getTimeStamp() {
		return this.timeStamp;
	}
	
	//---------> Abstract Methods
	public abstract String getDataSince(Date _start);
	public abstract String getDataBetween(Date _start, Date _end);
	public abstract String getDataBefore(Date _start);
	
}
