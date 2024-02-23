package com.ora.idm.connector;

import java.util.Map;

public interface IDMUserDetails {
	
	public String getUserId();
	
	public Map<String,Object> getAttributeMap();

}
