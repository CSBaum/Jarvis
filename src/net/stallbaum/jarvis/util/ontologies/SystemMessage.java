/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.Concept;

/**
 * @author sstallbaum
 *
 */
public class SystemMessage implements Concept, SecurityVocabulary {

	private int msgID;
	private int msgSubId;
	
	private Object obj;
	private int objType;
	
	/**
	 * 
	 */
	public SystemMessage() {
		// TODO Auto-generated constructor stub
	}
	
	public SystemMessage(int _msgId){
		this.msgID = _msgId;
	}
	
	public SystemMessage(int _msgId, int _msgSubId){
		this.msgID = _msgId;
		this.msgSubId = _msgSubId;
	}

	/**
	 * @return the msgID
	 */
	public int getMsgID() {
		return msgID;
	}

	/**
	 * @param msgID the msgID to set
	 */
	public void setMsgID(int msgID) {
		this.msgID = msgID;
	}

	/**
	 * @return the msgSubId
	 */
	public int getMsgSubId() {
		return msgSubId;
	}

	/**
	 * @param msgSubId the msgSubId to set
	 */
	public void setMsgSubId(int msgSubId) {
		this.msgSubId = msgSubId;
	}

	/**
	 * @return the obj
	 */
	public Object getObj() {
		return obj;
	}

	/**
	 * @param obj the obj to set
	 */
	public void setObj(Object obj) {
		this.obj = obj;
	}

	/**
	 * @return the objType
	 */
	public int getObjType() {
		return objType;
	}

	/**
	 * @param objType the objType to set
	 */
	public void setObjType(int objType) {
		this.objType = objType;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "SystemMessage [msgID=" + msgID + ", msgSubId=" + msgSubId
				+ ", " + (obj != null ? "obj=" + obj + ", " : "") + "objType="
				+ objType + "]";
	}

}
