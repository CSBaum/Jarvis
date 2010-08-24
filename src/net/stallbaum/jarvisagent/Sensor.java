/**
 * 
 */
package net.stallbaum.jarvisagent;

/**
 * @author Sean Stallbaum
 * 
 * This class is used to hold the configuration information for
 * a specific sensor on a robot
 *
 */
public class Sensor {

	private String name;
	private int type;
	private double fov;
	private int cycle;
	
	private boolean enabled;
	
	public Sensor() {
		this("", 0, 0.0d, 0);
	}
	
	public Sensor(String _name, int _type){
		this(_name, _type, 0.0d, 0);
	}
	
	public Sensor(String _name, int _type, double _fov){
		this(_name, _type, _fov, 0);
	}
	
	public Sensor(String _name, int _type, double _fov, int _cycle){
		super(); 
		
		this.name = _name;
		this.type = _type;
		this.fov = _fov;
		this.cycle = _cycle;
		this.enabled = true;
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
	 * @return the type
	 */
	public int getType() {
		return type;
	}

	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}

	/**
	 * @return the fov
	 */
	public double getFov() {
		return fov;
	}

	/**
	 * @param fov the fov to set
	 */
	public void setFov(double fov) {
		this.fov = fov;
	}

	/**
	 * @return the cycle
	 */
	public int getCycle() {
		return cycle;
	}

	/**
	 * @param cycle the cycle to set
	 */
	public void setCycle(int cycle) {
		this.cycle = cycle;
	}

	/**
	 * @return the enabled
	 */
	public boolean isEnabled() {
		return enabled;
	}

	/**
	 * @param enabled the enabled to set
	 */
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
