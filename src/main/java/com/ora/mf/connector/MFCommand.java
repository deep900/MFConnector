/**
 * 
 */
package com.ora.mf.connector;

import com.ora.mf.data.parser.MFDataParser;

/**
 * 
 */
public interface MFCommand {

	public String getCommand();
	
	public String getNextPrompt();
	
	public MFDataParser getMFDataParser();
	
	public void setMFDataParser(Class<MFDataParser> mfDataParserClass);
	
	public int getDelayForCommandInSecs();
	
}
