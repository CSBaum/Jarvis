package net.stallbaum.jarvis.util.ontologies;

import jade.content.Concept;

public class Robot implements Concept, SecurityVocabulary {

	private String name;
	private boolean hasAudio;
	private boolean hasSonar;
	private String cfgFile;
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
	 * @param cfgFile the cfgFile to set
	 */
	public void setCfgFile(String cfgFile) {
		this.cfgFile = cfgFile;
	}
	
}
