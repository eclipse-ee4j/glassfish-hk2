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

package org.glassfish.hk2.configuration.properties.test;

import java.io.File;

/**
 * A bean with a lot of useless stuff in it
 * 
 * @author jwells
 *
 */
public class FooBean {
    /** The default type name for this bean */
    public final static String TYPE_NAME = "FooBeanType";
    
    private boolean fooBool;
    private short fooShort;
    private int fooInt;
    private long fooLong;
    private float fooFloat;
    private double fooDouble;
    private char fooChar;
    private String fooString;
    private byte fooByte;
    private File fooFile;
    
    // The getter/setter will use SNMP
    private String snmpValue;
    
    /**
     * @return the fooBool
     */
    public boolean isFooBool() {
        return fooBool;
    }
    /**
     * @param fooBool the fooBool to set
     */
    public void setFooBool(boolean fooBool) {
        this.fooBool = fooBool;
    }
    /**
     * @return the fooShort
     */
    public short getFooShort() {
        return fooShort;
    }
    /**
     * @param fooShort the fooShort to set
     */
    public void setFooShort(short fooShort) {
        this.fooShort = fooShort;
    }
    /**
     * @return the fooInt
     */
    public int getFooInt() {
        return fooInt;
    }
    /**
     * @param fooInt the fooInt to set
     */
    public void setFooInt(int fooInt) {
        this.fooInt = fooInt;
    }
    /**
     * @return the fooLong
     */
    public long getFooLong() {
        return fooLong;
    }
    /**
     * @param fooLong the fooLong to set
     */
    public void setFooLong(long fooLong) {
        this.fooLong = fooLong;
    }
    /**
     * @return the fooFloat
     */
    public float getFooFloat() {
        return fooFloat;
    }
    /**
     * @param fooFloat the fooFloat to set
     */
    public void setFooFloat(float fooFloat) {
        this.fooFloat = fooFloat;
    }
    /**
     * @return the fooDouble
     */
    public double getFooDouble() {
        return fooDouble;
    }
    /**
     * @param fooDouble the fooDouble to set
     */
    public void setFooDouble(double fooDouble) {
        this.fooDouble = fooDouble;
    }
    /**
     * @return the fooChar
     */
    public char getFooChar() {
        return fooChar;
    }
    /**
     * @param fooChar the fooChar to set
     */
    public void setFooChar(char fooChar) {
        this.fooChar = fooChar;
    }
    /**
     * @return the fooString
     */
    public String getFooString() {
        return fooString;
    }
    /**
     * @param fooString the fooString to set
     */
    public void setFooString(String fooString) {
        this.fooString = fooString;
    }
    /**
     * @return the fooByte
     */
    public byte getFooByte() {
        return fooByte;
    }
    /**
     * @param fooByte the fooByte to set
     */
    public void setFooByte(byte fooByte) {
        this.fooByte = fooByte;
    }
    /**
     * @return the fooFile
     */
    public File getFooFile() {
        return fooFile;
    }
    /**
     * @param fooFile the fooFile to set
     */
    public void setFooFile(File fooFile) {
        this.fooFile = fooFile;
    }
    /**
     * @return the snmpValue
     */
    public String getSNMPValue() {
        return snmpValue;
    }
    /**
     * @param snmpValue the snmpValue to set
     */
    public void setSNMPValue(String snmpValue) {
        this.snmpValue = snmpValue;
    }
    
    

}
