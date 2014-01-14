package com.jaschaephraim.dnet.tests;

import java.math.BigDecimal;

import com.jaschaephraim.dnet.DNet;
import com.jaschaephraim.dnet.Discrete;
import com.jaschaephraim.dnet.Variable;
import com.jaschaephraim.dnet.exceptions.DistributionException;
import com.jaschaephraim.dnet.vectors.Distribution;
import com.jaschaephraim.dnet.vectors.State;

public abstract class Test {
	
	protected static DNet DNET = new DNet();
	
	protected static void report() throws Exception {
		report( new Variable[] {} );
	}
	
	protected static void report( Variable node ) throws Exception {
		report( new Variable[] { node } );
	}
	
	protected static void report( Variable[] variables ) throws Exception {
		reportEvidence();
		
		for ( Variable node : variables )
			reportVar( node );
		
		if ( DNET.decisions.size() > 0 ) {
			System.out.println( "ENUMERATION DECISION" );
			reportDecision();

			DNET.approx = true;
			System.out.println( "GIBBS SAMPLING DECISION" );
			reportDecision();
			DNET.approx = false;
		}
	}
	
	private static void reportVar( Variable node ) throws Exception {
		Distribution enumerate = DNET.prob( node );
		
		DNET.approx = true;
		Distribution gibbs = DNET.prob( node );
		DNET.approx = false;
				
		System.out.println( "DISTRIBUTION FOR VARIABLE '" + node + "'" );
		System.out.println( "   domain : " + node.getDomain() );
		System.out.println( "enumerate : " + enumerate );
		System.out.println( "    gibbs : " + gibbs );
		System.out.println( "    error : " + difference( enumerate, gibbs ) );
		System.out.println();
		
	}
	
	private static void reportEvidence() {
		System.out.println( "EVIDENCE");
		boolean evidence = false;
		for ( Discrete var : DNET.variables )
			if ( var.hasEvidence() ) {
				System.out.println( var + " : " + var.getEvidence() );
				evidence = true;
			}
		if ( !evidence )
			System.out.println( "None" );
		System.out.println();
	}
	
	private static void reportDecision() throws Exception {
		State decision = DNET.evaluate();
		BigDecimal utility = DNET.DUT.get( DNET.DUT.stateID( decision ) );
		System.out.println( "EXPECTED VALUE : " + utility );
		for ( int i = 0; i < DNET.decisions.size(); i++ )
			System.out.println( DNET.decisions.get( i ) + " : " + decision.get( i ) );
		System.out.println();
	}
	
	private static BigDecimal difference( Distribution d1, Distribution d2 ) throws DistributionException {
		int d1Size = d1.size();
		int d2Size = d2.size();
		if ( d1Size != d2Size )
			throw new DistributionException( "Cannot calculate difference of distributions of lengths " + d1Size + " and " + d2Size );
		return d1.get( 0 ).subtract( d2.get( 0 ) ).abs();
	}

}
