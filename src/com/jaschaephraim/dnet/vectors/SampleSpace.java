package com.jaschaephraim.dnet.vectors;

import java.util.Vector;

import com.jaschaephraim.dnet.Discrete;

public class SampleSpace< T > {

	private NodeVector< Discrete > parents;
	private Vector< T > vector = new Vector< T >( 0 );
	
	public SampleSpace( NodeVector< Discrete > nodeVector ) {
		this.parents = nodeVector;
	}

	public T get( int index ) {
		return vector.get( index );
	}
	
	public void add( int index, T element ) {
		vector.add( index, element );
	}

	public int size() {
		int size = 1;
		for ( Discrete parent : parents ) 
			size = size * parent.domainSize();
		return size;
	}
	
	public int stateID( State state ) {
		return ( int ) ( stateID( 0, state ) * size() );
	}

	public int domainID( int i, int j ) {
		return domainID( i, j, j );
	}
	
	private double stateID( int i, State state ) {
		int prevValueID;
		if ( i > 0 ) {
			Discrete prevParent = parents.get( i - 1 );
			prevValueID = prevParent.domainID( state.get( i - 1 ) );
		} else
			prevValueID = 0;
		if ( i == state.size() )
			return prevValueID;
		Discrete curParent = parents.get( i );
		int curDomainSize = curParent.domainSize();
		return stateID( i + 1, state ) / curDomainSize + prevValueID;
	}

	private int domainID( int i, int j, int k ) {
		Discrete curParent = parents.get( j );
		if ( j == 0 )
			return ( int ) ( curParent.domainSize() * ( double ) i / size() );
		double prevDomainSize = parents.get( j - 1 ).domainSize();
		double z = k - j % 2 == 0 ? prevDomainSize : 1.0 / prevDomainSize;
		return ( int ) ( curParent.domainSize() * ( ( double ) i / size() * z - domainID( i, j - 1, k ) ) );
	}
	
}
