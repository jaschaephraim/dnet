package com.jaschaephraim.dnet;

import com.jaschaephraim.dnet.exceptions.CycleException;
import com.jaschaephraim.dnet.vectors.NodeVector;

public abstract class Node {

	private String id;
	private NodeVector< Discrete > parents  = new NodeVector< Discrete >();
	private NodeVector< Node >     children = new NodeVector< Node >();
	
	public String toString() {
		return getID();
	}

	public void setID( String id ) {
		this.id = id;
	}

	public void addParent( Discrete parent ) throws Exception {
		if ( !hasParent( parent ) ) {
			checkNotDescendant( parent );
			parents.add( parent );
			parent.addChild( this );
		}
	}

	public String getID() {
		return id;
	}
	
	public NodeVector< Discrete > getParents() {
		return parents;
	}

	public NodeVector< Node > getChildren() {
		return children;
	}
	
	public boolean hasParent( Node parent ) {
		return parents.contains( parent ) ? true : false;
	}

	public boolean hasChild( Node node ) {
		return children.contains( node ) ? true : false;
	}

	protected void addChild( Node child ) throws Exception {
		if ( !hasChild( child ) ) {
			checkNotAncestor( child );
			children.add( child );
			child.addParent( ( Discrete ) this );
		}
	}

	protected Variable getParent( int parentID ) {
		return ( Variable ) parents.get( parentID );
	}

	protected int parentsSize() {
		return getParents().size();
	}

	private void checkNotDescendant( Node node ) throws Exception {
		if ( hasChild( node ) )
			throw new CycleException( "Node '" + node + "' already a descendant of '" + this + "'." );
		for ( Node child : getChildren() )
			child.checkNotDescendant( node );
	}
	
	private void checkNotAncestor( Node node ) throws Exception {
		if ( hasParent( node ) )
			throw new CycleException( "Node '" + node + "' already an ancestor of '" + this + "'." );
		for ( Node parent : getParents() )
			parent.checkNotAncestor( node );
	}

}
