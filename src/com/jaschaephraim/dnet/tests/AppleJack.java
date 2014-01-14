package com.jaschaephraim.dnet.tests;

import java.math.BigDecimal;

import com.jaschaephraim.dnet.Discrete;
import com.jaschaephraim.dnet.Prob;
import com.jaschaephraim.dnet.Utility;
import com.jaschaephraim.dnet.UtilityFunction;
import com.jaschaephraim.dnet.Variable;
import com.jaschaephraim.dnet.vectors.Domain;
import com.jaschaephraim.dnet.vectors.NodeVector;
import com.jaschaephraim.dnet.vectors.State;

/**
 * Kj¾rulf, Uffe B., & Madsen, Anders L. (2005).
 * Probabilistic networks: An introduction to Bayesian networks and influence diagrams.
 * Aalborg, Denmark: Aalborg University Dept. of Computer Science.
 * Page 56
 * 
 */
public class AppleJack extends Test {
	
	public static void main( String[] args ) throws Exception {
		
		Variable dry = DNET.newVariable( "Tree is dry" );
		dry.setDomain( Domain.BOOL );
		dry.setDistribution( new double[] { 0.1, 0.9 } );
		
		Variable sick = DNET.newVariable( "Tree is sick" );
		sick.setDomain( Domain.BOOL );
		sick.setDistribution( new double[] { 0.1, 0.9 } );
		
		Variable loses = DNET.newVariable( "Tree loses leaves" );
		loses.setDomain( Domain.BOOL );
		loses.addParent( dry );
		loses.addParent( sick );
		loses.setDistribution( State.TT, new double[] { 0.95, 0.05 } );
		loses.setDistribution( State.TF, new double[] { 0.85, 0.15 } );
		loses.setDistribution( State.FT, new double[] { 0.9 , 0.1  } );
		loses.setDistribution( State.FF, new double[] { 0.02, 0.98 } );
		
		Discrete which = DNET.newDecision( "Dry or sick?");
		which.setDomain( new Domain( new String[] { "Dry", "Sick" } ) );
		
		Utility utility = DNET.newUtility( "Correctness", new UtilityFunction() {

			@Override
			public BigDecimal eval( NodeVector< Discrete > parents ) throws Exception {
				Variable dry  = ( Variable ) parents.get( "Tree is dry" );
				Variable sick = ( Variable ) parents.get( "Tree is sick" );
				Discrete which = parents.get( "Dry or sick?" );
				
				Prob dryProb = DNET.prob( dry ).get( dry.domainID( true ) );
				Prob sickProb = DNET.prob( sick ).get( dry.domainID( true ) );
				
				if ( which.getEvidence().equals( "Dry" ) )
					return dryProb;
				return sickProb;
			}
			
		} );
		utility.addParent( dry );
		utility.addParent( sick );
		utility.addParent( which );
		
		loses.setEvidence( true );
		
		report( new Variable[] { dry, sick } );
		
	}

}
