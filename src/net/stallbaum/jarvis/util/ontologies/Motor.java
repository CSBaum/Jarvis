/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.Concept;

/**
 * @author sean
 *
 */
public class Motor implements Concept {

	private String name;
	private String gearRatio;
	private Integer rpm;
	private String torque;
	
	/**
	 * 
	 */
	public Motor() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the gearRatio
	 */
	public String getGearRatio() {
		return gearRatio;
	}

	/**
	 * @param gearRatio the gearRatio to set
	 */
	public void setGearRatio(String gearRatio) {
		this.gearRatio = gearRatio;
	}

	/**
	 * @return the rpm
	 */
	public Integer getRpm() {
		return rpm;
	}

	/**
	 * @param rpm the rpm to set
	 */
	public void setRpm(Integer rpm) {
		this.rpm = rpm;
	}

	/**
	 * @return the torque
	 */
	public String getTorque() {
		return torque;
	}

	/**
	 * @param torque the torque to set
	 */
	public void setTorque(String torque) {
		this.torque = torque;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Motor [name=" + name + ", gearRatio=" + gearRatio + ", rpm="
				+ rpm + ", torque=" + torque + "]";
	}

}
