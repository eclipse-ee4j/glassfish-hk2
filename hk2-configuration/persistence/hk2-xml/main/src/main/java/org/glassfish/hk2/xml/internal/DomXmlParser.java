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

package org.glassfish.hk2.xml.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Provider;
import javax.inject.Singleton;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.spi.Model;
import org.glassfish.hk2.xml.spi.PreGenerationRequirement;
import org.glassfish.hk2.xml.spi.XmlServiceParser;

/**
 * @author jwells
 *
 */
@Singleton
@Visibility(DescriptorVisibility.LOCAL)
@Named(XmlServiceParser.STREAM_PARSING_SERVICE)
@Rank(1) // heuristically use this one in favor of others
public class DomXmlParser implements XmlServiceParser {
    private final XMLInputFactory xif = XMLInputFactory.newInstance();
    
    @Inject @Named(XmlServiceParser.STREAM_PARSING_SERVICE)
    private Provider<XmlServiceImpl> xmlService;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#parseRoot(java.lang.Class, java.net.URI, javax.xml.bind.Unmarshaller.Listener)
     */
    @Override
    public <T> T parseRoot(Model rootModel, URI location, Listener listener, Map<String, Object> options)
            throws Exception {
        InputStream urlStream = location.toURL().openStream();
        try {
            return parseRoot(rootModel, urlStream, listener, options);
        }
        finally {
            urlStream.close();
        }
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public <T> T parseRoot(Model rootModel, InputStream input, Listener listener, Map<String, Object> options)
            throws Exception {
        XMLStreamReader xmlStreamReader = xif.createXMLStreamReader(input);
        try {
            return (T) XmlStreamImpl.parseRoot(xmlService.get(), rootModel, xmlStreamReader, listener);
        }
        finally {
            xmlStreamReader.close();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#getPreGenerationRequirement()
     */
    @Override
    public PreGenerationRequirement getPreGenerationRequirement() {
        return PreGenerationRequirement.LAZY_PREGENERATION;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#marshall(java.io.OutputStream, org.glassfish.hk2.xml.api.XmlRootHandle)
     */
    @Override
    public <T> void marshal(OutputStream outputStream, XmlRootHandle<T> root, Map<String, Object> options)
            throws IOException {
        XmlStreamImpl.marshall(outputStream, root);
    }
}
