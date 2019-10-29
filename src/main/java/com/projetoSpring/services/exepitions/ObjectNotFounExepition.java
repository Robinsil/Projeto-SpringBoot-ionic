package com.projetoSpring.services.exepitions;

public class ObjectNotFounExepition  extends RuntimeException{

	
	private static final long serialVersionUID = 1L;

	public ObjectNotFounExepition(String msg) {
		super(msg);
	}
	public ObjectNotFounExepition (String msg,Throwable cause) {
		
		super(msg,cause);
	}

}
