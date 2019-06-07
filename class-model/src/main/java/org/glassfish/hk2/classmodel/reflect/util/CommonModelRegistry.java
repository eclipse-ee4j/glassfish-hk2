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

package org.glassfish.hk2.classmodel.reflect.util;

import org.glassfish.hk2.classmodel.reflect.ParsingContext;
import org.objectweb.asm.ClassReader;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.service.packageadmin.ExportedPackage;
import org.osgi.service.packageadmin.PackageAdmin;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * @author Mahesh Kannan
 *
 */
public class CommonModelRegistry
    implements ResourceLocator {

    BundleContext ctx;

    PackageAdmin pkgAdmin;

    private static CommonModelRegistry _instance = new CommonModelRegistry();

    private CommonModelRegistry(){}

    public static CommonModelRegistry getInstance() {
        return _instance;
    }

    /*package*/
    void initialize(BundleContext ctx, PackageAdmin pkgAdmin) {
        this.ctx = ctx;
        this.pkgAdmin = pkgAdmin;
    }

    public boolean canLoadResources() {
        return pkgAdmin != null;
    }

    public void loadModel(ParsingContext ctx, String className) {
        int index = className.lastIndexOf('.');
        String packageName = index > 0 ? className.substring(0, index) : "";
        ExportedPackage pkg = pkgAdmin.getExportedPackage(packageName);

        if (pkg != null) {
            Bundle srcBundle = pkg.getExportingBundle();
            String resourceName = className.replace('.', '/');
            if (! resourceName.endsWith(".class"))
                resourceName += ".class";
            URL url = srcBundle.getResource(resourceName);
            byte[] data = null;
            if (url != null) {
                try {
                    InputStream is = url.openStream();
                    data = new byte[is.available()];
                    for (int remaining = data.length; remaining > 0; ) {
                        int read = is.read(data, data.length - remaining, remaining);
                        if (read > 0)
                            remaining -= read;
                    }

                    ByteArrayInputStream bis = new ByteArrayInputStream(data);
                    ClassReader cr = new ClassReader(bis);
                    cr.accept(ctx.getClassVisitor(url.toURI(), className), ClassReader.SKIP_DEBUG);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else {
//                System.out.println("** CommonModelRegistry::loadModel(" + resourceName + ") ==> ***NOT FOUND** "
//                        + "; pkg : " + pkg.toString() + "; bnd : " + srcBundle);
            }
        } else {
//            System.out.println("** CommonModelRegistry::loadModel NULL PACKAGE for: " + className);
        }
    }

    @Override
    public InputStream openResourceStream(String className) throws IOException {
        int index = className.lastIndexOf('/');
        String packageName = index > 0 ? className.substring(0, index) : "";
        ExportedPackage pkg = pkgAdmin.getExportedPackage(packageName.replace('/', '.'));

//        System.out.println("** CommonModelRegistry::openResourceStream called  for: " + className);

        InputStream inputStream = null;
        if (pkg != null) {
            Bundle srcBundle = pkg.getExportingBundle();
//            String resourceName = className.replace('.', '/');
//            if (! resourceName.endsWith(".class"))
//                resourceName += ".class";
            URL url = srcBundle.getResource(className);
            byte[] data = null;
            if (url != null) {
                try {
                    InputStream is = url.openStream();

                    data = new byte[is.available()];
                    for (int remaining = data.length; remaining > 0; ) {
                        int read = is.read(data, data.length - remaining, remaining);
                        if (read > 0)
                            remaining -= read;
                    }

                    inputStream = new ByteArrayInputStream(data);

                } catch (Exception ex) {
                    ex.printStackTrace();
                }

//                System.out.println("** CommonModelRegistry::loadModel("
//                        + className + ") ==> "
//                        + (data == null ? -1 : data.length));
            } else {
//                System.out.println("** CommonModelRegistry::loadModel("
//                        + className + ") ==> ***NOT FOUND** "
//                        + "; pkg : " + pkg.toString()
//                        + "; bnd : " + srcBundle
//                );
            }
        } else {
//            System.out.println("** CommonModelRegistry::loadModel NULL PACKAGE for: " + className);
        }

        return inputStream;
    }

    @Override
    public URL getResource(String className) {
        int index = className.lastIndexOf('/');
        String packageName = index > 0 ? className.substring(0, index) : "";
        ExportedPackage pkg = pkgAdmin.getExportedPackage(packageName.replace('/', '.'));

//        System.out.println("** CommonModelRegistry::getResource called  for: " + className);

        InputStream inputStream = null;
        if (pkg != null) {
            Bundle srcBundle = pkg.getExportingBundle();
//            String resourceName = className.replace('.', '/');
//            if (! resourceName.endsWith(".class"))
//                resourceName += ".class";
            return srcBundle.getResource(className);
        }

        return null;
    }
}
