package com.jaschaephraim.dnet;

import java.math.BigDecimal;

import com.jaschaephraim.dnet.Variable;
import com.jaschaephraim.dnet.exceptions.DuplicateNodeException;
import com.jaschaephraim.dnet.vectors.Distribution;
import com.jaschaephraim.dnet.vectors.NodeVector;
import com.jaschaephraim.dnet.vectors.State;
import com.jaschaephraim.dnet.vectors.SampleSpace;

public class DNet {
	
	public boolean approx = false;
	
	private static final int GIBBSCOUNT = 10000;
	private static final int GIBBSWAIT  = 0;
	
	public NodeVector< Discrete > decisions  = new NodeVector< Discrete >();
	public NodeVector< Variable > variables  = new NodeVector< Variable >();
	public Utility utility;
	public SampleSpace< BigDecimal > DUT = new SampleSpace< BigDecimal >( decisions );
	
	public Discrete newDecision( String id ) throws Exception {
		Discrete node = ( Discrete ) newNode( Discrete.class, id );
		decisions.add( node );
		return node;
	}

	public Variable newVariable( String id ) throws Exception {
		Variable node = ( Variable ) newNode( Variable.class, id );
		variables.add( node );
		return node;
	}
	
	public Utility newUtility( String id, UtilityFunction f ) throws Exception {
		utility = ( Utility ) newNode( Utility.class, id );
		utility.setFunction( f );
		return utility;
	}
	
	public Prob prob( Variable query, Object value ) throws Exception {
		return prob( query ).get( query.domainID( value ) );
	}
	
	public Distribution prob( Variable query ) throws Exception {
		if ( approx )
			return gibbs( query );
		return enumerate( query );
	}
	
	public State evaluate() throws Exception {
		int bestDecision = -1;
		
		for ( int i = 0; i < DUT.size(); i++ ) {
			for ( int j = 0; j < decisions.size(); j++ ) {
				Discrete decision = decisions.get( j );
				decision.setEvidence( decision.getValue( DUT.domainID( i, j ) ) );
			}
			DUT.add( i, utility.calc() );
			if ( bestDecision < 0 || DUT.get( i ).compareTo( DUT.get( bestDecision ) ) > 0 )
				bestDecision = i;
		}
		
		State solution = new State();
		for ( int i = 0; i < decisions.size(); i++ ) {
			Discrete decision = decisions.get( i );
			decision.clearEvidence();
			solution.add( decision.getValue( DUT.domainID( bestDecision, i ) ) );
		}
		
		return solution;
	}
	
	private Node newNode( Class< ? > type, String id ) throws Exception {
		if ( variables.contains( id ) )
			throw new DuplicateNodeException( id );
		Node node = ( Node ) type.newInstance();
		node.setID( id );
		return node;
	}

	private Distribution enumerate( Variable query ) throws Exception {
		Prob[] distribution = query.emptyProbArray();
		for ( int i = 0; i < query.domainSize(); i++ ) {
			query.setEvidence( query.getValue( i ) );
			distribution[ i ] = enumerate( 0 );
			query.clearEvidence();
		}
		return new Distribution( distribution );
	}
	
	private Prob enumerate( int i ) throws Exception {
		if ( i == variables.size() )
			return Prob.ONE;
		Variable node = variables.get( i );
		if ( node.hasEvidence() )
			return node.givenParents( node.getEvidence() ).multiply( enumerate( i + 1 ) );
		
		Prob sum = Prob.ZERO;
		for ( Object value : node.getDomain() ) {
			Prob conditional = node.givenParents( value );
			node.setEvidence( value );
			conditional = conditional.multiply( enumerate( i + 1 ) );
			node.clearEvidence();
			sum = sum.add( conditional );
		}
		return sum;
	}

	private Distribution gibbs( Variable query ) throws Exception {
		
		// Populate non-evidence vector and initialize to random values
		NodeVector< Variable > nonevidence = new NodeVector< Variable >();
		for ( Variable node : variables ) {
			if ( !node.hasEvidence() || node.equals( query ) ) {
				nonevidence.add( node );
				node.setRandomEvidence();
			}
		}
		
		// Perform Gibbs sampling GIBBSCOUNT times, recording after GIBBSWAIT interations
		Prob[] distribution = query.emptyProbArray();
		for ( int i = 0; i < GIBBSCOUNT; i++ ) {
			for ( Variable node : nonevidence ) {
				node.setEvidence( node.markovSample() );
				if ( i >= GIBBSWAIT ) {
					int id = query.domainID( query.getEvidence() );
					distribution[ id ] = distribution[ id ].add( Prob.ONE );
				}
			}
		}
		for ( Discrete node : nonevidence )
			node.clearEvidence();
		return new Distribution( distribution );
	}
	
}
