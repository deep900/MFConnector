/**
 * 
 */
package com.uob.connector.racf.idm;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;

import com.uob.connector.racf.exception.InvalidUserInputException;
import com.uob.connector.racf.mf.BaseCommand;
import com.uob.connector.racf.mf.MFCommand;
import com.uob.connector.racf.mf.MFConnectorDetails;
import com.uob.connectors.racf.operation.MFCommandManager;

/**
 * This is the main function class which will trigger the mainframe operations.
 */
public class IDMFunctions {

	private static final Logger log = LogManager.getLogger(IDMFunctions.class);	

	private MFCommandManager manager = MFCommandManager.getInstance();
	
	public IDMFunctions() {
		initLogger();
	}

	public String createUser(IDMUserDetails userDetailsObj, MFConnectorDetails mfConnectorDetails)
			throws IllegalArgumentException, InvalidUserInputException {
		try {
			log.info("Tring to create the user:" + userDetailsObj.toString());
			log.info("Printing the login details:" + mfConnectorDetails.toString());
			if (null == userDetailsObj || mfConnectorDetails == null) {
				throw new IllegalArgumentException("User details and the MainFrame connection details cannot be null");
			}
			MFCommand createUserCommand = createUserCommand(userDetailsObj, mfConnectorDetails);
			return manageCommand(createUserCommand);
			// TODO change the response in JSON format. //
		} catch (InvalidUserInputException exp) {
			log.error("Error while creating a user", exp);
			throw exp;
		} catch (IllegalArgumentException exp) {
			log.error("Error while creating a user", exp);
			throw exp;
		} catch (Exception exp) {
			log.error("Error while creating a user", exp);
		}
		return null;
	}

	private String manageCommand(MFCommand commandObj) {
		return manager.manageCommand(commandObj);
	}

	private String getUserAttribute(IDMUserDetails userDetails, UserAttributeEnum key) {
		return userDetails.getAttributeMap().get(key.name()).toString();
	}

	private MFCommand createUserCommand(IDMUserDetails userDetails, MFConnectorDetails mfConnectorDetails)
			throws InvalidUserInputException {
		if (userDetails.getUserId() == null || userDetails.getUserId().isEmpty()) {
			throw new InvalidUserInputException("User ID cannot be empty or null");
		}
		String lanId = getUserAttribute(userDetails, UserAttributeEnum.LAN_ID);
		String firstName = getUserAttribute(userDetails, UserAttributeEnum.FIRST_NAME);
		String lastName = getUserAttribute(userDetails, UserAttributeEnum.LAST_NAME);
		String owner = getUserAttribute(userDetails, UserAttributeEnum.OWNER);
		String defaultGrp = getUserAttribute(userDetails, UserAttributeEnum.DEFAULT_GROUP);
		String password = getUserAttribute(userDetails, UserAttributeEnum.PASSWORD);
		String data = getUserAttribute(userDetails, UserAttributeEnum.DATA);
		String uacc = getUserAttribute(userDetails, UserAttributeEnum.UACC);
		String command = "AU " + lanId + " NAME('" + firstName + " " + lastName + "') " + "OWNER(" + owner
				+ ") DFLTGRP (" + defaultGrp + ") PASSWORD(" + password + ") DATA ('" + data + "') UACC (" + uacc + ")";
		log.info("Printing the command:" + command);
		MFCommand createUserCommand = new BaseCommand(command, "READY", 0, mfConnectorDetails);
		return createUserCommand;
	}

	private IDMUserDetailsImpl getSampleUserDetails() {
		IDMUserDetailsImpl userDetails = new IDMUserDetailsImpl();
		userDetails.setUserId(getRandomName(6));
		HashMap userAttributeMap = new HashMap();
		userAttributeMap.put(UserAttributeEnum.LAN_ID.name(), getRandomName(6));
		userAttributeMap.put(UserAttributeEnum.FIRST_NAME.name(), getRandomName(6));
		userAttributeMap.put(UserAttributeEnum.LAST_NAME.name(), getRandomName(4));
		userAttributeMap.put(UserAttributeEnum.OWNER.name(), "OPSPWD");
		userAttributeMap.put(UserAttributeEnum.DEFAULT_GROUP.name(), "TH1");
		userAttributeMap.put(UserAttributeEnum.PASSWORD.name(), getRandomName(7) + getRandomSpecialChar());
		userAttributeMap.put(UserAttributeEnum.DATA.name(), "UOBT RORC");
		userAttributeMap.put(UserAttributeEnum.UACC.name(), "N");
		userDetails.setUserAttributesMap(userAttributeMap);
		return userDetails;
	}

	private MFConnectorDetails getMFConnectorDetails() {
		MFConnectorDetails mfConnectorDetails = new MFConnectorDetails();
		mfConnectorDetails.setApplicationName("TSO");
		
		mfConnectorDetails.setMfPortNumber(23);
		mfConnectorDetails.setMfUserName("intidn");
		
		return mfConnectorDetails;
	}

	private String getRandomName(int nameLength) {
		List arr1 = Arrays.asList("A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q",
				"R", "S", "T", "U", "V", "W", "X", "Y", "Z");
		List arr2 = Arrays.asList("0", "1", "2", "3", "4", "5", "6", "7", "8", "9");
		StringBuffer buffer = new StringBuffer();
		Random ran = new Random();
		for (int i = 0; i < (nameLength - 1); i++) {
			buffer.append(arr1.get(ran.nextInt(arr1.size() - 1)));
		}
		buffer.append(arr2.get(ran.nextInt(arr2.size() - 1)));
		return buffer.toString();
	}

	private String getRandomSpecialChar() {
		List<String> arr1 = Arrays.asList("@", "$", "#");
		Random ran = new Random();
		return arr1.get(ran.nextInt(arr1.size() - 1));
	}

	private static void initLogger() {
		String log4jConfigFile = "/app/IGASUB/RACF/log4j2.xml";
		File obj = new File(log4jConfigFile);
		if(!obj.exists()) {
			log.error("unable to locate the lo4j2 xml file.");
			return;
		}
		ConfigurationSource source;
		try {
			source = new ConfigurationSource(new FileInputStream(log4jConfigFile));
			Configurator.initialize(null, source);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void main(String args[]) {
		initLogger();
		log.info("Creating a new user in MF");
		IDMFunctions functions = new IDMFunctions();
		functions.createUser(functions.getSampleUserDetails(), functions.getMFConnectorDetails());
	}
}