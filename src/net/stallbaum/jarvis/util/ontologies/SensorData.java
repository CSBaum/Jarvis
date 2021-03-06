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
public abstract class SensorData implements Concept, SecurityVocabulary, SensorVocabulary {

	Date timeStamp;
	boolean isArchived;
	int type;
	int id;
	AID agent;
		
	/**
	 * 
	 */
	public SensorData() {
		// TODO Auto-generated constructor stub
	}
	
	public SensorData(AID _agent, int _type, int _id){
		this.agent = _agent;
		this.type = _type;
		this.id = _id;
		this.isArchived = false;
		this.timeStamp = new Date(); 
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
	
	public int getId() {
		return this.id;
	}
	
	@Override
	public String toString() {
		return "SensorData [timeStamp=" + timeStamp + ", isArchived="
				+ isArchived + ", type=" + type + ", agent=" + agent + "]";
	}

	//---------> Abstract Methods
	public abstract String toArchive();
	public abstract String getDataSince(Date _start);
	public abstract String getDataBetween(Date _start, Date _end);
	public abstract String getDataBefore(Date _start);
	
}
