package cs2030s.fp;

import java.util.NoSuchElementException;

/**
 * For chaining values that may be null.
 *
 * @author Tan Zong Zhi, Shaun (Group 16A)
 * @version CS2030S AY 21/22 Sem 2
 *
 * @param <T> The type of the value that may be null.
 */
public abstract class Maybe<T> {
  private static class None extends Maybe<Object> {
    private static final Maybe<?> NONE = new None();

    @Override
    public String toString() {
      return "[]";
    }

    @Override
    public boolean equals(Object obj) {
      return obj instanceof None;
    }

    @Override
    protected Object get() throws NoSuchElementException {
      throw new NoSuchElementException();
    }

    @Override
    public Maybe<Object> filter(BooleanCondition<? super Object> predicate) {
      return Maybe.none();
    }

    @Override
    public <U> Maybe<U> map(
        Transformer<? super Object, ? extends U> transformer) {
      return Maybe.none();
    }

    @Override
    public <U> Maybe<U> flatMap(Transformer<? super Object, 
        ? extends Maybe<? extends U>> transformer) {
      return Maybe.none();
    }

    @Override
    public Object orElse(Object elseValue) {
      return elseValue;
    }

    @Override
    public Object orElseGet(Producer<? extends Object> producer) {
      return producer.produce();
    }
  }

  private static class Some<T> extends Maybe<T> {
    private final T value;

    protected Some(T value) {
      this.value = value;
    }

    @Override
    public String toString() {
      return String.format("[%s]", this.value);
    }

    @Override
    public boolean equals(Object obj) {
      if (!(obj instanceof Some<?>)) {
        return false;
      }

      Some<?> someObj = (Some<?>) obj;
      return someObj.value == null || this.value == null
          // Avoids `(null).equals` error, when either `value` is null
          ? someObj.value == this.value
          : someObj.value.equals(this.value);
    }

    @Override
    protected T get() {
      return this.value;
    }

    @Override
    public Maybe<T> filter(BooleanCondition<? super T> predicate) {
      return this.value != null && !predicate.test(this.value)
          ? Maybe.none()
          : this;
    }

    @Override
    public <U> Maybe<U> map(Transformer<? super T, ? extends U> transformer) {
      return new Some<>(transformer.transform(this.value));
    }

    @Override
    public <U> Maybe<U> flatMap(Transformer<? super T, 
        ? extends Maybe<? extends U>> transformer) {
      Maybe<? extends U> transformed = transformer.transform(this.value);
      return transformed == Maybe.none()
          ? Maybe.none()
          : new Some<>(transformed.get());
    }

    @Override
    public T orElse(T elseValue) {
      return this.get();
    }

    @Override
    public T orElseGet(Producer<? extends T> producer) {
      return this.get();
    }
  }

  public static <T> Maybe<T> none() {
    @SuppressWarnings("unchecked")
    Maybe<T> output = (Maybe<T>) None.NONE;
    return output;
  }

  public static <T> Maybe<T> some(T t) {
    return new Some<T>(t);
  }

  public static <T> Maybe<T> of(T value) {
    return value == null
        ? Maybe.none()
        : Maybe.some(value);
  }

  protected abstract T get();

  public abstract Maybe<T> filter(BooleanCondition<? super T> predicate);

  public abstract <U> Maybe<U> map(
      Transformer<? super T, ? extends U> transformer);

  public abstract <U> Maybe<U> flatMap(Transformer<? super T,
      ? extends Maybe<? extends U>> transformer);

  public abstract T orElse(T elseValue);

  public abstract T orElseGet(Producer<? extends T> producer);
}
