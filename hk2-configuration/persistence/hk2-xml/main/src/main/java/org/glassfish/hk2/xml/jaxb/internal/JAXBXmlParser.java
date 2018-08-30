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

package org.glassfish.hk2.xml.jaxb.internal;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URI;
import java.util.Map;

import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.Unmarshaller.Listener;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.spi.Model;
import org.glassfish.hk2.xml.spi.PreGenerationRequirement;
import org.glassfish.hk2.xml.spi.XmlServiceParser;

/**
 * This is the default implementation
 * 
 * @author jwells
 */
@Singleton
@Named(XmlServiceParser.DEFAULT_PARSING_SERVICE)
@Visibility(DescriptorVisibility.LOCAL)
public class JAXBXmlParser implements XmlServiceParser {

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#parseRoot(java.lang.Class, java.net.URI, javax.xml.bind.Unmarshaller.Listener)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T parseRoot(Model rootModel, URI location, Listener listener, Map<String, Object> options) throws Exception {
        Class<?> clazz = rootModel.getProxyAsClass();
        
        JAXBContext context = JAXBContext.newInstance(clazz);
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setListener(listener);
        
        T root = (T) unmarshaller.unmarshal(location.toURL());
        
        return root;
    }
    
    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#parseRoot(java.lang.Class, java.net.URI, javax.xml.bind.Unmarshaller.Listener)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T parseRoot(Model rootModel, InputStream input, Listener listener, Map<String, Object> options) throws Exception {
        Class<?> clazz = rootModel.getProxyAsClass();
        
        JAXBContext context = JAXBContext.newInstance(clazz);
        
        Unmarshaller unmarshaller = context.createUnmarshaller();
        unmarshaller.setListener(listener);
        
        T root = (T) unmarshaller.unmarshal(input);
        
        return root;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#getPreGenerationRequirement()
     */
    @Override
    public PreGenerationRequirement getPreGenerationRequirement() {
        return PreGenerationRequirement.MUST_PREGENERATE;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#marshall(java.io.OutputStream, org.glassfish.hk2.xml.api.XmlRootHandle)
     */
    @Override
    public <T> void marshal(OutputStream outputStream, XmlRootHandle<T> rootHandle, Map<String, Object> options)
            throws IOException {
        T root = rootHandle.getRoot();
        if (root == null) return;
        
        XmlHk2ConfigurationBean xmlBean = (XmlHk2ConfigurationBean) root;
        Model model = xmlBean._getModel();
        
        Class<?> clazz = model.getProxyAsClass();
        
        try {
            JAXBContext context = JAXBContext.newInstance(clazz);
            
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            
            marshaller.marshal(root, outputStream);
        }
        catch (RuntimeException re) {
            throw new IOException(re);
        }
        catch (Exception e) {
            throw new IOException(e);
        }
    }

}
