package com.jaschaephraim.dnet.vectors;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

import com.jaschaephraim.dnet.Node;

public class NodeVector< T extends Node > implements Collection< T > {

	private Hashtable< String, Integer > table = new Hashtable< String, Integer >();
	private Vector< T > vector = new Vector< T >();

	@Override
	public Iterator< T > iterator() {
		return vector.iterator();
	}

	@Override
	public boolean add( T node ) {
		table.put( node.getID(), vector.size() );
		vector.add( ( T ) node );
		return true;
	}
	
	@Override
	public boolean addAll( Collection< ? extends T > nodes ) {
		for ( T node : nodes )
			if ( !add( node ) )
				return false;
		return true;
	}

	@Override
	public void clear() {
		table.clear();
		vector.clear();
	}

	@Override
	public boolean contains( Object object ) {
		return contains( ( ( Node ) object ).getID() );
	}

	@Override
	public boolean containsAll( Collection< ? > objects ) {
		for ( Object object : objects )
			if ( !( contains( object ) ) )
				return false;
		return true;
	}

	@Override
	public boolean isEmpty() {
		return vector.isEmpty();
	}

	@Override
	public boolean remove( Object object ) {
		if ( !contains( object ) )
			return false;
		Node node = ( Node ) object;
		vector.remove( table.get( node.getID() ) );
		table.remove( node.getID() );
		return true;
	}

	@Override
	public boolean removeAll( Collection< ? > objects ) {
		for ( Object object : objects )
			if ( !remove( object ) )
				return false;
		return true;
	}

	@Override
	public boolean retainAll( Collection< ? > c ) {
		throw new UnsupportedOperationException();
	}

	@Override
	public Object[] toArray() {
		return vector.toArray();
	}
	
	@SuppressWarnings( "hiding" )
	@Override
	public < T > T[] toArray( T[] array ) {
		return vector.toArray( array );
	}

	@Override
	public int size() {
		return vector.size();
	}

	public T get( int id ) {
		return vector.get( id );
	}

	public T get( String id ) throws Exception {
		return get( table.get( id ) );
	}
	
	public boolean contains( String id ) {
		return table.containsKey( id );
	}

}
