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

package org.glassfish.hk2.pbuf.test.utilities;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorFactory;
import org.glassfish.hk2.extension.ServiceLocatorGenerator;
import org.glassfish.hk2.pbuf.api.PBufUtilities;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.jvnet.hk2.external.generator.ServiceLocatorGeneratorImpl;

/**
 * @author jwells
 *
 */
public class Utilities {
    private final static ServiceLocatorGenerator GENERATOR = new ServiceLocatorGeneratorImpl();
    
    public static ServiceLocator enableLocator(Class<?>... classes) {
        ServiceLocator retVal = ServiceLocatorFactory.getInstance().create(null, null, GENERATOR);
        
        ServiceLocatorUtilities.addClasses(retVal, classes);
        
        PBufUtilities.enablePBufService(retVal);
        
        // Twice tests idempotence
        PBufUtilities.enablePBufService(retVal);
        
        return retVal;
    }
    
    public static byte[] readStreamFully(InputStream stream) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            byte buffer[] = new byte[1000];
            
            int readLength;
            while ((readLength = stream.read(buffer)) > 0) {
                baos.write(buffer, 0, readLength);
            }
            
            baos.flush();
            
            return baos.toByteArray();
        }
        finally {
            baos.close();
        }
        
    }
    
    public static int getNumPBufLengthBytes(byte bytes[]) {
        int lcv = 0;
        for (byte b : bytes) {
            if ((b & 0x80) == 0) {
                return lcv + 1;
            }
            
            lcv++;
        }
        
        return lcv+1;
    }

}
