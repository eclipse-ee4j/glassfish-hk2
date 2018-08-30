/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.xml.test.dynamic.overlay;

import java.beans.PropertyChangeEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

import org.glassfish.hk2.configuration.hub.api.Change;
import org.glassfish.hk2.configuration.hub.api.Change.ChangeCategory;
import org.glassfish.hk2.utilities.general.GeneralUtilities;

/**
 * This is diff'd against the change that was received to make
 * it easier to build up test cases
 * @author jwells
 *
 */
public final class ChangeDescriptor {
    private final ChangeCategory category;
    private final String typeName;
    private final List<String> instanceKey;
    private final String props[];
    private final String instance;
    private final String arName; // add-remove name also the old name
    
    public ChangeDescriptor(ChangeCategory category, String type, String instance, String arName, String... props) {
        this.category = category;
        this.typeName = type;
        this.props = props;
        this.instanceKey = tokenizeInstanceKey(instance);
        this.instance = instance;
        this.arName = arName;
    }
    
    private static List<String> tokenizeInstanceKey(String instance) {
        LinkedList<String> retVal = new LinkedList<String>();
        
        if (instance == null) return retVal;
        
        StringTokenizer st = new StringTokenizer(instance, ".");
        while (st.hasMoreTokens()) {
            String nextToken = st.nextToken();
            if (nextToken.startsWith("XMLServiceUID")) continue;
            
            retVal.add(nextToken);
        }
        
        return retVal;
    }
    
    private String checkInstanceKey(String recievedKey) {
        List<String> receivedToken = tokenizeInstanceKey(recievedKey);
        
        if (instanceKey.size() != receivedToken.size()) {
            return "Expected instance cardinality of " + instanceKey.size() + " does not match received " + receivedToken.size() +
                    " (" + instanceKey + " , " + receivedToken + ")";
        }
        
        for (int lcv = 0; lcv < receivedToken.size(); lcv++) {
            String expected = instanceKey.get(lcv);
            String received = receivedToken.get(lcv);
            
            if ("*".equals(expected)) continue;
            if (!GeneralUtilities.safeEquals(expected, received)) {
              return "Failed in " + this + " at index " + lcv;
            }
        }
        
        return null;
    }
    
    String check(Change change) {
        if (!GeneralUtilities.safeEquals(category, change.getChangeCategory())) {
            return "Category is not the same expected=" + category + " got=" + change.getChangeCategory();
        }
        
        if (!GeneralUtilities.safeEquals(typeName, change.getChangeType().getName())) {
            return "Type is not the same expected=" + typeName + " got=" + change.getChangeType().getName();
        }
        
        String errorInstanceKey = checkInstanceKey(change.getInstanceKey());
        if (errorInstanceKey != null) return errorInstanceKey;
        
        List<PropertyChangeEvent> modifiedProperties = change.getModifiedProperties();
        if (modifiedProperties == null) {
            modifiedProperties = Collections.emptyList();
        }
        
        if (props.length != modifiedProperties.size()) {
            return "Expectect property length of " + props.length + " but got size " + modifiedProperties.size();
        }
        for (int lcv = 0; lcv < props.length; lcv++) {
            String prop = props[lcv];
            
            // Props is unordered, must go through list
            boolean found = false;
            for (int inner = 0; inner < modifiedProperties.size(); inner++) {
                if (GeneralUtilities.safeEquals(prop, modifiedProperties.get(inner).getPropertyName())) {
                    found = true;
                    break;
                }
            }
            
            if (!found) {
              return "Did not find prop " + prop + " in " + this;
            }
        }
        
        return null;
    }
    
    @Override
    public String toString() {
        return category + " type=" + typeName + " name=" + arName + " instanceKey=" + instanceKey + " props=" + Arrays.toString(props);
    }
    
}
