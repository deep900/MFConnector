/**
 * 
 */
package com.ora.mf.connector;

import java.util.ArrayList;
import java.util.List;

/**
 * 
 */
public class ApplicationLoginCommands {
	
	private List<MFCommand> loginCommands = new ArrayList<MFCommand>();
	
	public static String APP_PRESS_ENTER = "APP_PRESS_ENTER";
	
	public List<MFCommand> getAppLoginCommands(MFConnectorDetails connectorDetails) {
		if(!loginCommands.isEmpty()) {
			return loginCommands;
		}
		MFCommand command1 = buildMFCommand(connectorDetails.getApplicationName(), "ENTER USERID -", 0,connectorDetails);
		loginCommands.add(0, command1);
		
		MFCommand command2 = buildMFCommand(connectorDetails.getMfUserName(), "ENTER CURRENT PASSWORD", 0,connectorDetails);
		loginCommands.add(1, (BaseCommand) command2);
		
		MFCommand command3 = buildMFCommand(connectorDetails.getMfPassword(), "LAST ACCESS AT", 0,connectorDetails);
		loginCommands.add(2, (BaseCommand) command3);
		
		MFCommand command4 = buildMFCommand(APP_PRESS_ENTER, "THE PROCEDURE", 0,connectorDetails);
		loginCommands.add(3, command4);
		
		MFCommand command5 = buildMFCommand(APP_PRESS_ENTER, "READY", 10,connectorDetails);
		loginCommands.add(4, command5);
		
		return loginCommands;
	}
	
	private MFCommand buildMFCommand(String command, String nextPrompt, int commandDelayInSecs,MFConnectorDetails connectorDetails) {
		return new BaseCommand(command, nextPrompt, commandDelayInSecs,connectorDetails);
	}
}
