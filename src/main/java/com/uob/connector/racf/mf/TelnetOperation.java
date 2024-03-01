/**
 * 
 */
package com.uob.connector.racf.mf;

import java.io.InputStream;
import java.io.PrintStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TelnetOperation {	

	private static final Logger log = LogManager.getLogger(TelnetOperation.class);

	private PrintStream outputStream;

	private InputStream inputStream;

	public TelnetOperation(InputStream inputStream, PrintStream outputStream) {
		this.inputStream = inputStream;
		this.outputStream = outputStream;
	}
	
	public String pressEnter(String nextPrompt) {
		try {			
			write("\n");
			return readUntil(nextPrompt);
		} catch (Exception e) {
			log.info("Error in enter command;");
			return "No Response";
		}
	}

	private void write(String value) {
		try {
			outputStream.println(value);
			outputStream.flush();
		} catch (Exception e) {
			log.info("Error in write command;" + value);
		}
	}

	public String sendCommand(String command, String nextPrompt) {
		try {			
			log.info("Sending command " + command + "," + nextPrompt);
			write(command);
			return readUntil(nextPrompt);
		} catch (Exception err) {
			log.error("Error in send command", err);
		}
		return null;
	}

	private String readUntil(String pattern) {
		try {
			StringBuffer sb = new StringBuffer();
			char ch = (char) inputStream.read();
			while (true) {
				sb.append(ch);
				log.debug("Response:" + sb.toString());
				if (sb.toString().contains(pattern)) {
					log.info("Response:" + sb.toString());
					return sb.toString();
				}
				ch = (char) inputStream.read();
			}
		} catch (Exception err) {
			log.error("Error in read command", err);
		}
		return null;
	}
}
