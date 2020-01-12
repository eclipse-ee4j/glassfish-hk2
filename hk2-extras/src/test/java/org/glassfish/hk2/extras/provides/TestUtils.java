package org.glassfish.hk2.extras.provides;

import java.util.Objects;

/**
 * Utility methods used in tests.
 */
final class TestUtils {
  private TestUtils() {
    throw new AssertionError("This class cannot be instantiated");
  }

  @FunctionalInterface
  public interface ThrowingRunnable {
    void run() throws Throwable;
  }

  /**
   * JUnit 4.13's {@code Assert.assertThrows(expectedThrowable, runnable)}.
   */
  public static <T extends Throwable> T assertThrows(
      Class<T> expectedThrowable,
      ThrowingRunnable runnable) {

    Objects.requireNonNull(expectedThrowable);
    Objects.requireNonNull(runnable);

    try {
      runnable.run();
    } catch (Throwable t) {
      if (expectedThrowable.isInstance(t))
        return expectedThrowable.cast(t);

      throw new AssertionError("Unexpected exception type thrown", t);
    }

    throw new AssertionError(
        "Expected "
            + expectedThrowable.getName()
            + " to be thrown, but nothing was thrown.");
  }
}
