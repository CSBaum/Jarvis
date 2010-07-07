/**
 * 
 */
package net.stallbaum.jarvisagent;

import java.text.FieldPosition;
import java.text.NumberFormat;

import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvisagent.javaclient3.*;
import net.stallbaum.jarvisagent.javaclient3.structures.PlayerConstants;
import net.stallbaum.jarvisagent.javaclient3.structures.PlayerPose;
import net.stallbaum.jarvisagent.javaclient3.structures.blobfinder.PlayerBlobfinderBlob;
import net.stallbaum.jarvisagent.javaclient3.structures.blobfinder.PlayerBlobfinderData;
import net.stallbaum.jarvisagent.javaclient3.structures.fiducial.PlayerFiducialData;
import net.stallbaum.jarvisagent.javaclient3.structures.fiducial.PlayerFiducialGeom;
import net.stallbaum.jarvisagent.javaclient3.structures.fiducial.PlayerFiducialItem;
import net.stallbaum.jarvisagent.javaclient3.structures.gripper.PlayerGripperGeom;
import net.stallbaum.jarvisagent.javaclient3.structures.laser.PlayerLaserConfig;
import net.stallbaum.jarvisagent.javaclient3.structures.laser.PlayerLaserGeom;
import net.stallbaum.jarvisagent.javaclient3.structures.localize.PlayerLocalizeSetPose;
import net.stallbaum.jarvisagent.javaclient3.structures.planner.PlayerPlannerData;
import net.stallbaum.jarvisagent.javaclient3.structures.position2d.PlayerPosition2dData;
import net.stallbaum.jarvisagent.javaclient3.structures.position2d.PlayerPosition2dGeom;
import net.stallbaum.jarvisagent.javaclient3.structures.ptz.PlayerPtzCmd;
import net.stallbaum.jarvisagent.javaclient3.structures.ptz.PlayerPtzData;
import net.stallbaum.jarvisagent.javaclient3.structures.rfid.PlayerRfidData;
import net.stallbaum.jarvisagent.javaclient3.structures.rfid.PlayerRfidTag;
import net.stallbaum.jarvisagent.javaclient3.structures.simulation.PlayerSimulationPose2dReq;
import net.stallbaum.jarvisagent.javaclient3.structures.sonar.PlayerSonarGeom;
import jade.core.Agent;

/**
 * @author Sean
 *
 */
public class PlayerAgent extends Agent implements SecurityVocabulary {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1233712675363055824L;
	static NumberFormat fmt = NumberFormat.getInstance ();
	
	protected int agentState = AGENT_INITIALIZING;
	protected int previousAgentState = AGENT_INITIALIZING;
	
	boolean isInitialized = false;
	
	// define minimum/maximum allowed values for the SONAR sensors
	static float SONAR_MIN_VALUE = 0.2f;
	static float SONAR_MAX_VALUE = 5.0f;
	// define the threshold (any value under this is considered an obstacle)
	static float SONAR_THRESHOLD = 0.5f;
	// define the wheel diameter (~example for a Pioneer 3 robot)
	static float WHEEL_DIAMETER  = 24.0f;
	
	// define the default rotational speed in rad/s
	static float DEF_YAW_SPEED   = 0.50f;
	
	// array to hold the SONAR sensor values
	static float[] sonarValues;
	// translational/rotational speed
	static float xspeed, yawspeed;
	static float leftSide, rightSide;


	/**
	 * 
	 */
	public PlayerAgent() {
		// TODO Auto-generated constructor stub
	}

