/*
 * This file is part of JBotSim.
 * 
 *    JBotSim is free software: you can redistribute it and/or modify it
 *    under the terms of the GNU Lesser General Public License as published by
 *    the Free Software Foundation, either version 3 of the License, or
 *    (at your option) any later version.
 *  
 *    Authors:
 *    Arnaud Casteigts		<casteig@site.uottawa.ca>
 */
package jbotsim.event;

import jbotsim.Link;
import jbotsim.Node;

public interface NodeListener{
	/**
	 * Notifies the underlying listener that the node has moved.
	 * @param n The node.
	 */
    public void nodeMoved(Node n);
    /**
     * Notifies the underlying listener that a link adjacent to the node has 
     * been added. 
     * @param l The added link.
     */
    public void linkAdded(Link l);
    /**
     * Notifies the underlying listener that a link adjacent to the node has 
     * been removed.
     * @param l The removed link.
     */
    public void linkRemoved(Link l);
    /**
     * Notifies the underlying listener that a property of the node has been 
     * changed.
     * @param n The node.
     * @param key The name of the changed property.
     */
    public void propertyChanged(Node n, String key);
}