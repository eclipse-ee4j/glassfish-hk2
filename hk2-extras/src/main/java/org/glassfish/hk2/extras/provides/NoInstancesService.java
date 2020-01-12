package org.glassfish.hk2.extras.provides;

import static org.glassfish.hk2.extras.provides.CompatibleWithJava8.setOf;

import java.util.NoSuchElementException;
import java.util.Objects;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ActiveDescriptor;
import org.glassfish.hk2.api.DynamicConfiguration;
import org.glassfish.hk2.api.DynamicConfigurationService;
import org.glassfish.hk2.api.Populator;
import org.glassfish.hk2.api.Rank;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;

/**
 * Applies a {@link NoInstancesFilter} to each class passed to {@link
 * DynamicConfiguration#addActiveDescriptor(Class)}, overriding the default
 * {@link DynamicConfigurationService}.
 */
@Singleton
@Rank(1) // Override the default DynamicConfigurationService.
final class NoInstancesService implements DynamicConfigurationService {
  private final ServiceLocator locator;

  @Inject
  public NoInstancesService(ServiceLocator locator) {
    this.locator = Objects.requireNonNull(locator);
  }

  private DynamicConfigurationService defaultConfigurationService() {
    return locator
        .getAllServiceHandles(DynamicConfigurationService.class)
        .stream()
        .filter(
            serviceHandle ->
                serviceHandle.getActiveDescriptor()
                             .getImplementationClass()
                    != getClass())
        .map(serviceHandle -> serviceHandle.getService())
        .findAny()
        .orElseThrow(
            () ->
                new NoSuchElementException(
                    "Default configuration service not found"));
  }

  @Override
  public DynamicConfiguration createDynamicConfiguration() {
    NoInstancesFilter filter = locator.getService(NoInstancesFilter.class);

    DynamicConfiguration config =
        defaultConfigurationService().createDynamicConfiguration();

    return new ForwardingDynamicConfiguration() {
      @Override
      public DynamicConfiguration delegate() {
        return config;
      }

      @Override
      public <T> ActiveDescriptor<T> addActiveDescriptor(Class<T> rawClass) {
        Objects.requireNonNull(rawClass);
        return filter.matches(rawClass)
            ? addActiveDescriptor(noInstancesDescriptor(rawClass))
            : config.addActiveDescriptor(rawClass);
      }
    };
  }

  @Override
  public Populator getPopulator() {
    return defaultConfigurationService().getPopulator();
  }

  private static <T> ActiveDescriptor<T> noInstancesDescriptor(Class<T> rawClass) {
    Objects.requireNonNull(rawClass);
    return new ProvidesDescriptor<>(
        rawClass,
        rawClass,
        rawClass,
        setOf(), // Provides no contracts, not even itself.
        ServiceLocatorUtilities.getPerLookupAnnotation(),
        root -> { throw new UnsupportedOperationException(); },
        instance -> { throw new UnsupportedOperationException(); });
  }
}
