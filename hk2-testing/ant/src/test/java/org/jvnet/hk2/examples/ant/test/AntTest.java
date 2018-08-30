/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.examples.ant.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;

import org.glassfish.hk2.utilities.DescriptorImpl;
import org.junit.Assert;
import org.junit.Test;
import org.jvnet.hk2.examples.ant.services.Service1;

/**
 * @author jwells
 *
 */
public class AntTest {
    @Test
    public void testWasGenerated() throws IOException {
        ClassLoader loader = getClass().getClassLoader();
        
        Enumeration<URL> defaultFiles = loader.getResources("META-INF/hk2-locator/default");
        
        while (defaultFiles.hasMoreElements()) {
            URL url = defaultFiles.nextElement();
            
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            try {
                DescriptorImpl di = new DescriptorImpl();
                
                while (di.readObject(reader)) {
                    if (di.getImplementation().equals(Service1.class.getName())) {
                        // Test passes
                        return;
                    }
                    
                }
                
            }
            finally {
                reader.close();
            }
        }
        
        Assert.fail("Did not find Service1 in the set of descriptors, the default file was not properly generated");
        
    }
}
