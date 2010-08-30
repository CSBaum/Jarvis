/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.Concept;

/**
 * @author Sean Stallbaum
 * 
 * This class is used to hold the configuration information for
 * a specific sensor on a robot
 *
 */
public class Sensor implements Concept {
	
	private static int TEMPERATURE = 1;
	private static int IR_PROXIMITY = 2;
	private static int MICROPHONE = 3;
	private static int ULTRASONIC = 4;
	
	private String name;
	private Integer type;
	private String description;
	private Double fov;
	private String fovScale;
	private Integer refreshRate;
	private String refreshScale;
	private Double resolution;
	private String resolutionScale;
	private Float minRange;
	private Float maxRange;
	private String rangeScale;

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
	
	public Sensor(String _name, int _type, double _fov, int _refresh){
		super(); 
		
		this.name = _name;
		this.type = _type;
		this.fov = _fov;
		this.refreshRate = _refresh;
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
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
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
		return refreshRate;
	}

	/**
	 * @param cycle the cycle to set
	 */
	public void setCycle(int refresh) {
		this.refreshRate = refresh;
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
	
	/**
	 * @return the resolution
	 */
	public double getResolution() {
		return resolution;
	}

	/**
	 * @param resolution the resolution to set
	 */
	public void setResolution(double resolution) {
		this.resolution = resolution;
	}

	/**
	 * @return the minRange
	 */
	public float getMinRange() {
		return minRange;
	}

	/**
	 * @param minRange the minRange to set
	 */
	public void setMinRange(float minRange) {
		this.minRange = minRange;
	}

	/**
	 * @return the maxRange
	 */
	public float getMaxRange() {
		return maxRange;
	}

	/**
	 * @param maxRange the maxRange to set
	 */
	public void setMaxRange(float maxRange) {
		this.maxRange = maxRange;
	}

	/**
	 * @return the fovScale
	 */
	public String getFovScale() {
		return fovScale;
	}

	/**
	 * @param fovScale the fovScale to set
	 */
	public void setFovScale(String fovScale) {
		this.fovScale = fovScale;
	}

	/**
	 * @return the refreshScale
	 */
	public String getRefreshScale() {
		return refreshScale;
	}

	/**
	 * @param refreshScale the refreshScale to set
	 */
	public void setRefreshScale(String refreshScale) {
		this.refreshScale = refreshScale;
	}

	/**
	 * @return the resolutionScale
	 */
	public String getResolutionScale() {
		return resolutionScale;
	}

	/**
	 * @param resolutionScale the resolutionScale to set
	 */
	public void setResolutionScale(String resolutionScale) {
		this.resolutionScale = resolutionScale;
	}

	/**
	 * @return the rangeScale
	 */
	public String getRangeScale() {
		return rangeScale;
	}

	/**
	 * @param rangeScale the rangeScale to set
	 */
	public void setRangeScale(String rangeScale) {
		this.rangeScale = rangeScale;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Name=" + name + ",\ntype=" + type + ",\ndescription="
				+ description + ",\nfov=" + fov + ",\nfovScale=" + fovScale
				+ ",\nrefreshRate=" + refreshRate + ",\nrefreshScale="
				+ refreshScale + ",\nresolution=" + resolution
				+ ",\nresolutionScale=" + resolutionScale + ",\nminRange="
				+ minRange + ",\nmaxRange=" + maxRange + ",\nrangeScale="
				+ rangeScale + ",\nenabled=" + enabled;
	}


}
