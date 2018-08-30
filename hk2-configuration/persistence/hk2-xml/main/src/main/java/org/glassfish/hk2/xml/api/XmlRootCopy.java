/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

package org.glassfish.hk2.xml.api;

/**
 * This represents a copy of the parent
 * {@link XmlRootHandle}.  This tree can
 * be traversed and modified without those
 * modifications being reflected in the parent.
 * When the {@link #merge()} method is called
 * the parent tree will get all the changes
 * made to this tree in one commit.  The XmlRootCopy
 * allows for multiple changes to be made to the
 * root and its children in one atomic unit (either
 * all changes are made to the parent or none of them)
 * <p>
 * If the parent was changed after this copy
 * was created then the merge will fail.  The
 * method {@link #isMergeable()} can be used
 * to determine if this copy can still be merged
 * back into the parent
 * 
 * @author jwells
 *
 */
public interface XmlRootCopy<T> {
    /**
     * Gets the XmlRootHandle from which this copy was created
     * @return
     */
    public XmlRootHandle<T> getParent();
    
    /**
     * Gets the root of the JavaBean tree
     * 
     * @return The root of the JavaBean tree.  Will
     * only return null if the tree has not yet
     * been created
     */
    public T getChildRoot();
    
    /**
     * Returns true if this child copy can still
     * have merge called on it succesfully
     * 
     * @return true if it is still possible to
     * call merge (i.e., there has not been
     * a change to the parent tree since this
     * copy was made)
     */
    public boolean isMergeable();
    
    /**
     * Merges the changes made to this tree into
     * the parent tree
     */
    public void merge();

}
