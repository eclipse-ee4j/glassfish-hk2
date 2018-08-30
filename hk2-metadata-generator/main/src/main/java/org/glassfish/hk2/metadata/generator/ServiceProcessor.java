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

package org.glassfish.hk2.metadata.generator;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedOptions;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.utilities.DescriptorImpl;

/**
 * The entry point for service &#64;Service annotations
 * 
 * @author jwells
 *
 */
@SupportedAnnotationTypes("org.jvnet.hk2.annotations.Service")
@SupportedOptions("org.glassfish.hk2.metadata.location")
public class ServiceProcessor extends AbstractProcessor {
    private static final String LOCATION_OPTION = "org.glassfish.hk2.metadata.location";
    private static final String LOCATION_DEFAULT = "META-INF/hk2-locator/default";
    
    private final TreeSet<DescriptorImpl> allDescriptors = new TreeSet<DescriptorImpl>(new DescriptorComparitor());
    private final ArrayList<Element> originators = new ArrayList<Element>();

    /* (non-Javadoc)
     * @see javax.annotation.processing.AbstractProcessor#process(java.util.Set, javax.annotation.processing.RoundEnvironment)
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations,
            RoundEnvironment roundEnv) {
        MultiException collectedExceptions = null;
        for (TypeElement annotation : annotations) {
            Set<? extends Element> clazzes = roundEnv.getElementsAnnotatedWith(annotation);
            
            for (Element clazzElement : clazzes) {
                if (!(clazzElement instanceof TypeElement)) continue;
                
                TypeElement clazz = (TypeElement) clazzElement;
                
                List<DescriptorImpl> descriptors;
                try {
                    descriptors = ServiceUtilities.getDescriptorsFromClass(clazz, processingEnv);
                }
                catch (Throwable th) {
                    if (collectedExceptions == null) {
                        collectedExceptions = new MultiException(th);
                    }
                    else {
                        collectedExceptions.addError(th);
                    }
                    
                    continue;
                }
                
                allDescriptors.addAll(descriptors);
                if (!descriptors.isEmpty()) {
                    originators.add(clazzElement);
                }
            }
        }
        
        if (collectedExceptions != null) {
            processingEnv.getMessager().printMessage(Kind.ERROR, collectedExceptions.getMessage());
            collectedExceptions.printStackTrace();
            return true;
        }
        
        if (allDescriptors.isEmpty()) return true;
        if (!roundEnv.processingOver()) return true;
        
        try {
            createFile();
        }
        catch (IOException e) {
            processingEnv.getMessager().printMessage(Kind.ERROR, e.getMessage());
            e.printStackTrace();
        }
        
        return true;
    }
    
    /**
     * Gets rid of warnings and this code should work with all source versions
     */
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latest();
    }
    
    private void createFile() throws IOException {
        String location = processingEnv.getOptions().get(LOCATION_OPTION);
        if (location == null) location = LOCATION_DEFAULT;
        location = location.trim();
        
        if (location.startsWith("/")) {
            throw new IOException("The " + LOCATION_OPTION +
                    " option to hk2-inhabitant-locator must be a relative path, it was " + location);
        }
        
        Filer filer = processingEnv.getFiler();
        FileObject fileObject = filer.createResource(StandardLocation.CLASS_OUTPUT,
                "", location, 
                originators.toArray(new Element[originators.size()]));
        
        Writer fileWriter = fileObject.openWriter();
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(fileWriter);
            
            printWriter.println("#");
            printWriter.println("# Generated by hk2-metadata-generator");
            printWriter.println("#");
            printWriter.println();
            
            for (DescriptorImpl di : allDescriptors) {
                di.writeObject(printWriter);
            }
            
        }
        finally {
            if (printWriter != null) {
                printWriter.close();
            }
            
            fileWriter.close();
        }
        
    }
    
    /**
     * This is a comparator making things that don't really compare, compare.
     * It is done to ensure that given the same set of descriptors we always
     * return the set in the same order, which will ensure that the output
     * of the generator is not different from run to run
     * 
     * @author jwells
     *
     */
    private static class DescriptorComparitor implements Comparator<DescriptorImpl> {
        private static <T> int safeCompare(Comparable<T> a, T b) {
            if (a == null && b == null) return 0;
            if (a == null) return -1;
            if (b == null) return 1;
            
            return a.compareTo(b);
        }
        
        private static int compareStringMaps(Set<String> s1, Set<String> s2) {
            int size1 = s1.size();
            int size2 = s2.size();
            
            if (size1 != size2) return (size1 - size2);
            
            TreeSet<String> s1sorted = new TreeSet<String>(s1);
            TreeSet<String> s2sorted = new TreeSet<String>(s2);
            
            StringBuffer s1b = new StringBuffer();
            for (String s1sv : s1sorted) {
                s1b.append(s1sv);
            }
            
            StringBuffer s2b = new StringBuffer();
            for (String s2sv : s2sorted) {
                s2b.append(s2sv);
            }
            
            return safeCompare(s1b.toString(), s2b.toString());
        }

        @Override
        public int compare(DescriptorImpl o1, DescriptorImpl o2) {
            int retVal = o2.getRanking() - o1.getRanking();
            if (retVal != 0) return retVal;
            
            retVal = safeCompare(o1.getImplementation(), o2.getImplementation());
            if (retVal != 0) return retVal;
            
            retVal = safeCompare(o1.getName(), o2.getName());
            if (retVal != 0) return retVal;
            
            retVal = safeCompare(o1.getScope(), o2.getScope());
            if (retVal != 0) return retVal;
            
            retVal = compareStringMaps(o1.getAdvertisedContracts(), o2.getAdvertisedContracts());
            if (retVal != 0) return retVal;
            
            retVal = compareStringMaps(o1.getQualifiers(), o2.getQualifiers());
            if (retVal != 0) return retVal;
            
            retVal = o1.getDescriptorType().compareTo(o2.getDescriptorType());
            if (retVal != 0) return retVal;
            
            retVal = o1.getDescriptorVisibility().compareTo(o2.getDescriptorVisibility());
            if (retVal != 0) return retVal;
            
            return 0;
        }
        
    }
}
