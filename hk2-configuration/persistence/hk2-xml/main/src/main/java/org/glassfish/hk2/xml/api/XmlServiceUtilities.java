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

import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.configuration.hub.api.ManagerUtilities;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.DescriptorImpl;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.xml.internal.DomXmlParser;
import org.glassfish.hk2.xml.internal.XmlServiceImpl;
import org.glassfish.hk2.xml.jaxb.internal.JAXBXmlParser;
import org.glassfish.hk2.xml.spi.XmlServiceParser;

/**
 * Useful utilities for initializing the HK2 XmlService
 * 
 * @author jwells
 */
public class XmlServiceUtilities {
    private static boolean isDuplicateException(MultiException me) {
        for (Throwable th : me.getErrors()) {
            while (th != null) {
                if (th instanceof DuplicateServiceException) return true;
                
                th = th.getCause();
            }
        }
        
        return false;
    }

    /**
     * Enables Hk2 XmlServices in the given locator.  Will
     * also enable the HK2 Configuration Hub if the hub has
     * not already been started.  This operation is idempotent
     * in that if the named XmlService is already available in the
     * given locator then this method does nothing.
     * 
     * Only the JAXB XML parser will be added
     * 
     * @param locator The non-null locator to which to add
     * the {@link XmlService}
     */
    public static void enableXmlService(ServiceLocator locator) {
        ManagerUtilities.enableConfigurationHub(locator);
        
        try {
            ServiceLocatorUtilities.addClasses(locator, true, JAXBXmlParser.class);
        }
        catch (MultiException me) {
            if (!isDuplicateException(me)) {
                throw me;
            }
            
            // Pass through
        }
        
        enableAllFoundParsers(locator);
    }
    
    private static void enableAllFoundParsers(ServiceLocator locator) {
        List<ActiveDescriptor<?>> allParsers = locator.getDescriptors(BuilderHelper.createContractFilter(XmlServiceParser.class.getName()));
        for (ActiveDescriptor<?> parserDescriptor : allParsers) {
            String name = parserDescriptor.getName();
            if (name == null) continue;
            
            ActiveDescriptor<?> found = locator.getBestDescriptor(BuilderHelper.createNameAndContractFilter(XmlService.class.getName(), name));
            if (found != null) continue;
            
            DescriptorImpl di = BuilderHelper.createDescriptorFromClass(XmlServiceImpl.class);
            di.setName(name);
            di.setRanking(parserDescriptor.getRanking());
            
            ServiceLocatorUtilities.addOneDescriptor(locator, di, false);
        }
        
    }
    
    /**
     * This will enable all of the same xml parsers as
     * {@link #enableXmlService(ServiceLocator)} but will
     * set the rank of the stream-based xml parser to be
     * higher than that of the JAXB based one
     * 
     * @param locator The non-null locator to which to add
     * the {@link XmlService}
     */
    public static void enableDomXmlService(ServiceLocator locator) {
        ManagerUtilities.enableConfigurationHub(locator);
        
        try {
            ServiceLocatorUtilities.addClasses(locator, true, DomXmlParser.class);
        }
        catch (MultiException me) {
            if (!isDuplicateException(me)) {
                throw me;
            }
            
            // Pass through
        }
        
        enableAllFoundParsers(locator);
    }
}
