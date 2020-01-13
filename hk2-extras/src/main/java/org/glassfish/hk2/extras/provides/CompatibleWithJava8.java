package org.glassfish.hk2.extras.provides;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Member;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * Provides utility methods that were added to the JDK after Java 8.
 */
final class CompatibleWithJava8 {
  private CompatibleWithJava8() {
    throw new AssertionError("This class cannot be instantiated");
  }

  /**
   * Java 9's {@code AccessibleObject.canAccess(receiver)}.
   */
  public static <T extends AccessibleObject & Member>
  boolean canAccess(T member, /*@Nullable*/ Object receiver) {
    Objects.requireNonNull(member);
    if (Modifier.isStatic(member.getModifiers())) {
      if (receiver != null) {
        throw new IllegalArgumentException();
      }
    } else {
      if (!member.getDeclaringClass().isInstance(receiver)) {
        throw new IllegalArgumentException();
      }
    }
    @SuppressWarnings("deprecation")
    boolean result = member.isAccessible();
    return result;
  }

  /**
   * Java 9's {@code List.of(elements)}.
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <E> List<E> listOf(E... elements) {
    Objects.requireNonNull(elements);
    return listCopyOf(Arrays.asList(elements));
  }

  /**
   * Java 9's {@code Set.of(elements)}.
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <E> Set<E> setOf(E... elements) {
    Objects.requireNonNull(elements);
    return setCopyOf(Arrays.asList(elements));
  }

  /**
   * Java 10's {@code List.copyOf(collection)}.
   */
  public static <E> List<E> listCopyOf(Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    List<E> result = new ArrayList<>();
    for (E element : collection)
      result.add(Objects.requireNonNull(element));
    return Collections.unmodifiableList(result);
  }

  /**
   * Java 10's {@code Set.copyOf(collection)}.
   */
  public static <E> Set<E> setCopyOf(Collection<? extends E> collection) {
    Objects.requireNonNull(collection);
    Set<E> result = new HashSet<>();
    for (E element : collection)
      result.add(Objects.requireNonNull(element));
    return Collections.unmodifiableSet(result);
  }

  /**
   * Java 10's {@code Map.copyOf(map)}.
   */
  public static <K, V> Map<K, V> mapCopyOf(Map<? extends K, ? extends V> map) {
    Objects.requireNonNull(map);
    Map<K, V> result = new HashMap<>();
    map.forEach(
        (key, value) ->
            result.put(
                Objects.requireNonNull(key),
                Objects.requireNonNull(value)));
    return Collections.unmodifiableMap(result);
  }

  /**
   * Java 10's {@code Collectors.toUnmodifiableList()}.
   */
  public static <E> Collector<E, ?, List<E>> toUnmodifiableList() {
    return Collectors.collectingAndThen(
        Collectors.toList(),
        result -> listCopyOf(result));
  }

  /**
   * Java 10's {@code Collectors.toUnmodifiableSet()}.
   */
  public static <E> Collector<E, ?, Set<E>> toUnmodifiableSet() {
    return Collectors.collectingAndThen(
        Collectors.toSet(),
        result -> setCopyOf(result));
  }

  /**
   * Java 10's {@code Collectors.toUnmodifiableMap(keyMapper, valueMapper)}.
   */
  public static <T, K, V> Collector<T, ?, Map<K, V>> toUnmodifiableMap(
      Function<? super T, ? extends K> keyMapper,
      Function<? super T, ? extends V> valueMapper) {
    Objects.requireNonNull(keyMapper);
    Objects.requireNonNull(valueMapper);
    return Collectors.collectingAndThen(
        Collectors.toMap(keyMapper, valueMapper),
        result -> mapCopyOf(result));
  }
}