	protected void setup() {
		System.out.println("Starting PlayerAgent: " + getAID().getName());

		// We need to setup behavior to listen for messages from controlling 
		//    JarvisAgent
		AgentPlayerCommBehavior apcb = new AgentPlayerCommBehavior(this, 250);
		addBehaviour(apcb);

		// we need to connect to the robot's listening socket
		//    and send a 'test' message
		//    if no response, sleep until we get connection (max # of tries??)
		// Once we are good, launch behavior to retrieve data and send commands
		//System.setProperty ("PlayerClient.debug", "true");
		PlayerClient        robot = null;
		Position2DInterface posi  = null;
		SonarInterface      soni  = null;
		
		try {
			// Connect to the Player server and request access to Position and Sonar
			robot  = new PlayerClient ("192.168.20.107", 6665);
			posi = robot.requestInterfacePosition2D (0, PlayerConstants.PLAYER_OPEN_MODE);
			soni = robot.requestInterfaceSonar      (0, PlayerConstants.PLAYER_OPEN_MODE);
		} catch (PlayerException e) {
			System.err.println ("SpaceWandererExample: > Error connecting to Player: ");
			System.err.println ("    [ " + e.toString() + " ]");
			System.exit (1);
		}
						
		robot.runThreaded (-1, -1);
		
		int inx = 0;
		
		while (inx < 10) {
			// get all SONAR values
			while (!soni.isDataReady ());
			System.out.println(getLocalName() + ":Numbner of sonars present: " + soni.getData().getRanges_count());
			sonarValues = soni.getData ().getRanges ();
			
			// ignore erroneous readings/keep interval [SONAR_MIN_VALUE; SONAR_MAX_VALUE]
			for (int i = 0; i < soni.getData ().getRanges_count (); i++)
				if (sonarValues[i] < SONAR_MIN_VALUE)
					sonarValues[i] = SONAR_MIN_VALUE;
				else
					if (sonarValues[i] > SONAR_MAX_VALUE)
						sonarValues[i] = SONAR_MAX_VALUE;
			System.out.println (decodeSonars (soni));
			
			// read and average the sonar values on the left and right side
			leftSide  = (sonarValues [0] + sonarValues [1]) / 2; // + sonarValues [3]) / 3;
			rightSide = (sonarValues [2] + sonarValues [3]) / 2; // + sonarValues [4]) / 3;
			
			// use a divider for the velocities depending on your desired speed (mm/s, m/s, etc) 
			leftSide = leftSide / 10;
			rightSide = rightSide / 10;
			
			// calculate the translational and rotational velocities
			xspeed = (leftSide + rightSide) / 2;
			yawspeed = (float)((leftSide - rightSide) * (180 / Math.PI) / WHEEL_DIAMETER);
			
			try { Thread.sleep (100); } catch (Exception e) { }
			
			// if the path is clear on the left OR on the right, use {x,yaw}speed
			if ((
					(sonarValues [0] > SONAR_THRESHOLD) && 
					(sonarValues [1] > SONAR_THRESHOLD) )  ||
					(
							(sonarValues [2] > SONAR_THRESHOLD) && 
							(sonarValues [3] > SONAR_THRESHOLD)
					)){
				System.out.println("The path is clear ....");
				System.out.println("Setting xspeed to: " + xspeed + " and yawspeed to: " + yawspeed);
				posi.setSpeed (xspeed, yawspeed);
				
			}
			else
			{
				// if we have obstacles in front (both left and right), rotate
				System.out.println("The path is NOT clear ....");
				
				if (sonarValues [0] < sonarValues [3]){
					posi.setSpeed (0, -DEF_YAW_SPEED);	
					System.out.println("Setting xspeed to: 0 and yawspeed to: -" + DEF_YAW_SPEED);
				}
				else {
					posi.setSpeed (0, DEF_YAW_SPEED);
					System.out.println("Setting xspeed to: 0 and yawspeed to: " + DEF_YAW_SPEED);
				}
			}
			inx++;
		}
		robot.close();

	}
	
	// Misc routines for nice alignment of text on screen
	static String align (NumberFormat fmt, float n, int sp) {
		StringBuffer buf = new StringBuffer ();
		FieldPosition fpos = new FieldPosition (NumberFormat.INTEGER_FIELD);
		fmt.format (n, buf, fpos);
		for (int i = 0; i < sp - fpos.getEndIndex (); ++i)
			buf.insert (0, ' ');
		return buf.toString ();
	}
	
	public static String decodeSonars (SonarInterface soni) {
		String out = "\nSonar vars: \n";
		for (int i = 0; i < soni.getData ().getRanges_count (); i++) {
			out += " [" + align (fmt, i+1, 2) + "] = " + 
			align(fmt, soni.getData ().getRanges ()[i], 5);
			if (((i+1) % 8) == 0)
				out += "\n";
		}
		return out;
	}
	
	static String byteArrayToHexString (byte in[]) {
		byte ch = 0x00;
		int i = 0;
		if (in == null || in.length <= 0)
			return null;

		String pseudo[] = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"A", "B", "C", "D", "E", "F" };

		StringBuffer out = new StringBuffer (in.length * 2);
		while (i < in.length) {
			ch = (byte) (in[i] & 0xF0); // Strip off high nibble
			ch = (byte) (ch >>> 4);
			// shift the bits down
			ch = (byte) (ch & 0x0F);
			// must do this is high order bit is on!

			out.append (pseudo[(int) ch]); // convert the nibble to a char
			ch = (byte) (in[i] & 0x0F);    // Strip off low nibble
			out.append (pseudo[(int) ch]); // convert the nibble to a char
			i++;
		}
		String rslt = new String (out);
		return rslt;
	}    
}
