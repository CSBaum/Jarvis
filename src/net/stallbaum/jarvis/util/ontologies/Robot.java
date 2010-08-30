package net.stallbaum.jarvis.util.ontologies;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import jade.content.Concept;

public class Robot implements Concept, SecurityVocabulary {

	private static int FRONT_CENTER = 1;
	private static int FRONT_LEFT = 2;
	private static int FRONT_RIGHT = 3;
	
	private static int LEFT_FRONT = 4;
	private static int LEFT_MIDDLE = 5;
	private static int LEFT_BACK = 6;
	
	private static int BACK_CENTER = 7;
	private static int BACK_LEFT = 8;
	private static int BACK_RIGHT = 9;
	
	private static int RIGHT_FRONT = 10;
	private static int RIGHT_MIDDLE = 11;
	private static int RIGHT_BACK = 12;
	
	private String name;
	private boolean hasAudio;
	private boolean hasSonar;
	
	private boolean hasFrontSensors = false;
	private boolean hasLeftSensors = false;
	private boolean hasRightSensors = false;
	private boolean hasBackSensors = false;
	
	private int tireCount = 0;
	private Tire tireType = null;
	private Motor motorType = null;
	
	private String cfgFile;
	
	private Hashtable <Sensor, Integer> sensors;
	
	/**
	 * 
	 */
	public Robot() {
		super();
		sensors = new Hashtable<Sensor, Integer>();
	}
	
	/**
	 * @param name
	 */
	public Robot(String name) {
		super();
		this.name = name;
		sensors = new Hashtable<Sensor, Integer>();
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
	 * @return the hasAudio
	 */
	public boolean isHasAudio() {
		return hasAudio;
	}
	/**
	 * @param hasAudio the hasAudio to set
	 */
	public void setHasAudio(boolean hasAudio) {
		this.hasAudio = hasAudio;
	}
	/**
	 * @return the hasSonar
	 */
	public boolean isHasSonar() {
		return hasSonar;
	}
	/**
	 * @param hasSonar the hasSonar to set
	 */
	public void setHasSonar(boolean hasSonar) {
		this.hasSonar = hasSonar;
	}
	/**
	 * @return the cfgFile
	 */
	public String getCfgFile() {
		return cfgFile;
	}
		
	/**
	 * @return the hasFrontSensors
	 */
	public boolean isHasFrontSensors() {
		return hasFrontSensors;
	}
	
	/**
	 * @return the hasLeftSensors
	 */
	public boolean isHasLeftSensors() {
		return hasLeftSensors;
	}
	
	/**
	 * @return the hasRightSensors
	 */
	public boolean isHasRightSensors() {
		return hasRightSensors;
	}
	
	/**
	 * @return the hasBackSensors
	 */
	public boolean isHasBackSensors() {
		return hasBackSensors;
	}
	
	/**
	 * @return the tireCount
	 */
	public int getTireCount() {
		return tireCount;
	}
	/**
	 * @param tireCount the tireCount to set
	 */
	public void setTireCount(int tireCount) {
		this.tireCount = tireCount;
	}
	/**
	 * @return the tireType
	 */
	public Tire getTireType() {
		return tireType;
	}
	/**
	 * @param tireType the tireType to set
	 */
	public void setTireType(Tire tireType) {
		this.tireType = tireType;
	}
	/**
	 * @return the motorType
	 */
	public Motor getMotorType() {
		return motorType;
	}
	/**
	 * @param motorType the motorType to set
	 */
	public void setMotorType(Motor motorType) {
		this.motorType = motorType;
	}
	
	/**
	 * @param cfgFile the cfgFile to set
	 */
	public void setCfgFile(String cfgFile) {
		this.cfgFile = cfgFile;
	}
	
	public Vector<Sensor> getFrontSensors() {
		Vector<Sensor> retVector = new Vector<Sensor>();
		
		Set<Sensor> keys = sensors.keySet();
		Iterator itr = keys.iterator();
		while (itr.hasNext()) {
			Map.Entry entry = (Entry)itr.next();
			int value = (Integer) entry.getValue();
			if (value < 4) {
				System.out.println("Adding a front sensor.");
				retVector.add((Sensor)entry);
			}
		}
		
		return retVector;
	}
	
	/**
	 * 
	 * @param _sensor
	 */
	public void setFrontCenterSensor(Sensor _sensor) {
		hasFrontSensors = true;
		this.sensors.put(_sensor, FRONT_CENTER);
	}
	
	/**
	 * 
	 * @param _sensor
	 */
	public void setFrontLeftSensor(Sensor _sensor) {
		hasFrontSensors = true;
		hasLeftSensors = true;
		this.sensors.put(_sensor, FRONT_LEFT);
	}
	
	/**
	 * 
	 * @param _sensor
	 */
	public void setFrontRightSensor(Sensor _sensor) {
		hasFrontSensors = true;
		hasRightSensors = true;
		this.sensors.put(_sensor, FRONT_RIGHT);
	}
	
	/**
	 * 
	 * @param _sensor
	 */
	public void setBackCenterSensor(Sensor _sensor) {
		hasBackSensors = true;
		this.sensors.put(_sensor, BACK_CENTER);
	}
	
	/**
	 * 
	 * @param _sensor
	 */
	public void setBackLeftSensor(Sensor _sensor) {
		hasBackSensors = true;
		hasLeftSensors = true;
		this.sensors.put(_sensor, BACK_LEFT);
	}
	
	/**
	 * 
	 * @param _sensor
	 */
	public void setBackRightSensor(Sensor _sensor) {
		hasBackSensors = true;
		hasRightSensors = true;
		this.sensors.put(_sensor, BACK_RIGHT);
	}
	
	public void setLeftFrontSensor(Sensor _sensor) {
		hasFrontSensors = true;
		hasLeftSensors = true;
		this.sensors.put(_sensor, LEFT_FRONT);
	}
	
	public void setLeftMiddleSensor(Sensor _sensor) {
		hasLeftSensors = true;
		this.sensors.put(_sensor, LEFT_MIDDLE);
	}
	
	public void setLeftBackSensor(Sensor _sensor) {
		hasBackSensors = true;
		hasLeftSensors = true;
		this.sensors.put(_sensor, LEFT_BACK);
	}
	
	public void setRightFrontSensor(Sensor _sensor) {
		hasFrontSensors = true;
		hasRightSensors = true;
		this.sensors.put(_sensor, RIGHT_FRONT);
	}
	
	public void setRightMiddleSensor(Sensor _sensor) {
		hasRightSensors = true;
		this.sensors.put(_sensor, RIGHT_MIDDLE);
	}
	
	public void setRightBackSensor(Sensor _sensor) {
		hasBackSensors = true;
		hasRightSensors = true;
		this.sensors.put(_sensor, RIGHT_BACK);
	}
	
	/**
	 * 
	 * @return
	 */
	public int getSensorCount() {
		return sensors.size();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "name=" + name + ",\nhasAudio=" + hasAudio + ",\nhasSonar="
				+ hasSonar + ",\nhasFrontSensors=" + hasFrontSensors
				+ ",\nhasLeftSensors=" + hasLeftSensors + ",\nhasRightSensors="
				+ hasRightSensors + ",\nhasBackSensors=" + hasBackSensors
				+ ",\ntireCount=" + tireCount + ",\ntireType=" + tireType
				+ ",\nmotorType=" + motorType + ",\ncfgFile=" + cfgFile
				+ ",\nsensors=" + sensors.size();
	}
	
	
}
