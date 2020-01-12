package org.glassfish.hk2.extras.provides;

import static org.glassfish.hk2.extras.provides.CompatibleWithJava8.canAccess;
import static org.glassfish.hk2.extras.provides.CompatibleWithJava8.setOf;
import static org.glassfish.hk2.extras.provides.CompatibleWithJava8.toUnmodifiableSet;

import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Scope;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.ContractIndicator;
import org.glassfish.hk2.api.Descriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationListener;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Filter;
import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.PerLookup;
import org.glassfish.hk2.api.Self;
import org.glassfish.hk2.api.ServiceHandle;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.glassfish.hk2.utilities.reflection.ReflectionHelper;
import org.glassfish.hk2.utilities.reflection.TypeChecker;
import org.jvnet.hk2.annotations.Contract;
import org.jvnet.hk2.annotations.ContractsProvided;
import org.jvnet.hk2.annotations.Optional;

/**
 * Enables the {@link Provides} annotation.
 */
@Singleton
public class ProvidesListener implements DynamicConfigurationListener {
  private final ServiceLocator locator;
  private final ProvidersSeen seen = new ProvidersSeen();

  @Inject
  public ProvidesListener(ServiceLocator locator) {
    this.locator = Objects.requireNonNull(locator);
  }

  /**
   * Matches services whose {@linkplain Descriptor#getImplementation()
   * implementation} classes are scanned for {@link Provides} annotations.
   *
   * <p>The default filter matches all services.  Override this method to permit
   * a smaller subset of services.  For example:
   *
   * <pre>
   *   &#64;Singleton
   *   public class FilteredProvidesListener extends ProvidesListener {
   *     &#64;Inject
   *     public FilteredProvidesListener(ServiceLocator locator) {
   *       super(locator);
   *     }
   *
   *     &#64;Override
   *     protected Filter getFilter() {
   *       return d -&gt; d.getImplementation().startsWith("com.example.");
   *     }
   *   }
   * </pre>
   */
  protected Filter getFilter() {
    return any -> true;
  }

  @Override
  public void configurationChanged() {
    Filter filter = getFilter();

    Set<ActiveDescriptor<?>> providers = new LinkedHashSet<>();
    for (ActiveDescriptor<?> provider : locator.getDescriptors(filter))
      providers.add(locator.reifyDescriptor(provider));

    seen.retainAll(providers);

    DynamicConfigurationService configurationService =
        locator.getService(DynamicConfigurationService.class);

    DynamicConfiguration configuration =
        configurationService.createDynamicConfiguration();

    int added = 0;
    for (ActiveDescriptor<?> provider : providers)
      added += addDescriptors(provider, configuration);

    if (added > 0)
      configuration.commit();
  }

