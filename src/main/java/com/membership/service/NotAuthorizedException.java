package com.membership.service;

public class NotAuthorizedException extends Exception {
	private String message;

	public NotAuthorizedException(String message) {
		super(message);
		this.message = message;
	}
	
	public NotAuthorizedException() {
		super();
	}
	
	public NotAuthorizedException(Throwable t) {
		super(t);
	}
}
