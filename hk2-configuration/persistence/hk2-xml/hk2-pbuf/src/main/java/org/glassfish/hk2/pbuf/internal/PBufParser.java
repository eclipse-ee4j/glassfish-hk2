/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.pbuf.internal;

import java.beans.Introspector;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;
import javax.xml.bind.Unmarshaller.Listener;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;

import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.IterableProvider;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.Visibility;
import org.glassfish.hk2.pbuf.api.PBufUtilities;
import org.glassfish.hk2.utilities.general.GeneralUtilities;
import org.glassfish.hk2.xml.api.XmlHk2ConfigurationBean;
import org.glassfish.hk2.xml.api.XmlRootHandle;
import org.glassfish.hk2.xml.api.XmlService;
import org.glassfish.hk2.xml.internal.ChildDataModel;
import org.glassfish.hk2.xml.internal.ChildDescriptor;
import org.glassfish.hk2.xml.internal.ChildType;
import org.glassfish.hk2.xml.internal.ModelImpl;
import org.glassfish.hk2.xml.internal.ParentedModel;
import org.glassfish.hk2.xml.jaxb.internal.BaseHK2JAXBBean;
import org.glassfish.hk2.xml.spi.Model;
import org.glassfish.hk2.xml.spi.PreGenerationRequirement;
import org.glassfish.hk2.xml.spi.XmlServiceParser;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import com.google.protobuf.DescriptorProtos;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.EnumValueDescriptor;
import com.google.protobuf.DynamicMessage;
import com.google.protobuf.InvalidProtocolBufferException;

/**
 * The pbuf parser
 * 
 * @author jwells
 */
@Singleton
@Named(PBufUtilities.PBUF_SERVICE_NAME)
@Visibility(DescriptorVisibility.LOCAL)
public class PBufParser implements XmlServiceParser {
    private final HashMap<Class<?>, Descriptors.Descriptor> allProtos = new HashMap<Class<?>, Descriptors.Descriptor>();
    private final HashMap<Class<?>, Descriptors.EnumDescriptor> allEnums = new HashMap<Class<?>, Descriptors.EnumDescriptor>();
    
    @Inject @Named(PBufUtilities.PBUF_SERVICE_NAME)
    private IterableProvider<XmlService> xmlService;

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#parseRoot(org.glassfish.hk2.xml.spi.Model, java.net.URI, javax.xml.bind.Unmarshaller.Listener)
     */
    @Override
    public <T> T parseRoot(Model rootModel, URI location, Listener listener, Map<String, Object> options)
            throws Exception {
        InputStream is = location.toURL().openStream();
        try {
            return parseRoot(rootModel, is, listener, options);
        }
        finally {
            is.close();
        }
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#parseRoot(org.glassfish.hk2.xml.spi.Model, java.io.InputStream, javax.xml.bind.Unmarshaller.Listener)
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T> T parseRoot(Model rootModel, InputStream input,
            Listener listener, Map<String, Object> options) throws Exception {
        try {
            Set<Descriptors.FileDescriptor> protoFiles = new HashSet<Descriptors.FileDescriptor>();
            convertAllModels((ModelImpl) rootModel, protoFiles);
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        
        boolean markSupported = input.markSupported();
        if (markSupported) {
            input.mark(Integer.MAX_VALUE);
        }
        
        boolean useLength = getPrependSize(options);
        
        byte[] rawBytes;
        int size = -1;
        if (useLength) {
        	CodedInputStream usedCIS = getUsedInputStream(options);
            CodedInputStream cis = (usedCIS != null) ? usedCIS : CodedInputStream.newInstance(input);
            if (options != null) {
            	options.put(PBufUtilities.PBUF_STREAMING_OPTION, new CISStreamCloser(cis));
            }
            
            try {
                size = cis.readInt32();
            }
            catch (InvalidProtocolBufferException ipbe) {
                MultiException me = new MultiException(new EOFException());
                me.addError(ipbe);
                
                throw me;
            }
            
            if (size <= 0) {
                throw new AssertionError("Invalid size of protocol buffer on the wire: " + size);
            }
            
           rawBytes = cis.readRawBytes(size);
        }
        else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            try {
                byte buffer[] = new byte[1000];
                
                int readLength;
                while ((readLength = input.read(buffer)) > 0) {
                    baos.write(buffer, 0, readLength);
                }
            }
            finally {
                baos.close();
            }
            
            rawBytes = baos.toByteArray();
        }
            
        DynamicMessage message;
        try {
            message = internalUnmarshal((ModelImpl) rootModel, rawBytes);
        }
        catch (InvalidProtocolBufferException ipbe) {
            MultiException me = new MultiException(ipbe);
            if (markSupported) {
                byte debugBytes[];
                if (useLength) {
                    input.reset();
                    
                    byte[] lengthBytes = getLengthBytes(input);
                    
                    debugBytes = new byte[lengthBytes.length + rawBytes.length];
                    System.arraycopy(lengthBytes, 0, debugBytes, 0, lengthBytes.length);
                    System.arraycopy(rawBytes, 0, debugBytes, lengthBytes.length, rawBytes.length);
                }
                else {
                    debugBytes = rawBytes;
                }
                
                String inputAsString = GeneralUtilities.prettyPrintBytes(debugBytes);
                
                IllegalStateException ise = new IllegalStateException("Invalid protocol buffer:\n" + inputAsString);
                me.addError(ise);
            }
            
            throw me;
        }
        
        XmlHk2ConfigurationBean retVal = parseDynamicMessage((ModelImpl) rootModel,
            null,
            message,
            listener);
        
        return (T) retVal;
    }
    
    private static byte[] getLengthBytes(InputStream input) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            int b = input.read();
            baos.write(b);
            
            if ((b & 0x80) == 0) {
                return baos.toByteArray();
            }
        }
        finally {
            baos.close();
        }
        
