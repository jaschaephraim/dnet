package com.jaschaephraim.dnet;

import com.jaschaephraim.dnet.exceptions.DistributionException;
import com.jaschaephraim.dnet.exceptions.DomainException;
import com.jaschaephraim.dnet.vectors.Distribution;
import com.jaschaephraim.dnet.vectors.State;
import com.jaschaephraim.dnet.vectors.SampleSpace;

public class Variable extends Discrete {

	private SampleSpace< Distribution > CPT = new SampleSpace< Distribution >( getParents() );
	
	public void setDistribution( double[] distribution ) throws Exception {
		setDistribution( new State(), new Distribution( distribution ) );
	}
	
	public void setDistribution( Distribution distribution ) throws Exception {
		setDistribution( new State(), distribution );
	}
	
	public void setDistribution( State condCase, double[] distribution ) throws Exception {
		setDistribution( condCase, new Distribution( distribution ) );
	}
	
	public void setDistribution( State condCase, Distribution distribution ) throws Exception {
		checkValidCondition( condCase );
		checkValidDistribution( distribution );
		CPT.add( CPT.stateID( condCase ), distribution );
	}
	
	public Prob givenParents( Object value ) throws Exception {
		return givenParents().get( domainID( value ) );
	}

	public Distribution givenParents() throws Exception {
		State state = new State();
		for ( Discrete parent : getParents() )
			state.add( parent.getEvidence() );
		return getDistribution( CPT.stateID( state ) );
	}

	public Prob prior( Object value ) throws Exception {
		return prior().get( domainID( value ) );
	}

	public Distribution prior() throws Exception {
		Prob[] distribution = emptyProbArray();
		for ( int i = 0; i < CPT.size(); i++ ) {
			Prob product = Prob.ONE;
			for ( int j = 0; j < parentsSize(); j++ ) {
				Variable parent = getParent( j );
				Object condition = getValue( CPT.domainID( i, j ) );
				product = product.multiply( parent.prior( condition ) );
			}
			Distribution conditional = getDistribution( i );
			conditional.multiply( product );
			for ( int j = 0; j < domainSize(); j++ )
				distribution[ j ] = distribution[ j ].add( conditional.get( j ) );
		}
		return new Distribution( distribution );
	}

	protected Prob[] emptyProbArray() {
		Prob[] array = new Prob[ domainSize() ];
		for ( int i = 0; i < array.length; i++ )
			array[ i ] = Prob.ZERO;
		return array;
	}

	protected Object markovSample() throws Exception {
		Distribution markovDistribution = givenBlanket();
		Prob random = new Prob( Double.toString( Math.random() ) );
		Prob sum = Prob.ZERO;
		for ( int i = 0; i < domainSize(); i++ ) {
			sum = sum.add( markovDistribution.get( i ) );
			int comparison = random.compareTo( sum );
			if ( comparison < 1 )
				return getValue( i );
		}
		throw new DomainException( "Domain of '" + this + "' not set." );
	}

	private Distribution getDistribution( int id ) {
		Distribution original = CPT.get( id );
		Prob[] copy = emptyProbArray();
		for ( int i = 0; i < domainSize(); i++ )
			copy[ i ] = new Prob( original.get( i ) );
		return new Distribution( copy );
	}

	private Distribution givenBlanket() throws Exception {
		Distribution distribution = givenParents();
		Prob[] test = emptyProbArray();
		for ( int i = 0; i < distribution.size(); i++ )
			test[ i ] = new Prob( distribution.get( i ) );
		Object evidence = getEvidence();
		for ( int i = 0; i < domainSize(); i++ ) {
			setEvidence( getValue( i ) );
			Prob prob = test[ i ];
			for ( Node child : getChildren() ) {
				if ( child instanceof Variable ) {
					Variable node = ( Variable ) child;
					Object nodeEvidence = node.getEvidence();
					Prob conditional = node.givenParents( nodeEvidence );
					prob = prob.multiply( conditional );
				}
			}
			test[ i ] = prob;
		}
		setEvidence( evidence );
		return new Distribution( test );
	}
	
	private void checkValidDistribution( Distribution distribution ) throws Exception {
		if ( distribution.size() != domainSize() )
			throw new DistributionException( "Distribution has " + distribution.size() + "probabilites, should have " + domainSize() + " for node '" + this + "'." );
	}

}
