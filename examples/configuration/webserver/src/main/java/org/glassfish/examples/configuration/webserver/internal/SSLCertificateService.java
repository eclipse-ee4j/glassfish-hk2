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

package org.glassfish.examples.configuration.webserver.internal;

import java.io.File;

import org.glassfish.examples.configuration.webserver.SSLCertificateBean;
import org.glassfish.hk2.configuration.api.Configured;
import org.glassfish.hk2.configuration.api.ConfiguredBy;
import org.jvnet.hk2.annotations.Service;

/**
 * This class represents an SSL certificate service.  It demonstrates how we can
 * have multiple certificates, each one based on a different configuration
 * bean.  In a true webserver this service might handle a real SSL certificate
 * It also demonstrates the use of &quot;$bean&quot; as a configured parameter,
 * which causes the entire bean to be injected rather than a field from
 * the bean
 * 
 * @author jwells
 *
 */
@Service @ConfiguredBy("SSLCertificateBean")
public class SSLCertificateService {
    @Configured("$bean")
    private SSLCertificateBean certificateBean;
    
    /**
     * Returns the location of the public certificate
     * 
     * @return The public certificate for this SSL service
     */
    public File getCertificate() {
        return certificateBean.getCertificateLocation();
    }

}
