package com.ora.idm.connector;

import java.util.Map;

public interface UserDetails {
	
	public String getUserId();
	
	public Map<String,Object> getAttributeMap();

}
