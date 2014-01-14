package com.jaschaephraim.dnet.tests;

import java.math.BigDecimal;

import com.jaschaephraim.dnet.Discrete;
import com.jaschaephraim.dnet.Prob;
import com.jaschaephraim.dnet.Utility;
import com.jaschaephraim.dnet.UtilityFunction;
import com.jaschaephraim.dnet.Variable;
import com.jaschaephraim.dnet.vectors.Distribution;
import com.jaschaephraim.dnet.vectors.Domain;
import com.jaschaephraim.dnet.vectors.NodeVector;
import com.jaschaephraim.dnet.vectors.State;

/**
 * Neapolitan, Richard E. (2004).
 * Learning Bayesian networks.
 * Prentice Hall series in artificial intelligence.
 * Upper Saddle River, N.J: Prentice Hall/Pearson Education.
 * Page 257
 *
 */
public class BuyCar extends Test {
	
	public static void main( String[] args ) throws Exception {
		
		Variable trans = DNET.newVariable( "Good transmission" );
		trans.setDomain( Domain.BOOL );
		trans.setDistribution( new double[] { 0.8, 0.2 } );
		
		Variable fail = DNET.newVariable( "Fails test" );
		fail.setDomain( Domain.BOOL );
		fail.addParent( trans );
		fail.setDistribution( State.T, new Distribution( new double[] { 0.3, 0.7 } ) );
		fail.setDistribution( State.F, new Distribution( new double[] { 0.9, 0.1 } ) );
		
		Discrete step1 = DNET.newDecision( "First step" );
		step1.setDomain( new Domain( new String[] { "Buy test", "Buy car", "Do not uy car" } ) );
		
		Discrete step2 = DNET.newDecision( "Second step" );
		step2.setDomain( new Domain( new String[] { "Buy car", "Do not buy car" } ) );
		step2.addParent( step1 );
		step2.addParent( fail );
		
		Utility bank = DNET.newUtility( "Money in the bank", new UtilityFunction() {

			@Override
			public BigDecimal eval( NodeVector< Discrete > parents ) throws Exception {
				Variable trans = ( Variable ) parents.get( "Good transmission" );
				Discrete step1 = parents.get( "First step" );
				Discrete step2 = parents.get( "Second step" );
				
				Distribution probTrans = DNET.prob( trans );
				Prob goodTrans = probTrans.get( trans.domainID( true ) );
				Prob badTrans = probTrans.get( trans.domainID( false ) );
				
				// Walk away immediately
				
				if ( step1.getEvidence().equals( "Do not buy car" ) )
					return new BigDecimal( "10000" );
				
				// Purchase car immediately without testing
				// Return expected value
				
				if ( step1.getEvidence().equals( "Buy car" ) ) {
					BigDecimal goodExp = new BigDecimal( "11000" ).multiply( goodTrans );
					BigDecimal badExp = new BigDecimal( "8000" ).multiply( badTrans );
					return goodExp.add( badExp );
				}
				
				// Purchase test for $200
				
				// Walk away after testing
				if ( step2.getEvidence().equals( "Do not buy car" ) )
					return new BigDecimal( "9800" );
				
				// Purchase car after testing
				// Return expected value
				BigDecimal goodExp = new BigDecimal( "10800" ).multiply( goodTrans );
				BigDecimal badExp = new BigDecimal( "7800" ).multiply( badTrans );
				return goodExp.add( badExp );
				
			}
			
		} );
		bank.addParent( trans );
		bank.addParent( step1 );
		bank.addParent( step2 );
				
		report();
		
	}

}
