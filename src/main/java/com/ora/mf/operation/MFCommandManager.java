/**
 * 
 */
package com.ora.mf.operation;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.PriorityBlockingQueue;

import org.springframework.context.ApplicationListener;
import org.springframework.core.metrics.ApplicationStartup;

import com.ora.mf.connector.MFCommand;
import com.ora.mf.connector.MFCommandStatus;
import com.ora.mf.connector.MFCommandStatusEvent;
import com.ora.mf.connector.MFResponse;

/**
 * 
 */
public class MFCommandManager implements ApplicationListener<MFCommandStatusEvent>{

	private PriorityBlockingQueue<MFCommand> commandRequestQueue = new PriorityBlockingQueue<MFCommand>();
	
	private HashMap<String,MFResponse> commandResponseMap = new HashMap<String, MFResponse>();
	
	private MFCommandManager() {
		
	}
	
	private static MFCommandManager mfCommandManager = new MFCommandManager();
	
	private MainFrameConnector connector = MainFrameConnector.getInstance();
	
	public static MFCommandManager getInstance() {
		return mfCommandManager;
	}
	
	public Map<String, MFResponse> getCommandResponseMap() {
		return commandResponseMap;
	}
	
	public String manageCommand(MFCommand commandObj) {
		this.commandRequestQueue.add(commandObj);
		analyseAndTrigger();
		return commandObj.getLookupKey() + "|QueuePosition:" + commandRequestQueue.size();
	}
	
	private void analyseAndTrigger() {
		MFCommand command = this.commandRequestQueue.peek();
		if(command.getCurrentStatus().equals(MFCommandStatus.NOT_STARTED)) {
			connector.setConnector(command.getConnectorDetails());
			connector.performCommand(command);
		}
	}

	@Override
	public void onApplicationEvent(MFCommandStatusEvent event) {
			//TODO 
		/*
		 * 1. Remove the command from queue
		 * 2. Update the response hash map for enquiry
		 * 3. call analyseAndTrigger.
		 */
	}
}
