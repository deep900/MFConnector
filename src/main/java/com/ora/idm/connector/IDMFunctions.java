/**
 * 
 */
package com.ora.idm.connector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.ora.idm.exception.InvalidUserInputException;
import com.ora.mf.connector.BaseCommand;
import com.ora.mf.connector.MFCommand;
import com.ora.mf.connector.MFConnectorDetails;
import com.ora.mf.connector.MFResponse;
import com.ora.mf.connector.MainFrameConnector;

/**
 * This is the main function class which will trigger the mainframe operations.
 */
public class IDMFunctions {

	private static final Logger log = LogManager.getLogger(IDMFunctions.class);

	private MainFrameConnector mfConnector;

	public void createUser(UserDetails userDetailsObj, MFConnectorDetails mfConnectorDetails)
			throws IllegalArgumentException, InvalidUserInputException {
		try {
			log.info("Tring to create the user:" + userDetailsObj.toString());
			log.info("Printing the login details:" + mfConnectorDetails.toString());
			if (null == userDetailsObj || mfConnectorDetails == null) {
				throw new IllegalArgumentException("User details and the MainFrame connection details cannot be null");
			}
			mfConnector = MainFrameConnector.getInstance(mfConnectorDetails);
			MFCommand createUserCommand = createUserCommand(userDetailsObj);
			MFResponse response = mfConnector.performCommand(createUserCommand);
			log.info("Printing the response: " + response.getRawResponse());
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
	}

	private String getUserAttribute(UserDetails userDetails, UserAttributeEnum key) {
		return userDetails.getAttributeMap().get(key.name()).toString();
	}

	private MFCommand createUserCommand(UserDetails userDetails) throws InvalidUserInputException {
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
		String command = "AU " + lanId + " NAME('" + firstName + lastName + "') " + "OWNER(" + owner + ") DFLTGRP ("
				+ defaultGrp + ") PASSWORD(" + password + ") DATA ('" + data + "') UACC (" + uacc + ")";
		log.info("Printing the command:" + command);
		MFCommand createUserCommand = new BaseCommand(command, "READY", 0);
		return createUserCommand;
	}

	private UserDetails getSampleUserDetails() {
		IDMUserDetails userDetails = new IDMUserDetails();
		userDetails.setUserId(getRandomName(6));
		HashMap userAttributeMap = new HashMap();
		userAttributeMap.put(UserAttributeEnum.FIRST_NAME, getRandomName(8));
		userAttributeMap.put(UserAttributeEnum.LAST_NAME, getRandomName(4));
		userAttributeMap.put(UserAttributeEnum.OWNER, "OPSPWD");
		userAttributeMap.put(UserAttributeEnum.DEFAULT_GROUP, "TH1");
		userAttributeMap.put(UserAttributeEnum.PASSWORD, getRandomName(10));
		userAttributeMap.put(UserAttributeEnum.DATA, "XXXT RORC");
		userAttributeMap.put(UserAttributeEnum.UACC, "N");
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
		for (int i = 0; i < nameLength; i++) {
			buffer.append(arr1.get(ran.nextInt(arr1.size() - 1)));
		}
		buffer.append(arr2.get(ran.nextInt(arr2.size() - 1)));
		return buffer.toString();
	}

	public static void main(String args[]) {
		log.info("Creating a new user in MF");
		IDMFunctions functions = new IDMFunctions();
		functions.createUser(functions.getSampleUserDetails(), functions.getMFConnectorDetails());
	}
}
