package com.riease.common.helper;

/**
 * @author Wesley
 *
 */
public class LengthIncorrectException extends Exception {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LengthIncorrectException() {
        super();
    }
    
    public LengthIncorrectException(String message) {
        super(message);
    }
}
