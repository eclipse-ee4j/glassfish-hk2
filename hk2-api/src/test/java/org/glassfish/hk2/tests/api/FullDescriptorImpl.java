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

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Singleton;

import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * For testing purposes
 * 
 * @author jwells
 *
 */
@Blue @Green @NotQualifierAnnotation @Red
public class FullDescriptorImpl extends DescriptorImpl implements MarkerInterface, MarkerInterface2, MarkerInterface3 {
    private final static Set<String> FULL_CONTRACTS = new LinkedHashSet<String>();
    /** Given name */
    public final static String FULL_NAME = "Full";
    private final static Map<String, List<String>> FULL_METADATA =
            new LinkedHashMap<String, List<String>>();
    /** Given key1 */
    public final static String FULL_KEY1 = "key1";
    /** Given key2 */
    public final static String FULL_KEY2 = "key2";
    /** Given value1 */
    public final static String FULL_VALUE1 = "value1";
    /** Given value2 */
    public final static String FULL_VALUE2 = "value2";
    private final static Set<String> FULL_ANNOTATIONS = new LinkedHashSet<String>();
    /** Given initial rank */
    public final static int FULL_INITIAL_RANK = -1;
    /** Given initial proxiable */
    public final static Boolean FULL_INITIAL_PROXIABLE = Boolean.FALSE;
    /** Given initial proxyForSameScope */
    public final static Boolean FULL_INITIAL_PROXY_FOR_SAME_SCOPE = Boolean.TRUE;
    /** Given initial service id */
    public final static Long FULL_INITIAL_SID = new Long(-2);
    /** Given initial locator id */
    public final static Long FULL_INITIAL_LID = new Long(-3);
    /** The name of the class analysis service */
    public final static String FULL_ANALYSIS_SERVICE = "jax-rs";
    
    static {
        FULL_CONTRACTS.add(FullDescriptorImpl.class.getName());
        FULL_CONTRACTS.add(MarkerInterface.class.getName());
        
        List<String> key1_values = new LinkedList<String>();
        key1_values.add(FULL_VALUE1);
        
        List<String> key2_values = new LinkedList<String>();
        key2_values.add(FULL_VALUE1);
        key2_values.add(FULL_VALUE2);
        
        FULL_METADATA.put(FULL_KEY1, key1_values);
        FULL_METADATA.put(FULL_KEY2, key2_values);
        
        FULL_ANNOTATIONS.add(Green.class.getName());
        FULL_ANNOTATIONS.add(Blue.class.getName());
    }
    
    /**
     * 
     */
    private static final long serialVersionUID = 5270371169142849733L;
    
    /**
     * For testing
     */
    public FullDescriptorImpl() {
        super(FULL_CONTRACTS,
                FULL_NAME,
                Singleton.class.getName(),
                FullDescriptorImpl.class.getName(),
                FULL_METADATA,
                FULL_ANNOTATIONS,
                DescriptorType.PROVIDE_METHOD,
                DescriptorVisibility.LOCAL,
                new HK2LoaderImpl(),
                FULL_INITIAL_RANK,
                FULL_INITIAL_PROXIABLE,
                FULL_INITIAL_PROXY_FOR_SAME_SCOPE,
                FULL_ANALYSIS_SERVICE,
                FULL_INITIAL_SID,
                FULL_INITIAL_LID);
    }

}
