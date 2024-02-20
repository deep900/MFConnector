/**
 * 
 */
package com.ora.idm.connector;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

/**
 * 
 */
@Data
public class IDMUserDetails implements UserDetails {

	private String userId;
	
	private Map<String, Object> userAttributesMap = new HashMap<String, Object>();

	@Override
	public String getUserId() {		
		return userId;
	}

	@Override
	public Map<String,Object> getAttributeMap() {
		return userAttributesMap;
	}
	
}