        throw new IOException("Reached end of stream without an end to the length!");
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#getPreGenerationRequirement()
     */
    @Override
    public PreGenerationRequirement getPreGenerationRequirement() {
        return PreGenerationRequirement.MUST_PREGENERATE;
    }

    /* (non-Javadoc)
     * @see org.glassfish.hk2.xml.spi.XmlServiceParser#marshal(java.io.OutputStream, org.glassfish.hk2.xml.api.XmlRootHandle)
     */
    @Override
    public <T> void marshal(OutputStream outputStream, XmlRootHandle<T> root, Map<String, Object> options)
            throws IOException {
        T rootObject = root.getRoot();
        if (rootObject == null) return;
        
        XmlHk2ConfigurationBean rootBean = (XmlHk2ConfigurationBean) rootObject;
        ModelImpl model = rootBean._getModel();
        
        try {
          Set<Descriptors.FileDescriptor> protoFiles = new HashSet<Descriptors.FileDescriptor>();
          convertAllModels(model, protoFiles);
        }
        catch (IOException ioe) {
            throw ioe;
        }
        catch (Exception e) {
            throw new IOException(e);
        }
        
        DynamicMessage dynamicMessage = internalMarshal(rootBean);
        int size = dynamicMessage.getSerializedSize();
        
        CodedOutputStream usedCos = getUsedOutputStream(options);
        CodedOutputStream cos= (usedCos != null) ? usedCos : CodedOutputStream.newInstance(outputStream);
        if (options != null) {
        	options.put(PBufUtilities.PBUF_STREAMING_OPTION, new COSStreamCloser(cos));
        }
        
        boolean prependSize = getPrependSize(options);
        
        try {
          if (prependSize) {
              cos.writeInt32NoTag(size);
          }
          
          dynamicMessage.writeTo(cos);
        }
        finally {
            cos.flush();
        }
    }
    
