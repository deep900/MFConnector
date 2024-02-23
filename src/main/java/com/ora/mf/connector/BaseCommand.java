package com.ora.mf.connector;

import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ora.mf.data.parser.MFDataParser;

public class BaseCommand implements MFCommand, Comparable {
	
	private static final Logger log = LogManager.getLogger(BaseCommand.class);
	
	private String command;
	
	private String nextPrompt;
	
	private MFDataParser mfDataParser;
	
	private int commandDelayInSeconds = 0;
	
	private MFCommandStatus currentMFCommandStatus = MFCommandStatus.NOT_STARTED;
	
	private String lookupKey;	
	
	private MFConnectorDetails connector;
	
	public BaseCommand(String command, String nextPrompt, int commandDelayInSeconds,MFConnectorDetails connector) {
		if(null == command || null == nextPrompt) {
			throw new IllegalArgumentException("Command or next prompt cannot be null");
		}
		this.command = command;
		this.nextPrompt = nextPrompt;
		this.commandDelayInSeconds = commandDelayInSeconds;
		this.lookupKey = UUID.randomUUID().toString();
		this.connector = connector;
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

	@Override
	public MFCommandStatus getCurrentStatus() {
		return currentMFCommandStatus;
	}

	@Override
	public String getLookupKey() {		
		return lookupKey;
	}

	@Override
	public MFConnectorDetails getConnectorDetails() {
		return connector;
	}

	@Override
	public int compareTo(Object o) {		
		return 0;
	}

	@Override
	public void setCurrentStatus(MFCommandStatus status) {
		this.currentMFCommandStatus = status;		
	}

}
