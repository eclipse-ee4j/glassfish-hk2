/*
 * Copyright (c) 2020 TechEmpower. All rights reserved.
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

package org.glassfish.hk2.extras.provides;

import java.lang.annotation.Annotation;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import jakarta.inject.Named;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DescriptorType;
import org.glassfish.hk2.api.DescriptorVisibility;
import org.glassfish.hk2.api.HK2Loader;
import org.glassfish.hk2.api.Injectee;
import org.glassfish.hk2.api.ProxyForSameScope;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.UseProxy;
import org.glassfish.hk2.utilities.BuilderHelper;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;

/**
 * An {@link ActiveDescriptor} implementation used by {@link ProvidesListener}
 * and {@link NoInstancesService}.
 */
final class ProvidesDescriptor<T> implements ActiveDescriptor<T> {
  private final AnnotatedElement annotatedElement;
  private final Class<?> implementationClass;
  private final Type implementationType;
  private final Set<Type> contracts;
  private final Annotation scopeAnnotation;
  private final Function<ServiceHandle<?>, T> createFunction;
  private final Consumer<T> disposeFunction;

  ProvidesDescriptor(AnnotatedElement annotatedElement,
                     Class<?> implementationClass,
                     Type implementationType,
                     Set<Type> contracts,
                     Annotation scopeAnnotation,
                     Function<ServiceHandle<?>, T> createFunction,
                     Consumer<T> disposeFunction) {

    this.annotatedElement = Objects.requireNonNull(annotatedElement);
    this.implementationClass = Objects.requireNonNull(implementationClass);
    this.implementationType = Objects.requireNonNull(implementationType);
    this.contracts = Collections.unmodifiableSet(new HashSet<>(Objects.requireNonNull(contracts)));
    this.scopeAnnotation = Objects.requireNonNull(scopeAnnotation);
    this.createFunction = Objects.requireNonNull(createFunction);
    this.disposeFunction = Objects.requireNonNull(disposeFunction);
  }

  @Override
  public T create(ServiceHandle<?> root) {
    return createFunction.apply(root);
  }

  @Override
  public void dispose(T instance) {
    disposeFunction.accept(instance);
  }

  @Override
  public Class<?> getImplementationClass() {
    return implementationClass;
  }

  @Override
  public Type getImplementationType() {
    return implementationType;
  }

  @Override
  public Set<Type> getContractTypes() {
    return contracts;
  }

  @Override
  public boolean isReified() {
    return true;
  }

  @Override
  public Annotation getScopeAsAnnotation() {
    return scopeAnnotation;
  }

  @Override
  public Class<? extends Annotation> getScopeAnnotation() {
    return getScopeAsAnnotation().annotationType();
  }

  @Override
  public Set<Annotation> getQualifierAnnotations() {
    return Collections.unmodifiableSet(
        ReflectionHelper.getQualifierAnnotations(annotatedElement));
  }

  @Override
  public List<Injectee> getInjectees() {
    return Collections.emptyList();
  }

  @Override
  public /*@Nullable*/ Long getFactoryServiceId() {
    return null;
  }

  @Override
  public /*@Nullable*/ Long getFactoryLocatorId() {
    return null;
  }

  @Override
  public String getImplementation() {
    return getImplementationClass().getName();
  }

  @Override
  public Set<String> getAdvertisedContracts() {
    return getContractTypes()
        .stream()
        .map(contract -> ReflectionHelper.getRawClass(contract))
        .filter(contractClass -> contractClass != null)
        .map(contractClass -> contractClass.getName())
        .collect(
            Collectors.collectingAndThen(
                Collectors.toSet(),
                set -> Collections.unmodifiableSet(set)));
  }

  @Override
  public String getScope() {
    return getScopeAnnotation().getName();
  }

  @Override
  public /*@Nullable*/ String getName() {
    return Arrays.stream(annotatedElement.getAnnotations())
                 .filter(annotation -> annotation.annotationType() == Named.class)
                 .map(annotation -> ((Named) annotation))
                 .map(annotation -> annotation.value())
                 .findAny()
                 .orElse(null);
  }

  @Override
  public Set<String> getQualifiers() {
    return getQualifierAnnotations()
        .stream()
        .map(annotation -> annotation.annotationType())
        .map(annotationType -> annotationType.getName())
        .collect(
            Collectors.collectingAndThen(
                Collectors.toSet(),
                set -> Collections.unmodifiableSet(set)));
  }

  @Override
  public DescriptorType getDescriptorType() {
    return DescriptorType.CLASS;
  }

  @Override
  public DescriptorVisibility getDescriptorVisibility() {
    return DescriptorVisibility.NORMAL;
  }

  @Override
  public Map<String, List<String>> getMetadata() {
    Map<String, List<String>> metadata = new HashMap<>();

    Annotation scope = getScopeAsAnnotation();
    BuilderHelper.getMetadataValues(scope, metadata);

    for (Annotation qualifier : getQualifierAnnotations())
      BuilderHelper.getMetadataValues(qualifier, metadata);

    metadata.replaceAll((key, values) -> Collections.unmodifiableList(values));
    return Collections.unmodifiableMap(metadata);
  }

  @Override
  public /*@Nullable*/ HK2Loader getLoader() {
    return null;
  }

  /*@GuardedBy("this")*/
  private int ranking = 0;

  /*@GuardedBy("this")*/
  private boolean initialRankingFound = false;

  @Override
  public synchronized int getRanking() {
    if (!initialRankingFound) {
      Rank rank = annotatedElement.getAnnotation(Rank.class);
      if (rank != null)
        ranking = rank.value();

      initialRankingFound = true;
    }

    return ranking;
  }

  @Override
  public synchronized int setRanking(int ranking) {
    int previousRanking = getRanking();
    this.ranking = ranking;
    return previousRanking;
  }

  @Override
  public /*@Nullable*/ Boolean isProxiable() {
    UseProxy useProxy = annotatedElement.getAnnotation(UseProxy.class);
    return (useProxy == null) ? null : useProxy.value();
  }

  @Override
  public /*@Nullable*/ Boolean isProxyForSameScope() {
    ProxyForSameScope proxyForSameScope =
        annotatedElement.getAnnotation(ProxyForSameScope.class);

    return (proxyForSameScope == null) ? null : proxyForSameScope.value();
  }

  @Override
  public /*@Nullable*/ String getClassAnalysisName() {
    return null;
  }

  @Override
  public /*@Nullable*/ Long getServiceId() {
    return null;
  }

  @Override
  public /*@Nullable*/ Long getLocatorId() {
    return null;
  }

  /*@GuardedBy("this")*/
  private /*@Nullable*/ T cache = null;

  /*@GuardedBy("this")*/
  private boolean isCacheSet = false;

  @Override
  public synchronized /*@Nullable*/ T getCache() {
    if (!isCacheSet)
      throw new IllegalStateException();

    return cache;
  }

  @Override
  public synchronized boolean isCacheSet() {
    return isCacheSet;
  }

  @Override
  public synchronized void setCache(T cacheMe) {
    cache = cacheMe;
    isCacheSet = true;
  }

  @Override
  public synchronized void releaseCache() {
    cache = null;
    isCacheSet = false;
  }

  @Override
  public String toString() {
    return getClass().getSimpleName() + "[" + annotatedElement + "]";
  }
}