    private XmlHk2ConfigurationBean parseDynamicMessage(ModelImpl model,
            XmlHk2ConfigurationBean parent,
            DynamicMessage message,
            Listener listener) throws IOException {
        BaseHK2JAXBBean bean = (BaseHK2JAXBBean) xmlService.get().createBean(model.getOriginalInterfaceAsClass());
        
        Descriptors.Descriptor descriptor = message.getDescriptorForType();
        
        listener.beforeUnmarshal(bean, parent);
        
        for(Map.Entry<QName, ChildDescriptor> entry : model.getAllChildrenDescriptors().entrySet()) {
            QName qname = entry.getKey();
            ChildDescriptor childDescriptor = entry.getValue();
            
            String localPart = qname.getLocalPart();
            String protoPart = PBUtilities.camelCaseToUnderscore(localPart);
            
            Descriptors.FieldDescriptor fieldDescriptor = descriptor.findFieldByName(protoPart);
            if (fieldDescriptor == null) {
                throw new IOException("Unknown field " + protoPart + " in " + bean);
            }
            
            ChildDataModel childDataModel = childDescriptor.getChildDataModel();
            if (childDataModel != null) {
                boolean fieldSet = message.hasField(fieldDescriptor);
                if (!fieldSet) {
                    continue;
                }
                
                Object value = message.getField(fieldDescriptor);
                
                value = convertFieldForUnmarshal(value, childDataModel);
                
                bean._setProperty(qname, value);
            }
            else {
                ParentedModel parentedNode = childDescriptor.getParentedModel();
                
                if (ChildType.DIRECT.equals(parentedNode.getChildType())) {
                    // Cannot call hasField on repeated fields in pbuf
                    boolean fieldSet = message.hasField(fieldDescriptor);
                    if (!fieldSet) {
                        continue;
                    }
                }
                
                Object value = message.getField(fieldDescriptor);
                if (value == null) {
                    continue;
                }
                
                DynamicMessage dynamicChild = null;
                XmlHk2ConfigurationBean child = null;
                int repeatedFieldCount = 0;
                
                switch (parentedNode.getChildType()) {
                case DIRECT:
                    if (!(value instanceof DynamicMessage)) {
                        throw new AssertionError("Do not know how to handle a non-dynamic direct message " + value);
                    }
                    dynamicChild = (DynamicMessage) value;
                    
                    child = parseDynamicMessage(parentedNode.getChildModel(),
                            bean,
                            dynamicChild,
                            listener);
                    
                    bean._setProperty(qname, child);
                    break;
                case LIST:
                    repeatedFieldCount = message.getRepeatedFieldCount(fieldDescriptor);
                    ArrayList<XmlHk2ConfigurationBean> list = new ArrayList<XmlHk2ConfigurationBean>(repeatedFieldCount);
                    
                    for (int lcv = 0; lcv < repeatedFieldCount; lcv++) {
                        Object childBean = message.getRepeatedField(fieldDescriptor, lcv);
                        if (!(childBean instanceof DynamicMessage)) {
                            throw new AssertionError("Do not know how to handle a non-dynamic list message " + childBean);
                        }
                        dynamicChild = (DynamicMessage) childBean;
                        
                        child = parseDynamicMessage(parentedNode.getChildModel(),
                                bean,
                                dynamicChild,
                                listener);
                        
                        list.add(child);
                    }
                    
                    bean._setProperty(qname, list);
                    break;
                case ARRAY:
                    ModelImpl childModel = parentedNode.getChildModel();
                    repeatedFieldCount = message.getRepeatedFieldCount(fieldDescriptor);
                    Object array = Array.newInstance(childModel.getOriginalInterfaceAsClass(), repeatedFieldCount);
                    
                    for (int lcv = 0; lcv < repeatedFieldCount; lcv++) {
                        Object childBean = message.getRepeatedField(fieldDescriptor, lcv);
                        if (!(childBean instanceof DynamicMessage)) {
                            throw new AssertionError("Do not know how to handle a non-dynamic array message " + childBean);
                        }
                        dynamicChild = (DynamicMessage) childBean;
                        
                        child = parseDynamicMessage(parentedNode.getChildModel(),
                                bean,
                                dynamicChild,
                                listener);
                        
                        Array.set(array, lcv, child);
                    }
                    
                    bean._setProperty(qname, array);
                    break;
                default:
                    throw new IOException("Unknown child type: " + parentedNode.getChildType());
                }
            }
        }
        
        listener.afterUnmarshal(bean, parent);
        
        return bean;
    }
    
    private DynamicMessage internalUnmarshal(ModelImpl model, byte[] bytes) throws Exception {
        Class<?> originalAsClass = model.getOriginalInterfaceAsClass();
        String originalInterface = model.getOriginalInterface();
        String protoName = getSimpleName(originalInterface);
        
        Descriptors.Descriptor descriptor;
        synchronized (allProtos) {
            descriptor = allProtos.get(originalAsClass);
        }
        if (descriptor == null) {
            throw new IOException("Unknown model: " + originalInterface + " with protoName=" + protoName);
        }
        
        DynamicMessage retVal = DynamicMessage.parseFrom(descriptor, bytes);
        return retVal;
    }
    
    
    
