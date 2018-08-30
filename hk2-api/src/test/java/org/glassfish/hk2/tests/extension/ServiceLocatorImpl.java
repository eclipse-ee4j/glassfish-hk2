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

package org.glassfish.hk2.tests.extension;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.MethodParameter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.api.ServiceLocatorState;
import org.glassfish.hk2.api.Unqualified;

/**
 * @author jwells
 *
 */
public class ServiceLocatorImpl implements ServiceLocator {
    private boolean shutdownCalled = false;
    private final String name;
  
    /**
     * For use by the test
     * @param name The name of this locator
     */
    public ServiceLocatorImpl(String name) {
        this.name = name;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getName()
     */
    @Override
    public String getName() {
        return name;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#shutdown()
     */
    @Override
    public void shutdown() {
        shutdownCalled = true;

    }
  
    /**
     * Called by the test
     * @return true if shutdown has been called
     */
    public boolean isShutdown() {
        return shutdownCalled;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getDescriptors(org.glassfish.hk2.api.Filter)
     */
    @Override
    public List<ActiveDescriptor<?>> getDescriptors(Filter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#reifyDescriptor(org.glassfish.hk2.api.Descriptor)
     */
    @Override
    public ActiveDescriptor<?> reifyDescriptor(Descriptor descriptor) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getInjecteeDescriptor(org.glassfish.hk2.api.Injectee)
     */
    @Override
    public ActiveDescriptor<?> getInjecteeDescriptor(Injectee injectee) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getServiceHandle(org.glassfish.hk2.api.ActiveDescriptor)
     */
    @Override
    public <T> ServiceHandle<T> getServiceHandle(
            ActiveDescriptor<T> activeDescriptor) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getService(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.ServiceHandle)
     */
    @Override
    public <T> T getService(ActiveDescriptor<T> activeDescriptor,
            ServiceHandle<?> root) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getService(java.lang.reflect.Type)
     */
    @Override
    public <T> T getService(Type contractOrImpl, Annotation... qualifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getAllServices(java.lang.reflect.Type)
     */
    @Override
    public <T> List<T> getAllServices(Type contractOrImpl, Annotation... qualifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getService(java.lang.reflect.Type, java.lang.String)
     */
    @Override
    public <T> T getService(Type contractOrImpl, String name, Annotation... qualifiers) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getAllServices(org.glassfish.hk2.api.Filter)
     */
    @Override
    public List<?> getAllServices(Filter searchCriteria) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#create(java.lang.Class)
     */
    @Override
    public <T> T create(Class<T> createMe) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#inject(java.lang.Object)
     */
    @Override
    public void inject(Object injectMe) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#postConstruct(java.lang.Object)
     */
    @Override
    public void postConstruct(Object postConstructMe) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#preDestroy(java.lang.Object)
     */
    @Override
    public void preDestroy(Object preDestroyMe) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getBestDescriptor(org.glassfish.hk2.api.Filter)
     */
    @Override
    public ActiveDescriptor<?> getBestDescriptor(Filter filter) {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getServiceHandle(java.lang.reflect.Type, java.lang.annotation.Annotation[])
     */
    @Override
    public <T> ServiceHandle<T> getServiceHandle(Type contractOrImpl,
            Annotation... qualifiers) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getAllServiceHandles(java.lang.reflect.Type, java.lang.annotation.Annotation[])
     */
    @Override
    public List<ServiceHandle<?>> getAllServiceHandles(
            Type contractOrImpl, Annotation... qualifiers)
            throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getServiceHandle(java.lang.reflect.Type, java.lang.String, java.lang.annotation.Annotation[])
     */
    @Override
    public <T> ServiceHandle<T> getServiceHandle(Type contractOrImpl,
            String name, Annotation... qualifiers) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getAllServiceHandles(org.glassfish.hk2.api.Filter)
     */
    @Override
    public List<ServiceHandle<?>> getAllServiceHandles(
            Filter searchCriteria) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getLocatorId()
     */
    @Override
    public long getLocatorId() {
        // TODO Auto-generated method stub
        return 0;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#reifyDescriptor(org.glassfish.hk2.api.Descriptor, org.glassfish.hk2.api.Injectee)
     */
    @Override
    public ActiveDescriptor<?> reifyDescriptor(Descriptor descriptor,
            Injectee injectee) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getServiceHandle(org.glassfish.hk2.api.ActiveDescriptor, org.glassfish.hk2.api.Injectee)
     */
    @Override
    public <T> ServiceHandle<T> getServiceHandle(
            ActiveDescriptor<T> activeDescriptor, Injectee injectee)
            throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getAllServices(java.lang.annotation.Annotation, java.lang.annotation.Annotation[])
     */
    @Override
    public <T> List<T> getAllServices(Annotation qualifier,
            Annotation... qualifiers) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getAllServiceHandles(java.lang.annotation.Annotation, java.lang.annotation.Annotation[])
     */
    @Override
    public List<ServiceHandle<?>> getAllServiceHandles(Annotation qualifier,
            Annotation... qualifiers) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }
    
    public <T> T createAndInitialize(Class<T> createMe) {
        return null;
    }

    @Override
    public <T> T getService(Class<T> contractOrImpl, Annotation... qualifiers)
            throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T getService(Class<T> contractOrImpl, String name,
            Annotation... qualifiers) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<T> getAllServices(Class<T> contractOrImpl,
            Annotation... qualifiers) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> ServiceHandle<T> getServiceHandle(Class<T> contractOrImpl,
            Annotation... qualifiers) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> ServiceHandle<T> getServiceHandle(Class<T> contractOrImpl,
            String name, Annotation... qualifiers) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> List<ServiceHandle<T>> getAllServiceHandles(
            Class<T> contractOrImpl, Annotation... qualifiers)
            throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public <T> T getService(ActiveDescriptor<T> activeDescriptor,
            ServiceHandle<?> root, Injectee injectee) throws MultiException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public ServiceLocatorState getState() {
        return ServiceLocatorState.RUNNING;
    }

    @Override
    public <T> T create(Class<T> createMe, String strategy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void inject(Object injectMe, String strategy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void postConstruct(Object postConstructMe, String strategy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void preDestroy(Object preDestroyMe, String strategy) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public <U> U createAndInitialize(Class<U> createMe, String strategy) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getDefaultClassAnalyzerName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setDefaultClassAnalyzerName(String defaultClassAnalyzer)
            throws MultiException {
        // TODO Auto-generated method stub
        
    }

    @Override
    public ServiceLocator getParent() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean getNeutralContextClassLoader() {
        // TODO Auto-generated method stub
        return true;
    }

    @Override
    public void setNeutralContextClassLoader(boolean neutralContextClassLoader) {
        // TODO Auto-generated method stub
        
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#getDefaultUnqualified()
     */
    @Override
    public Unqualified getDefaultUnqualified() {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.api.ServiceLocator#setDefaultUnqualified(org.glassfish.hk2.api.Unqualified)
     */
    @Override
    public void setDefaultUnqualified(Unqualified unqualified) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public Object assistedInject(Object injectMe, Method method,
            MethodParameter... params) {
        // TODO Auto-generated method stub
        return null;
        
    }

    @Override
    public Object assistedInject(Object injectMe, Method method,
            ServiceHandle<?> root, MethodParameter... params) {
        // TODO Auto-generated method stub
        return null;
        
    }
}
