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

package org.glassfish.hk2.configuration.hub.api;

import java.util.Set;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.TwoPhaseResource;

/**
 * A writeable version of a {@link BeanDatabase}. Types and instances can be
 * added to this in-memory database
 * 
 * @author jwells
 * 
 */
public interface WriteableBeanDatabase extends BeanDatabase {
    /**
     * Gets an unmodifiable set of all the types in the bean database
     * 
     * @return A non-null unmodifiable and possibly empty set of
     * all the types in the database
     */
    public Set<WriteableType> getAllWriteableTypes();
    
    /**
     * Adds a type of the given name
     * 
     * @param typeName
     *            The name of the type to add
     * @return The non-null type that has been added to the database
     */
    public WriteableType addType(String typeName);

    /**
     * Removed the given type and all of its instances from the database. The
     * set of changes will include the instances removed prior to the change
     * indicating that the type was removed
     * 
     * @param typeName
     *            The non-null type name
     * @return The type that was removed
     */
    public Type removeType(String typeName);

    /**
     * Gets a writeable type of the given name
     * 
     * @param typeName
     *            The non-null name of the type to fetch
     * @return The existing type, or null if the type does not already exist
     */
    public WriteableType getWriteableType(String typeName);

    /**
     * Gets or creates a writeable type with the given name
     * 
     * @param typeName
     *            The non-null name of the type to find or create
     * @return The non-null writeable type that was created or found
     */
    public WriteableType findOrAddWriteableType(String typeName);
    
    /**
     * Gets the commit message for this writeable bean database
     * @return The possibly null commit message for this
     * writeable bean database
     */
    public Object getCommitMessage();
    
    /**
     * Sets the commit message for this writeable bean database
     * @param commitMessage The possibly null commit message
     * for this writeable bean database
     */
    public void setCommitMessage(Object commitMessage);
    
    /**
     * Returns a two-phase resource that can be used by a
     * DynamicConfiguration to tie the transaction done by
     * this WriteableBeanDatabase into a commit done by the
     * DynamicConfiguration.  When the DynamicConfiguration
     * calls commit this WriteableBeanDatabase will be part
     * of the transaction
     * 
     * @return A non-null TwoPhaseResource to be used by a
     * DynamicConfiguration
     */
    public TwoPhaseResource getTwoPhaseResource();

    /**
     * This method should be called when the writeable database should become
     * the current database. All changes will be communicated to the listeners.
     * If the current database has been modified since this writeable database
     * was created then this method will throw an IllegalStateException.
     * This version of commit will use the commit message set on this writeable
     * bean database
     * 
     * @throws IllegalStateException if the current database has been modified
     * since this writeable database copy was created
     * @throws MultiException if there were user implementations of {@link BeanDatabaseUpdateListener}
     * that failed by throwing exceptions this exception will be thrown wrapping those exceptions
     */
    public void commit() throws IllegalStateException, MultiException;
    
    /**
     * This method should be called when the writeable database should become
     * the current database. All changes will be communicated to the listeners.
     * If the current database has been modified since this writeable database
     * was created then this method will throw an IllegalStateException.
     * This version of commit will use the commit message passed in rather than the
     * one set on this writeable bean database
     * 
     * @param commitMessage An object to pass to any {@link BeanDatabaseUpdateListener}
     * that is registered
     * @throws IllegalStateException if the current database has been modified
     * since this writeable database copy was created
     * @throws MultiException if there were user implementations of {@link BeanDatabaseUpdateListener}
     * that failed by throwing exceptions this exception will be thrown wrapping those exceptions
     */
    public void commit(Object commitMessage) throws IllegalStateException, MultiException;

}