    @SuppressWarnings("unchecked")
    private <T>  DynamicMessage internalMarshal(XmlHk2ConfigurationBean bean) throws IOException {
        Map<String, Object> blm = bean._getBeanLikeMap();
        ModelImpl model = bean._getModel();
        
        Class<?> originalAsClass = model.getOriginalInterfaceAsClass();
        String originalInterface = model.getOriginalInterface();
        String protoName = getSimpleName(originalInterface);
        
        Descriptors.Descriptor descriptor;
        synchronized (allProtos) {
            descriptor = allProtos.get(originalAsClass);
        }
        
        if (descriptor == null) {
            throw new IOException("Unknown model: " + originalInterface + " with protoName=" + protoName);
        }
        
        DynamicMessage.Builder retValBuilder = DynamicMessage.newBuilder(descriptor);
        
        for (Map.Entry<QName, ChildDescriptor> allEntry : model.getAllChildrenDescriptors().entrySet()) {
            QName qname = allEntry.getKey();
            ChildDescriptor childDescriptor = allEntry.getValue();
            
            String localPart = qname.getLocalPart();
            String protoPart = PBUtilities.camelCaseToUnderscore(localPart);
            
            Descriptors.FieldDescriptor fieldDescriptor = descriptor.findFieldByName(protoPart);
            if (fieldDescriptor == null) {
                throw new IOException("Unknown field " + protoPart + " in " + bean);
            }
            
            ChildDataModel childDataModel = childDescriptor.getChildDataModel();
            if (childDataModel != null) {
                if (!bean._isSet(localPart)) {
                    continue;
                }
                
                Object value = blm.get(localPart);
                Class<?> childType = childDataModel.getChildTypeAsClass();
                Object convertedValue = convertFieldForMarshal(value, childType);
              
                if (convertedValue != null) {
                    retValBuilder.setField(fieldDescriptor, convertedValue);
                }
            }
            else {
                ParentedModel parentedModel = childDescriptor.getParentedModel();
                
                switch(parentedModel.getChildType()) {
                case DIRECT:
                    Object directValue = blm.get(localPart);
                    if (directValue != null) {
                        DynamicMessage subMessage = internalMarshal((XmlHk2ConfigurationBean) directValue);
                        
                        retValBuilder.setField(fieldDescriptor, subMessage);
                    }
                    break;
                case LIST:
                    Object listValue = blm.get(localPart);
                    if (listValue != null) {
                        List<XmlHk2ConfigurationBean> asList = (List<XmlHk2ConfigurationBean>) listValue;
                        
                        for (XmlHk2ConfigurationBean childBean : asList) {
                            DynamicMessage subMessage = internalMarshal(childBean);
                            
                            retValBuilder.addRepeatedField(fieldDescriptor, subMessage);
                        }
                    }
                    break;
                case ARRAY:
                    Object arrayValue = blm.get(localPart);
                    if (arrayValue != null) {
                        int count = Array.getLength(arrayValue);
                        
                        if (count <= 0) {
                            continue;
                        }
                        
                        for (int lcv = 0; lcv < count; lcv++) {
                            XmlHk2ConfigurationBean child = (XmlHk2ConfigurationBean) Array.get(arrayValue, lcv);
                            
                            DynamicMessage subMessage = internalMarshal(child);
                            
                            retValBuilder.addRepeatedField(fieldDescriptor, subMessage);
                        }
                    }
                    break;
                default:
                    throw new AssertionError("Unknown child type: " + parentedModel.getChildType());
                    
                }
                
            }
            
        }
        
        return retValBuilder.build();
    }
    
    private void convertAllModels(ModelImpl model, Set<Descriptors.FileDescriptor> protoFiles) throws Exception {
        synchronized (allProtos) {
            Class<?> modelClass = model.getOriginalInterfaceAsClass();
            Descriptors.Descriptor dd = allProtos.get(modelClass);
            if (dd != null) {
                protoFiles.add(dd.getFile());
                return;
            }
        
            for (ParentedModel pModel : model.getAllChildren()) {
                convertAllModels(pModel.getChildModel(), protoFiles);
            }
        
            dd = allProtos.get(modelClass);
            if (dd != null) {
                protoFiles.add(dd.getFile());
                return;
            }
        
            Descriptors.Descriptor converted = convertModelToDescriptor(model, protoFiles);
        
            protoFiles.add(converted.getFile());
        
            allProtos.put(modelClass, converted);
        }
    }
    
