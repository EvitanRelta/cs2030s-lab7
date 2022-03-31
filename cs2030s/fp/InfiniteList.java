package cs2030s.fp;

import java.util.List;

/**
 * For lazy evaluation of values that are expensive to produce.
 *
 * @author Tan Zong Zhi, Shaun (Group 16A)
 * @version CS2030S AY 21/22 Sem 2
 *
 * @param <T> The type of the value in the list, returned by the 'head' method.
 */
public class InfiniteList<T> {
  /** Lazily evaluted head value. */
  private final Lazy<Maybe<T>> head;
  /** Lazily evaluted tail value. */
  private final Lazy<InfiniteList<T>> tail;

  /**
   * To initialise an InfiniteList with no head nor tail values.
   */
  private InfiniteList() {
    this.head = null;
    this.tail = null;
  }

  /**
   * To initialise an InfiniteList with an already evaluated head value, but an unevalutated tail.
   *
   * @param head The already evaluated head value.
   * @param tail Producer for the unevaluted tail value.
   */
  private InfiniteList(T head, Producer<InfiniteList<T>> tail) {
    this.head = Lazy.of(Maybe.some(head));
    this.tail = Lazy.of(tail);
  }

  /**
   * To initialise a InfiniteList with the head and tail that's not evaluated yet.
   *
   * @param head The Lazily evaluated head value.
   * @param tail The Lazily evaluated tail value.
   */
  private InfiniteList(Lazy<Maybe<T>> head, Lazy<InfiniteList<T>> tail) {
    this.head = head;
    this.tail = tail;
  }

  /**
   * Factory method for initialising an InfiniteList with elements all just being 
   * the producer's produced value.
   *
   * @param <T> The value type of the initialised InfiniteList.
   * @param producer The Producer producing the value.
   * @return The initialised InfiniteList.
   */
  public static <T> InfiniteList<T> generate(Producer<T> producer) {
    return new InfiniteList<>(
          Lazy.of(() -> Maybe.some(producer.produce())),
          Lazy.of(() -> InfiniteList.generate(producer))
        );
  }

  /**
   * Factory method for initialising an InfiniteList with the first element being 
   * the 'seed', and subsequent elements are Transformer 'next' being applied to 
   * the previous element.
   *
   * @param <T> The value type of the initialised InfiniteList.
   * @param seed The first element.
   * @param next The Transformer that is applied to the seed.
   * @return The initialised InfiniteList.
   */
  public static <T> InfiniteList<T> iterate(T seed, Transformer<T, T> next) {
    return new InfiniteList<>(
          seed,
          () -> InfiniteList.iterate(next.transform(seed), next)
        );
  }

  /**
   * Returns the first evaluated value that isn't equals to 'Maybe.none()'.
   *
   * @return The first evaluated value that's != Maybe.none().
   */
  public T head() {
    return this.head.get()
        .orElseGet(() -> this.tail().head());
  }

  /**
   * Returns the rest of the InfiniteList, whereby the previous element
   * is not a 'Maybe.none()'.
   *
   * @return The rest of the InfiniteList, where previous element != Maybe.none().
   */
  public InfiniteList<T> tail() {
    return this.head.get().equals(Maybe.none())
        ? this.tail.get().tail()
        : this.tail.get();
  }

  /**
   * Returns a new InfiniteList that has the 'mapper' Transformer applied to all
   * elements.
   *
   * @param <R> The value type of the returned InfiniteList.
   * @param mapper The Transformer that will be applied.
   * @return The new InfiniteList that has 'mapper' applied to all its elements.
   */
  public <R> InfiniteList<R> map(Transformer<? super T, ? extends R> mapper) {
    return new InfiniteList<>(
          this.head.map(x -> x.map(mapper)),
          this.tail.map(x -> x.map(mapper))
        );
  }

  public InfiniteList<T> filter(BooleanCondition<? super T> predicate) {
    // TODO
    return new InfiniteList<>();
  }

  public static <T> InfiniteList<T> sentinel() {
    // TODO
    return new InfiniteList<>();
  }

  public InfiniteList<T> limit(long n) {
    // TODO
    return new InfiniteList<>();
  }

  public InfiniteList<T> takeWhile(BooleanCondition<? super T> predicate) {
    // TODO
    return new InfiniteList<>();
  }

  public boolean isSentinel() {
    return false;
  }

  public <U> U reduce(U identity, Combiner<U, ? super T, U> accumulator) {
    // TODO
    return null;
  }

  public long count() {
    // TODO
    return 0;
  }

  public List<T> toList() {
    // TODO
    return List.of();
  }

  public String toString() {
    return "[" + this.head + " " + this.tail + "]";
  }
}
