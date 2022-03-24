package cs2030s.fp;

/**
 * For lazy evaluation of values that are expensive to produce.
 *
 * @author Tan Zong Zhi, Shaun (Group 16A)
 * @version CS2030S AY 21/22 Sem 2 
 *
 * @param <T> The type of the produced value, returned by the 'get' method.
 */
public class Lazy<T> {
  /** Producer that evaluates the value when 'producer.producer()' is called. */
  private Producer<? extends T> producer;
  /** Value wrapped in a 'Maybe' class. */
  private Maybe<T> value;

  /**
   * Overloaded private constructor to initialise a Lazy with an already 
   * evaluated value for 'Lazy::of'.
   *
   * @param value The value returned by 'get' method.
   */
  private Lazy(T value) {
    this.value = Maybe.some(value);
  }

  /**
   * Overloaded private constructor to initialise a Lazy with delayed 
   * evaluation, whereby the value is obtained from param 'producer'.
   *
   * @param producer The 'Producer' that returns the evaluated value.
   */
  private Lazy(Producer<? extends T> producer) {
    this.producer = producer;
    this.value = Maybe.none();
  }

  /**
   * Factory method for initialising a Lazy with an already evaluated value.
   * 
   * @param <T> The type of the value being wrapped, and returned by the 'get' method.
   * @param v The value returned by 'get' method.
   * @return The initialised Lazy instance, with its value being 'v'.
   */
  public static <T> Lazy<T> of(T v) {
    return new Lazy<>(v);
  }

  /**
   * Factory method for initialising a Lazy with delayed evaluation, whereby
   * the value is obtained from Producer param 's'.
   *
   * @param <T> The type of the value being wrapped, and returned by the 'get' method.
   * @param s The 'Producer' that returns the evaluated value.
   * @return The initialised Lazy instance, with delayed evaluation.
   */
  public static <T> Lazy<T> of(Producer<? extends T> s) {
    return new Lazy<>(s);
  }

  /**
   * If the value has not been evaluated yet, compute it via 'this.producer'
   * and "cache" it. Subsequent 'get' calls returns "cached" value.
   *
   * @return The computed/"cached" value.
   */
  public T get() {
    T rawValue = this.value
        .orElseGet(this.producer);
    this.value = Maybe.some(rawValue);
    return rawValue;
  }

  /**
   * Returns the string representation of the wrapped value. If value has not
   * been computed yet, returns "?".
   *
   * @return The string representation of the value, or "?" if it's not computed yet.
   */
  @Override
  public String toString() {
    return this.value
        .map(String::valueOf)
        .orElse("?");
  }

  /**
   * Applies transformer function on the wrapped value.
   *
   * @param <U> The type of the returned wrapped value.
   * @param transformer The 'Transformer' object that is applied to the value.
   * @return The delayed evaluation of the transformed value, wrapped using 'Lazy'.
   */
  public <U> Lazy<U> map(Transformer<? super T, ? extends U> transformer) {
    Producer<U> newProducer = () -> transformer
        .transform(this.get());
    return Lazy.of(newProducer);
  }

  /**
   * Applies transformer function on wrapped value, and flattens the transformed
   * Lazy-wrapped value to avoid nested Lazys.
   *
   * @param <U> The type of the returned wrapped value.
   * @param transformer The 'Transformer' object applied to value, to give a 'Lazy'.
   * @return The delayed evaluation of the flatten transformed value, wrapped usig 'Lazy'.
   */
  public <U> Lazy<U> flatMap(Transformer<? super T, 
        ? extends Lazy<? extends U>> transformer) {
    Producer<U> newProducer = () -> transformer
        .transform(this.get())
        .get();
    return Lazy.of(newProducer);
  }

  /**
   * Returns a lazily evaluated boolean, obtained by applying param 'predicate'
   * to the wrapped value.
   *
   * @param predicate The predicate function applied to the value.
   * @return The delayed evaluation of the predicate, wrapped using 'Lazy'.
   */
  public Lazy<Boolean> filter(BooleanCondition<? super T> predicate) {
    Producer<Boolean> newProducer = () -> predicate.test(this.get());
    return Lazy.of(newProducer);
  }

  /**
   * Evaluates the values of 'this' and 'obj', and returns 'true' if both 
   * are 'Lazy' instances containing equal values.
   *
   * @param obj The 'Object' to compare equality with.
   * @return Whether or not 'this' and 'obj' are both 'Lazy' instances with equal values.
   */
  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof Lazy<?>)) {
      return false;
    }

    Lazy<?> lazyObj = (Lazy<?>) obj;

    // Ensure both Lazy instanced are evaluated
    this.get();
    lazyObj.get();

    return this.value
        .equals(lazyObj.value);
  }

  /**
   * Combines the values of 'this' amd 'lazyObj' via a 'Combiner'.
   *
   * @param <S> The type of the wrapped value inside 'lazyObj'.
   * @param <R> The type of the returned wrapped value.
   * @param lazyObj The other 'Lazy' instance to combine values with.
   * @param combiner The 'Combiner' function to combined the 2 values by.
   * @return The delayed evaluation of the combined values of 'this' and 'lazyObj'.
   */
  public <S, R> Lazy<R> combine(Lazy<? extends S> lazyObj, 
      Combiner<? super T, ? super S, ? extends R> combiner) {
    Producer<R> newProducer = () -> combiner.combine(this.get(), lazyObj.get());
    return Lazy.of(newProducer);
  }
}
