/**
 * 
 */
package com.uob.connector.racf.mf;

import lombok.Data;

/**
 * This class has the basic details of MainFrame connection properties.
 */
@Data
public class MFConnectorDetails {

	private String mfHostName;
	
	private int mfPortNumber;
	
	private String mfUserName;
	
	private String mfPassword;
	
	private String applicationName;	
	
}
