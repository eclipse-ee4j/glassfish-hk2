/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.metadata.tests.stub.impl;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.jvnet.hk2.annotations.Service;
import org.jvnet.hk2.metadata.tests.stub.LargeInterface;

/**
 * @author jwells
 *
 */
@Service
public class NotUseableLargeInterface implements LargeInterface {

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#notOverridden(boolean)
     */
    @Override
    public boolean notOverridden(boolean param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodVoids()
     */
    @Override
    public void methodVoids() {
        throw new AssertionError("Must not be used");

    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodBoolean(boolean)
     */
    @Override
    public boolean methodBoolean(boolean param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodByte(byte)
     */
    @Override
    public byte methodByte(byte param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodChar(char)
     */
    @Override
    public char methodChar(char param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodDouble(double)
     */
    @Override
    public double methodDouble(double param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodFloat(float)
     */
    @Override
    public float methodFloat(float param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodInt(int)
     */
    @Override
    public int methodInt(int param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodInt(long)
     */
    @Override
    public long methodInt(long param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodShort(short)
     */
    @Override
    public short methodShort(short param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodDeclared(java.util.Map, java.lang.String, java.util.Random)
     */
    @Override
    public List<String> methodDeclared(Map<Object, String> param,
            String param1, Random param2) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodBooleanArray(boolean[])
     */
    @Override
    public boolean[] methodBooleanArray(boolean[] param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodByteArray(byte[])
     */
    @Override
    public byte[][][][] methodByteArray(byte[] param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodCharArray(char[][])
     */
    @Override
    public char[] methodCharArray(char[][] param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodDoubleArray(double[][][])
     */
    @Override
    public double[][] methodDoubleArray(double[][][] param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodFloatArray(float[][])
     */
    @Override
    public float[] methodFloatArray(float[][] param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodIntArray(int[])
     */
    @Override
    public int[][] methodIntArray(int[] param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodIntArray(long[][][][][])
     */
    @Override
    public long[] methodIntArray(long[][][][][] param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodShortArray(short)
     */
    @Override
    public short[] methodShortArray(short[] param) {
        throw new AssertionError("Must not be used");
    }

    /* (non-Javadoc)
     * @see org.jvnet.hk2.metadata.tests.stub.LargeInterface#methodDeclaredArray(java.util.Map[], java.lang.String[], java.util.Random[])
     */
    @Override
    public List<String>[] methodDeclaredArray(Map<Object, String>[] param,
            String[] param1, Random... param2) {
        throw new AssertionError("Must not be used");
    }

}
