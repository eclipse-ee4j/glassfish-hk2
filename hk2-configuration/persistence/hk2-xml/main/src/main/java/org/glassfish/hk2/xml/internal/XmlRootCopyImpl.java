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

package org.glassfish.hk2.xml.internal;

import org.glassfish.hk2.xml.api.XmlHandleTransaction;
import org.glassfish.hk2.xml.api.XmlRootCopy;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.jaxb.internal.BaseHK2JAXBBean;

/**
 * @author jwells
 *
 */
public class XmlRootCopyImpl<T> implements XmlRootCopy<T> {
    private final XmlRootHandleImpl<T> parent;
    private final long basis;
    private final T copy;
    
    /* package */ XmlRootCopyImpl(XmlRootHandleImpl<T> parent, long basis, T copy) {
        if (copy == null) throw new IllegalStateException("Only a non-empty Handle can be copied");
        
        this.parent = parent;
        this.basis = basis;
        this.copy = copy;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.api.XmlRootCopy#getParent()
     */
    @Override
    public XmlRootHandle<T> getParent() {
        return parent;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.api.XmlRootCopy#getChildRoot()
     */
    @Override
    public T getChildRoot() {
        return copy;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.api.XmlRootCopy#isMergeable()
     */
    @Override
    public boolean isMergeable() {
        return (parent.getRevision() == basis);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.api.XmlRootCopy#merge()
     */
    @Override
    public void merge() {
        boolean success = false;
        XmlHandleTransaction<T> handle = parent.lockForTransaction();
        try {
            if (!isMergeable()) {
                throw new AssertionError("Parent has changed since copy was made, no merge possible");
            }
        
            BaseHK2JAXBBean copyBean = (BaseHK2JAXBBean) copy;
            BaseHK2JAXBBean original = (BaseHK2JAXBBean) parent.getRoot();
            
            Differences differences = Utilities.getDiff(original, copyBean);
            
            if (!differences.getDifferences().isEmpty()) {
                Utilities.applyDiff(differences, parent.getChangeInfo());
            }
            
            success = true;
        }
        finally {
            if (success) {
                handle.commit();
            }
            else {
                handle.abandon();
            }
        }
    }
    
    @Override
    public String toString() {
        return "XmlRootCopyImpl(" + parent + "," + basis + "," + copy + "," + System.identityHashCode(this) + ")";
    }
}
