package com.ora.mf.connector;

import java.lang.reflect.InvocationTargetException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ora.mf.data.parser.MFDataParser;

public class BaseCommand implements MFCommand {
	
	private static final Logger log = LogManager.getLogger(BaseCommand.class);
	
	private String command;
	
	private String nextPrompt;
	
	private MFDataParser mfDataParser;
	
	private int commandDelayInSeconds = 0;
	
	public BaseCommand(String command, String nextPrompt, int commandDelayInSeconds) {
		if(null == command || null == nextPrompt) {
			throw new IllegalArgumentException("Command or next prompt cannot be null");
		}
		this.command = command;
		this.nextPrompt = nextPrompt;
		this.commandDelayInSeconds = commandDelayInSeconds;
	}

	@Override
	public String getCommand() {	
		return command;
	}

	@Override
	public String getNextPrompt() {		
		return nextPrompt;
	}

	@Override
	public MFDataParser getMFDataParser() {		
		return mfDataParser;
	}

	@Override
	public void setMFDataParser(Class<MFDataParser> mfDataParserClass) {
		try {
			this.mfDataParser =  (MFDataParser) mfDataParserClass.getConstructors()[0].newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| SecurityException e) {		
			log.error("Error while creating the metadata parser",e);
		}
	}

	@Override
	public int getDelayForCommandInSecs() {
		return this.commandDelayInSeconds;
	}
	
	@Override
	public String toString() {
		return "[Command]:" +command + "[Next Prompt]:" + nextPrompt;
	}

}
