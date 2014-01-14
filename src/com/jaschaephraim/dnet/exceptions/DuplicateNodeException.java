package com.jaschaephraim.dnet.exceptions;

public class DuplicateNodeException extends Exception {
	
	private static final long serialVersionUID = -2458713990055707276L;

	public DuplicateNodeException( String id ) {
		super( "There is already a node named '" + id + "'." );
	}

}