  /**
   * Adds descriptors for each of the methods and fields annotated with {@link
   * Provides} in the specified service.  This method is idempotent.
   *
   * @param providerDescriptor the descriptor of the service which may contain
   *        {@link Provides} annotations
   * @param configuration the configuration to be modified with new descriptors
   * @return the number of descriptors added as a result of the call
   */
  private int addDescriptors(ActiveDescriptor<?> providerDescriptor,
                             DynamicConfiguration configuration) {

    Objects.requireNonNull(providerDescriptor);
    Objects.requireNonNull(configuration);

    if (!seen.add(providerDescriptor))
      return 0;

    Class<?> providerClass = providerDescriptor.getImplementationClass();
    Type providerType = providerDescriptor.getImplementationType();

    int added = 0;

    for (Method method : providerClass.getMethods()) {
      Provides providesAnnotation = method.getAnnotation(Provides.class);
      if (providesAnnotation == null)
        continue;

      if (!seen.add(providerDescriptor, method))
        continue;

      Class<?> providedClass = method.getReturnType();

      Type providedType =
          TypeUtils.resolveType(
              providerType,
              method.getGenericReturnType());

      if (TypeUtils.containsTypeVariable(providedType))
        continue;

      if (Arrays.stream(method.getParameters())
                .map(
                    parameter ->
                        TypeUtils.resolveType(
                            providerType,
                            parameter.getParameterizedType()))
                .anyMatch(
                    parameterType ->
                        TypeUtils.containsTypeVariable(parameterType)))
        continue;

      Set<Type> providedContracts =
          getContracts(
              providesAnnotation,
              providedType);

      Annotation scopeAnnotation =
          getScopeAnnotation(
              providerDescriptor,
              method,
              providedContracts);

      AtomicReference<ActiveDescriptor<?>> selfHolder = new AtomicReference<>();

      Supplier<ActiveDescriptor<?>> selfSupplier =
          () -> {
            ActiveDescriptor<?> self = selfHolder.get();
            if (self == null)
              throw new NoSuchElementException();
            return self;
          };

      Function<ServiceHandle<?>, Object> createFunction;
      if (Modifier.isStatic(method.getModifiers()))
        createFunction =
            getCreateFunctionFromStaticMethod(
                method,
                providerType,
                selfSupplier,
                locator);
      else
        createFunction =
            getCreateFunctionFromInstanceMethod(
                method,
                providerType,
                providerDescriptor,
                selfSupplier,
                locator);

      Consumer<Object> disposeFunction =
          getDisposeFunction(
              providerDescriptor,
              providesAnnotation,
              method,
              providedClass,
              providedType,
              providerClass,
              providerType,
              selfSupplier,
              locator);

      if (disposeFunction == null)
        continue;

      ActiveDescriptor<?> self =
          configuration.addActiveDescriptor(
              new ProvidesDescriptor<>(
                  method,
                  providedClass,
                  providedType,
                  providedContracts,
                  scopeAnnotation,
                  createFunction,
                  disposeFunction));

      selfHolder.set(self);

      added++;
    }

    for (Field field : providerClass.getFields()) {
      Provides providesAnnotation = field.getAnnotation(Provides.class);
      if (providesAnnotation == null)
        continue;

      if (!seen.add(providerDescriptor, field))
        continue;

      Class<?> providedClass = field.getType();

      Type providedType =
          TypeUtils.resolveType(
              providerType,
              field.getGenericType());

      if (TypeUtils.containsTypeVariable(providedType))
        continue;

      Set<Type> providedContracts =
          getContracts(
              providesAnnotation,
              providedType);

      Annotation scopeAnnotation =
          getScopeAnnotation(
              providerDescriptor,
              field,
              providedContracts);

      Function<ServiceHandle<?>, Object> createFunction =
          Modifier.isStatic(field.getModifiers())
              ? getCreateFunctionFromStaticField(field, locator)
              : getCreateFunctionFromInstanceField(providerDescriptor, field, locator);

      // There is no automatic disposal for fields.
      Consumer<Object> disposeFunction = instance -> {};

      configuration.addActiveDescriptor(
          new ProvidesDescriptor<>(
              field,
              providedClass,
              providedType,
              providedContracts,
              scopeAnnotation,
              createFunction,
              disposeFunction));

      added++;
    }

    return added;
  }

  /**
   * Returns the set of contracts defined by a method or field that is annotated
   * with {@link Provides}.
   *
   * @param providesAnnotation the {@link Provides} annotation on the method or field
   * @param providedType the {@link Method#getGenericReturnType()} of the
   *        annotated method or the {@link Field#getGenericType()} of the
   *        annotated field
   */
  private static Set<Type> getContracts(Provides providesAnnotation,
                                        Type providedType) {

    Objects.requireNonNull(providesAnnotation);
    Objects.requireNonNull(providedType);

    if (providesAnnotation.contracts().length > 0)
      return Arrays.stream(providesAnnotation.contracts())
                   .collect(toUnmodifiableSet());

    // This block of code reproduces the behavior of
    // org.jvnet.hk2.internal.Utilities#getAutoAdvertisedTypes(Type)

    Class<?> rawClass = ReflectionHelper.getRawClass(providedType);
    if (rawClass == null)
      return setOf(providedType);

    ContractsProvided explicit = rawClass.getAnnotation(ContractsProvided.class);
    if (explicit != null)
      return Arrays.stream(explicit.value())
                   .collect(toUnmodifiableSet());

    return Stream.concat(Stream.of(providedType),
                         ReflectionHelper.getAllTypes(providedType)
                                         .stream()
                                         .filter(t -> isContract(t)))
                 .collect(toUnmodifiableSet());
  }

