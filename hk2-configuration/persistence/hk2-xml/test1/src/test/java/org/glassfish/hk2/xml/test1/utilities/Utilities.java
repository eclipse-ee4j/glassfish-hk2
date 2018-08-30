/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test1.utilities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.json.api.JsonUtilities;
import org.glassfish.hk2.pbuf.api.PBufUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.xml.api.XmlServiceUtilities;
import org.jvnet.hk2.external.generator.ServiceLocatorGeneratorImpl;

/**
 * @author jwells
 *
 */
public class Utilities {
    private final static ServiceLocatorGenerator GENERATOR = new ServiceLocatorGeneratorImpl();

    /**
     * Creates a fresh service locator with the XmlService added
     * 
     * @return A service locator with the XmlService added
     */
    public static ServiceLocator createLocator(Class<?>... classes) {
        ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(null, null, GENERATOR);
        
        ServiceLocatorUtilities.addClasses(retVal, classes);
        
        XmlServiceUtilities.enableXmlService(retVal);
        
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
        
        return retVal;
    }
    
    public static ServiceLocator createInteropLocator(Class<?>...classes) {
        ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(null, null, GENERATOR);
        
        ServiceLocatorUtilities.addClasses(retVal, classes);
        
        XmlServiceUtilities.enableDomXmlService(retVal);
        XmlServiceUtilities.enableXmlService(retVal);
        JsonUtilities.enableJsonService(retVal);
        PBufUtilities.enablePBufService(retVal);
        
        return retVal;
    }
    
    public static void writeBytesToFile(String fileName, byte writeMe[]) throws IOException {
        File f = new File(fileName);
        
        FileOutputStream fos = new FileOutputStream(f);
        try {
            fos.write(writeMe);
        }
        finally {
            fos.close();
        }
    }
    
    public static byte[] readBytesFromURL(URL url) throws IOException {
        InputStream is = url.openStream();
        try {
            byte buffer[] = new byte[2000];
            
            ByteArrayOutputStream bais = new ByteArrayOutputStream();
            try {
                
                int readSize;
                while ((readSize = is.read(buffer)) > 0) {
                    bais.write(buffer, 0, readSize);
                }
            }
            finally {
                bais.close();
            }
            
            return bais.toByteArray();
        }
        finally {
            is.close();
        }
        
    }

}
