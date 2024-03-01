package com.uob.connector.racf.idm;

import java.util.Map;

public interface IDMUserDetails {
	
	public String getUserId();
	
	public Map<String,Object> getAttributeMap();

}
