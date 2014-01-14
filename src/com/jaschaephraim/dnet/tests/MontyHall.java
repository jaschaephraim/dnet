package com.jaschaephraim.dnet.tests;

import java.math.BigDecimal;

import com.jaschaephraim.dnet.Discrete;
import com.jaschaephraim.dnet.Utility;
import com.jaschaephraim.dnet.UtilityFunction;
import com.jaschaephraim.dnet.Variable;
import com.jaschaephraim.dnet.vectors.Distribution;
import com.jaschaephraim.dnet.vectors.Domain;
import com.jaschaephraim.dnet.vectors.NodeVector;
import com.jaschaephraim.dnet.vectors.State;

public class MontyHall extends Test {

	public static void main(String[] args) throws Exception {
		
		Domain doors = new Domain( new Object[] { 1, 2, 3 } );
		Distribution thirds = Distribution.even( 3 );
		
		Variable location = DNET.newVariable( "Location" );
		location.setDomain( doors );
		location.setDistribution( thirds );
		
		Variable choice = DNET.newVariable( "Choice" );
		choice.setDomain( doors );
		choice.setDistribution( thirds );
		
		Variable reveal = DNET.newVariable( "Reveal" );
		reveal.setDomain( doors );
		reveal.addParent( location );
		reveal.addParent( choice );
		reveal.setDistribution( new State( new Object[] { 1, 1 } ), new Distribution( new double[] { 0.0, 0.5, 0.5 } ) );
		reveal.setDistribution( new State( new Object[] { 1, 2 } ), new Distribution( new double[] { 0.0, 0.0, 1.0 } ) );
		reveal.setDistribution( new State( new Object[] { 1, 3 } ), new Distribution( new double[] { 0.0, 1.0, 0.0 } ) );
		reveal.setDistribution( new State( new Object[] { 2, 1 } ), new Distribution( new double[] { 0.0, 0.0, 1.0 } ) );
		reveal.setDistribution( new State( new Object[] { 2, 2 } ), new Distribution( new double[] { 0.5, 0.0, 0.5 } ) );
		reveal.setDistribution( new State( new Object[] { 2, 3 } ), new Distribution( new double[] { 1.0, 0.0, 0.0 } ) );
		reveal.setDistribution( new State( new Object[] { 3, 1 } ), new Distribution( new double[] { 0.0, 1.0, 0.0 } ) );
		reveal.setDistribution( new State( new Object[] { 3, 2 } ), new Distribution( new double[] { 1.0, 0.0, 0.0 } ) );
		reveal.setDistribution( new State( new Object[] { 3, 3 } ), new Distribution( new double[] { 0.5, 0.5, 0.0 } ) );
		
		Discrete switchChoice = DNET.newDecision( "Switch" );
		switchChoice.setDomain( Domain.BOOL );
		
		Utility win = DNET.newUtility( "Win", new UtilityFunction() {

			@Override
			public BigDecimal eval( NodeVector< Discrete > parents )
					throws Exception {
				Variable location = ( Variable ) parents.get( "Location" );
				Variable choice = ( Variable ) parents.get( "Choice" );
				Discrete switchChoice = ( Discrete ) parents.get( "Switch" );
				
				Distribution distribution = DNET.prob( location );
				if ( switchChoice.getEvidence().equals( true ) )
					return distribution.get( 2 );
				return distribution.get( choice.domainID( choice.getEvidence() ) );
			}
			
		} );
		win.addParent( location );
		win.addParent( choice );
		win.addParent( switchChoice );
		
		
		choice.setEvidence( 1 );
		reveal.setEvidence( 2 );
		
		report( location );
	}

}
