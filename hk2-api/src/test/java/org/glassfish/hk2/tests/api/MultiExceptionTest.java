/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.tests.api;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.LinkedList;
import java.util.List;

import junit.framework.Assert;

import org.glassfish.hk2.api.MultiException;
import org.junit.Test;

/**
 * @author jwells
 *
 */
public class MultiExceptionTest {
    private final static String EXPECTED = "expected";
    
    private final static String E1 = "E1";
    private final static String E2 = "E2";
    
    /**
     * Tests that I can create a no-args multi exception
     */
    @Test
    public void testNoArgsMultiException() {
        MultiException me = new MultiException();
        
        Assert.assertTrue(me.getErrors().isEmpty());
        
        Assert.assertTrue(me.toString().contains("MultiException"));
    }
    
    /**
     * Tests that I can create a single throwable multi exception
     */
    @Test
    public void testSingleThrowableMultiException() {
        IllegalArgumentException iae = new IllegalArgumentException(EXPECTED);
        
        MultiException me = new MultiException(iae);
        
        List<Throwable> ths = me.getErrors();
        Assert.assertEquals(1, ths.size());
        Assert.assertEquals(iae, ths.get(0));
        
        Assert.assertTrue(me.toString().contains(EXPECTED));
    }
    
    /**
     * Tests that I can create a multi throwable multi exception
     */
    @Test
    public void testMultiThrowableMultiException() {
        List<Throwable> putMeIn = new LinkedList<Throwable>();
        
        IllegalArgumentException iae = new IllegalArgumentException(E1);
        IllegalStateException ise = new IllegalStateException(E2);
        
        putMeIn.add(iae);
        putMeIn.add(ise);
        
        MultiException me = new MultiException(putMeIn);
        
        List<Throwable> ths = me.getErrors();
        Assert.assertEquals(2, ths.size());
        Assert.assertEquals(iae, ths.get(0));
        Assert.assertEquals(ise, ths.get(1));
        
        Assert.assertTrue(me.toString().contains(E1));
        Assert.assertTrue(me.toString().contains(E2));
    }
    
    /**
     * Tests that I can create a multi throwable multi exception
     */
    @Test
    public void testPrintException() {
        List<Throwable> putMeIn = new LinkedList<Throwable>();
        
        IllegalArgumentException iae = new IllegalArgumentException(E1);
        IllegalStateException ise = new IllegalStateException(E2);
        
        putMeIn.add(iae);
        putMeIn.add(ise);
        
        MultiException me = new MultiException(putMeIn);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream writer = new PrintStream(baos);
        
        me.printStackTrace(writer);
        
        writer.close();
        
        String asString = baos.toString();
        
        Assert.assertTrue(asString.contains(E1));
        Assert.assertTrue(asString.contains(E2));
    }
    
    /**
     * Tests that I can create a multi throwable multi exception
     */
    @Test
    public void testPrintExceptionPrintWriter() {
        List<Throwable> putMeIn = new LinkedList<Throwable>();
        
        IllegalArgumentException iae = new IllegalArgumentException(E1);
        IllegalStateException ise = new IllegalStateException(E2);
        
        putMeIn.add(iae);
        putMeIn.add(ise);
        
        MultiException me = new MultiException(putMeIn);
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintWriter writer = new PrintWriter(baos);
        
        me.printStackTrace(writer);
        
        writer.close();
        
        String asString = baos.toString();
        
        Assert.assertTrue(asString.contains(E1));
        Assert.assertTrue(asString.contains(E2));
    }
    
    /**
     * Tests that I can create a multi throwable multi exception
     */
    @Test
    public void testGetMessage() {
        List<Throwable> putMeIn = new LinkedList<Throwable>();
        
        IllegalArgumentException iae = new IllegalArgumentException(E1);
        IllegalStateException ise = new IllegalStateException(E2);
        
        putMeIn.add(iae);
        putMeIn.add(ise);
        
        MultiException me = new MultiException(putMeIn);
        
        String asString = me.getMessage();
        
        Assert.assertTrue(asString.contains(E1));
        Assert.assertTrue(asString.contains(E2));
    }
    
    /**
     * Tests that I can create a multi throwable multi exception
     */
    @Test
    public void testToString() {
        List<Throwable> putMeIn = new LinkedList<Throwable>();
        
        IllegalArgumentException iae = new IllegalArgumentException(E1);
        IllegalStateException ise = new IllegalStateException(E2);
        
        putMeIn.add(iae);
        putMeIn.add(ise);
        
        MultiException me = new MultiException(putMeIn);
        
        String asString = me.toString();
        
        Assert.assertTrue(asString.contains(E1));
        Assert.assertTrue(asString.contains(E2));
    }

    /**
     * Tests that I can serialize a multi exception
     */
    @Test
    public void testSerializeMultiException() throws ClassNotFoundException, IOException {
        final MultiException me = new MultiException();
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        final ObjectOutputStream oos = new ObjectOutputStream(baos);
        oos.writeObject(me);
        final byte[] bits = baos.toByteArray();
        Assert.assertNotNull(bits);
        Assert.assertTrue(bits.length > 0);
        final ByteArrayInputStream bais = new ByteArrayInputStream(bits);
        final ObjectInputStream ois = new ObjectInputStream(bais);
        final Object lazarus = ois.readObject();
        Assert.assertTrue(lazarus instanceof MultiException);
    }
    
    /**
     * Tests that we can concurrently access the list of errors.  This
     * test will fail out with a ConcurrentModificationException
     * if the implementation is bad
     * 
     * @throws InterruptedException
     */
    @Test
    public void testConcurrentAccessOfErrors() throws InterruptedException {
        MultiException me = new MultiException(new IllegalStateException("Initial Exception"));
        
        Thread t = new Thread(new ExceptionChangerRunner(20, me));
        
        t.start();
        
        for (Throwable th : me.getErrors()) {
            Thread.sleep(10);
            
            Assert.assertNotNull(th);
        }
    }
    
    private static class ExceptionChangerRunner implements Runnable {
        private final int numToAdd;
        private final MultiException me;
        
        private ExceptionChangerRunner(int numToAdd, MultiException me) {
            this.numToAdd = numToAdd;
            this.me = me;
        }

        /* (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            for (int lcv = 0; lcv < numToAdd; lcv++) {
                me.addError(new IllegalStateException("Adding exception " + lcv));
                
                try {
                    Thread.sleep(5);
                }
                catch (InterruptedException ie) {
                    throw new RuntimeException(ie);
                }
                
            }
            
        }
        
    }

}
