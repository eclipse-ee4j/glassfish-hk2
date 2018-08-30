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

package org.glassfish.hk2.xml.tools;

import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;
import javassist.NotFoundException;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

import org.glassfish.hk2.xml.internal.Generator;
import org.glassfish.hk2.xml.internal.alt.papi.TypeElementAltClassImpl;

/**
 * @author jwells
 *
 */
@SupportedAnnotationTypes("org.glassfish.hk2.xml.api.annotations.Hk2XmlPreGenerate")
public class Hk2XmlGenerator extends AbstractProcessor {
    private volatile boolean initialized;
    private ClassPool defaultClassPool;
    private CtClass superClazz;
    
    /**
     * Gets rid of warnings and this code should work with all source versions
     */
    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
    
    private  void initializeHk2XmlGenerator() {
        if (initialized) return;
        
        synchronized (this) {
            if (initialized) return;
            
            defaultClassPool = new ClassPool(true);
        
            ClassLoader localLoader = getClass().getClassLoader();
            if (!(localLoader instanceof URLClassLoader)) {
                throw new RuntimeException("Unknown classloader: " + localLoader);
            }
        
            @SuppressWarnings("resource")
            URLClassLoader urlLoader = (URLClassLoader) localLoader;
        
            for (URL url : urlLoader.getURLs()) {
                URI uri;
                try {
                    uri = url.toURI();
                }
                catch (URISyntaxException e) {
                    throw new RuntimeException(e);
                }
            
                File asFile = new File(uri);
                if (!asFile.exists() || !asFile.canRead()) {
                    continue;
                }
                try {
                    defaultClassPool.appendClassPath(asFile.getAbsolutePath());
                }
                catch (NotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        
            try {
                superClazz = defaultClassPool.get("org.glassfish.hk2.xml.jaxb.internal.BaseHK2JAXBBean");
            }
            catch (NotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        Filer filer = processingEnv.getFiler();
        
        for (TypeElement annotation : annotations) {
            Set<? extends Element> clazzes = roundEnv.getElementsAnnotatedWith(annotation);
            
            for (Element clazzElement : clazzes) {
                if (!(clazzElement instanceof TypeElement)) continue;
                
                initializeHk2XmlGenerator();
                
                TypeElement clazz = (TypeElement) clazzElement;
                
                TypeElementAltClassImpl altClass = new TypeElementAltClassImpl(clazz, processingEnv);
                
                try {
                    CtClass ctClass = Generator.generate(altClass, superClazz, defaultClassPool);
                    
                    String ctClassName = ctClass.getName();
                    
                    JavaFileObject jfo = filer.createClassFile(ctClassName, clazzElement);
                    
                    OutputStream outputStream = jfo.openOutputStream();
                    DataOutputStream dataOutputStream = null;
                    try {
                        dataOutputStream = new DataOutputStream(outputStream);
                    
                        ctClass.toBytecode(dataOutputStream);
                    }
                    finally {
                        if (dataOutputStream != null) {
                            dataOutputStream.close();
                        }
                        
                        outputStream.close();
                    }
                }
                catch (Throwable e) {
                    String msg = e.getMessage();
                    if (msg == null) msg = "Exception of type " + e.getClass().getName();
                
                    processingEnv.getMessager().printMessage(Kind.ERROR, "While processing class: " + clazz.getQualifiedName() + " got exeption: " + msg);
                    e.printStackTrace();
                }
            }
        }
        
        return true;
    }

}
