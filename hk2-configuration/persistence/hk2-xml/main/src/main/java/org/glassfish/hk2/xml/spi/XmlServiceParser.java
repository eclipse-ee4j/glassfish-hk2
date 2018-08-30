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

package org.glassfish.hk2.xml.spi;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import javax.xml.bind.Unmarshaller;

import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.jvnet.hk2.annotations.Contract;

/**
 * If an implementation of this exists it will be used to parse the XML file rather
 * than the default implementation which uses JAXB
 * 
 * @author jwells
 */
@Contract
public interface XmlServiceParser {
    /**
     * The default Xml parsing service will have this name
     */
    public static final String DEFAULT_PARSING_SERVICE = "JAXBXmlParsingService";
    
    /**
     * A stream parsing service that does not use JAXB but an internal stream based
     * implementation
     */
    public static final String STREAM_PARSING_SERVICE = "StreamXmlParsingService";
    
    /**
     * This method must return an instance of the given class as the root of
     * an XML graph
     * 
     * @param rootModel The Model object of the root to be parsed
     * @param location The location of the file to parse
     * @param listener A listener that must be called via the contract of Unmarshaller.Listener
     * @param options optional (possibly null) options from the caller
     * @return The root object with all fields filled in from the given document
     */
    public <T> T parseRoot(Model rootModel, URI location, Unmarshaller.Listener listener, Map<String, Object> options) throws Exception;
    
    /**
     * This method must return an instance of the given class as the root of
     * an XML graph
     * 
     * @param rootModel The Model object of the root to be parsed
     * @param input A non-null input stream.  This stream will NOT be closed by this method
     * @param listener A listener that must be called via the contract of Unmarshaller.Listener
     * @param options optional (possibly null) options from the caller
     * @return The root object with all fields filled in from the given document
     */
    public <T> T parseRoot(Model rootModel, InputStream input, Unmarshaller.Listener listener, Map<String, Object> options) throws Exception;
    
    
    /**
     * This tells the system whether or not it needs to pregenerate all proxies
     * prior to parsing a document or if the proxies can be generated lazily
     * 
     * @return The value that indicates whether or not the proxies can be
     * loaded lazily or must be pre-generated prior to parsing a document
     */
    public PreGenerationRequirement getPreGenerationRequirement();
    
    /**
     * Marshalls this tree into the given stream.  Will hold the WRITE
     * lock of this tree while it does so that the tree cannot change
     * underneath while it is being written out.  It will use a basic
     * indentation and new-line scheme
     * 
     * @param outputStream A non-closed output stream.  This method will
     * not close the output stream
     * @param root The root of the tree to marshall
     * @param options optional (possibly null) options from the caller
     * @throws IOException On any exception that might happen
     */
    public <T> void marshal(OutputStream outputStream, XmlRootHandle<T> root, Map<String, Object> options) throws IOException;

}
