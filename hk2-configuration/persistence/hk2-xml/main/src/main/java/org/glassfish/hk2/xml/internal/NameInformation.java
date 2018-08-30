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

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Information about the name to XmlElement mappings and
 * about children with no XmlElement at all
 * 
 * @author jwells
 *
 */
public class NameInformation {
    private final Map<String, XmlElementData> nameMapping;
    private final Set<String> noXmlElement;
    private final Map<String, String> addMethodToVariableName;
    private final Map<String, String> removeMethodToVariableName;
    private final Map<String, String> lookupMethodToVariableName;
    private final Set<String> referenceSet;
    private final Map<String, List<XmlElementData>> aliases;
    private final XmlElementData valueData;
    
    public NameInformation(Map<String, XmlElementData> nameMapping,
            Set<String> unmappedNames,
            Map<String, String> addMethodToVariableName,
            Map<String, String> removeMethodToVariableName,
            Map<String, String> lookupMethodToVariableName,
            Set<String> referenceSet,
            Map<String, List<XmlElementData>> aliases,
            XmlElementData valueData) {
        this.nameMapping = nameMapping;
        this.noXmlElement = unmappedNames;
        this.addMethodToVariableName = addMethodToVariableName;
        this.removeMethodToVariableName = removeMethodToVariableName;
        this.lookupMethodToVariableName = lookupMethodToVariableName;
        this.referenceSet = referenceSet;
        this.aliases = aliases;
        this.valueData = valueData;
    }
    
    public String getNamespaceMap(String mapMe) {
        if (mapMe == null) return null;
        if (!nameMapping.containsKey(mapMe)) return mapMe;
        return nameMapping.get(mapMe).getNamespace();
    }
    
    public String getNameMap(String mapMe) {
        if (mapMe == null) return null;
        if (!nameMapping.containsKey(mapMe)) return mapMe;
        return nameMapping.get(mapMe).getName();
    }
    
    public List<XmlElementData> getAliases(String variableName) {
        return aliases.get(variableName);
    }
    
    public String getDefaultNameMap(String mapMe) {
        if (mapMe == null) return Generator.JAXB_DEFAULT_DEFAULT;
        if (!nameMapping.containsKey(mapMe)) return Generator.JAXB_DEFAULT_DEFAULT;
        return nameMapping.get(mapMe).getDefaultValue();
    }
    
    public String getXmlWrapperTag(String mapMe) {
        if (mapMe == null) return null;
        if (!nameMapping.containsKey(mapMe)) return null;
        return nameMapping.get(mapMe).getXmlWrapperTag();
    }
    
    public boolean hasNoXmlElement(String variableName) {
        if (variableName == null) return true;
        return noXmlElement.contains(variableName);
    }
    
    public boolean isReference(String variableName) {
        if (variableName == null) return false;
        return referenceSet.contains(variableName);
    }
    
    public boolean isRequired(String variableName) {
        if (variableName == null) return false;
        if (!nameMapping.containsKey(variableName)) return false;
        return nameMapping.get(variableName).isRequired();
    }
    
    public String getOriginalMethodName(String variableName) {
        if (variableName == null) return null;
        if (!nameMapping.containsKey(variableName)) return null;
        return nameMapping.get(variableName).getOriginalMethodName();
    }
    
    public Format getFormat(String variableName) {
        if (variableName == null) return Format.ATTRIBUTE;
        if ((valueData != null) && valueData.getName().equals(variableName)) return Format.VALUE;
        if (!nameMapping.containsKey(variableName)) return Format.ATTRIBUTE;
        return nameMapping.get(variableName).getFormat();
    }
    
    public String getAddVariableName(String methodName) {
        return addMethodToVariableName.get(methodName);
    }
    
    public String getRemoveVariableName(String methodName) {
        return removeMethodToVariableName.get(methodName);
    }
    
    public String getLookupVariableName(String methodName) {
        return lookupMethodToVariableName.get(methodName);
    }
    
    @Override
    public String toString() {
        return "NameInformation(nameMapping=" + nameMapping +
                ",noXmlElement=" + noXmlElement +
                ",addMethodToVariableName=" + addMethodToVariableName +
                ",removeMethodToVariableName=" + removeMethodToVariableName +
                ",lookupMethodToVariableName=" + lookupMethodToVariableName +
                ",referenceSet=" + referenceSet +
                ",aliases=" + aliases +
                ",valueData=" + valueData +
                "," + System.identityHashCode(this) + ")";
        
    }
}
