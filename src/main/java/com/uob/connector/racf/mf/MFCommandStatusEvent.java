/**
 * 
 */
package com.uob.connector.racf.mf;

import org.springframework.context.ApplicationEvent;

/**
 * 
 */
public class MFCommandStatusEvent extends ApplicationEvent{

	private static final long serialVersionUID = 1L;

	public MFCommandStatusEvent(MFResponse source) {
		super(source);		
	}

}
