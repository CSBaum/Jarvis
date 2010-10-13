/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.core.AID;

import java.util.Date;

/**
 * @author Administrator
 *
 */
public class TemperatureData extends SensorData {

	Float temp;
	
	/**
	 * 
	 */
	public TemperatureData() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param _agent
	 */
	public TemperatureData(AID _agent, int _type) {
		super(_agent, _type);
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvis.util.ontologies.SensorData#getDataSince(java.util.Date)
	 */
	@Override
	public String getDataSince(Date _start) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvis.util.ontologies.SensorData#getDataBetween(java.util.Date, java.util.Date)
	 */
	@Override
	public String getDataBetween(Date _start, Date _end) {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see net.stallbaum.jarvis.util.ontologies.SensorData#getDataBefore(java.util.Date)
	 */
	@Override
	public String getDataBefore(Date _start) {
		// TODO Auto-generated method stub
		return null;
	}

	public void setTemp(Float _temp){
		this.temp = _temp;
	}
	
	public Float getTemp(){
		return this.temp;
	}

	@Override
	public String toString() {
		return "TemperatureData [temp=" + temp + ", timeStamp=" + timeStamp
				+ ", isArchived=" + isArchived + ", type=" + type + ", agent="
				+ agent + "]";
	}
}