  /**
   * Returns {@code true} if the specified type is a contract.
   */
  private static boolean isContract(Type type) {
    Objects.requireNonNull(type);

    // This block of code reproduces the behavior of
    // org.jvnet.hk2.internal.Utilities#hasContract(Class)

    Class<?> rawClass = ReflectionHelper.getRawClass(type);
    if (rawClass == null)
      return false;

    if (rawClass.isAnnotationPresent(Contract.class))
      return true;

    for (Annotation annotation : rawClass.getAnnotations())
      if (annotation.annotationType().isAnnotationPresent(ContractIndicator.class))
        return true;

    return false;
  }

  /**
   * Returns the scope annotation for a method or field that is annotated with
   * {@link Provides}.
   *
   * @param providerDescriptor the descriptor of the service that defines the
   *        method or field, in case the scope of that service is relevant
   * @param providerMethodOrField the method or field that is annotated with
   *        {@link Provides}
   * @param providedContracts the contracts provided by the method or field
   */
  private static <T extends AccessibleObject & Member> Annotation
  getScopeAnnotation(ActiveDescriptor<?> providerDescriptor,
                     T providerMethodOrField,
                     Set<Type> providedContracts) {

    Objects.requireNonNull(providerDescriptor);
    Objects.requireNonNull(providerMethodOrField);
    Objects.requireNonNull(providedContracts);

    for (Annotation annotation : providerMethodOrField.getAnnotations())
      if (annotation.annotationType().isAnnotationPresent(Scope.class))
        return annotation;

    for (Type contract : providedContracts) {
      Class<?> rawClass = ReflectionHelper.getRawClass(contract);
      if (rawClass != null)
        for (Annotation annotation : rawClass.getAnnotations())
          if (annotation.annotationType().isAnnotationPresent(Scope.class))
            return annotation;
    }

    if (!Modifier.isStatic(providerMethodOrField.getModifiers())) {
      Annotation providerScopeAnnotation =
          providerDescriptor.getScopeAsAnnotation();

      if (providerScopeAnnotation != null)
        return providerScopeAnnotation;
    }

    return ServiceLocatorUtilities.getPerLookupAnnotation();
  }

  /**
   * Returns a function that creates instances of services by invoking a static
   * method that is annotated with {@link Provides}.
   *
   * @param method the static method that is annotated with {@link Provides}
   * @param providerType the type of the service that defines the method
   * @param self supplies the descriptor of the service provided by the method
   * @param locator the service locator
   */
  private static Function<ServiceHandle<?>, Object>
  getCreateFunctionFromStaticMethod(Method method,
                                    Type providerType,
                                    Supplier<ActiveDescriptor<?>> self,
                                    ServiceLocator locator) {

    Objects.requireNonNull(method);
    Objects.requireNonNull(providerType);
    Objects.requireNonNull(self);
    Objects.requireNonNull(locator);

    return (ServiceHandle<?> root) -> {
      Object[] arguments =
          Arrays.stream(method.getParameters())
                .map(
                    parameter -> {
                      if (isSelf(parameter))
                        return self.get();

                      return InjectUtils.serviceFromParameter(
                          parameter,
                          providerType,
                          root,
                          locator);
                    })
                .toArray(length -> new Object[length]);

      if (!canAccess(method, null))
        method.setAccessible(true);

      Object provided;
      try {
        provided = method.invoke(null, arguments);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new MultiException(e);
      }

      return provided;
    };
  }

