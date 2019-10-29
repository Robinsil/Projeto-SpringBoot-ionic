package com.projetoSpring.services.exepitions;

public class ConstraintExeption  extends RuntimeException{

	
	private static final long serialVersionUID = 1L;

	public ConstraintExeption(String msg) {
		super(msg);
	}
	public ConstraintExeption (String msg,Throwable cause) {
		
		super(msg,cause);
	}

}
