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

package org.glassfish.hk2.classmodel.reflect.test.ordering;

import org.glassfish.hk2.classmodel.reflect.Parser;
import org.glassfish.hk2.classmodel.reflect.ParsingContext;
import org.glassfish.hk2.classmodel.reflect.Type;
import org.glassfish.hk2.classmodel.reflect.util.InputStreamArchiveAdapter;
import org.junit.Ignore;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * main program to test a particular archive scanning
 * 
 */
@Ignore
public class JarTest {
    public static void main(String[] args) {
        if (args.length!=1) {
            System.out.println("usage : JarTest <path_to_jar_file>");
            return;
        }
        JarTest jt = new JarTest();
        try {
            jt.process(args[0]);
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }

    public void process(String path) throws IOException {
        File f = new File(path);
        if (!f.exists()) {
            System.out.println("File not found : " + path);
            return;
        }
        Logger logger = Logger.getAnonymousLogger();
        logger.setLevel(Level.FINE);
        ParsingContext pc = new ParsingContext.Builder().logger(logger).build();
        Parser parser = new Parser(pc);
        long start = System.currentTimeMillis();

        parser.parse(f, new Runnable() {
            @Override
            public void run() {
                System.out.println("Done parsing");
            }
        });
        try {
            parser.awaitTermination();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Collection<Type> types = pc.getTypes().getAllTypes();
        System.out.println("Finished parsing " + types.size() + " classes in " + (System.currentTimeMillis() - start) + "ms");

    }

}
