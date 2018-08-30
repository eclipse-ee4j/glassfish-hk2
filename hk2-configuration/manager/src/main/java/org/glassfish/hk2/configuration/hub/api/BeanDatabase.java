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

import java.io.PrintStream;
import java.util.Set;

/**
 * A database of beans organized as types, where a type
 * can have multiple instances of a configuration bean
 * 
 * @author jwells
 *
 */
public interface BeanDatabase {
    /**
     * Gets an unmodifiable set of all the types in the bean database
     * 
     * @return A non-null unmodifiable and possibly empty set of
     * all the types in the database
     */
    public Set<Type> getAllTypes();
    
    /**
     * Gets the type with the given name
     * 
     * @param type The non-null name
     * @return The type corresponding to the given name.  May return null
     */
    public Type getType(String type);
    
    /**
     * Returns the instance with the given instanceKey from the
     * type with the given name
     * 
     * @param type The non-null name of the type to get the instance from
     * @param instanceKey The non-null key of the instance
     * @return The bean from the given type with the given name.  Will return
     * null if the type does not exist or an instance with that key does not exist
     */
    public Instance getInstance(String type, String instanceKey);
    
    /**
     * Dumps the type and instance names to stderr
     */
    public void dumpDatabase();
    
    /**
     * Dumps the type and instance names to the given stream
     * @param output - The non-null outut stream to write the database to
     */
    public void dumpDatabase(PrintStream output);
    
    /**
     * Dumps the type and instance names to a String for debugging
     * 
     * @return A string with all type and instance names
     */
    public String dumpDatabaseAsString();

}
