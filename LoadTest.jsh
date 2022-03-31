Transformer<Integer, Integer> incr1 = x -> {
  String msg = String.format("Executed: %s -> %s", x, x + 1);
  System.out.println(msg);
  return x + 1;
};
InfiniteList<Integer> nums = InfiniteList.iterate(1, incr1);
