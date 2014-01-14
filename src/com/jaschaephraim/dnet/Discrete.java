package com.jaschaephraim.dnet;

import com.jaschaephraim.dnet.exceptions.DomainException;
import com.jaschaephraim.dnet.exceptions.StateException;
import com.jaschaephraim.dnet.vectors.Domain;
import com.jaschaephraim.dnet.vectors.State;

public class Discrete extends Node {
	
	private Object evidence;
	private Domain domain;
	
	public void setEvidence( Object evidence ) throws Exception {
		if ( evidence != null && !inDomain( evidence ) )
			throw new DomainException( "Value '" + evidence + "' is not in domain of node '" + this + "'." );
		this.evidence = evidence;
	}
	
	public void clearEvidence() {
		evidence = null;
	}

	public void setDomain( Domain domain ) {
		this.domain = domain;
	}
	
	public Object getEvidence() {
		return evidence;
	}

	public Domain getDomain() {
		return domain;
	}
	
	public boolean hasEvidence() {
		return evidence != null;
	}

	public int domainSize() {
		return domain.size();
	}
	
	public int domainID( Object value ) {
		return domain.indexOf( value );
	}

	protected void setRandomEvidence() throws Exception {
		setEvidence( getValue( ( int ) ( Math.random() * domainSize() ) ) );
	}

	protected Object getValue( int i ) {
		return domain.get( i );
	}

	protected boolean inDomain( Object value ) {
		return domain.contains( value );
	}

	protected void checkValidCondition( State state ) throws Exception {
		if ( state.size() != parentsSize() )
			throw new StateException( "State specifies " + state.size() + " values instead of " + parentsSize() +" for node " + this + "." );
		for ( int i = 0; i < parentsSize(); i++ ) {
			Object value = state.get( i );
			if ( value != null ) {
				Discrete parent = getParents().get( i );
				if ( !parent.inDomain( value ) )
					throw new DomainException( "Value '" + value + "' is not in domain of node '" + parent + "'." );
			}
		}
	}

}
