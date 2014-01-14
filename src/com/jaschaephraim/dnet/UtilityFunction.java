package com.jaschaephraim.dnet;

import java.math.BigDecimal;

import com.jaschaephraim.dnet.vectors.NodeVector;

public interface UtilityFunction {
	
	public BigDecimal eval( NodeVector< Discrete > parents ) throws Exception;
	
}
