/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

import jade.content.Concept;

/**
 * @author sean
 *
 */
public class Problem implements Concept {
	private String agentName = "";
	private int num;
	private String msg;
	
	public int getNum() {
		return this.num;
	}
	
	public String getMsg() {
		return this.msg;
	}
	
	public void setNum(int code){
		this.num = code;
	}
	
	public void setMsg(String Errormsg){
		this.msg = Errormsg;
	}
	
	public void setAgentName(String _agent){
		this.agentName = _agent;
	}
	
	public String getAgentName() {
		return this.agentName;
	}
	
	public Problem(){
		
	}
	
	public Problem(int _num, String _msg) {
		this.msg = _msg;
		this.num = _num;
	}
	
	public Problem(int _num, String _msg, String _agent){
		this.agentName = _agent;
		this.msg = _msg;
		this.num = _num;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Problem [Error Code = " + num + ", Error Message =" + msg + ", Agent = " + agentName + "]";
	}
}
