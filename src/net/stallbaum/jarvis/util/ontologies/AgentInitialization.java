/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import java.sql.ResultSet;

import jade.content.Concept;

/**
 * @author sstallbaum
 *
 */
public class AgentInitialization implements Concept, SecurityVocabulary {

	private int agentType;
	
	private Robot robot = null;
	private NetworkAgent network = null;
	
	private Location loc = null;
	
	private ResultSet rs = null;
	
	private String alertId = ""; 
	
	/**
	 * Default Constructor -- values are added via calling Class
	 */
	public AgentInitialization() {
		
	}
	
	/**
	 * Alternative creation option where we pass the result set and we populate
	 * the initialization information
	 */
	public AgentInitialization(ResultSet _rs) {
		rs = _rs;
	}

	/**
	 * @return the agentType
	 */
	public int getAgentType() {
		return agentType;
	}

	/**
	 * @param agentType the agentType to set
	 */
	public void setAgentType(int agentType) {
		this.agentType = agentType;
	}

	/**
	 * @return the robot
	 */
	public Robot getRobot() {
		return robot;
	}

	/**
	 * @param robot the robot to set
	 */
	public void setRobot(Robot robot) {
		this.robot = robot;
	}

	/**
	 * @return the network
	 */
	public NetworkAgent getNetwork() {
		return network;
	}

	/**
	 * @param network the network to set
	 */
	public void setNetwork(NetworkAgent network) {
		this.network = network;
	}

	/**
	 * @return the loc
	 */
	public Location getLoc() {
		return loc;
	}

	/**
	 * @param loc the loc to set
	 */
	public void setLoc(Location loc) {
		this.loc = loc;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getAlertId(){
		return alertId;
	}

	/**
	 * 
	 * @param alert
	 */
	public void setAlertId(String alert) {
		this.alertId = alert;
	}
}
