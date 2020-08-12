/*
 * Copyright (c) 2010, 2020 Oracle and/or its affiliates. All rights reserved.
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

package org.glassfish.hk2.classmodel.reflect.impl;

import org.glassfish.hk2.classmodel.reflect.*;
import org.glassfish.hk2.classmodel.reflect.Type;
import org.objectweb.asm.*;
import org.objectweb.asm.signature.SignatureReader;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * ASM class visitor, used to build to model
 *
 * @author Jerome Dochez
 */
@SuppressWarnings("unchecked")
public class ModelClassVisitor extends ClassVisitor {

    private static Logger logger = Logger.getLogger(ModelClassVisitor.class.getName());
  
    private final ParsingContext ctx;
    private final TypeBuilder typeBuilder;
    private final URI definingURI;
    private final String entryName;
    TypeImpl type;
    boolean deepVisit =false;
    private final ClassVisitingContext classContext;
    private final MemberVisitingContext visitingContext;
    private final ModelFieldVisitor fieldVisitor;
    private final ModelMethodVisitor methodVisitor;
    private final ModelAnnotationVisitor annotationVisitor;
    private final ModelDefaultAnnotationVisitor defaultAnnotationVisitor;
    private static final int discarded = 0;
    private final boolean isApplicationClass;


    public ModelClassVisitor(ParsingContext ctx, URI definingURI, String entryName,
                             boolean isApplicationClass) {
        super(Opcodes.ASM7);
        
        this.ctx = ctx;
        this.definingURI = definingURI;
        this.entryName = entryName;
        typeBuilder = ctx.getTypeBuilder(definingURI);
        classContext = new ClassVisitingContext();
        visitingContext = new MemberVisitingContext(ctx.getConfig().modelUnAnnotatedMembers());
        fieldVisitor = new ModelFieldVisitor(visitingContext);
        methodVisitor = new ModelMethodVisitor(visitingContext);
        annotationVisitor = new ModelAnnotationVisitor();
        defaultAnnotationVisitor = new ModelDefaultAnnotationVisitor(methodVisitor.getContext());
        this.isApplicationClass = isApplicationClass;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        String parentName = (superName!=null?org.objectweb.asm.Type.getObjectType(superName).getClassName():null);
        TypeProxy parent = null;
        Class<? extends Type> typeType = typeBuilder.getType(access);
        if (!typeType.equals(AnnotationType.class)) {
            parent = (parentName!=null?typeBuilder.getHolder(parentName, typeType):null);
        }
        if (parent!=null && !parentName.equals(Object.class.getName())) {
            // put a temporary parent until we eventually visit it. 
            TypeImpl parentType = typeBuilder.getType(access, parentName, null);
            parent.set(parentType);
        }
        String className = org.objectweb.asm.Type.getObjectType(name).getClassName();
        URI classDefURI=null;
        try {
            int index = entryName.length() - name.length() - 6;
            if (null == definingURI || index==0) {
                classDefURI=definingURI;
            } else {
                String newPath=(index>0?definingURI.getPath() + entryName.substring(0, index):definingURI.getPath());
                classDefURI = new URI(definingURI.getScheme(), newPath, definingURI.getFragment());
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        
        if (logger.isLoggable(Level.FINER)) {
          logger.log(Level.FINER, "visiting {0} with classDefURI={1}", new Object[] {entryName, classDefURI});
        }

//        if (!new File(classDefURI).exists()) {
//          throw new IllegalStateException(entryName + ": " + classDefURI.toString());
//        }
        
        type = ctx.getTypeBuilder(classDefURI).getType(access, className, parent);
        type.setApplicationClass(isApplicationClass);
        type.getProxy().visited();
        type.addDefiningURI(classDefURI);
        deepVisit = ctx.getConfig().getAnnotationsOfInterest().isEmpty();

        classContext.type = type;
        classContext.interfaces = interfaces;
        classContext.parent = parent;
        // reverse index
        if (parent!=null) {
            parent.addSubTypeRef(type);
        }


        try {
            ExtensibleTypeImpl classModel = (ExtensibleTypeImpl) type;
            if (signature!=null) {
                SignatureReader reader = new SignatureReader(signature);
                SignatureVisitorImpl signatureVisitor = new SignatureVisitorImpl(typeBuilder);
                reader.accept(signatureVisitor);
                if (!signatureVisitor.getImplementedInterfaces().isEmpty()) {
                    for (ParameterizedInterfaceModelImpl pim : signatureVisitor.getImplementedInterfaces()) {
                        if (pim.getRawInterfaceProxy()!=null) {
                            classModel.isImplementing(pim);
                            if (classModel instanceof ClassModel) {
                                pim.getRawInterfaceProxy().
                                    addImplementation((ClassModel) classModel);
                            }
                        }
                    }
                }
                classModel.setFormalTypeParameters(signatureVisitor.getFormalTypeParameters());
            } else {
                if (!typeType.equals(AnnotationType.class)) {
                    for (String intf : interfaces) {
                        String interfaceName = org.objectweb.asm.Type.getObjectType(intf).getClassName();
                        TypeImpl interfaceModel = typeBuilder.getType(Opcodes.ACC_INTERFACE, interfaceName, null);
                        TypeProxy<InterfaceModel> typeProxy = typeBuilder.getHolder(interfaceName, InterfaceModel.class);
                        if (typeProxy.get() == null) {
                            typeProxy.set((InterfaceModel) interfaceModel);
                        }
                        
                        classModel.isImplementing(typeProxy);
                        if (classModel instanceof ClassModel) {
                            typeProxy.addImplementation((ClassModel) classModel);
                        }
                    }
                }
            }
        } catch(ClassCastException e) {
            // ignore
        } catch(Exception ne) {
            ne.printStackTrace();
        }

    }

    @Override
    public void visitSource(String source, String debug) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitOuterClass(String owner, String name, String desc) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        desc = unwrap(desc);
        
        final AnnotationTypeImpl at = (AnnotationTypeImpl) typeBuilder.getType(Opcodes.ACC_ANNOTATION, desc, null);
        final AnnotationModelImpl am = new AnnotationModelImpl(type, at);

        // reverse index
        at.getAnnotatedElements().add(type);

        // forward index
        type.addAnnotation(am);

        if (ctx.getConfig().getAnnotationsOfInterest().contains(desc)) {
            logger.log(Level.FINER, "Inspecting fields of {0}", type.getName());
            deepVisit =true;
        }
        annotationVisitor.setAnnotation(am);
        return annotationVisitor;
    }

    @Override
    public void visitAttribute(Attribute attr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void visitInnerClass(String name, String outerName, String innerName, int access) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public FieldVisitor visitField(int access, final String name, final String desc, final String signature, final Object value) {

        if (!deepVisit) {
            return null;
        }

        ExtensibleTypeImpl cm;
        if (!(type instanceof ExtensibleTypeImpl)) {
            logger.log(
                    Level.SEVERE,
                    "Field visitor invoked for field {0}in type {1} which is not a ClassModel type instance but a {2}",
                    new Object[]{name, type.getName(), type.getClass().getName()}
            );
            return null;
        }
        cm = (ExtensibleTypeImpl) type;


        final FieldModelImpl field = typeBuilder.getFieldModel(name, null, cm);

        SignatureReader reader = new SignatureReader(signature == null ? desc : signature);
        FieldSignatureVisitorImpl visitor = new FieldSignatureVisitorImpl(typeBuilder, field);
        reader.accept(visitor);

        org.objectweb.asm.Type asmType = org.objectweb.asm.Type.getType(desc);
        field.setType(asmType);
        if (field.getTypeProxy() == null) {
            field.setTypeProxy(typeBuilder.getHolder(asmType.getClassName()));
        }

        field.setAccess(access);
        fieldVisitor.getContext().field = field;
        fieldVisitor.getContext().typeDesc = desc;
        fieldVisitor.getContext().access = access;
        fieldVisitor.getContext().classModel = cm;

        return fieldVisitor;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!deepVisit) {
            return null;
        }


        ExtensibleType cm;
        if (!(type instanceof ExtensibleType)) {
            logger.log(
                    Level.SEVERE,
                    "Method visitor invoked for method {0} in type {1} which is not an ExtensibleType type instance but a {2}",
                    new Object[]{name, type.getName(), type.getClass().getName()}
            );
            return null;
        }
        cm = (ExtensibleType) type;
        MethodModelImpl methodModel = new MethodModelImpl(
                name, cm, (signature == null ? desc : signature)
        );

        SignatureReader reader = new SignatureReader(signature == null ? desc : signature);
        MethodSignatureVisitorImpl visitor = new MethodSignatureVisitorImpl(typeBuilder, methodModel);
        reader.accept(visitor);

        methodModel.setParameters(visitor.getParameters());
        methodModel.setReturnType(visitor.getReturnType());

        // fallback for void, primitive data types, java.lang.Object and generic wildcards types
        ParameterizedTypeImpl returnType = (ParameterizedTypeImpl) methodModel.getReturnType();
        org.objectweb.asm.Type type = org.objectweb.asm.Type.getReturnType(desc);
        returnType.setType(type);

        org.objectweb.asm.Type[] types = org.objectweb.asm.Type.getArgumentTypes(desc);
        for (int i = 0; i < methodModel.getParameters().size(); i++) {
            ParameterImpl parameter = (ParameterImpl) methodModel.getParameter(i);
            parameter.setType(types[i]);
        }

        methodVisitor.getContext().method = methodModel;
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        type=null;
    }                                                            

    private String unwrap(String desc) {
        return org.objectweb.asm.Type.getType(desc).getClassName();
    }

    private static class ClassVisitingContext {
        TypeImpl type;
        TypeProxy parent;
        String[] interfaces;
    }

    private static class MemberVisitingContext {
        final boolean modelUnAnnotatedMembers;

        private MemberVisitingContext(boolean modelUnAnnotatedMembers) {
            this.modelUnAnnotatedMembers = modelUnAnnotatedMembers;
        }
    }

    private static class FieldVisitingContext extends MemberVisitingContext {
        FieldModelImpl field;
        String typeDesc;
        ExtensibleTypeImpl classModel;
        int access;

        private FieldVisitingContext(boolean modelUnAnnotatedMembers) {
            super(modelUnAnnotatedMembers);
        }
    }

    private static class MethodVisitingContext extends MemberVisitingContext {
        MethodModelImpl method;

        private MethodVisitingContext(boolean modelUnAnnotatedMembers) {
            super(modelUnAnnotatedMembers);
        }
    }

    private static class AnnotationVisitingContext {
        AnnotationModelImpl annotation;
        ArrayDeque parent = new ArrayDeque();
    }

    private class ModelMethodVisitor extends MethodVisitor {

        private final MethodVisitingContext context;

        private ModelMethodVisitor(MemberVisitingContext context) {
            super(Opcodes.ASM7);
            
            this.context = new MethodVisitingContext(context.modelUnAnnotatedMembers);
        }

        MethodVisitingContext getContext() {
            return context;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
            if (context.method==null) {
                // probably an annotation method, ignore
                return null;
            }
          
            AnnotationTypeImpl annotationType = (AnnotationTypeImpl) typeBuilder.getType(Opcodes.ACC_ANNOTATION, unwrap(desc), null);
            AnnotationModelImpl annotationModel = new AnnotationModelImpl(context.method, annotationType);

            // reverse index
            annotationType.getAnnotatedElements().add(context.method);

            // forward index
            context.method.addAnnotation(annotationModel);
            annotationVisitor.setAnnotation(annotationModel);
            return annotationVisitor;
        }

        @Override
        public AnnotationVisitor visitParameterAnnotation(
                final int parameterIndex,
                final String desc,
                final boolean visible
        ) {

            AnnotationTypeImpl annotationType = (AnnotationTypeImpl) typeBuilder.getType(Opcodes.ACC_ANNOTATION, unwrap(desc), null);
            ParameterImpl parameter = (ParameterImpl) context.method.getParameter(parameterIndex);

            AnnotationModelImpl annotationModel = new AnnotationModelImpl(parameter, annotationType);

            // reverse index.
            annotationType.getAnnotatedElements().add(parameter);

            // forward index
            parameter.addAnnotation(annotationModel);
            annotationVisitor.setAnnotation(annotationModel);
            return annotationVisitor;
        }

        @Override
        public void visitEnd() {
            if (context.modelUnAnnotatedMembers || !context.method.getAnnotations().isEmpty()) {
                type.addMethod(context.method);
            }
//            context.method=null;
        }

        @Override
        public AnnotationVisitor visitAnnotationDefault() {
          return defaultAnnotationVisitor;
        }
    }
    
    
    private class ModelDefaultAnnotationVisitor extends AnnotationVisitor {

        private final MethodVisitingContext context;

        public ModelDefaultAnnotationVisitor(MethodVisitingContext visitingContext) {
            super(Opcodes.ASM7);
            this.context = visitingContext;
        }

        @Override
        public void visit(java.lang.String desc, java.lang.Object value) {
            AnnotationTypeImpl am = (AnnotationTypeImpl) context.method.owner;
            am.addDefaultValue(context.method.getName(), value);
        }
    }

    private class ModelFieldVisitor extends FieldVisitor {

        private final FieldVisitingContext context;

        private ModelFieldVisitor(MemberVisitingContext context) {
            super(Opcodes.ASM7);

            this.context = new FieldVisitingContext(context.modelUnAnnotatedMembers);
        }

        FieldVisitingContext getContext() {
            return context;
        }

        @Override
        public AnnotationVisitor visitAnnotation(String s, boolean b) {
            FieldModelImpl field = context.field;

            AnnotationTypeImpl annotationType = (AnnotationTypeImpl) typeBuilder.getType(Opcodes.ACC_ANNOTATION, unwrap(s), null);
            AnnotationModelImpl annotationModel = new AnnotationModelImpl(field, annotationType);

            // reverse index.
            annotationType.getAnnotatedElements().add(field);

            // forward index
            field.addAnnotation(annotationModel);
            annotationVisitor.setAnnotation(annotationModel);
            return annotationVisitor;
        }

        @Override
        public void visitEnd() {

            // if we have been requested to model unannotated members OR the field has annotations.
            if (context.modelUnAnnotatedMembers || !context.field.getAnnotations().isEmpty()) {

                // reverse index.
                if (context.field.getTypeProxy() != null) {
                    context.field.getTypeProxy().addFieldRef(context.field);
                }

                // forward index
                if ((Opcodes.ACC_STATIC & context.access) == Opcodes.ACC_STATIC) {
                    context.classModel.addStaticField(context.field);
                } else {
                    context.classModel.addField(context.field);
                }
            }

            context.field = null;
        }
    }

    private class ModelAnnotationVisitor extends AnnotationVisitor {

        private final AnnotationVisitingContext context;

        private ModelAnnotationVisitor() {
            super(Opcodes.ASM7);

            this.context = new AnnotationVisitingContext();
        }

        AnnotationVisitingContext getContext() {
            return context;
        }

        void setAnnotation(AnnotationModelImpl annotation) {
            this.context.annotation = annotation;
            context.parent.add(annotation);
        }

        @Override
        public void visit(String name, Object value) {
            addValue(name, value);
        }

        @Override
        public AnnotationVisitor visitArray(String name) {
            ArrayVisitor arrayVisitor = new ArrayVisitor(annotationVisitor);
            addValue(name, arrayVisitor.getValues());
            context.parent.add(arrayVisitor.getValues());
            return arrayVisitor;
        }

        private void addValue(String name, Object value) {
            if (!context.parent.isEmpty()) {
                Object parent = context.parent.peekLast();
                if (parent instanceof AnnotationModelImpl) {
                    ((AnnotationModelImpl) parent).addValue(name, value);
                } else if (parent instanceof List) {
                    ((List) parent).add(value);
                } else if (parent instanceof Map) {
                    ((Map) parent).put(name, value);
                } else {
                    throw new IllegalStateException();
                }
            }
        }

        @Override
        public void visitEnum(String name, String desc, String value) {
            final Type type = (Type) typeBuilder.getType(Opcodes.ACC_ENUM, unwrap(desc), null);
            final EnumModel enumModel = new EnumModelImpl(type, value);

            addValue(name, enumModel);
        }

        @Override
        public AnnotationVisitor visitAnnotation(String name, String desc) {
            desc = unwrap(desc);

            final AnnotationTypeImpl at = (AnnotationTypeImpl) typeBuilder.getType(Opcodes.ACC_ANNOTATION, desc, null);
            final AnnotationModelImpl am = new AnnotationModelImpl(null, at);

            addValue(name, am);
            context.parent.add(am);
            return annotationVisitor;
        }

        @Override
        public void visitEnd() {
            if (!context.parent.isEmpty()) {
                context.parent.pollLast();
            }
        }
    }

    private class ArrayVisitor extends AnnotationVisitor {

        protected List values = new ArrayList();

        public ArrayVisitor(AnnotationVisitor av) {
            super(Opcodes.ASM7, av);
        }

        @Override
        public void visit(String name, Object value) {
            values.add(unwrap(value));
        }

        private Object unwrap(Object value) {
            if (org.objectweb.asm.Type.class.isInstance(value)) {
                return org.objectweb.asm.Type.class.cast(value).getClassName();
            }
            return value;
        }

        public List getValues() {
            return values;
        }
    }

}
