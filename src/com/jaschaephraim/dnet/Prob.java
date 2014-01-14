package com.jaschaephraim.dnet;

import java.math.BigDecimal;
import java.math.MathContext;

public class Prob extends BigDecimal {
	
	private static final long serialVersionUID = -2868548770789965824L;
	
	public static final Prob ZERO = new Prob( BigDecimal.ZERO );
	public static final Prob ONE  = new Prob( BigDecimal.ONE );

	public Prob( String val ) {
		super( val );
	}
	
	protected Prob( BigDecimal prob ) {
		this( prob.toString() );
	}
	
	@Override
	public boolean equals( Object value ) {
		BigDecimal bdValue = new BigDecimal( value.toString() );
		return compareTo( bdValue ) == 0;
	}

	@Override
	public Prob abs() {
		return new Prob( super.abs() );
	}

	@Override
	public Prob add( BigDecimal augend ) {
		return add( new Prob( augend ) );
	}

	@Override
	public Prob subtract( BigDecimal subtrahend ) {
		return subtract( new Prob( subtrahend ) );
	}

	@Override
	public Prob multiply( BigDecimal multiplicand ) {
		return multiply( new Prob( multiplicand ) );
	}

	@Override
	public Prob divide( BigDecimal divisor ) {
		return divide( new Prob( divisor ) );
	}

	@Override
	public Prob pow( int n ) {
		return new Prob( super.pow( n ) );
	}

	public Prob add( Prob augend ) {
		return new Prob( super.add( augend ) );
	}
	
	public Prob subtract( Prob subtrahend) {
		return new Prob( super.subtract( subtrahend ) );
	}

	public Prob multiply( Prob multiplicand ) {
		return new Prob( super.multiply( multiplicand ) );
	}

	public Prob divide( Prob divisor ) {
		return new Prob( super.divide( divisor, MathContext.DECIMAL128 ) );
	}

}
