/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

package com.sun.enterprise.module.common_impl;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

import com.sun.enterprise.module.ModulesRegistry;

/**
 * hk2 and modules usage tracing utilities.
 * @author Jerome Dochez
 * 
 */
public class TracingUtilities {

    private final static boolean enabled = Boolean.getBoolean("hk2.module.tracestate");
    
    
    public interface Loader {
        Class loadClass(String type) throws ClassNotFoundException;
    }

    public static boolean isEnabled() {
        return enabled;
    }
    
    public static File getLocation() {
        String location = System.getProperty("hk2.module.loglocation");
        if (location==null) {
            location = System.getProperty("user.dir");
        }
        File f = new File(location);
        if (f.isAbsolute()) {
            return f;
        } else {
            return new File(System.getProperty("user.dir"), location);
        }
    }

    public static void traceResolution(ModulesRegistry registry, long bundleId, String bundleName, Loader loader) {
        traceState(registry, "resolved", bundleId, bundleName, loader);
    }

    public static void traceStarted(ModulesRegistry registry, long bundleId, String bundleName, Loader loader) {
        traceState(registry, "started", bundleId, bundleName, loader);
    }

    public static void traceState(ModulesRegistry registry, String state, long bundleId, String bundleName, Loader loader) {
        File out = new File(getLocation(), state + "-" + bundleId+".log");
        Writer w = null;
        try {
            w = new FileWriter(out);
            w.append("\n");
            w.append("Module ["+ bundleId + "] " + state + " " + bundleName+"\n");
            String prefix="-";
            StackTraceElement[] stack = Thread.currentThread().getStackTrace();

            w.append("\n");
            w.append("-----------------------------------\n");
            w.append("Inhabitants / stack combination\n");
            w.append("-----------------------------------\n");

            String currentBundleName = bundleName;
            
            for (int i=0;i<stack.length;i++) {
                 {
                    // now let's find out the first non hk2 class asking for this...
                    int j=i+1;
                    for (;j<stack.length;j++) {
                        StackTraceElement caller = stack[j];
                        if (!caller.getClassName().contains("hk2")) {
                            break;
                        }
                    }


                }

            }
            w.append("\n");

            w.append("---------------------------\n");
            w.append("Complete thread stack Trace\n");
            w.append("---------------------------\n");

            for (StackTraceElement element : stack) {
                w.append(element.toString()+"\n");
            }


        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } finally {
            if (w!=null) {
                try {
                    w.close();
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
            }
        }

    }    
}
