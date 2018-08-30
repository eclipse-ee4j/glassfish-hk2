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

package org.glassfish.hk2.xml.integration.test.utilities;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.configuration.api.ConfigurationUtilities;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.xml.api.XmlServiceUtilities;
import org.jvnet.hk2.external.generator.ServiceLocatorGeneratorImpl;

/**
 * @author jwells
 *
 */
public class IntegrationTestUtilities {
    public final static ServiceLocatorGenerator GENERATOR = new ServiceLocatorGeneratorImpl();

    /**
     * Creates a fresh service locator with the XmlService added
     * 
     * @return A service locator with the XmlService added
     */
    public static ServiceLocator createLocator(Class<?>... classes) {
        ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(null, null, GENERATOR);
        
        ServiceLocatorUtilities.addClasses(retVal, classes);
        
        XmlServiceUtilities.enableXmlService(retVal);
        ConfigurationUtilities.enableConfigurationSystem(retVal);
        
        return retVal;
    }
    
    /**
     * Creates a fresh service locator with the XmlService added
     * 
     * @return A service locator with the XmlService added
     */
    public static ServiceLocator createDomLocator(Class<?>... classes) {
        ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(null, null, GENERATOR);
        
        ServiceLocatorUtilities.addClasses(retVal, classes);
        
        XmlServiceUtilities.enableDomXmlService(retVal);
        ConfigurationUtilities.enableConfigurationSystem(retVal);
        
        return retVal;
    }

}
