/**
 * 
 */
package com.uob.connectors.racf.operation;

import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.uob.connector.racf.idm.IDMFunctions;
import com.uob.connector.racf.mf.ErrorCodes;
import com.uob.connector.racf.mf.MFCommand;
import com.uob.connector.racf.mf.MFCommandStatus;
import com.uob.connector.racf.mf.MFResponse;

/**
 * 
 */
public final class MFCommandManager {

	private static final Logger log = LogManager.getLogger(IDMFunctions.class);	
	
	private PriorityBlockingQueue<MFCommand> commandRequestQueue = new PriorityBlockingQueue<MFCommand>();
	
	private HashMap<String,MFResponse> commandResponseMap = new HashMap<String, MFResponse>();
	
	
	private MFCommandManager() {
		
	}
	
	private static MFCommandManager mfCommandManager = new MFCommandManager();
	
	private MainFrameConnector connector = MainFrameConnector.getInstance();
	
	public static MFCommandManager getInstance() {
		return mfCommandManager;
	}	
	
	public String manageCommand(MFCommand commandObj) {
		this.commandRequestQueue.add(commandObj);
		analyseAndTrigger();
		return commandObj.getLookupKey() + "|QueuePosition:" + commandRequestQueue.size();
	}
	
	private void analyseAndTrigger() {
		MFCommand command = this.commandRequestQueue.peek();
		if(null == command) {
			log.info("No more commands to process");
			return;
		}
		if(command.getCurrentStatus().equals(MFCommandStatus.NOT_STARTED)) {
			connector.setConnector(command.getConnectorDetails());
			MFResponse response = connector.performCommand(command);
			commandCompleted(response);
		}
	}
	
	private void commandCompleted(MFResponse response) {		
		String lookupKey = response.getResponseLookupKey();
		MFCommand removed = commandRequestQueue.poll();
		log.info("Removed the command from queue" + removed.toString());
		commandResponseMap.put(lookupKey, response);
		analyseAndTrigger();
	}
	
	
	public MFResponse getResponse(String lookupKey) {
		if(commandResponseMap.containsKey(lookupKey)) {
			return commandResponseMap.get(lookupKey);
		} else {
			return getResponseNotReady();
		}
	}
	
	private MFResponse getResponseNotReady() {
		MFResponse response = new MFResponse();
		response.setResponseCode(ErrorCodes.RESPONSE_NOT_YET_READY);
		return response;
	}	
}
