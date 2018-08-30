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

package org.glassfish.hk2.configuration.hub.test;

import java.io.Serializable;

/**
 * @author jwells
 *
 */
public class GenericJavaBean implements Serializable {
    /** For serialization */
    private static final long serialVersionUID = -7225319624147000913L;
    
    private String name;
    private String other;
    
    public GenericJavaBean() {
    }
    
    public GenericJavaBean(String name, String other) {
        this.name = name;
        this.other = other;
    }
    
    public String getName() {
        return name;
    }
    
    public String getOther() {
        return other;
    }
    
    public void setOther(String other) {
        this.other = other;
    }

}
