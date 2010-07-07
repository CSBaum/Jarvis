/**
 * 
 */
package net.stallbaum.jarvis.util.ontologies;

/**
 * @author sean
 *
 */
public interface SecurityVocabulary {

	//-------> Basic vocabulary
	   public static final int NEW_ACCOUNT = 1;
	   public static final int DEPOSIT = 2;
	   public static final int WITHDRAWAL = 3;
	   public static final int BALANCE = 4;
	   public static final int OPERATIONS = 5;
	   public static final int ADMIN = 6;
	   public static final int NOT_ENOUGH_MONEY = 10;
	   public static final int ACCOUNT_NOT_FOUND = 11;
	   public static final int ILLEGAL_OPERATION = 12;
	   
	   // ------> Agent Types
	   public static final int ROBOT_AGENT = 1;
	   public static final int NETWORK_AGENT = 2;
	   public static final int REMOTE_AGENT = 3;
	   
	   //-------> Agent States
	   public static final int AGENT_INITIALIZING = 0;
	   public static final int AGENT_STANDBY = 1;
	   public static final int AGENT_STAND_BY_SENSORS = 2;
	   public static final int AGENT_ACTIVE = 3;
	   public static final int AGENT_HALTING = 4;
	   
	   //-------> Agent Operations
	   public static final int INITIALIZE = 1;
	   public static final int SHUTDOWN = 2;
	   public static final int STANDBY = 3;
	   public static final int STANDBY_WITH_SENSORS = 4;
	   public static final int ACTIVATE = 5;
	   public static final int REQUEST_DATA = 6;
	   
	   //-------> System States
	   public static final int SYSTEM_INITIALIZING = 0;
	   public static final int SYSTEM_STANDBY = 1;
	   public static final int SYSTEM_SECURITY_NETONLY = 2;
	   public static final int SYSTEM_SECURITY_ROBOTONLY = 3;
	   public static final int SYSTEM_SECURITY_ALL = 4;
	   public static final int SYSTEM_HALTING = 5;
	   public static final int SYSTEM_ALARM = 10;
	   
	   //-------> System Messages
	   public static int SYSTEM_INIT = 0;
	   public static String SYSTEM_INIT_MSG = "System-Init";
	   public static int SYSTEM_HALT = 1;
	   public static String SYSTEM_HALT_MSG = "System-Halt";
	   public static int SYSTEM_SET_SECURITY_LEVEL = 2;
	   public static String SYSTEM_SET_SECURITY_LEVEL_MSG = "Set-Security-Level";
	   public static int SYSTEM_RESET = 3;
	   public static String SYSTEM_RESET_MSG = "System-Reset";
	   public static int SYSTEM_PAUSE = 4;
	   public static int SYSTEM_ADD_AGENT = 10;
	   public static int SYSTEM_REMOVE_AGENT = 11;
	   public static int SYSTEM_ACTIVATE_AGENT = 15;
	   public static int SYSTEM_DEACTIVEATE_AGENT = 16;
	   public static int SYSTEM_QUERY_AGENT = 17;
	   public static int SYSTEM_AGENT_INITIALIZED = 20;
	   public static int SYSTEM_AGENT_INITIALIZE_FAILURE = 21;
	   
	   public static int SECURITY_LEVEL_OFF = 100;
	   public static int SECURITY_LEVEL_NETWORK_AGENTS_ONLY = 101;
	   public static int SECURITY_LEVEL_ROBOT_AGENTS_ONLY = 102;
	   public static int SECURITY_LEVEL_ALL_ON = 103;
	   
	   public static int QUERY_TYPE_STATUS = 0;
	   public static int QUERY_TYPE_LOG = 1;
	   public static int QUERY_TYPE_EVENT_DATA = 2;
	   
	   //------> Data Types
	   public static int MAP_DATA = 1;
	   public static int SONAR_DATA = 2;
	   
	   //-------> Error Codes
	   public static int INVALID_MSGTYPE = 100;
	   public static String INVALID_MSGTYPE_MSG = "Invalid Message Performative";
	   public static int INVALID_MSG_CONTENT_HEADER = 101;
	   public static String INVALID_MSG_CONTENT_HEADER_MSG = "Invalid Message Header";
	   public static int INVALID_SYSTEM_MESSAGE = 102;
	   public static String INVALID_SYSTEM_MESSAGE_MSG = "Invalid System Message";
	   public static int UNSUPPORTED_SYS_MSG = 103;
	   public static String UNSUPPORTED_SYS_MSG_MSG = "This System Message is not currently supported.";
	   
	   public static int UNREADABLE_CONTENT = 200;
	   public static String UNREADABLE_CONTENT_MSG = "Unable to read content object";
	   
	   public static int INVALID_AGENT_STATE = 300;
	   public static String INVALID_AGENT_STATE_MSG = "- has an invalid agent state - ";
	   
	   public static int INVALID_MRO_TYPE = 400;
	   public static String INVALID_MRO_TYPE_MSG = "Invalid MakeRobotOperation request type recieved";

	   //-------> Ontology vocabulary
	   public static final String ROBOT = "Robot";
	   public static final String ROBOT_ID = "id";
	   public static final String ROBOT_NAME = "name";
	   public static final String ROBOT_AUDIO = "has_audio";
	   public static final String ROBOT_LASER = "has_laster";
	   
	   public static final String ROBOT_OPERATION = "Operation";
	   public static final String ROBOT_OPERATION_TYPE = "type";
	   public static final String ROBOT_OPERATION_AMOUNT = "amount";
	   public static final String ROBOT_OPERATION_BALANCE = "balance";
	   public static final String ROBOT_OPERATION_ACCOUNTID = "accountId";
	   public static final String ROBOT_OPERATION_DATE = "date";

	   public static final String OPERATION = "Operation";
	   public static final String OPERATION_TYPE = "type";
	   public static final String OPERATION_AMOUNT = "amount";
	   public static final String OPERATION_BALANCE = "balance";
	   public static final String OPERATION_ACCOUNTID = "accountId";
	   public static final String OPERATION_DATE = "date";

	   public static final String MAKE_ROBOT_OPERATION = "MakeRobotOperation";
	   public static final String MAKE_ROBOT_OPERATION_TYPE = "type";
	   public static final String MAKE_ROBOT_OPERATION_AMOUNT = "amount";
	   public static final String MAKE_ROBOT_OPERATION_ACCOUNTID = "accountId";

	   public static final String INFORMATION = "Information";
	   public static final String INFORMATION_TYPE = "type";
	   public static final String INFORMATION_ACCOUNTID = "accountId";

	   public static final String PROBLEM = "Problem";
	   public static final String PROBLEM_NUM = "num";
	   public static final String PROBLEM_MSG="msg";

}
