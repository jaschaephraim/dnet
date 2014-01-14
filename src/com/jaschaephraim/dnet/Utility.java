package com.jaschaephraim.dnet;

import java.math.BigDecimal;

public class Utility extends Node {
	
	private UtilityFunction f;
	
	public void setFunction( UtilityFunction f ) {
		this.f = f;
	}
	
	protected BigDecimal calc() throws Exception {
		return f.eval( getParents() );
	}
	
}
