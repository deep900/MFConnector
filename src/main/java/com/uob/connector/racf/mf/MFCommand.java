/**
 * 
 */
package com.uob.connector.racf.mf;

import com.uob.connector.racf.data.parser.MFDataParser;

/**
 * 
 */
public interface MFCommand {

	public String getCommand();
	
	public String getNextPrompt();
	
	public MFDataParser getMFDataParser();
	
	public void setMFDataParser(Class<MFDataParser> mfDataParserClass);
	
	public int getDelayForCommandInSecs();
	
	public MFCommandStatus getCurrentStatus();
	
	public String getLookupKey();
	
	public MFConnectorDetails getConnectorDetails();
	
	public void setCurrentStatus(MFCommandStatus status);
	
}
