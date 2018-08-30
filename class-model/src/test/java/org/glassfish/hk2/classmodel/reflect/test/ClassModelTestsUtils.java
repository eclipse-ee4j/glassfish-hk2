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

import org.glassfish.hk2.classmodel.reflect.Parser;
import org.glassfish.hk2.classmodel.reflect.ParsingContext;
import org.glassfish.hk2.classmodel.reflect.Type;
import org.glassfish.hk2.classmodel.reflect.Types;
import org.glassfish.hk2.classmodel.reflect.util.ParsingConfig;
import org.junit.Assert;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Created by IntelliJ IDEA.
 * User: dochez
 * Date: Aug 12, 2010
 * Time: 7:01:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class ClassModelTestsUtils {

    static Types types = null;

    private final static ClassModelTestsUtils instance = new ClassModelTestsUtils();


    public static Types getTypes() throws IOException, InterruptedException {

        synchronized(instance) {

            if (types == null) {
                File userDir = new File(System.getProperty("user.dir"));
                File modelDir = new File(userDir, "target" + File.separator + "test-classes");

                if (modelDir.exists()) {
                    ParsingContext pc = (new ParsingContext.Builder().config(new ParsingConfig() {
                        @Override
                        public Set<String> getAnnotationsOfInterest() {
                            return Collections.emptySet();
                        }

                        @Override
                        public Set<String> getTypesOfInterest() {
                            return Collections.emptySet();
                        }

                        @Override
                        public boolean modelUnAnnotatedMembers() {
                            return true;
                        }
                    })).build();
                    Parser parser = new Parser(pc);

                    parser.parse(modelDir, null);
                    Exception[] exceptions = parser.awaitTermination(100, TimeUnit.SECONDS);
                    if (exceptions!=null) {
                        for (Exception e : exceptions) {
                            System.out.println("Found Exception ! : " +e);
                        }
                        Assert.assertTrue("Exceptions returned", exceptions.length==0);
                    }
                    types = pc.getTypes();
                }
            }
        }
        return types;
    }

}
