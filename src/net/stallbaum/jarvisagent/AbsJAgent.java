/**
 * 
 */
package net.stallbaum.jarvisagent;

import jade.core.AID;
import jade.core.Agent;
import net.stallbaum.jarvis.util.ontologies.AlertConfirmation;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;

/**
 * @author Administrator
 *
 */
public abstract class AbsJAgent extends Agent implements SecurityVocabulary {

	abstract public int getState();
	abstract public String getConversationId();
	abstract public String getAlertId();
	abstract public AID getReceiver();
	abstract public AID getSender();
	abstract public void setSender(AID _sender);
	
	//------> Alert based methods
	public abstract void setAlertStataus(boolean _flag);
	public abstract boolean getAlertStatus();
	public abstract void setAlertConfirmation(AlertConfirmation _confirm);
	public abstract AlertConfirmation getAlertConfirmation();
}
