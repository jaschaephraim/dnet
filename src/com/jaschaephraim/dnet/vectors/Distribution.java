package com.jaschaephraim.dnet.vectors;

import java.util.Vector;

import com.jaschaephraim.dnet.Prob;

public class Distribution extends Vector< Prob > {

	private static final long serialVersionUID = -4157813731594949713L;
	
	public Distribution( Prob[] distribution ) {
		initVector( distribution );
	}
	
	public Distribution( double[] distribution ) {
		Prob[] bdDistribution = new Prob[ distribution.length ];
		for ( int i = 0; i < distribution.length; i++ )
			bdDistribution[ i ] = new Prob( Double.toString( distribution[ i ] ) );
		initVector( bdDistribution );
	}
	
	public void multiply( Prob multiplicand ) {
		for ( int i = 0; i < size(); i++ )
			set( i, get( i ).multiply( multiplicand ) );
	}
	
	private void initVector( Prob[] distribution ) {
		for ( Prob prob : distribution )
			add( prob );
		normalize();
	}

	private void normalize() {
		Prob sum = Prob.ZERO;
		for ( Prob prob : this )
			sum = sum.add( prob );
		Prob alpha = Prob.ONE.divide( sum );
		multiply( alpha );
	}
	
	public static Distribution even( int domainSize ) {
		Prob defaultProb = Prob.ONE.divide( new Prob( Integer.toString( domainSize ) ) );
		Prob[] distribution = new Prob[ domainSize ];
		for ( int i = 0; i < domainSize; i++ )
			distribution[ i ] = defaultProb;
		return new Distribution( distribution );
	}

}
