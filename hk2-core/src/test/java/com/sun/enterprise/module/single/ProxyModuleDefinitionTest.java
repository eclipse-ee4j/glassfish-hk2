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

package com.sun.enterprise.module.single;

import static org.junit.Assert.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.jar.Manifest;
import java.util.logging.Logger;

import org.junit.Test;

import com.sun.enterprise.module.ModuleDependency;
import com.sun.enterprise.module.ModuleMetadata;

/**
 * Tests for {@link ProxyModuleDefinition}
 * 
 * @author Jeff Trent
 */
public class ProxyModuleDefinitionTest {

  @Test
  public void testGetLocations() throws Exception {
    ClassLoader loader = ProxyModuleDefinitionTest.class.getClassLoader();
    ProxyModuleDefinition pmd = new ProxyModuleDefinition(loader);
    assertNotNull(pmd.getLocations());
    assertNotSame(pmd.getLocations(), pmd.getLocations());
    List<URI> coll = Arrays.asList(pmd.getLocations());
    System.out.println(coll);
  }
  
  @Test
  public void testGetLocationsIsNotHeapIntensive() throws Exception {
    ClassLoader loader = ProxyModuleDefinitionTest.class.getClassLoader();
    ArrayList<ProxyModuleDefinition> list = new ArrayList<ProxyModuleDefinition>();

//    long totalMemory = Runtime.getRuntime().totalMemory();

    System.gc();
    System.gc();
    Thread.sleep(100);
    
    long freeMemory0 = getMemoryUse();
    ProxyModuleDefinition pmd = new ProxyModuleDefinition(loader);
    long freeMemory1 = getMemoryUse();
    
    for (int i = 0; i < 100; i++) {
      pmd = new ProxyModuleDefinition(loader);
      list.add(pmd);
    }
    
    System.gc();
    System.gc();
    Thread.sleep(100);
    
    long freeMemory2 = getMemoryUse();
    
    Logger.getAnonymousLogger().fine("First Object: " + (freeMemory1 - freeMemory0));

    long avgHeapPerObject = (freeMemory2-freeMemory1)/100;
    Logger.getAnonymousLogger().fine("100 Objects: " + (freeMemory2 - freeMemory1)
        + ", or " + avgHeapPerObject + " per object");
    
    assertTrue("expect less heap consumed: " + avgHeapPerObject, avgHeapPerObject < 8192);
  }
  
  @Test
  public void testGetManifest() throws Exception {
    ClassLoader loader = ProxyModuleDefinitionTest.class.getClassLoader();
    ProxyModuleDefinition pmd = new ProxyModuleDefinition(loader);
    Manifest mf = pmd.getManifest();
    assertNotNull(mf);
    assertNotSame(mf, pmd.getManifest());
    assertNotNull(mf.getEntries());
//    assertFalse(mf.getEntries().isEmpty());
  }
  
  @Test
  public void testGetMetadata() throws Exception {
    ClassLoader loader = ProxyModuleDefinitionTest.class.getClassLoader();
    ProxyModuleDefinition pmd = new ProxyModuleDefinition(loader);
    ModuleMetadata md = pmd.getMetadata();
    assertNotNull(md);
    assertNotSame(md, pmd.getMetadata());
    assertNotNull(md.getEntries());
    assertFalse(md.getEntries().iterator().hasNext());
  }
  
  @Test
  public void testGetDependencies() throws Exception {
    ClassLoader loader = ProxyModuleDefinitionTest.class.getClassLoader();
    ProxyModuleDefinition pmd = new ProxyModuleDefinition(loader);
    ModuleDependency md[] = pmd.getDependencies();
    assertNotSame(md, pmd.getDependencies());
  }
  
  private static long getMemoryUse(){
    long totalMemory = Runtime.getRuntime().totalMemory();
    long freeMemory = Runtime.getRuntime().freeMemory();
    return (totalMemory - freeMemory);
  }


}
