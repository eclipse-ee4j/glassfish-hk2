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
import org.glassfish.hk2.classmodel.reflect.util.*;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.jar.Manifest;
import java.util.logging.Level;

/**
 * scans all glassfish jar, glassfish must be installed in ~/glassfish
 */
@Ignore
public class GFInstallationTest {

    @Test
    public void foo() throws IOException {
        List<File> files = new ArrayList<File>();
        long startTime = System.currentTimeMillis();
        File home = new File(System.getProperty("user.home"));
        File gf = new File(home,"glassfish/modules");
        Assert.assertTrue(gf.exists());
        long start = System.currentTimeMillis();
        for (File f : gf.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        })) {
            files.add(f);
        }

        ParsingContext.Builder builder = new ParsingContext.Builder();
        builder.archiveSelector(new ArchiveSelector() {

            @Override
            public boolean selects(ArchiveAdapter adapter) {
                Manifest manifest = null;
                try {
                    manifest = adapter.getManifest();
                } catch (IOException e) {
                    return true;
                }
                String bundleName = manifest.getMainAttributes().getValue("Bundle-SymbolicName");
                if (bundleName.contains("auto-depends")) {
                    return true;
                }
                // if it is not auto-depends, must import it...
                String imports = manifest.getMainAttributes().getValue("Import-Package");
                if (imports!=null && imports.indexOf("hk2")==-1) {
                    //System.out.println("Ignoring service-less " + adapter.getName());
                    return false;
                }
                return true;
            }
        });

        builder.logger().setLevel(Level.FINE);
        final Set<String> annotations = new HashSet<String>();
        annotations.add("org.jvnet.hk2.annotations.Service");
        builder.config(new ParsingConfig() {
            final Set<String> empty = Collections.emptySet();

            @Override
            public Set<String> getAnnotationsOfInterest() {
                return annotations;
            }

            @Override
            public Set<String> getTypesOfInterest() {
                return empty;
            }

            @Override
            public boolean modelUnAnnotatedMembers() {
                return true;
            }
        });
        ParsingContext context = builder.build();
        Parser parser = new Parser(context);

        
        for (final File f: files) {
            parser.parse(f, new Runnable() {
                @Override
                public void run() {
                    System.out.println("Finished parsing " + f.getName());
                }
            });
        }
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
        /*
        AnnotationTypeImpl service = TypesImpl.all.annotations.getElement("Lorg/jvnet/hk2/annotations/Service;");
        for (RefType type : service.allAnnotatedTypes()) {
            System.out.println("My services are " + type.getName);
        }
        */
        
    }

    public static void main(String[] args) {
        try {
            (new GFInstallationTest()).foo();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

    }

}
