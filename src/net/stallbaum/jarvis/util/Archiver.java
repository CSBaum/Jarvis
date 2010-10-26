/**
 * 
 */
package net.stallbaum.jarvis.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import static java.nio.file.StandardOpenOption.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Date;

import jade.core.AID;
import jade.util.Logger;
import net.stallbaum.jarvis.util.ontologies.SecurityVocabulary;
import net.stallbaum.jarvis.util.ontologies.SensorData;
import net.stallbaum.jarvis.util.ontologies.SensorVocabulary;
import net.stallbaum.jarvis.util.ontologies.TemperatureData;

/**
 * @author Administrator
 *
 *	The data structure that this archiver is maintaining looks like this:
 *
 *		[rootDirector]
 *			[Agent1]
 *				[SensorType1]
 *					[SensorId1]
 *						102212010-data.txt
 *				[SensorType2]
 *				[SensorType]
 *			[Agent2]
 *
 */
public class Archiver implements SecurityVocabulary,SensorVocabulary{

	Logger logger = jade.util.Logger.getMyLogger(this.getClass().getName());
	
	Connection conn;
	Statement stmt;
	ResultSet archiveRS;
	
	private String rootDir = "c:\\Java\\Jarvis\\data\\archives";
	
	/**
	 * 
	 */
	private Archiver() {
		// TODO Auto-generated constructor stub
	}
	
	public Archiver(Connection _conn) throws SQLException{
		this.conn = _conn;
		this.stmt = conn.createStatement();
		buildArchive();
	}
	
	/**
	 * 
	 * @param _root
	 * @param _conn
	 * @throws SQLException
	 */
	public Archiver(String _root, Connection _conn) throws SQLException{
		this.rootDir = _root;
		this.conn = _conn;
		this.stmt = conn.createStatement();
		buildArchive();
	}
	
	public boolean archiveData(SensorData data){
		boolean retStatus = false;
		boolean writeData = false;
		
		String fqnFile = "";
		String file = "";
		String sensorType = "";
		
		//----> Build Path
		sensorType = getSensorType(data.getType());
		if (sensorType != ""){
			String agentName = data.getAgent().getLocalName();
			logger.info("Agent Name: " + agentName);
			fqnFile = rootDir + "\\" + 
					  agentName + "\\" + 
					  sensorType + "\\" + 
					  data.getId(); 
		}
		
		//----> Build Filename
		
		//file = data.getTimeStamp() + "-" + sensorType + ".log";
		file = sensorType + ".log";
		
		fqnFile += "\\" + file;
		
		//----> SHouldn't have to format text, just use toString of the obj
		//----> Write data
		switch(data.getType()){
			case TEMPERATURE_SENSOR: TemperatureData temp = (TemperatureData)data;
									 writeData = writeData(fqnFile, file, temp.toString(), true);
									 break;
			case ULTRASONIC_SENSOR: break;
			default: break;
		}
		
		if(writeData){
			//----< Update ARchive table
		}
		
		return retStatus;
	}

	/**
	 * 
	 * @param agent
	 * @param sensorType
	 * @param sensorId
	 * @param startDate
	 * @param endData
	 * @return
	 */
	public SensorData[] retrieveData(AID agent, int sensorType, int sensorId, Date startDate, Date endData){
		SensorData[] data = {};
		
		return data;
	}
	
	//-----------------------------------------------
	//   Private Methods
	//-----------------------------------------------
	//

	private void buildArchive() throws SQLException{
		archiveRS = stmt.executeQuery("Select * from archives");
	}
	
	/**
	 * @param sensorType - value to be looked up
	 */
	public String getSensorType(int sensorType) {
		String sType = "Unknown";
		
		switch(sensorType){
			case TEMPERATURE_SENSOR: sType = "Temperature";
									 break;
			case ULTRASONIC_SENSOR: sType = "Ultrasonic";
									break;
			default: sType = "Unknown";
					 break;
		}
		
		return sType;
	}
	
	private boolean writeData(String filePath, String fileName, String _data, boolean append){
		boolean retStatus = true;
		OutputStream out = null;
		
		//-----> Chack for existing directroy
		Path path = Paths.get(filePath);
		if (path.notExists()){
			try {
				Files.createDirectories(path);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		Path file = Paths.get(filePath + "\\" + fileName);
		byte data[] = _data.getBytes();

		if (append) {
			try {
			    out = new BufferedOutputStream(file.newOutputStream(CREATE, APPEND));
			    out.write(data, 0, data.length);
			    out.flush();
		        out.close();
			} catch (IOException x) {
				logger.severe("Unable to archive data to file: " + fileName + " - " + x.getLocalizedMessage());
				retStatus = false;
			} 
		}
		else 
		{
			// overwrite file if it exists
			try {
			    out = new BufferedOutputStream(file.newOutputStream(CREATE));
			    out.write(data, 0, data.length);
			    out.flush();
		        out.close();
			} catch (IOException x) {
				logger.severe("Unable to archive data to file: " + fileName + " - " + x.getLocalizedMessage());
				retStatus = false;
			} 
		}
		return retStatus;
	}
	
}
