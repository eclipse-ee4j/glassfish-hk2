/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.spi;

import java.io.Serializable;

import javax.xml.namespace.QName;

/**
 * @author jwells
 *
 */
public interface Model extends Serializable {
    /**
     * @return the originalInterface
     */
    public String getOriginalInterface();
    
    /**
     * @return the original interface as a class
     */
    public Class<?> getOriginalInterfaceAsClass();

    /**
     * @return the translatedClass
     */
    public String getTranslatedClass();

    /**
     * @return the rootName
     */
    public QName getRootName();

    /**
     * @return the keyProperty
     */
    public QName getKeyProperty();
    
    /**
     * Gets the class of the proxy for this bean
     * 
     * @return The class of the generated proxy
     */
    public Class<?> getProxyAsClass();
}
