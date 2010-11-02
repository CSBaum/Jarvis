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
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.CharacterIterator;
import java.text.SimpleDateFormat;
import java.text.StringCharacterIterator;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

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
		
		PreparedStatement stmt;
		ResultSet rs;
		
		String fqnFile = "";
		String file = "";
		String sensorType = "";
		String agentName = "";
		
		//----> Build Path
		sensorType = getSensorType(data.getType());
		if (sensorType != ""){
			agentName = data.getAgent().getLocalName();
			fqnFile = rootDir + "\\" + 
					  agentName + "\\" + 
					  sensorType + "\\" + 
					  data.getId(); 
		}
		
		//----> Build Filename
		Date tStamp = data.getTimeStamp();
		SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
		file = formatter.format(tStamp) + "-" + sensorType + ".log";
		
		//----> SHouldn't have to format text, just use toString of the obj
		//----> Write data
		String dataString = "";
		
		switch(data.getType()){
			case TEMPERATURE_SENSOR: TemperatureData temp = (TemperatureData)data;
									 dataString = temp.toString();
									 writeData = writeData(fqnFile, file, dataString, true);
									 break;
			case ULTRASONIC_SENSOR: break;
			default: break;
		}
		
		
		if(writeData){
			//----< Update ARchive table
			try {
				String sql = "INSERT INTO archives (agent, sensorType, sensorId, file, timestamp) " +
							 "VALUES(?,?,?,?,?)";
				stmt = conn.prepareStatement(sql);
				
				//----> Statement configuration
				stmt.setEscapeProcessing(true);
				stmt.setString(1, agentName);
				stmt.setInt(2, data.getType());
				stmt.setInt(3, data.getId());
				stmt.setString(4, data.toArchive());
				stmt.setDate(5, new java.sql.Date(data.getTimeStamp().getTime()));
				
				//logger.info("Using Inser SQL --> " + sql);

				int ret = stmt.executeUpdate();
				logger.info("SQL Insert returned: " + ret);
				retStatus = true;
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				logger.warning("Unable to insert archive data: " + e.getLocalizedMessage());
				logger.warning("SQL State: " + e.getSQLState().toString());
				logger.warning("Insert Stack Trace: " + e.getStackTrace().toString());
			}	
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
	public Vector<SensorData> retrieveData(AID agent, int sensorType, int sensorId, Date startDate, Date endDate){
		Vector<SensorData> data = new Vector<SensorData>();
		
		PreparedStatement stmt = null;
		ResultSet rs = null;
		String sql = "SELECT * FROM archives WHERE agent = ? AND sensorType = ? AND sensorId = ? AND timestamp >= ? AND timestamp <= ?";
		
		if (endDate == null){
			endDate = new Date();
		}
		
		try {
			stmt = conn.prepareStatement(sql);
			stmt.setEscapeProcessing(true);
			stmt.setString(1, agent.getLocalName());
			stmt.setInt(2, sensorType);
			stmt.setInt(3, sensorId);
			stmt.setDate(4, new java.sql.Date(startDate.getTime()));
			stmt.setDate(5, new java.sql.Date(endDate.getTime()));
			rs = stmt.executeQuery();
			int rowCount;
			rs.last();
			rowCount = rs.getRow();
			rs.beforeFirst();
			logger.info("Found " + rowCount + " archived records.");

			while(rs.next()){
				// Based on type of sensor create the appropriate object
				//      - add data to object from restultSet
				//      - add obj to array
				String agentName = rs.getString("agent");
				int sensType = rs.getInt("sensorType");
				int sensId = rs.getInt("sensorId");
				String contents = rs.getString("file");
				java.sql.Date timestamp = rs.getDate("timestamp");
								
				switch(sensType){
					case 1: TemperatureData sData = new TemperatureData(new AID(agentName,AID.ISLOCALNAME ), sensType, sensId);
							String temp[] = contents.split("=");
							sData.setTemp(Float.parseFloat(temp[1]));
							data.add(sData);
							break;
				}
			}
			
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if (stmt != null)  try { stmt.close(); } catch (SQLException e) { e.printStackTrace(); }
		}
		return data;
	}
	
	//-----------------------------------------------
	//   Private Methods
	//-----------------------------------------------
	//

	private void buildArchive() throws SQLException{
		archiveRS = stmt.executeQuery("Select * from archives");
	}
	
	private String esacpeString(String aText){
		String new_sql;
		StringBuilder result = new StringBuilder();
		StringCharacterIterator iterator = new StringCharacterIterator(aText);
		char character =  iterator.current();
		
		while (character != CharacterIterator.DONE ){
			if (character == '\\') {
				result.append("\\");
			}
		}

		logger.info("The new string is: " + result);
		return result.toString();
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
		
		logger.info("Creating loffile: " + filePath + "\\" + fileName);
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
