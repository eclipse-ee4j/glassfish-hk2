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

package org.acme.security;

import java.security.AccessControlException;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.Permission;
import java.security.PrivilegedAction;
import java.security.ProtectionDomain;
import java.util.LinkedList;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ValidationInformation;
import org.glassfish.hk2.api.Validator;

/**
 * This is the true validator, and will run against every lookup
 * and injection.
 * <p>
 * In order to bind or unbind services the caller must have AllPermission
 * <p>
 * 
 * @author jwells
 *
 */
public class ValidatorImpl implements Validator {
    private final static boolean VERBOSE = Boolean.parseBoolean(System.getProperty(
            "org.jvnet.hk2.examples.securitylockdown.debug", "false"));
    private final static String ACCESS_IN_PACKAGE = "accessClassInPackage.";
    
    /**
     * Ensures that anyone who wants to bind or unbind services has AllPermission
     * 
     * @return true if the caller on the stack has AllPermission
     */
    private boolean validateBindAndUnbind() {
        return checkPerm(new AllPermission());
    }
    
    private boolean validateLookupAPI(ActiveDescriptor<?> candidate) {
        List<Permission> lookupPermissions = getLookupPermissions(candidate);
     
        for (Permission lookupPermission : lookupPermissions) {
            if (!checkPerm(lookupPermission)) {
                if (VERBOSE) {
                    System.out.println("candidate " + candidate +
                        " LOOKUP FAILED the security check for permission " + lookupPermission);
                }
                return false;
            }
        }
        
        return true;
        
    }
    
    private boolean validateInjection(ActiveDescriptor<?> candidate, Injectee injectee) {
        List<Permission> lookupPermissions = getLookupPermissions(candidate);
        
        // If this is an Inject, get the protection domain of the injectee
        final Class<?> injecteeClass = injectee.getInjecteeClass();
        
        ProtectionDomain pd = AccessController.doPrivileged(new PrivilegedAction<ProtectionDomain>() {

            @Override
            public ProtectionDomain run() {
                return injecteeClass.getProtectionDomain();
            }
            
        });
        
        for (Permission lookupPermission : lookupPermissions) {
            if (!pd.implies(lookupPermission)) {
                if (VERBOSE) {
                    System.out.println("candidate " + candidate + " injectee " + injectee +
                        " LOOKUP FAILED the security check for " + lookupPermission);
                }
                
                return false;
            }
        }
        
        return true;
    }
    
    /**
     * Validates the lookup operation, with the two sub-cases of someone
     * calling the lookup API directly (injectee is null) or the lookup
     * occuring on behalf of an injection point (injectee is not null)
     * 
     * @param candidate The descriptor that would be used to create the object
     * to be injected or returned from the lookup
     * @param injectee If not null the injection point that will be injected into
     * @return true if the candidate can be looked up, false if the candidate should
     * not be available for lookup
     */
    private boolean validateLookup(ActiveDescriptor<?> candidate, Injectee injectee) {
        if (injectee == null) {
            return validateLookupAPI(candidate);
        }
        
        return validateInjection(candidate, injectee);
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Validator#validate(org.glassfish.hk2.api.Operation, org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.Injectee)
     */
    @Override
    public boolean validate(ValidationInformation info) {
        switch(info.getOperation()) {
        case BIND:
        case UNBIND:
            return validateBindAndUnbind();
        case LOOKUP:
            return validateLookup(info.getCandidate(), info.getInjectee());
        default:
            return false;
        }
    }
    
    private static Permission getLookupPermission(String packName) {
        RuntimePermission retVal = new RuntimePermission(ACCESS_IN_PACKAGE + packName);
        
        return retVal;
    }
    
    private static List<Permission> getLookupPermissions(ActiveDescriptor<?> ad) {
        LinkedList<Permission> retVal = new LinkedList<Permission>();
        
        for (String contract : ad.getAdvertisedContracts()) {
            int index = contract.lastIndexOf('.');
            String packName = contract.substring(0, index);
            retVal.add(getLookupPermission(packName));
        }
        
        return retVal;
    }
    
    private static boolean checkPerm(Permission p) {
        try {
            AccessController.checkPermission(p);
            return true;
        }
        catch (AccessControlException ace) {
            return false;
        }
    }

}