    private static DescriptorProtos.FieldDescriptorProto.Type convertChildDataModelToType(ChildDataModel cdm) {
        Class<?> childClass = cdm.getChildTypeAsClass();
        
        if (childClass.equals(String.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
        }
        if (childClass.equals(int.class) || childClass.equals(Integer.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32;
        }
        if (childClass.equals(long.class) || childClass.equals(Long.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT64;
        }
        if (childClass.equals(boolean.class) || childClass.equals(Boolean.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_BOOL;
        }
        if (childClass.equals(double.class) || childClass.equals(Double.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_DOUBLE;
        }
        if (childClass.equals(float.class) || childClass.equals(Float.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_FLOAT;
        }
        if (childClass.equals(byte.class) || childClass.equals(Byte.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_BYTES;
        }
        if (childClass.equals(char.class) || childClass.equals(Character.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_STRING;
        }
        if (childClass.equals(short.class) || childClass.equals(Short.class)) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_INT32;
        }
        if (childClass.isEnum()) {
            return DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM;
        }
        
        throw new AssertionError("Unknown type to convert " + childClass.getName());
    }
    
    private static String getSimpleName(String dotDelimitedName) {
        int index = dotDelimitedName.lastIndexOf('.');
        if (index < 0) return dotDelimitedName;
        
        return dotDelimitedName.substring(index + 1);
    }
    
    private static String getPackageName(String dotDelimitedName) {
        int index = dotDelimitedName.lastIndexOf('.');
        if (index < 0) return null;
        
        return dotDelimitedName.substring(0, index);
    }
    
    private static String getProtoNameFromModel(ModelImpl mi) {
        String originalInterface = mi.getOriginalInterface();
        String protoName = getSimpleName(originalInterface);
        return protoName;
    }
    
    private static String getPackageNameFromModel(ModelImpl mi) {
        String originalInterface = mi.getOriginalInterface();
        String packageName = getPackageName(originalInterface);
        return packageName;
    }
    
    private static String getXmlTypeValueFromMethodName(String methodName, Object source) {
        if (methodName == null) {
            throw new AssertionError("Do not know the method name of " + source);
        }
        
        if (methodName.startsWith("get") || methodName.startsWith("set")) {
            return Introspector.decapitalize(methodName.substring(3));
        }
        
        if (methodName.startsWith("is")) {
            return Introspector.decapitalize(methodName.substring(2));
        }
        
        throw new IllegalStateException("Unknown method name pattern, not a get or a set or an is: " + methodName);
    }
    
    private static void validateXmlType(Class<?> originalInterface, Map<QName, ChildDescriptor> allChildren) {
        XmlType xmlType = originalInterface.getAnnotation(XmlType.class);
        if (xmlType == null) {
            throw new IllegalStateException("When using protocol buffers the XmlType MUST be on the interface.  The interface " + originalInterface.getName() +
                    " does not have one");
        }
        
        String propOrder[] = xmlType.propOrder();
        
        Set<String> uniq = new HashSet<String>();
        for (String order : propOrder) {
            if (uniq.contains(order)) {
                throw new IllegalStateException("XmlType propOrder field on " + originalInterface.getName() + " has duplicate value " + order);
            }
            
            uniq.add(order);
        }
        
        Set<String> extras = new HashSet<String>();
        Set<String> missing = new HashSet<String>(uniq);
        for (ChildDescriptor cd : allChildren.values()) {
            ChildDataModel cdm = cd.getChildDataModel();
            String interfaceMethod;
            if (cdm != null) {
                interfaceMethod = getXmlTypeValueFromMethodName(cdm.getOriginalMethodName(), cdm);
                
            }
            else {
                ParentedModel pm = cd.getParentedModel();
                
                interfaceMethod = getXmlTypeValueFromMethodName(pm.getOriginalMethodName(), pm);
            }
            
            missing.remove(interfaceMethod);
            if (!uniq.contains(interfaceMethod)) {
                extras.add(interfaceMethod);
            }
        }
        
        if (!missing.isEmpty() || !extras.isEmpty()) {
            throw new IllegalStateException("On interface " + originalInterface.getName() +
                    " the XmlType propOrder field had these extra fields " + missing +
                    " or missing fields " + extras);
        }
    }
    
    private Descriptors.Descriptor convertModelToDescriptor(ModelImpl model, Set<Descriptors.FileDescriptor> knownFiles) throws Exception {
        Map<QName, ChildDescriptor> allChildren = model.getAllChildrenDescriptors();
        
        String protoName = getProtoNameFromModel(model);
        String packageName = getPackageNameFromModel(model);
        
        DescriptorProtos.DescriptorProto.Builder builder = DescriptorProtos.DescriptorProto.newBuilder();
        builder.setName(protoName);
        
        Class<?> originalInterface = model.getOriginalInterfaceAsClass();
        
        validateXmlType(originalInterface, allChildren);
        
        int oneOfNumber = 0;
        Map<String, Integer> oneOfToIndexMap = new HashMap<String, Integer>();
        String currentOneOf = null;
        
        int number = 1;
        for(Map.Entry<QName, ChildDescriptor> entry : allChildren.entrySet()) {
            QName entryKey = entry.getKey();
            String localPart = entryKey.getLocalPart();
            ChildDescriptor childDescriptor = entry.getValue();
            
            String protoPart = PBUtilities.camelCaseToUnderscore(localPart);
            
            DescriptorProtos.FieldDescriptorProto.Builder fBuilder =
                    DescriptorProtos.FieldDescriptorProto.newBuilder().setName(protoPart);
            fBuilder.setNumber(number);
            number++;
            
            ChildDataModel dataModel = childDescriptor.getChildDataModel();
            if (dataModel != null) {
                fBuilder.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL);
                
                Class<?> dataType = dataModel.getChildTypeAsClass();
                String originalMethodName = dataModel.getOriginalMethodName();
                
                String oneOfValue = PBUtilities.getOneOf(originalInterface, originalMethodName, dataType);
                if (GeneralUtilities.safeEquals(oneOfValue, currentOneOf)) {
                    if (oneOfValue != null) {
                        int oneOfDeclIndex = oneOfToIndexMap.get(oneOfValue);
                        
                        fBuilder.setOneofIndex(oneOfDeclIndex);
                    }
                }
                else {
                    if (oneOfValue != null) {
                        DescriptorProtos.OneofDescriptorProto.Builder oneOfBuilder = DescriptorProtos.OneofDescriptorProto.newBuilder();
                        oneOfBuilder.setName(oneOfValue);
                        
                        int oneOfIndex = oneOfNumber++;
                        builder.addOneofDecl(oneOfIndex, oneOfBuilder.build());
                        
                        oneOfToIndexMap.put(oneOfValue, oneOfIndex);

                        fBuilder.setOneofIndex(oneOfIndex);
                    }
                    
                    currentOneOf = oneOfValue;
                }
                
                if (dataModel.getDefaultAsString() != null) {
                    fBuilder.setDefaultValue(dataModel.getDefaultAsString());
                }
            
                DescriptorProtos.FieldDescriptorProto.Type fieldType = convertChildDataModelToType(dataModel);
                if (DescriptorProtos.FieldDescriptorProto.Type.TYPE_ENUM.equals(fieldType)) {
                    String fieldTypeName = convertEnumToDescriptor(dataModel, knownFiles);
                    
                    fBuilder.setTypeName(fieldTypeName);
                }
                
                fBuilder.setType(fieldType);
            }
            else {
                ParentedModel pm = childDescriptor.getParentedModel();
                
                // Set the type
                fBuilder.setType(DescriptorProtos.FieldDescriptorProto.Type.TYPE_MESSAGE);
                
                Class<?> childDataType = pm.getChildModel().getOriginalInterfaceAsClass();
                String originalMethodName = pm.getOriginalMethodName();
                
                String oneOfValue = PBUtilities.getOneOf(originalInterface, originalMethodName, childDataType);
                if (GeneralUtilities.safeEquals(oneOfValue, currentOneOf)) {
                    if (oneOfValue != null) {
                        int oneOfDeclIndex = oneOfToIndexMap.get(oneOfValue);
                        
                        fBuilder.setOneofIndex(oneOfDeclIndex);
                    }
                }
                else {
                    if (oneOfValue != null) {
                        DescriptorProtos.OneofDescriptorProto.Builder oneOfBuilder = DescriptorProtos.OneofDescriptorProto.newBuilder();
                        oneOfBuilder.setName(oneOfValue);
                        
                        int oneOfIndex = oneOfNumber++;
                        builder.addOneofDecl(oneOfIndex, oneOfBuilder.build());
                        
                        oneOfToIndexMap.put(oneOfValue, oneOfIndex);

                        fBuilder.setOneofIndex(oneOfIndex);
                    }
                        
                    currentOneOf = oneOfValue;
                }
                
                ModelImpl childModel = pm.getChildModel();
                String childTypeName = childModel.getOriginalInterface();
                
                fBuilder.setTypeName("." + childTypeName);
                
                ChildType childType = pm.getChildType();
                if (childType.equals(ChildType.ARRAY) || childType.equals(ChildType.LIST)) {
                    fBuilder.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_REPEATED);
                }
                else {
                    fBuilder.setLabel(DescriptorProtos.FieldDescriptorProto.Label.LABEL_OPTIONAL);
                }
            }
            
            builder.addField(fBuilder.build());
            
        }
        
        DescriptorProtos.DescriptorProto proto = builder.build();
        
        DescriptorProtos.FileDescriptorProto.Builder fileBuilder = DescriptorProtos.FileDescriptorProto.newBuilder();
        fileBuilder.addMessageType(proto);
        if (packageName != null) {
          fileBuilder.setPackage(packageName);
        }
        
        DescriptorProtos.FileDescriptorProto fProto = fileBuilder.build();
        
        Descriptors.FileDescriptor fDesc = Descriptors.FileDescriptor.buildFrom(fProto,
                knownFiles.toArray(new Descriptors.FileDescriptor[knownFiles.size()]));
        
        Descriptors.Descriptor fD = fDesc.findMessageTypeByName(protoName);
        
        return fD;
    }
    
    private static String getExtendedSimpleName(Class<?> expectedType) {
        String name = expectedType.getName();
        
        int index = name.lastIndexOf('.');
        if (index < 0) {
            return name;
        }
        
        String retVal = name.substring(index + 1);
        retVal = retVal.replace('$', '_');
        return retVal;
    }
    
    private String convertEnumToDescriptor(ChildDataModel childDataModel,
            Set<Descriptors.FileDescriptor> knownFiles) throws Exception {
        synchronized (allEnums) {
            Class<?> expectedType = childDataModel.getChildTypeAsClass();
            if (allEnums.containsKey(expectedType)) {
                return "." + expectedType.getName();
            }
        
            String enumSimpleTypeName = getExtendedSimpleName(expectedType);
            String enumPackageName = expectedType.getPackage().getName();
            String enumTypeName = enumPackageName + "." + enumSimpleTypeName;
        
            DescriptorProtos.EnumDescriptorProto.Builder builder = DescriptorProtos.EnumDescriptorProto.newBuilder();
            builder.setName(enumSimpleTypeName);
        
            int number = 0;
            for (Object e : expectedType.getEnumConstants()) {
                DescriptorProtos.EnumValueDescriptorProto.Builder enumBuilder = DescriptorProtos.EnumValueDescriptorProto.newBuilder();
            
                enumBuilder.setName(e.toString());
                enumBuilder.setNumber(number);
                number++;
            
                builder.addValue(enumBuilder.build());
            }
        
            DescriptorProtos.EnumDescriptorProto eProto = builder.build();
        
            DescriptorProtos.FileDescriptorProto.Builder fileBuilder = DescriptorProtos.FileDescriptorProto.newBuilder();
            fileBuilder.addEnumType(eProto);
            if (enumPackageName != null) {
              fileBuilder.setPackage(enumPackageName);
            }
        
            DescriptorProtos.FileDescriptorProto fProto = fileBuilder.build();
        
            Descriptors.FileDescriptor fDesc = Descriptors.FileDescriptor.buildFrom(fProto,
                knownFiles.toArray(new Descriptors.FileDescriptor[knownFiles.size()]));
        
            knownFiles.add(fDesc);
        
            Descriptors.EnumDescriptor fD = fDesc.findEnumTypeByName(enumSimpleTypeName);
        
            allEnums.put(expectedType, fD);
        
            return "." + enumTypeName;
        }
    }
    
    private Object convertFieldForMarshal(Object field, Class<?> expectedType) {
        if (field == null) {
            if (String.class.equals(expectedType)) {
                return new String("");
            }
            
            return null;
        }
        
        if (expectedType.isEnum()) {
            Descriptors.EnumDescriptor enumDescriptor;
            synchronized (allEnums) {
                enumDescriptor = allEnums.get(expectedType);
            }
            
            if (enumDescriptor == null) {
                throw new IllegalStateException("Unknown enum type " + expectedType.getName());
            }
            
            EnumValueDescriptor retVal = enumDescriptor.findValueByName(field.toString());
            if (retVal == null) {
                throw new IllegalStateException("Unknown enum value " + field + " in enumeration " + expectedType.getName());
            }
            
            return retVal;
        }
        
        if (field instanceof Short) {
            Short s = (Short) field;
            return new Integer(s.intValue());
        }
        if (field instanceof Character) {
            Character c = (Character) field;
            return new String(c.toString());
        }
        if (field instanceof Byte) {
            Byte b = (Byte) field;
            byte retVal[] = new byte[1];
            retVal[0] = b.byteValue();
            return retVal;
        }
        
        return field;
    }
    
    private Object convertFieldForUnmarshal(Object field, ChildDataModel expected) {
        if (field == null) return null;
        
        Class<?> expectedType = (Class<Object>) expected.getChildTypeAsClass();
        
        if (expectedType.isEnum()) {
            Descriptors.EnumDescriptor enumDescriptor;
            synchronized (allEnums) {
                enumDescriptor = allEnums.get(expectedType);
            }
            
            if (enumDescriptor == null) {
                throw new IllegalStateException("Could not find enumeration type " + expectedType.getName());
            }
            
            EnumValueDescriptor evd = (EnumValueDescriptor) field;
            String enumValueName = evd.getName();
            
            Object found = null;
            for (Object c : expectedType.getEnumConstants()) {
                if (c.toString().equals(enumValueName)) {
                    found = c;
                    break;
                }
            }
            
            if (found == null) {
                throw new IllegalStateException("Could not find enumeration value " + enumValueName + " in enum " +
                    expectedType.getName());
            }
            
            return found;
        }
        
        if (expectedType.equals(short.class) || expectedType.equals(Short.class)) {
            Integer i = (Integer) field;
            return i.shortValue();
        }
        
        if (expectedType.equals(char.class) || expectedType.equals(Character.class)) {
            String s = (String) field;
            return s.charAt(0);
        }
        
        if (expectedType.equals(byte.class) || expectedType.equals(Byte.class)) {
            ByteString b = (ByteString) field;
            return b.byteAt(0);
        }
        
        if (String.class.equals(expectedType) && ((String) field).isEmpty()) {
            // PBuf returns empty string for null.  There is no way to
            // tell the difference, so we are just converting empty
            // string back null
            return null;
        }
        
        return field;
    }
    
    private static CodedInputStream getUsedInputStream(Map<String, Object> options) {
    	if (options == null) return null;
    	
    	CISStreamCloser retVal = (CISStreamCloser) options.get(PBufUtilities.PBUF_STREAMING_OPTION);
    	if (retVal == null) return null;
    	
    	return retVal.stream;
    }
    
    private static CodedOutputStream getUsedOutputStream(Map<String, Object> options) {
    	if (options == null) return null;
    	
    	COSStreamCloser retVal = (COSStreamCloser) options.get(PBufUtilities.PBUF_STREAMING_OPTION);
    	if (retVal == null) return null;
    	
    	return retVal.stream;
    }
    
    private static boolean getPrependSize(Map<String, Object> options) {
        if (options == null) return true;
        
        Boolean val = (Boolean) options.get(PBufUtilities.PBUF_OPTION_INT32_HEADER);
        if (val == null) return true;
        
        return val;
    }
    
    private static class CISStreamCloser implements AutoCloseable {
    	private final CodedInputStream stream;
    	
    	private CISStreamCloser(CodedInputStream stream) {
    		this.stream = stream;
    	}

		@Override
		public void close() throws Exception {
			// Do nothing
		}
    	
    }
    
    private static class COSStreamCloser implements AutoCloseable {
    	private final CodedOutputStream stream;
    	
    	private COSStreamCloser(CodedOutputStream stream) {
    		this.stream = stream;
    	}

		@Override
		public void close() throws Exception {
			// Do nothing
		}
    	
    }
    
    @Override
    public String toString() {
        return "PBufParser(" + System.identityHashCode(this) + ")";
    }
}
