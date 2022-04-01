package cs2030s.fp;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * An lazily evaluated infinite linked list, where the next elements are 
 * generated via a function; either by a 'Producer' via the 'generate' method
 * or by a 'Transformer' via the 'iterate' method. A 'Stream' rip-off basically.
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
  /** Cached sentinel instance, to mark the end of InfiniteList. */
  private static final InfiniteList<?> SENTINEL = new Sentinel();

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
        .orElseGet(() -> this.tail.get().head());
  }

  /**
   * Returns the next InfiniteList tail that has a head != Maybe.none()
   * 
   * @return The next InfiniteList tail with a non-None head.
   */
  public InfiniteList<T> tail() {
    return this.getNextNonNoneHead()
        .tail.get()
        .getNextNonNoneHead();
  }
  
  /**
   * Helper method for 'tail'. Returns 'this' if head != Maybe.none(),
   * else continue to recurse on its tail.
   *
   * @return 'this' if head != Maybe.none(), else next non-None-head InfiniteList.
   */
  protected InfiniteList<T> getNextNonNoneHead() {
    return this.head.get()
        .map(x -> this)
        .orElseGet(() -> this.tail.get()
            .getNextNonNoneHead()
        );
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

  /**
   * Returns a new InfiniteList with all elements not satisfying the
   * 'predicate' replaced with Maybe.none().
   *
   * @param predicate The predicate function to filter the elements by.
   * @return The new InfiniteList with elements failing the 'predicate' replaced with Maybe.none().
   */
  public InfiniteList<T> filter(BooleanCondition<? super T> predicate) {
    return new InfiniteList<>(
        this.head.map(x -> x.filter(predicate)),
        this.tail.map(x -> x.filter(predicate))
    );
  }

  /**
   * Returns a sentinel, which denotes the end of an InfiniteList.
   *
   * @param <T> The generic type of the returned InfiniteList.
   * @return The sentinel.
   */
  public static <T> InfiniteList<T> sentinel() {
    @SuppressWarnings("unchecked")
    InfiniteList<T> output = (InfiniteList<T>) InfiniteList.SENTINEL;
    return output;
  }

  /**
   * Returns a new InfiniteList that's a finite copy of 'this' with a
   * length less than/equals to 'n'.
   *
   * @param n The number of elements in the returned finite InfiniteList.
   * @return A finite InfiniteList copy.
   */
  public InfiniteList<T> limit(long n) {
    return Maybe.some(n)
        .filter(x -> x > 0)
        .map(unused -> new InfiniteList<>(
            this.head,
            this.tail.map(x -> x.limit(
                this.head.get()
                    .map(unused2 -> n - 1)
                    .orElse(n)
            ))
        ))
        .orElseGet(InfiniteList::sentinel);
  }

  /**
   * Returns a new InfiniteList which terminates the moment an element fails
   * the 'predicate'.
   *
   * @param predicate The predicate which determines when to terminate.
   * @return The new InfiniteList that terminates upon failing the 'predicate'.
   */
  public InfiniteList<T> takeWhile(BooleanCondition<? super T> predicate) {
    Lazy<Maybe<T>> newHead = Lazy.of(() -> Maybe.some(this.head())
        .filter(predicate)
    );
    return new InfiniteList<>(
      newHead,
      Lazy.of(() -> newHead.get()
          .map(unused -> this.tail()
              .takeWhile(predicate)
          )
          .orElseGet(InfiniteList::sentinel)
      )
    );
  }

  /**
   * Returns whether 'this' is a 'Sentinel' instance, which is always false.
   *
   * @return Always false.
   */
  public boolean isSentinel() {
    return false;
  }

  /**
   * Reduces all elements to a single value of type 'U' by combining them
   * using 'accumulator'.
   *
   * @param <U> Return value type.
   * @param identity Initial value.
   * @param accumulator Binary function for combining the elements.
   * @return The value obtained by combining all the elements.
   */
  public <U> U reduce(U identity, Combiner<U, ? super T, U> accumulator) {
    return this.tail()
        .reduce(
            accumulator.combine(identity, this.head()),
            accumulator
        );
  }

  /**
   * Returns the number of elements in the InfiniteList.
   *
   * @return The number of elements.
   */
  public long count() {
    return this.reduce(0, (acc, x) -> acc + 1);
  }

  /**
   * Evalutes all the elements in this InfiniteList, and returns them 
   * in a 'List' in the same order.
   *
   * @return A 'List' of all the elements in this InfiniteList.
   */
  public List<T> toList() {
    return this.reduce(
        new ArrayList<>(),
        (list, x) -> {
          list.add(x);
          return list;
        }
    );
  }

  /**
   * Returns the string representation of this InfiniteList. If the element
   * has not been evaluated before, it'd be shown as "?".
   *
   * @return The string representation of this InfiniteList.
   */
  public String toString() {
    return "[" + this.head + " " + this.tail + "]";
  }

  
  // ============================ Nested Classes ============================

  private static class Sentinel extends InfiniteList<Object> {
    /**
     * Returns the string representation of a 'Sentinel', which is "-".
     *
     * @return "-".
     */
    @Override
    public String toString() {
      return "-";
    }

    /**
     * Always throws a 'NoSuchElementException', as a 'Sentinel' doesn't
     * have a head.
     *
     * @return Never returns, always throws 'NoSuchElementException'.
     */
    @Override
    public Object head() throws NoSuchElementException {
      throw new NoSuchElementException();
    }

    /**
     * Always throws a 'NoSuchElementException', as a 'Sentinel' doesn't
     * have a tail.
     *
     * @return Never returns, always throws 'NoSuchElementException'.
     */
    @Override
    public InfiniteList<Object> tail() throws NoSuchElementException {
      throw new NoSuchElementException();
    }

    /**
     * Helper method for 'tail'. Returns a sentinel, as sentinels has
     * no elements.
     *
     * @return A sentinel.
     */
    @Override
    protected InfiniteList<Object> getNextNonNoneHead() {
      return this;
    }

    /**
     * Returns whether 'this' is a 'Sentinel' instance, which is always true.
     *
     * @return Always true.
     */
    @Override
    public boolean isSentinel() {
      return true;
    }

    /**
     * Always returns a 'Sentinel' as sentinels don't have any elements.
     * 'InfiniteList.sentinel()' is used for typecasting purposes.
     *
     * @return A sentinel.
     */
    @Override
    public <R> InfiniteList<R> map(Transformer<Object, ? extends R> mapper) {
      return InfiniteList.sentinel();
    }

    /**
     * Always returns a 'Sentinel' as sentinels don't have any elements.
     *
     * @return A sentinel.
     */
    @Override
    public InfiniteList<Object> filter(BooleanCondition<Object> predicate) {
      return this;
    }

    /**
     * Always returns a 'Sentinel' as sentinels don't have any elements.
     *
     * @return A sentinel.
     */
    @Override
    public InfiniteList<Object> limit(long n) {
      return this;
    }

    /**
     * Always returns a 'Sentinel' as sentinels don't have any elements.
     *
     * @return A sentinel.
     */
    @Override
    public InfiniteList<Object> takeWhile(BooleanCondition<Object> predicate) {
      return this;
    }

    /**
     * Always returns 'identity', as sentinels don't have any elements.
     *
     * @param <U> Return value type.
     * @param identity Initial value.
     * @param accumulator Binary function for combining the elements.
     * @return The 'identity' param.
     */
    @Override
    public <U> U reduce(U identity, Combiner<U, Object, U> accumulator) {
      return identity;
    }

    /**
     * Returns the number of elements in the InfiniteList, which is always
     * zero, as sentinels don't have elements.
     *
     * @return 0.
     */
    @Override
    public long count() {
      return 0;
    }

    /**
     * Returns an empty 'List', as sentinels don't have any elements.
     *
     * @return An empty 'List'.
     */
    @Override
    public List<Object> toList() {
      return new ArrayList<>();
    }
  }
}
