/*
 * Copyright (c) 2007, 2018 Oracle and/or its affiliates. All rights reserved.
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

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.enterprise.module.ModuleDefinition;
import com.sun.enterprise.module.ModuleDependency;
import com.sun.enterprise.module.ModuleMetadata;

/**
 * Creates a ModuleDefinition backed up by a a single classloader.
 * 
 * <p/>
 * The implementation does not cache any data - everything is recalculated
 * for each call.  Callers are therefore encouraged to either supply their
 * own caching, or minimize the calls to methods of this class.
 * 
 * @author Jerome Dochez
 */
public class ProxyModuleDefinition implements ModuleDefinition {
  
  private final ClassLoader classLoader;
  private final List<ManifestProxy.SeparatorMappings> mappings;

  public ProxyModuleDefinition(ClassLoader classLoader) throws IOException {
    this(classLoader, null);
  }

  public ProxyModuleDefinition(ClassLoader classLoader,
      List<ManifestProxy.SeparatorMappings> mappings) throws IOException {
    this.classLoader = classLoader;
    this.mappings = mappings;
  }

  private static byte[] readFully(URL url) throws IOException {
    DataInputStream dis = null;
    try {
      URLConnection con = url.openConnection();
      int len = con.getContentLength();
      InputStream in = con.getInputStream();
      dis = new DataInputStream(in);
      byte[] bytes = new byte[len];
      dis.readFully(bytes);
      return bytes;
    } catch (IOException e) {
      IOException x = new IOException("Failed to read " + url);
      x.initCause(e);
      throw x;
    } finally {
      if (dis != null) {
        dis.close();
      }
    }
  }

  public String getName() {
    return "Static Module";
  }

  public String[] getPublicInterfaces() {
    return new String[0];
  }

  public ModuleDependency[] getDependencies() {
    return new ModuleDependency[0];
  }

  public URI[] getLocations() {
    List<URI> uris = new ArrayList<URI>();
    if (classLoader instanceof URLClassLoader) {
      URLClassLoader urlCL = (URLClassLoader) classLoader;
      for (URL url : urlCL.getURLs()) {
        try {
          uris.add(url.toURI());
        } catch (URISyntaxException e) {
          Logger.getAnonymousLogger().log(Level.SEVERE, e.getMessage(), e);
        }
      }
    } else {
      String cp = System.getProperty("java.class.path");
      if (ok(cp)) {
        String[] paths = cp.split(System.getProperty("path.separator"));
        if (ok(paths)) {
          for (int i = 0; i < paths.length; i++) {
            uris.add(new File(paths[i]).toURI());
          }
        }
      }
    }
    return uris.toArray(new URI[uris.size()]);
  }

  public String getVersion() {
    return "1.0.0";
  }

  public String getImportPolicyClassName() {
    return null;
  }

  public String getLifecyclePolicyClassName() {
    return null;
  }

  public Manifest getManifest() {
    return generate(new ModuleMetadata());
  }

  public ModuleMetadata getMetadata() {
    ModuleMetadata metadata = new ModuleMetadata();
    generate(metadata);
    return metadata;
  }

  
  protected Manifest generate(ModuleMetadata metadata) {
    try {
      Manifest manifest = new ManifestProxy(classLoader, mappings);
            
      return manifest;
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  
  private static boolean ok(String s) {
    return s != null && s.length() > 0;
  }

  private static boolean ok(String[] ss) {
    return ss != null && ss.length > 0;
  }
}
