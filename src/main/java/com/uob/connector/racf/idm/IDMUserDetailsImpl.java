/**
 * 
 */
package com.uob.connector.racf.idm;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class IDMUserDetailsImpl implements IDMUserDetails {

	private String userId;
	
	private Map<String, Object> userAttributesMap = new HashMap<String, Object>();
	
	public IDMUserDetailsImpl() {
		
	}

	@Override
	public String getUserId() {		
		return userId;
	}

	@Override
	public Map<String,Object> getAttributeMap() {
		return userAttributesMap;
	}
	
}
