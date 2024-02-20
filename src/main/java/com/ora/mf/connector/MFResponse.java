/**
 * 
 */
package com.ora.mf.connector;

import lombok.Data;

/**
 * 
 */
@Data
public class MFResponse {	

	private String rawResponse;
	
	private Object parsedResponse;
	
	private int responseCode;

}
