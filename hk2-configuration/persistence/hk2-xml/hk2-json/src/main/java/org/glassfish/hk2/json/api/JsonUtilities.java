/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.json.api;

import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.json.internal.JsonParser;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.api.XmlServiceUtilities;

public class JsonUtilities {
    /**
     * The name of the XmlService that will be added
     */
    public static final String JSON_SERVICE_NAME = "JsonService";
    
    private static boolean isDup(MultiException me) {
        if (me == null) return false;
        
        for (Throwable th : me.getErrors()) {
            if (th instanceof DuplicateServiceException) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * This method will make available a {@link XmlService} named
     * {@link #JSON_SERVICE_NAME} to be used to parse JSON
     * formatted data.  It will ensure that the XmlService
     * subsystem is properly initialized as per
     * {@link XmlServiceUtilities#enableXmlService(ServiceLocator)}
     * 
     * @param locator The non-null locator on which to enable
     * a Json version of the {@link XmlService}
     */
    public static void enableJsonService(ServiceLocator locator) {
        try {
            ServiceLocatorUtilities.addClasses(locator, true, JsonParser.class);
        }
        catch (MultiException me) {
            if (!isDup(me)) throw me;
        }
        
        XmlServiceUtilities.enableXmlService(locator);
    }
}
