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

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;

import org.glassfish.hk2.configuration.hub.api.BeanDatabase;
import org.glassfish.hk2.configuration.hub.api.Type;
import org.glassfish.hk2.utilities.reflection.Pretty;

/**
 * @author jwells
 *
 */
public class Utilities {
    /**
     * Dumps the database given
     * 
     * @param database The DB to dump
     * @param stream the stream to dump it to
     */
    public static void dumpDatabase(BeanDatabase database, PrintStream stream) {
        for (Type type : database.getAllTypes()) {
            Set<String> instanceNames = type.getInstances().keySet();
            
            stream.println(type.getName() + " -> " + Pretty.collection(instanceNames));
        }
        
    }
    
    public static String dumpDatabaseAsString(BeanDatabase database) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream printer = new PrintStream(baos);
        try {
            dumpDatabase(database, printer);
                
            printer.close();
                
            return new String(baos.toByteArray(), StandardCharsets.UTF_8);
        }
        finally {
            printer.close();
        }
    }

}
