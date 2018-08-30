/*
 * Copyright (c) 2010, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.classmodel.reflect.test;

import org.glassfish.hk2.classmodel.reflect.*;
import org.glassfish.hk2.classmodel.reflect.impl.AnnotationTypeImpl;
import org.glassfish.hk2.classmodel.reflect.util.ResourceLocator;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

/**
 * Simple test to use the locator pattern.
 */
public class LocatorTest {

    @Test
    @Ignore
    public void testLocator() throws IOException {
        List<URL> files = new ArrayList<URL>();
        long startTime = System.currentTimeMillis();
        File home = new File(System.getProperty("user.home"));
        File gf = new File(home, "glassfish/modules");
        Assert.assertTrue(gf.exists());
        long start = System.currentTimeMillis();
        for (File f : gf.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                // I am parsing kernel, so don't add it to the cp
                if (name.endsWith("kernel.jar")) return false;
                
                return name.endsWith(".jar");
            }
        })) {
            files.add(f.toURI().toURL());
        }

        final URLClassLoader cl = new URLClassLoader(files.toArray(new URL[files.size()]), this.getClass().getClassLoader());

        ParsingContext.Builder builder = new ParsingContext.Builder();
        builder.logger().setLevel(Level.FINE);
        builder.locator(new ResourceLocator() {
            @Override
            public URL getResource(String name) {
                if (name.indexOf(".")==-1) return null; // intrinsic types.
                if (name.startsWith("java/")) return null; // no jdk class parsing.
                return cl.getResource(name);
            }

            @Override
            public InputStream openResourceStream(String name)
                throws IOException {
              return cl.getResourceAsStream(name);
            }
        });
        ParsingContext context = builder.build();
        Parser parser = new Parser(context);

        parser.parse(new File(gf, "kernel.jar"), new Runnable() {
            @Override
            public void run() {
                System.out.println("Finished parsing kernel.jar ");
            }
        });
        try {
            Exception[] faults = parser.awaitTermination(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        System.out.println("Found " + files.size() + " files in " + (System.currentTimeMillis() - start));
        for (Type t : context.getTypes().getAllTypes()) {
            if (t instanceof AnnotationTypeImpl) {
                System.out.println("Found annotation : " + ((AnnotationTypeImpl) t).getName() + " in " + t.getDefiningURIs());
            }
        }
        System.out.println("parsed " + files.size() + " in " + (System.currentTimeMillis() - startTime) + " ms");

        AnnotationType at = context.getTypes().getBy(AnnotationType.class, "org.jvnet.hk2.annotations.Contract");
        for (AnnotatedElement ae : at.allAnnotatedTypes()) {
            System.out.println(ae.getName() + " is a contract ");
            if (ae instanceof InterfaceModel) {
                InterfaceModel im = (InterfaceModel) ae;
                for (ClassModel cm : im.allImplementations()) {
                    if (cm.getAnnotation("org.jvnet.hk2.annotations.Service")!=null) {
                        System.out.println("And  " + cm.getName() + " is a service provider ");
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        try {
            (new LocatorTest()).testLocator();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }
}