  /**
   * Returns a function that creates instances of services by invoking an
   * instance method that is annotated with {@link Provides}.
   *
   * @param method the instance method that is annotated with {@link Provides}
   * @param providerType the type of the service that defines the method
   * @param providerDescriptor the descriptor of the service that defines the
   *        method
   * @param self supplies the descriptor of the service provided by the method
   * @param locator the service locator
   */
  private static Function<ServiceHandle<?>, Object>
  getCreateFunctionFromInstanceMethod(Method method,
                                      Type providerType,
                                      ActiveDescriptor<?> providerDescriptor,
                                      Supplier<ActiveDescriptor<?>> self,
                                      ServiceLocator locator) {

    Objects.requireNonNull(providerDescriptor);
    Objects.requireNonNull(providerType);
    Objects.requireNonNull(method);
    Objects.requireNonNull(self);
    Objects.requireNonNull(locator);

    return (ServiceHandle<?> root) -> {
      Object[] arguments =
          Arrays.stream(method.getParameters())
                .map(
                    parameter -> {
                      if (isSelf(parameter))
                        return self.get();

                      return InjectUtils.serviceFromParameter(
                          parameter,
                          providerType,
                          root,
                          locator);
                    })
                .toArray(length -> new Object[length]);

      Object provider =
          locator.getService(providerDescriptor, root, null);

      if (!canAccess(method, provider))
        method.setAccessible(true);

      Object provided;
      try {
        provided = method.invoke(provider, arguments);
      } catch (IllegalAccessException | InvocationTargetException e) {
        throw new MultiException(e);
      }

      return provided;
    };
  }

  /**
   * Returns a function that creates instances of services by reading a static
   * field that is annotated with {@link Provides}.
   *
   * @param field the static field that is annotated with {@link Provides}
   * @param locator the service locator
   */
  private static Function<ServiceHandle<?>, Object>
  getCreateFunctionFromStaticField(Field field, ServiceLocator locator) {

    Objects.requireNonNull(field);
    Objects.requireNonNull(locator);

    return (ServiceHandle<?> root) -> {
      if (!canAccess(field, null))
        field.setAccessible(true);

      Object provided;
      try {
        provided = field.get(null);
      } catch (IllegalAccessException e) {
        throw new MultiException(e);
      }

      return provided;
    };
  }

  /**
   * Returns a function that creates instances of services by reading an
   * instance field that is annotated with {@link Provides}.
   *
   * @param providerDescriptor the descriptor of the service that defines the
   *        field
   * @param field the instance field that is annotated with {@link Provides}
   * @param locator the service locator
   */
  private static Function<ServiceHandle<?>, Object>
  getCreateFunctionFromInstanceField(ActiveDescriptor<?> providerDescriptor,
                                     Field field,
                                     ServiceLocator locator) {

    Objects.requireNonNull(providerDescriptor);
    Objects.requireNonNull(field);
    Objects.requireNonNull(locator);

    return (ServiceHandle<?> root) -> {
      Object provider = locator.getService(providerDescriptor, root, null);

      if (!canAccess(field, provider))
        field.setAccessible(true);

      Object provided;
      try {
        provided = field.get(provider);
      } catch (IllegalAccessException e) {
        throw new MultiException(e);
      }

      return provided;
    };
  }

