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

package org.glassfish.hk2.configuration.hub.internal;

import java.io.PrintStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.glassfish.hk2.configuration.hub.api.BeanDatabase;
import org.glassfish.hk2.configuration.hub.api.Instance;
import org.glassfish.hk2.configuration.hub.api.Type;

/**
 * @author jwells
 *
 */
public class BeanDatabaseImpl implements BeanDatabase {
    private final long revision;
    private final HashMap<String, TypeImpl> types = new HashMap<String, TypeImpl>();

    /**
     * Creates a new, fresh database
     */
    /* package */ BeanDatabaseImpl(long revision) {
        this.revision = revision;
    }

    /* package */ BeanDatabaseImpl(long revision, BeanDatabase beanDatabase) {
        this(revision);

        for (Type type : beanDatabase.getAllTypes()) {
            types.put(type.getName(), new TypeImpl(type, ((WriteableTypeImpl) type).getHelper()));
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#getAllTypes()
     */
    @Override
    public synchronized Set<Type> getAllTypes() {
        return Collections.unmodifiableSet(new HashSet<Type>(types.values()));
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#getInstance(java.lang.String, java.lang.Object)
     */
    @Override
    public synchronized Instance getInstance(String type, String instanceKey) {
        Type t = getType(type);
        if (t == null) return null;

        return t.getInstance(instanceKey);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#getType(java.lang.String)
     */
    @Override
    public synchronized Type getType(String type) {
        return types.get(type);
    }

    /* package */ long getRevision() {
        return revision;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#dumpDatabase()
     */
    @Override
    public void dumpDatabase() {
        dumpDatabase(System.err);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#dumpDatabase(java.io.PrintStream)
     */
    @Override
    public void dumpDatabase(PrintStream output) {
        Utilities.dumpDatabase(this, output);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.configuration.hub.api.BeanDatabase#dumpDatabaseAsString()
     */
    @Override
    public String dumpDatabaseAsString() {
        return Utilities.dumpDatabaseAsString(this);
    }

    @Override
    public String toString() {
        return "BeanDatabaseImpl(" + revision + "," + System.identityHashCode(this) + ")";
    }
}
