package com.jaschaephraim.dnet.tests;

import java.math.BigDecimal;

import com.jaschaephraim.dnet.*;
import com.jaschaephraim.dnet.vectors.Distribution;
import com.jaschaephraim.dnet.vectors.Domain;
import com.jaschaephraim.dnet.vectors.NodeVector;
import com.jaschaephraim.dnet.vectors.State;

/**
 * Russell, S. J., & Norvig, P. (2010).
 * Artificial intelligence: A modern approach (3rd ed.).
 * Prentice Hall series in artificial intelligence.
 * Upper Saddle River, N.J: Prentice Hall/Pearson Education.
 * Page 512
 *
 */
public class BurglarAlarm extends Test {

	public static void main(String[] args) throws Exception {
		
		Variable b = DNET.newVariable( "Burglary" );
		b.setDomain( Domain.BOOL );
		b.setDistribution( new double[] { 0.001, 0.999 } );
		
		Variable e = DNET.newVariable( "Earthquake" );
		e.setDomain( Domain.BOOL );
		e.setDistribution( new double[] { 0.002, 0.998 } );
		
		Variable a = DNET.newVariable( "Alarm" );
		a.setDomain( Domain.BOOL );
		a.addParent( b );
		a.addParent( e );
		a.setDistribution( State.TT, new double[] { 0.95 , 0.05 } );
		a.setDistribution( State.TF, new double[] { 0.94 , 0.06 } );
		a.setDistribution( State.FT, new double[] { 0.29 , 0.71 } );
		a.setDistribution( State.FF, new double[] { 0.001, 0.999 } );
		
		Variable j = DNET.newVariable( "John calls" );
		j.setDomain( Domain.BOOL );
		j.addParent( a );
		j.setDistribution( State.T, new double[] { .9 , .1 } );
		j.setDistribution( State.F, new double[] { .05, .95 } );
		
		Variable m = DNET.newVariable( "Mary calls" );
		m.setDomain( Domain.BOOL );
		m.addParent( a );
		m.setDistribution( State.T, new double[] {  .7, .3  } );
		m.setDistribution( State.F, new double[] { .01, .99 } );
		
		Discrete isBurglary = DNET.newDecision( "Burglary?");
		isBurglary.setDomain( Domain.BOOL );
		
		Utility utility = DNET.newUtility( "Correctness", new UtilityFunction() {

			@Override
			public BigDecimal eval( NodeVector< Discrete > parents ) throws Exception {
				Variable b  = ( Variable ) parents.get( "Burglary" );
				Discrete isBurglary = parents.get( "Burglary?" );
				
				Distribution bProb = DNET.prob( b );
				Prob bTrue = bProb.get( b.domainID( true ) );
				Prob bFalse = bProb.get( b.domainID( false ) );
				
				if ( isBurglary.getEvidence().equals( true ) )
					return bTrue;
				return bFalse;
			}
			
		} );
		utility.addParent( b );
		utility.addParent( isBurglary );
		
		j.setEvidence( true );
		m.setEvidence( true );
		
		report( b );
		
	}

}
