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
public class Alert implements SecurityVocabulary, Concept {

	AID sendingAgent;
	int	senorType;
	SensorData data;
	
	String alertID;
	
	/**
	 * 
	 */
	public Alert() {
		// TODO Auto-generated constructor stub
		generateAlertId();
	}
	
	/**
	 * 
	 * @param _agent
	 * @param _sensorType
	 */
	public Alert(AID _agent, int _sensorType){
		this.sendingAgent = _agent;
		this.senorType = _sensorType;
		generateAlertId();
	}
	
	public String getAlertId() {
		return this.alertID;
	}
	
	public SensorData getData() {
		return this.data;
	}
	
	public void setData(SensorData _data) {
		this.data = _data;
	}
	
	//-----> Private MEmbers
	private void generateAlertId() {
		this.alertID = "";
		
	}

}
