package com.jaschaephraim.dnet.vectors;

import java.util.Vector;

public class State extends Vector< Object > {
	
	private static final long serialVersionUID = 272051018259455814L;
	
	public static final State T  = new State( new Object[] { true  } );
	public static final State F  = new State( new Object[] { false } );
	public static final State TT = new State( new Object[] { true , true  } );
	public static final State TF = new State( new Object[] { true , false } );
	public static final State FT = new State( new Object[] { false, true  } );
	public static final State FF = new State( new Object[] { false, false } );
		
	public State() {}
	
	public State( Object[] event ) {
		for ( Object value : event )
			add( value );
	}

}
