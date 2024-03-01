/**
 * 
 */
package com.uob.connector.racf.mf;

import lombok.Data;

/**
 * 
 */
@Data
public class MFResponse {	

	private String rawResponse;
	
	private Object parsedResponse;
	
	private int responseCode;
	
	public String responseLookupKey;

}
