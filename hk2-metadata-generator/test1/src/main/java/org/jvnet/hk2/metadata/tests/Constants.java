/*
 * Copyright (c) 2015, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.metadata.tests;

/**
 * @author jwells
 *
 */
public class Constants {
    public final static String GENERATE_METHOD_CREATE_IMPL = "com.acme.service.GenerateMethodImpl";
    public final static String GENERATE_METHOD_CREATE_CONTRACT = "com.acme.api.GenerateMethod";
    public final static String GENERATE_METHOD_CREATE_NAME1 = "name1";
    public final static String GENERATE_METHOD_CREATE_NAME2 = "name2";
    public final static String GENERATE_METHOD_CREATE_NAME3 = "name3";
    public final static String GENERATE_METHOD_CREATE_NAME4 = "name4";
    public final static String GENERATE_METHOD_CREATE_NAME5 = "name5";
    
    public final static String GENERATE_METHOD_DELETE_IMPL = "com.acme.service.DeleteImpl";
    public final static String GENERATE_METHOD_DELETE_CONTRACT = "com.acme.api.GenerateMethod";
    public final static String GENERATE_METHOD_DELETE_SCOPE = "javax.inject.Singleton";
    
    // metadata constants
    public final static String KEY1 = "key1";
    public final static String VALUE1 = "value1";
    public final static String KEY2 = "key2";
    public final static String VALUE2 = "value2";
    public final static String KEY3 = "key3";
    public final static String VALUE3 = "3";
    public final static String KEY4 = "key4";
    public final static String VALUE4 = Constants.class.getName();
    public final static String KEY5 = "key5";
    public final static String VALUE5_1 = "5_1";
    public final static String VALUE5_2 = "5_2";
    public final static String VALUE5_3 = "5_3";
    public final static String KEY6 = "key6";
    public final static long VALUE6_1 = 6001L;
    public final static long VALUE6_2 = 6002L;
    public final static long VALUE6_3 = 6003L;
    
    /** The name for non-defaulted things */
    public final static String NON_DEFAULT_NAME = "non-default-name";
    
    /** The rank to use when testing for rank */
    public final static int RANK = 13;
    
    /** The rank to use when testing for rank on factory method */
    public final static int FACTORY_METHOD_RANK = -1;
    
    /** A custom analyzer for a descriptor */
    public final static String CUSTOM_ANALYZER = "CustomAnalyzer";

}
