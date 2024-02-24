/**
 * 
 */
package com.uob.connectors.racf.operation;

import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.commons.net.telnet.TelnetClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.uob.connector.racf.mf.ApplicationLoginCommands;
import com.uob.connector.racf.mf.ErrorCodes;
import com.uob.connector.racf.mf.MFCommand;
import com.uob.connector.racf.mf.MFCommandStatus;
import com.uob.connector.racf.mf.MFConnectorDetails;
import com.uob.connector.racf.mf.MFResponse;
import com.uob.connector.racf.mf.TelnetOperation;

/**
 * This class is to be used only by the MFCommandManager and delegate the commands.
 */
public final class MainFrameConnector {

	private static final Logger log = LogManager.getLogger(MainFrameConnector.class);

	private static TelnetClient telnetClient = new TelnetClient();

	private InetAddress address;

	private int port;

	private static MFConnectorDetails connectionDetailsObj;

	private TelnetOperation telnetOperation;

	private MainFrameConnector() {

	}

	private static MainFrameConnector connector = new MainFrameConnector();

	protected static MainFrameConnector getInstance() {	
		return connector;
	}
	
	public void setConnector(MFConnectorDetails mfConnector) {
		this.connectionDetailsObj = mfConnector;
	}

	private TelnetOperation connect() throws IOException {
		log.info("Trying to login to the telnet client");
		try {
			if (telnetClient.isConnected()) {
				return new TelnetOperation(telnetClient.getInputStream(),
						new PrintStream(telnetClient.getOutputStream()));
			}
			try {
				address = InetAddress.getByName(connectionDetailsObj.getMfHostName());
			} catch (UnknownHostException err) {
				log.error("Error while looking for the host name", err);
				return null;
			}
			port = this.connectionDetailsObj.getMfPortNumber();
			telnetClient.connect(address, port);
			telnetClient.setConnectTimeout(5000);
			telnetClient.setKeepAlive(true);
			return new TelnetOperation(telnetClient.getInputStream(), new PrintStream(telnetClient.getOutputStream()));
		} catch (IOException e) {
			log.error("Error while connecting to host:" + address.getHostAddress() + "," + port, e);
			throw e;
		}
	}

	protected synchronized MFResponse performCommand(MFCommand command) {
		try {
			log.info("Performing the command: " + command.getCommand());
			command.setCurrentStatus(MFCommandStatus.INPROGRESS);
			telnetOperation = connect();
			if (null != telnetOperation) {
				prepareForCommand();
				String rawResponse = telnetOperation.sendCommand(command.getCommand(), command.getNextPrompt());
				command.setCurrentStatus(MFCommandStatus.COMPLETED);
				return makeResponse(rawResponse, ErrorCodes.SUCCESSFUL);
			} else {
				log.error("Unable to telnet system");
				command.setCurrentStatus(MFCommandStatus.FAILED);
				return makeResponse("Unable to reach the target system", ErrorCodes.NOT_FOUND);				
			}
		} catch (Exception err) {
			log.error("Error while performing the command.", err);
			command.setCurrentStatus(MFCommandStatus.FAILED);
			return makeResponse(err.getMessage(), ErrorCodes.INTERNAL_SERVER_ERROR);

		} finally {
			disconnectTelnetSession();
		}
	}

	private void prepareForCommand() {
		StringBuffer buffer = new StringBuffer();
		ApplicationLoginCommands loginCommands = new ApplicationLoginCommands();
		List<MFCommand> mfCommandList = loginCommands.getAppLoginCommands(connectionDetailsObj);
		mfCommandList.forEach(command -> {
			String response = "";
			if (command.getCommand().equalsIgnoreCase(ApplicationLoginCommands.APP_PRESS_ENTER)) {
				response = telnetOperation.pressEnter(command.getNextPrompt());
			} else {
				response = telnetOperation.sendCommand(command.getCommand(), command.getNextPrompt());				
			}
			if (command.getDelayForCommandInSecs() > 0) {
				try {
					Thread.currentThread().sleep(command.getDelayForCommandInSecs() * 1000);
				} catch (InterruptedException e) {
					log.error("Error while perform command", e);
				}
			}
			buffer.append(response);
		});
		log.info(buffer.toString());
	}

	private void disconnectTelnetSession() {
		if (null != telnetClient && telnetClient.isConnected()) {
			try {
				telnetClient.disconnect();
				log.info("Telnet connection disconnected successfully");
			} catch (IOException e) {
				log.error("Error while disconnecting the telnet session", e);
			}
		}
	}

	private MFResponse makeResponse(String string, int code) {
		MFResponse response = new MFResponse();
		response.setRawResponse(string);
		response.setResponseCode(code);
		return response;
	}
}