  /**
   * Returns a function that disposes of instances of services that were
   * retrieved from a method annotated with {@link Provides}.  Returns {@code
   * null} if the {@link Provides} annotation has a non-empty {@link
   * Provides#disposeMethod()} and the method it specifies is not found.
   *
   * @param providerDescriptor the descriptor of the service that defines the
   *        method
   * @param providesAnnotation the {@link Provides} annotation on the method
   * @param providerMethod the method that is annotated with {@link Provides}
   * @param providedClass the {@link Method#getReturnType()} ()}
   * @param providedType the {@link Method#getGenericReturnType()}
   * @param providerClass the class of the service that defines the method
   * @param providerType the type of the service that defines the method
   * @param self supplies the descriptor of the service provided by the method
   * @param locator the service locator
   */
  private static <T extends AccessibleObject & Member> /*@Nullable*/ Consumer<Object>
  getDisposeFunction(ActiveDescriptor<?> providerDescriptor,
                     Provides providesAnnotation,
                     Method providerMethod,
                     Class<?> providedClass,
                     Type providedType,
                     Class<?> providerClass,
                     Type providerType,
                     Supplier<ActiveDescriptor<?>> self,
                     ServiceLocator locator) {

    Objects.requireNonNull(providerDescriptor);
    Objects.requireNonNull(providesAnnotation);
    Objects.requireNonNull(providerMethod);
    Objects.requireNonNull(providedClass);
    Objects.requireNonNull(providedType);
    Objects.requireNonNull(providerClass);
    Objects.requireNonNull(providerType);
    Objects.requireNonNull(self);
    Objects.requireNonNull(locator);

    if (providesAnnotation.disposeMethod().isEmpty())
      return instance -> {
        if (instance != null)
          locator.preDestroy(instance);
      };

    switch (providesAnnotation.disposalHandledBy()) {
      case PROVIDED_INSTANCE: {
        Method disposeMethod =
            Arrays.stream(providedClass.getMethods())
                  .filter(method -> method.getName().equals(providesAnnotation.disposeMethod()))
                  .filter(method -> !Modifier.isStatic(method.getModifiers()))
                  .filter(method -> method.getParameterCount() == 0)
                  .findAny()
                  .orElse(null);

        if (disposeMethod == null)
          return null;

        return instance -> {
          if (instance == null)
            return;

          if (!canAccess(disposeMethod, instance))
            disposeMethod.setAccessible(true);

          try {
            disposeMethod.invoke(instance);
          } catch (IllegalAccessException | InvocationTargetException e) {
            throw new MultiException(e);
          }
        };
      }

      case PROVIDER: {
        Method disposeMethod =
            Arrays.stream(providerClass.getMethods())
                  .filter(method -> method.getName().equals(providesAnnotation.disposeMethod()))
                  .filter(method -> method.getParameterCount() >= 1)
                  .filter(method -> {
                    Type parameterType =
                        TypeUtils.resolveType(
                            providerType,
                            method.getGenericParameterTypes()[0]);

                    return TypeChecker.isRawTypeSafe(
                        parameterType,
                        providedType);
                  })
                  .findAny()
                  .orElse(null);

        if (disposeMethod == null)
          return null;

        return instance -> {
          if (instance == null)
            return;

          List<ServiceHandle<?>> perLookupHandles = new ArrayList<>();

          try {
            int indexOfArgumentToDispose = 0;
            Parameter[] parameters = disposeMethod.getParameters();
            Object[] arguments = new Object[parameters.length];

            for (int i = 0; i < parameters.length; i++) {
              if (i == indexOfArgumentToDispose)
                arguments[i] = instance;

              else if (isSelf(parameters[i]))
                arguments[i] = self.get();

              else {
                ServiceHandle<?> parameterHandle =
                    InjectUtils.serviceHandleFromParameter(
                        parameters[i],
                        providerType,
                        locator);

                if (parameterHandle == null)
                  arguments[i] = null;

                else {
                  if (isPerLookup(parameterHandle))
                    perLookupHandles.add(parameterHandle);

                  arguments[i] = parameterHandle.getService();
                }
              }
            }

            if (Modifier.isStatic(disposeMethod.getModifiers())) {
              if (!canAccess(disposeMethod, null))
                disposeMethod.setAccessible(true);

              try {
                disposeMethod.invoke(null, arguments);
              } catch (IllegalAccessException | InvocationTargetException e) {
                throw new MultiException(e);
              }

              return;
            }

            ServiceHandle<?> providerHandle =
                locator.getServiceHandle(providerDescriptor);

            if (isPerLookup(providerHandle))
              perLookupHandles.add(providerHandle);

            Object provider = providerHandle.getService();
            if (!canAccess(disposeMethod, provider))
              disposeMethod.setAccessible(true);

            try {
              disposeMethod.invoke(provider, arguments);
            } catch (IllegalAccessException | InvocationTargetException e) {
              throw new MultiException(e);
            }

          } finally {
            for (ServiceHandle<?> handle : perLookupHandles)
              handle.close();
          }
        };
      }
    }

    throw new AssertionError(
        "Unknown "
            + Provides.DisposalHandledBy.class.getSimpleName()
            + " value: "
            + providesAnnotation.disposalHandledBy());
  }

