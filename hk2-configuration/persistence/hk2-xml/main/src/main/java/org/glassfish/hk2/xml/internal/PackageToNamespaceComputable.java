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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlNs;
import javax.xml.bind.annotation.XmlSchema;

import org.glassfish.hk2.utilities.cache.Computable;
import org.glassfish.hk2.utilities.cache.ComputationErrorException;

public class PackageToNamespaceComputable implements Computable<Package, Map<String, String>> {
    private final static Map<String, String> EMPTY = Collections.emptyMap();
    
    public static Map<String, String> calculateNamespaces(Class<?> clazz) {
        Package p = clazz.getPackage();
       
        return new PackageToNamespaceComputable().compute(p);
    }

    @Override
    public Map<String, String> compute(Package key)
            throws ComputationErrorException {
        XmlSchema xmlSchema = key.getAnnotation(XmlSchema.class);
        if (xmlSchema == null) return EMPTY;
        
        if (xmlSchema.xmlns() == null) return EMPTY;
        
        Map<String, String> retVal = new HashMap<String, String>();
        for (XmlNs xmlNs : xmlSchema.xmlns()) {
            retVal.put(xmlNs.prefix(), xmlNs.namespaceURI());
        }
        
        return retVal;
    }
}
