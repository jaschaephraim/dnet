package com.jaschaephraim.dnet.vectors;

import java.util.Vector;

public class Domain extends Vector< Object > {

	public static final Domain BOOL = new Domain( new Object[] { true, false } );
	
	private static final long serialVersionUID = 665771880598100196L;
		
	public Domain() {}
	
	public Domain( Object[] event ) {
		for ( Object value : event )
			add( value );
	}

}