  /**
   * Returns {@code true} if the specified service handle has {@link PerLookup}
   * scope.
   */
  private static boolean isPerLookup(ServiceHandle<?> handle) {
    Objects.requireNonNull(handle);
    return handle.getActiveDescriptor().getScopeAnnotation() == PerLookup.class;
  }

  /**
   * Returns {@code true} if the specified parameter is a {@link Self} injection
   * point.
   */
  private static boolean isSelf(Parameter parameter) {
    Objects.requireNonNull(parameter);
    return parameter.isAnnotationPresent(Self.class)
        && parameter.getType() == ActiveDescriptor.class
        && !parameter.isAnnotationPresent(Optional.class)
        && Arrays.stream(parameter.getAnnotations())
                 .noneMatch(
                     annotation ->
                         ReflectionHelper.isAnnotationAQualifier(annotation));
  }

  /**
   * Remembers which {@link Provides} sources have already been seen so that
   * duplicate descriptors won't be added for any given source.
   */
  private static final class ProvidersSeen {
    private final Set<CacheKey> cache = ConcurrentHashMap.newKeySet();

    /**
     * Removes the providers from this cache that are not in the specified set
     * of providers.
     */
    void retainAll(Set<ActiveDescriptor<?>> providers) {
      Objects.requireNonNull(providers);
      cache.removeIf(
          key -> key.provider != null && !providers.contains(key.provider));
    }

    /**
     * Modifies this cache to remember that the specified provider has been
     * seen.  Returns {@code true} if the provider was not seen before.
     */
    boolean add(ActiveDescriptor<?> provider) {
      Objects.requireNonNull(provider);
      CacheKey key = new CacheKey(provider, null);
      return cache.add(key);
    }

    /**
     * Modifies this cache to remember that the specified method or field of the
     * specified provider has been seen.  Returns {@code true} if the method or
     * field was not seen before.
     */
    boolean add(ActiveDescriptor<?> provider, Member methodOrField) {
      Objects.requireNonNull(provider);
      Objects.requireNonNull(methodOrField);

      CacheKey key =
          Modifier.isStatic(methodOrField.getModifiers())
              ? new CacheKey(null, methodOrField)
              : new CacheKey(provider, methodOrField);

      return cache.add(key);
    }

    private static final class CacheKey {
      private final /*@Nullable*/ ActiveDescriptor<?> provider;
      private final /*@Nullable*/ Member methodOrField;

      CacheKey(/*@Nullable*/ ActiveDescriptor<?> provider,
               /*@Nullable*/ Member methodOrField) {

        this.provider = provider;
        this.methodOrField = methodOrField;
      }

      @Override
      public boolean equals(/*@Nullable*/ Object object) {
        if (object == this) {
          return true;
        } else if (!(object instanceof CacheKey)) {
          return false;
        } else {
          CacheKey that = (CacheKey) object;
          return Objects.equals(this.provider, that.provider)
              && Objects.equals(this.methodOrField, that.methodOrField);
        }
      }

      @Override
      public int hashCode() {
        int hash = 1;
        hash = 31 * hash + Objects.hashCode(provider);
        hash = 31 * hash + Objects.hashCode(methodOrField);
        return hash;
      }
    }
  }
}
