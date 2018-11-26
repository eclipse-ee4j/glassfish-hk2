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

package org.glassfish.hk2.pbuf.api;

import org.glassfish.hk2.api.DuplicateServiceException;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.pbuf.internal.PBufParser;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.xml.api.XmlServiceUtilities;

public class PBufUtilities {
    /** The name of the XmlService that uses PBuf as its encoding/decoding format */
    public final static String PBUF_SERVICE_NAME = "PBufXmlParser";
    
    /**
     * This option controls whether or not the marshaller/unmarshaller puts an
     * int32 at the front of the encoding for the length or expects the int32
     * when reading the stream.  The value must be of type {@link Boolean}.
     * By default this is true (an int32 is prepended for length when writing
     * and expected when reading).  If this value is false then the InputStream
     * given to the unmarshaller must end when the protobuf ends
     */
    public final static String PBUF_OPTION_INT32_HEADER = "PbufInt32Header";
    
    /**
     * If using a streaming protocol and the PBUF_OPTION_INT32_HEADER is true
     * (the default value) then you must allow this option to be set and used
     * in all calls using the same underlying stream.  The object put in here
     * will implement AutoCloseable but it is not necessary to call close on
     * it.  However, it must be used in all calls using the same Input or Output
     * streams as underlying protobuffer state is maintained in the object put
     * into this field by the parser implementation
     */
    public final static String PBUF_STREAMING_OPTION = "PbufStream";
    
    private static boolean isDup(MultiException me) {
        if (me == null) return false;
        
        for (Throwable th : me.getErrors()) {
            if (th instanceof DuplicateServiceException) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Idempotently enables the PBuf parser in the given ServiceLocator.
     * 
     * @param locator The non-null locator to enable the XmlParser
     */
    public static void enablePBufService(ServiceLocator locator) {
        try {
            ServiceLocatorUtilities.addClasses(locator, true, PBufParser.class);
        }
        catch (MultiException me) {
            if (!isDup(me)) throw me;
        }
        
        XmlServiceUtilities.enableXmlService(locator);
    }
}
