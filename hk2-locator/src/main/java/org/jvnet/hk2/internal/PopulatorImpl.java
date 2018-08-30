/*
 * Copyright (c) 2013, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.jvnet.hk2.internal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorFileFinder;
import org.glassfish.hk2.api.DescriptorFileFinderInformation;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.PopulatorPostProcessor;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ClasspathDescriptorFileFinder;
import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * Implementation of the Populator for DynamicConfigurationService
 * 
 * @author jwells
 */
public class PopulatorImpl implements Populator {
    private final ServiceLocator serviceLocator;
    private final DynamicConfigurationService dcs;
    
    /* package */ PopulatorImpl(ServiceLocator serviceLocator,
            DynamicConfigurationService dcs) {
        this.serviceLocator = serviceLocator;
        this.dcs = dcs;
    }

    @Override
    public List<ActiveDescriptor<?>> populate(DescriptorFileFinder fileFinder,
            PopulatorPostProcessor... postProcessors) throws IOException {
        List<ActiveDescriptor<?>> descriptors = new LinkedList<ActiveDescriptor<?>> ();

        if (fileFinder == null) {
            fileFinder = serviceLocator.getService(DescriptorFileFinder.class);
            if (fileFinder == null) return descriptors;
        }
        
        if (postProcessors == null) postProcessors = new PopulatorPostProcessor[0];
        
        List<InputStream> descriptorFileInputStreams;
        List<String> descriptorInformation = null;
        try {
            descriptorFileInputStreams = fileFinder.findDescriptorFiles();
            if (fileFinder instanceof DescriptorFileFinderInformation) {
                DescriptorFileFinderInformation dffi = (DescriptorFileFinderInformation) fileFinder;
                
                descriptorInformation = dffi.getDescriptorFileInformation();
                if (descriptorInformation != null && 
                        (descriptorInformation.size() != descriptorFileInputStreams.size())) {
                    throw new IOException("The DescriptorFileFinder implementation " +
                            fileFinder.getClass().getName() + " also implements DescriptorFileFinderInformation, " +
                            "however the cardinality of the list returned from getDescriptorFileInformation (" +
                            descriptorInformation.size() + ") does not equal the cardinality of the list " +
                            "returned from findDescriptorFiles (" + descriptorFileInputStreams.size() + ")");
                }
            }
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Throwable th) {
            throw new MultiException(th);
        }
        
        Collector collector = new Collector();

        DynamicConfiguration config = dcs.createDynamicConfiguration();

        int lcv = 0;
        for (InputStream is : descriptorFileInputStreams) {
            String identifier = (descriptorInformation == null) ? null : descriptorInformation.get(lcv) ;
            lcv++;

            BufferedReader br = new BufferedReader(new InputStreamReader(is));

            try {
                boolean readOne = false;

                do {
                    DescriptorImpl descriptorImpl = new DescriptorImpl();

                    try {
                        readOne = descriptorImpl.readObject(br);
                    }
                    catch (IOException ioe) {
                        if (identifier != null) {
                            collector.addThrowable(new IOException("InputStream with identifier \"" + identifier + "\" failed", ioe));
                        }
                        else {
                            collector.addThrowable(ioe);
                        }
                    }

                    if (readOne) {
                            
                        for (PopulatorPostProcessor pp : postProcessors) {
                            try {
                                descriptorImpl = pp.process(serviceLocator, descriptorImpl);
                            }
                            catch (Throwable th) {
                                if (identifier != null) {
                                    collector.addThrowable(new IOException("InputStream with identifier \"" + identifier + "\" failed", th));
                                }
                                else {
                                    collector.addThrowable(th);
                                }
                                descriptorImpl = null;
                            }

                            if (descriptorImpl == null) {
                                break;
                            }
                        }
                            
                        if (descriptorImpl != null) {
                            descriptors.add(config.bind(descriptorImpl, false));
                        }

                    }
                } while (readOne);

            } finally {
                br.close();
            }
        }
        
        // Prior to commit!
        collector.throwIfErrors();

        config.commit();

        return descriptors;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.Populator#populate()
     */
    @Override
    public List<ActiveDescriptor<?>> populate() throws IOException {
        return populate(new ClasspathDescriptorFileFinder());
    }

}
