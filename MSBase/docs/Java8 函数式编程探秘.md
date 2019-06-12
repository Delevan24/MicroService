Java8 函数式编程探秘
2018/01/29 | 分类： 基础技术 | 0 条评论 | 标签： JAVA8, 函数式编程

分享到：
原文出处： 琴水玉
引子
将行为作为数据传递
怎样在一行代码里同时计算一个列表的和、最大值、最小值、平均值、元素个数、奇偶分组、指数、排序呢？

答案是思维反转！将行为作为数据传递。 文艺青年的代码如下所示：

public class FunctionUtil {
 
   public static <T,R> List<R> multiGetResult(List<Function<List<T>, R>> functions, List<T> list) {
     return functions.stream().map(f -> f.apply(list)).collect(Collectors.toList());
   }
 
   public static void main(String[] args) {
     System.out.println(multiGetResult(
         Arrays.asList(
             list -> list.stream().collect(Collectors.summarizingInt(x->x)),
             list -> list.stream().filter(x -> x < 50).sorted().collect(Collectors.toList()),
             list -> list.stream().collect(Collectors.groupingBy(x->(x%2==0? "even": "odd"))),
             list -> list.stream().sorted().collect(Collectors.toList()),
             list -> list.stream().sorted().map(Math::sqrt).collect(Collectors.toMap(x->x, y->Math.pow(2,y)))),
         Arrays.asList(64,49,25,16,9,4,1,81,36)));
   }
}
呃，有点卖弄小聪明。 不过要是能将行为作为数据自由传递和施加于数据集产生结果，那么其代码表达能力将如庄子之言，恣意潇洒而无所极限。病毒，不就是将行为匿身于数据中的一种表现么？

行为就是数据。

Java8函数框架解读
函数编程的最直接的表现，莫过于将函数作为数据自由传递，结合泛型推导能力，使代码表达能力获得飞一般的提升。那么，Java8是怎么支持函数编程的呢？主要有三个核心概念：

函数接口(Function)
流(Stream)
聚合器(Collector)
函数接口
关于函数接口，需要记住的就是两件事：

函数接口是行为的抽象；
函数接口是数据转换器。
最直接的支持就是 java.util.Function 包。定义了四个最基础的函数接口：

Supplier<T>: 数据提供器，可以提供 T 类型对象；无参的构造器，提供了 get 方法；
Function<T,R>: 数据转换器，接收一个 T 类型的对象，返回一个 R类型的对象； 单参数单返回值的行为接口；提供了 apply, compose, andThen, identity 方法；
Consumer<T>: 数据消费器， 接收一个 T类型的对象，无返回值，通常用于设置T对象的值； 单参数无返回值的行为接口；提供了 accept, andThen 方法；
Predicate<T>: 条件测试器，接收一个 T 类型的对象，返回布尔值，通常用于传递条件函数； 单参数布尔值的条件性接口。提供了 test (条件测试) , and-or- negate(与或非) 方法。
其中, compose, andThen, and, or, negate 用来组合函数接口而得到更强大的函数接口。

其它的函数接口都是通过这四个扩展而来。

在参数个数上扩展： 比如接收双参数的，有 Bi 前缀， 比如 BiConsumer<T,U>, BiFunction<T,U,R> ;
在类型上扩展： 比如接收原子类型参数的，有 [Int|Double|Long][Function|Consumer|Supplier|Predicate]
特殊常用的变形： 比如 BinaryOperator ， 是同类型的双参数 BiFunction<T,T,T> ，二元操作符 ； UnaryOperator 是 Function<T,T> 一元操作符。
那么，这些函数接口可以接收哪些值呢？

类/对象的静态方法引用、实例方法引用。引用符号为双冒号 ::
类的构造器引用，比如 Class::new
lambda表达式
在博文“使用函数接口和枚举实现配置式编程(Java与Scala实现)”, “精练代码：一次Java函数式编程的重构之旅” 给出了基本的例子。后面还有更多例子。重在自己练习和尝试。

聚合器
先说聚合器。每一个流式计算的末尾总有一个类似 collect(Collectors.toList()) 的方法调用。collect 是 Stream 的方法，而参数则是聚合器Collector。已有的聚合器定义在Collectors 的静态方法里。 那么这个聚合器是怎么实现的呢？

Reduce
大部分聚合器都是基于 Reduce 操作实现的。 Reduce ，名曰推导，含有三个要素： 初始值 init, 二元操作符 BinaryOperator, 以及一个用于聚合结果的数据源S。

Reduce 的算法如下：

STEP1: 初始化结果 R = init ；

STEP2: 每次从 S 中取出一个值 v，通过二元操作符施加到 R 和 v ，产生一个新值赋给 R = BinaryOperator(R, v)；重复 STEP2， 直到 S 中没有值可取为止。

比如一个列表求和，Sum([1,2,3]) , 那么定义一个初始值 0 以及一个二元加法操作 BO = a + b ，通过三步完成 Reduce 操作：step1: R = 0; step2: v=1, R = 0+v = 1; step2: v=2, R = 1 + v = 3 ; step3: v = 3, R = 3 + v = 6。

四要素
一个聚合器的实现，通常需要提供四要素：

一个结果容器的初始值提供器 supplier ；
一个用于将每次二元操作的中间结果与结果容器的值进行操作并重新设置结果容器的累积器 accumulator ；
一个用于对Stream元素和中间结果进行操作的二元操作符 combiner ；
一个用于对结果容器进行最终聚合的转换器 finisher（可选) 。
Collectors.CollectorImpl 的实现展示了这一点：

static class CollectorImpl<T, A, R> implements Collector<T, A, R> {
        private final Supplier<A> supplier;
        private final BiConsumer<A, T> accumulator;
        private final BinaryOperator<A> combiner;
        private final Function<A, R> finisher;
        private final Set<Characteristics> characteristics;
 
        CollectorImpl(Supplier<A> supplier,
                      BiConsumer<A, T> accumulator,
                      BinaryOperator<A> combiner,
                      Function<A,R> finisher,
                      Set<Characteristics> characteristics) {
            this.supplier = supplier;
            this.accumulator = accumulator;
            this.combiner = combiner;
            this.finisher = finisher;
            this.characteristics = characteristics;
        }
}
列表类聚合器
列表类聚合器实现，基本是基于Reduce 操作完成的。 看如下代码：

public static <T>
    Collector<T, ?, List<T>> toList() {
        return new CollectorImpl<>((Supplier<List<T>>) ArrayList::new, List::add,
                                   (left, right) -> { left.addAll(right); return left; },
                                   CH_ID);
首先使用 ArrayList::new 创造一个空列表； 然后 List:add 将Stream累积操作的中间结果加入到这个列表；第三个函数则将两个列表元素进行合并成一个结果列表中。 就是这么简单。集合聚合器 toSet(), 字符串连接器 joining()，以及列表求和(summingXXX)、最大(maxBy)、最小值(minBy)等都是这个套路。

映射类聚合器
映射类聚合器基于Map合并来完成。看这段代码：

private static <K, V, M extends Map<K,V>>
    BinaryOperator<M> mapMerger(BinaryOperator<V> mergeFunction) {
        return (m1, m2) -> {
            for (Map.Entry<K,V> e : m2.entrySet())
                m1.merge(e.getKey(), e.getValue(), mergeFunction);
            return m1;
        };
    }
根据指定的值合并函数 mergeFunction, 返回一个map合并器，用来合并两个map里相同key的值。mergeFunction用来对两个map中相同key的值进行运算得到新的value值，如果value值为null，会移除相应的key，否则使用value值作为对应key的值。这个方法是私有的，主要为支撑 toMap，groupingBy 而生。

toMap的实现很简短，实际上就是将指定stream的每个元素分别使用给定函数keyMapper, valueMapper进行映射得到 newKey, newValue，从而形成新的Map[newKey,newValue] (1), 再使用mapMerger(mergeFunction) 生成的 map 合并器将其合并到 mapSupplier (2)。如果只传 keyMapper, valueMapper，那么就只得到结果(1)。

public static <T, K, U, M extends Map<K, U>>
    Collector<T, ?, M> toMap(Function<? super T, ? extends K> keyMapper,
                                Function<? super T, ? extends U> valueMapper,
                                BinaryOperator<U> mergeFunction,
                                Supplier<M> mapSupplier) {
        BiConsumer<M, T> accumulator
                = (map, element) -> map.merge(keyMapper.apply(element),
                                              valueMapper.apply(element), mergeFunction);
        return new CollectorImpl<>(mapSupplier, accumulator, mapMerger(mergeFunction), CH_ID);
    }
toMap 的一个示例见如下代码：

List<Integer> list = Arrays.asList(1,2,3,4,5);
Supplier<Map<Integer,Integer>> mapSupplier = () -> list.stream().collect(Collectors.toMap(x->x, y-> y * y));
 
Map<Integer, Integer> mapValueAdd = list.stream().collect(Collectors.toMap(x->x, y->y, (v1,v2) -> v1+v2, mapSupplier));
System.out.println(mapValueAdd);
将一个 List 转成 map[1=1,2=2,3=3,4=4,5=5]，然后与另一个map[1=1,2=4,3=9,4=16,5=25]的相同key的value进行相加。注意到, toMap 的最后一个参数是 Supplier<Map> ， 是 Map 提供器，而不是 Map 对象。如果用Map对象，会报 no instances of type variables M exists so that conforms to Supplier<M>。 在函数式编程的世界里，通常是用函数来表达和组合的。

自定义聚合器
让我们仿照 Collectors.toList() 做一个自定义的聚合器。实现一个含N个数的斐波那契序列 List<Integer>。由于 Reduce 每次都从流中取一个数，因此需要生产一个含N个数的stream；可使用 Arrays.asList(1,2,3,4,5,6,7,8,9,10).stream() ， 亦可使用 IntStream.range(1,11) ，不过两者的 collector 方法是不一样的。这里我们取前者。

现在，需要构造四要素：

可变的结果容器提供器 Supplier<List<Integer>> = () -> [0, 1] ； 注意这里不能使用 Arrays.asList , 因为该方法生成的列表是不可变的。
累积器 BiConsumer<List<Integer>, Integer> accumulator()： 这里流的元素未用，仅仅用来使计算进行和终止。新的元素从结果容器中取最后两个相加后产生新的结果放到结果容器中。
组合器 BinaryOperator<List<Integer>> combiner() ： 照葫芦画瓢，目前没看出这步是做什么用；直接 return null; 也是OK的。
最终转换器 Function<List<Integer>, List<Integer>> finisher() ：在最终转换器中，移除初始设置的两个值 0, 1 。
代码如下：

/**
 * Created by shuqin on 17/12/5.
 */
public class FiboCollector implements Collector<Integer, List<Integer>, List<Integer>> {
 
  public Supplier<List<Integer>> supplier() {
    return () -> {
      List<Integer> result = new ArrayList<>();
      result.add(0); result.add(1);
      return result;
    };
  }
 
  @Override
  public BiConsumer<List<Integer>, Integer> accumulator() {
    return (res, num) -> {
      Integer next = res.get(res.size()-1) + res.get(res.size()-2);
      res.add(next);
    };
  }
 
  @Override
  public BinaryOperator<List<Integer>> combiner() {
    return null;
    //return (left, right) -> { left.addAll(right); return left; };
  }
 
  @Override
  public Function<List<Integer>, List<Integer>> finisher() {
    return res -> { res.remove(0); res.remove(1); return res; };
  }
 
  @Override
  public Set<Characteristics> characteristics() {
    return Collections.emptySet();
  }
 
}
 
List<Integer> fibo = Arrays.asList(1,2,3,4,5,6,7,8,9,10).stream().collect(new FiboCollector());
System.out.println(fibo);
流
流（Stream）是Java8对函数式编程的重要支撑。大部分函数式工具都围绕Stream展开。

Stream的接口
Stream 主要有四类接口：

流到流之间的转换：比如 filter(过滤), map(映射转换), mapTo[Int|Long|Double] (到原子类型流的转换), flatMap(高维结构平铺)，flatMapTo[Int|Long|Double], sorted(排序)，distinct(不重复值)，peek(执行某种操作，流不变，可用于调试)，limit(限制到指定元素数量), skip(跳过若干元素) ；
流到终值的转换： 比如 toArray（转为数组），reduce（推导结果），collect（聚合结果），min(最小值), max(最大值), count (元素个数)， anyMatch (任一匹配), allMatch(所有都匹配)， noneMatch(一个都不匹配)， findFirst（选择首元素），findAny(任选一元素) ；
直接遍历： forEach (不保序遍历，比如并行流), forEachOrdered（保序遍历) ；
构造流： empty (构造空流)，of (单个元素的流及多元素顺序流)，iterate (无限长度的有序顺序流)，generate (将数据提供器转换成无限非有序的顺序流)， concat (流的连接)， Builder (用于构造流的Builder对象)
除了 Stream 本身自带的生成Stream 的方法，数组和容器及StreamSupport都有转换为流的方法。比如 Arrays.stream , [List|Set|Collection].[stream|parallelStream] , StreamSupport.[int|long|double|]stream；

流的类型主要有：Reference(对象流)， IntStream (int元素流), LongStream (long元素流)， Double (double元素流) ，定义在类 StreamShape 中，主要将操作适配于类型系统。

flatMap 的一个例子见如下所示，将一个二维数组转换为一维数组：


List<Integer> nums = Arrays.asList(Arrays.asList(1,2,3), Arrays.asList(1,4,9), Arrays.asList(1,8,27))
                           .stream().flatMap(x -> x.stream()).collect(Collectors.toList());
System.out.println(nums);
collector实现
这里我们仅分析串行是怎么实现的。入口在类 java.util.stream.ReferencePipeline 的 collect 方法：


container = evaluate(ReduceOps.makeRef(collector));
return collector.characteristics().contains(Collector.Characteristics.IDENTITY_FINISH)
          ? (R) container : collector.finisher().apply(container);
这里的关键是 ReduceOps.makeRef(collector)。 点进去：


public static <T, I> TerminalOp<T, I>
    makeRef(Collector<? super T, I, ?> collector) {
        Supplier<I> supplier = Objects.requireNonNull(collector).supplier();
        BiConsumer<I, ? super T> accumulator = collector.accumulator();
        BinaryOperator<I> combiner = collector.combiner();
        class ReducingSink extends Box<I>
                implements AccumulatingSink<T, I, ReducingSink> {
            @Override
            public void begin(long size) {
                state = supplier.get();
            }
 
            @Override
            public void accept(T t) {
                accumulator.accept(state, t);
            }
 
            @Override
            public void combine(ReducingSink other) {
                state = combiner.apply(state, other.state);
            }
        }
        return new ReduceOp<T, I, ReducingSink>(StreamShape.REFERENCE) {
            @Override
            public ReducingSink makeSink() {
                return new ReducingSink();
            }
 
            @Override
            public int getOpFlags() {
                return collector.characteristics().contains(Collector.Characteristics.UNORDERED)
                       ? StreamOpFlag.NOT_ORDERED
                       : 0;
            }
        };
    }
 
private static abstract class Box<U> {
        U state;
 
        Box() {} // Avoid creation of special accessor
 
        public U get() {
            return state;
        }
    }
Box 是一个结果值的持有者； ReducingSink 用begin, accept, combine 三个方法定义了要进行的计算；ReducingSink是有状态的流数据消费的计算抽象，阅读Sink接口文档可知。ReduceOps.makeRef(collector) 返回了一个封装了Reduce操作的ReduceOps对象。注意到，这里都是声明要执行的计算，而不涉及计算的实际过程。展示了表达与执行分离的思想。真正的计算过程启动在 ReferencePipeline.evaluate 方法里:


final <R> R evaluate(TerminalOp<E_OUT, R> terminalOp) {
        assert getOutputShape() == terminalOp.inputShape();
        if (linkedOrConsumed)
            throw new IllegalStateException(MSG_STREAM_LINKED);
        linkedOrConsumed = true;
 
        return isParallel()
               ? terminalOp.evaluateParallel(this, sourceSpliterator(terminalOp.getOpFlags()))
               : terminalOp.evaluateSequential(this, sourceSpliterator(terminalOp.getOpFlags()));
    }
使用 IDE 的 go to implementations 功能， 跟进去，可以发现，最终在 AbstractPipeLine 中定义了：


@Override
    final <P_IN> void copyInto(Sink<P_IN> wrappedSink, Spliterator<P_IN> spliterator) {
        Objects.requireNonNull(wrappedSink);
 
        if (!StreamOpFlag.SHORT_CIRCUIT.isKnown(getStreamAndOpFlags())) {
            wrappedSink.begin(spliterator.getExactSizeIfKnown());
            spliterator.forEachRemaining(wrappedSink);
            wrappedSink.end();
        }
        else {
            copyIntoWithCancel(wrappedSink, spliterator);
        }
    }
Spliterator 用来对流中的元素进行分区和遍历以及施加Sink指定操作，可以用于并发计算。Spliterator的具体实现类定义在 Spliterators 的静态类和静态方法中。其中有：


数组Spliterator:
static final class ArraySpliterator<T> implements Spliterator<T>
static final class IntArraySpliterator implements Spliterator.OfInt
static final class LongArraySpliterator implements Spliterator.OfLong
static final class DoubleArraySpliterator implements Spliterator.OfDouble
 
迭代Spliterator:
static class IteratorSpliterator<T> implements Spliterator<T>
static final class IntIteratorSpliterator implements Spliterator.OfInt
static final class LongIteratorSpliterator implements Spliterator.OfLong
static final class DoubleIteratorSpliterator implements Spliterator.OfDouble
 
抽象Spliterator:
public static abstract class AbstractSpliterator<T> implements Spliterator<T>
private static abstract class EmptySpliterator<T, S extends Spliterator<T>, C>
public static abstract class AbstractIntSpliterator implements Spliterator.OfInt
public static abstract class AbstractLongSpliterator implements Spliterator.OfLong
public static abstract class AbstractDoubleSpliterator implements Spliterator.OfDouble
每个具体类都实现了trySplit，forEachRemaining，tryAdvance，estimateSize，characteristics， getComparator。 trySplit 用于拆分流，提供并发能力；forEachRemaining，tryAdvance 用于遍历和消费流中的数据。下面展示了IteratorSpliterator的forEachRemaining，tryAdvance 两个方法的实现。可以看到，木有特别的地方，就是遍历元素并将指定操作施加于元素。

@Override
        public void forEachRemaining(Consumer<? super T> action) {
            if (action == null) throw new NullPointerException();
            Iterator<? extends T> i;
            if ((i = it) == null) {
                i = it = collection.iterator();
                est = (long)collection.size();
            }
            i.forEachRemaining(action);
        }
 
        @Override
        public boolean tryAdvance(Consumer<? super T> action) {
            if (action == null) throw new NullPointerException();
            if (it == null) {
                it = collection.iterator();
                est = (long) collection.size();
            }
            if (it.hasNext()) {
                action.accept(it.next());
                return true;
            }
            return false;
        }
整体流程就是这样。回顾一下：

Collector 定义了必要的聚合操作函数；
ReduceOps.makeRef 将 Collector 封装成一个计算对象 ReduceOps ，依赖的 ReducingSink 定义了具体的流数据消费过程；
Spliterator 用于对流中的元素进行分区和遍历以及施加Sink指定的操作。
Pipeline
那么，Spliterator 又是从哪里来的呢？是通过类 java.util.stream.AbstractPipeline 的方法 sourceSpliterator 拿到的：


private Spliterator<?> sourceSpliterator(int terminalFlags) {
        // Get the source spliterator of the pipeline
        Spliterator<?> spliterator = null;
        if (sourceStage.sourceSpliterator != null) {
            spliterator = sourceStage.sourceSpliterator;
            sourceStage.sourceSpliterator = null;
        }
        else if (sourceStage.sourceSupplier != null) {
            spliterator = (Spliterator<?>) sourceStage.sourceSupplier.get();
            sourceStage.sourceSupplier = null;
        }
        else {
            throw new IllegalStateException(MSG_CONSUMED);
        }
        // code for isParallel
       return spliterator;
}
这里的 sourceStage 是一个 AbstractPipeline。 Pipeline 是实现流式计算的流水线抽象，也是Stream的实现类。可以看到，java.util.stream 定义了四种 pipeline: DoublePipeline, IntPipeline, LongPipeline, ReferencePipeline。可以重点看 ReferencePipeline 的实现。比如 filter, map


abstract class ReferencePipeline<P_IN, P_OUT>
        extends AbstractPipeline<P_IN, P_OUT, Stream<P_OUT>>
        implements Stream<P_OUT>
 
@Override
    public final Stream<P_OUT> filter(Predicate<? super P_OUT> predicate) {
        Objects.requireNonNull(predicate);
        return new StatelessOp<P_OUT, P_OUT>(this, StreamShape.REFERENCE,
                                     StreamOpFlag.NOT_SIZED) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<P_OUT> sink) {
                return new Sink.ChainedReference<P_OUT, P_OUT>(sink) {
                    @Override
                    public void begin(long size) {
                        downstream.begin(-1);
                    }
 
                    @Override
                    public void accept(P_OUT u) {
                        if (predicate.test(u))
                            downstream.accept(u);
                    }
                };
            }
        };
    }
 
    @Override
    @SuppressWarnings("unchecked")
    public final <R> Stream<R> map(Function<? super P_OUT, ? extends R> mapper) {
        Objects.requireNonNull(mapper);
        return new StatelessOp<P_OUT, R>(this, StreamShape.REFERENCE,
                                     StreamOpFlag.NOT_SORTED | StreamOpFlag.NOT_DISTINCT) {
            @Override
            Sink<P_OUT> opWrapSink(int flags, Sink<R> sink) {
                return new Sink.ChainedReference<P_OUT, R>(sink) {
                    @Override
                    public void accept(P_OUT u) {
                        downstream.accept(mapper.apply(u));
                    }
                };
            }
        };
    }
套路基本一样，关键点在于 accept 方法。filter 只在满足条件时将值传给下一个 pipeline, 而 map 将计算的值传给下一个 pipeline. StatelessOp 没有什么逻辑，JDK文档解释是：Base class for a stateless intermediate stage of a Stream。相应还有一个 StatefulOp, Head。 这些都是 ReferencePipeline ，负责将值在 pipeline 之间传递，交给 Sink 去计算。


static class Head<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT>
abstract static class StatelessOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT>
abstract static class StatefulOp<E_IN, E_OUT> extends ReferencePipeline<E_IN, E_OUT>
至此，我们对整个流计算过程有了更清晰的认识。 细节可以再逐步推敲。

函数式编程的益处
更精练的代码
函数编程的一大益处，是用更精练的代码表达常用数据处理模式。函数接口能够轻易地实现模板方法模式，只要将不确定的业务逻辑抽象成函数接口，然后传入不同的lambda表达式即可。博文“精练代码：一次Java函数式编程的重构之旅” 展示了如何使用函数式编程来重构常见代码，萃取更多可复用的代码模式。

这里给出一个列表分组的例子。实际应用常常需要将一个列表 List[T] 转换为一个 Map[K, List[T]] ， 其中 K 是通过某个函数来实现的。 看下面一段代码：


public static Map<String, List<OneRecord>> buildRecordMap(List<OneRecord> records, List<String> colKeys) {
    Map<String, List<OneRecord>> recordMap = new HashMap<>();
    records.forEach(
        record -> {
          String recordKey = buildRecordKey(record.getFieldValues(), colKeys);
          if (recordMap.get(recordKey) == null) {
            recordMap.put(recordKey, new ArrayList<OneRecord>());
          }
          recordMap.get(recordKey).add(record);
    });
    return recordMap;
  }
可以使用 Collectors.groupingby 来简洁地实现：


public static Map<String, List<OneRecord>> buildRecordMapBrief(List<OneRecord> records, List<String> colKeys) {
    return records.stream().collect(Collectors.groupingBy(
        record -> buildRecordKey(record.getFieldValues(), colKeys)
    ));
  }
很多常用数据处理算法，都可以使用函数式编程的流式计算简洁表达。

更通用的代码
使用函数接口，结合泛型，很容易用精练的代码，写出非常通用的工具方法。 实际应用中，常常会有这样的需求： 有两个对象列表srcList和destList，两个对象类型的某个字段K具有相同的值；需要根据这个相同的值合并对应的两个对象的信息。

这里给出了一个列表合并函数，可以将一个对象列表合并到指定的对象列表中。实现是： 先将待合并的列表srcList根据key值函数keyFunc构建起srcMap,然后遍历dest列表的对象R，将待合并的信息srcMap[key]及T通过合并函数mergeFunc生成的新对象R添加到最终结果列表。


public static <K,R> List<R> mergeList(List<R> srcList, List<R> destList ,
                                      Function<R,K> keyFunc,
                                      BinaryOperator<R> mergeFunc) {
  return mergeList(srcList, destList, keyFunc, keyFunc, mergeFunc);
}
 
public static <T,S,K,R> List<R> mergeList(List<S> srcList, List<T> destList ,
                                          Function<S,K> skeyFunc, Function<T,K> dkeyFunc,
                                          BiFunction<S,T,R> mergeFunc) {
 
  Map<K,S> srcMap = srcList.stream().collect(Collectors.toMap(skeyFunc, s -> s, (k1,k2) -> k1));
  return destList.stream().map(
      dest -> {
        K key = dkeyFunc.apply(dest);
        S src = srcMap.get(key);
        return mergeFunc.apply(src, dest);
      }
  ).collect(Collectors.toList());
 
}
更可测的代码
使用函数接口可以方便地隔离外部依赖，使得类和对象的方法更纯粹、更具可测性。博文“使用Java函数接口及lambda表达式隔离和模拟外部依赖更容易滴单测”，“改善代码可测性的若干技巧”集中讨论了如何使用函数接口提升代码的可单测性。

组合的力量
函数编程的强大威力，在于将函数接口组合起来，构建更强大更具有通用性的实用工具方法。超越类型，超越操作与数据的边界。

前面提到，函数接口就是数据转换器。比如Function<T,R> 就是“将T对象转换成R对象的行为或数据转换器”。对于实际工程应用的普通级函数编程足够了。不过，要玩转函数接口，就要升级下认识。 比如 Function<BiFunction<S,Q,R>, Function<T,R>> 该怎么理解呢？这是“一个一元函数g(h(s,q)) ，参数指定的二元函数h(s,q)应用于指定的两个参数S,Q，得到一个一元函数f(t)，这个函数接收一个T对象，返回一个R对象”。 如下代码所示：

public static <T,S,Q,R> Function<BiFunction<S,Q,R>, Function<T,R>> op(Function<T,S> funcx, Function<T,Q> funcy) {
  return opFunc -> aT -> opFunc.apply(funcx.apply(aT), funcy.apply(aT));
}
 
System.out.println(op(x-> x.toString().length(), y-> y+",world").apply((x,y) -> x+" " +y).apply("hello"));
实现的是 h(t) = h(funx(t), funy(t)) ，h(x,y) 是一个双参数函数。

“Java函数接口实现函数组合及装饰器模式” 展示了如何使用极少量的代码实现装饰器模式，将简单的函数接口组合成更强大功能的复合函数接口。

来看上面的 public static <T,S,K,R> List<R> mergeList(List<S> srcList, List<T> destList , Function<S,K> skeyFunc, Function<T,K> dkeyFunc,BiFunction<S,T,R> mergeFunc) ， 通用性虽好，可是有5个参数，有点丑。怎么改造下呢？ 看实现，主要包含两步：1. 将待合并列表转化为 srcMap: map<K,S>; 2. 使用指定的函数 dKeyFunc, mergeFunc 作用于destList和srcMap，得到最终结果。可以改写代码如下：

public static <T,S,K,R> List<R> mergeList(List<S> srcList, List<T> destList ,
                                          Function<S,K> skeyFunc, Function<T,K> dkeyFunc,
                                          BiFunction<S,T,R> mergeFunc) {
    return join(destList, mapKey(srcList, skeyFunc)).apply(dkeyFunc, (BiFunction) mergeFunc);
 
  }
 
  public static <T,K> Map<K,T> mapKey(List<T> list, Function<T,K> keyFunc) {
    return list.stream().collect(Collectors.toMap(keyFunc, t -> t, (k1,k2) -> k1));
  }
 
  public static <T,S,K,R> BiFunction<Function<T,K>, BiFunction<S,T,R>, List<R>> join(List<T> destList, Map<K,S> srcMap) {
    return (dkeyFunc,mergeFunc) -> destList.stream().map(
        dest -> {
          K key = dkeyFunc.apply(dest);
          S src = srcMap.get(key);
          return mergeFunc.apply(src, dest);
        }).collect(Collectors.toList());
  }
 
System.out.println(mergeList(Arrays.asList(1,2), Arrays.asList("an", "a"), s-> s, t-> t.toString().length(), (s,t) -> s+t));
mapKey 是一个通用函数，用于将一个 list 按照指定的 keyFunc 转成一个 Map; join 函数接受一个 list 和待合并的 srcMap, 返回一个二元函数，该函数使用指定的 dkeyFunc 和 mergeFunc 来合并指定数据得到最终的结果列表。这可称之为“延迟指定行为”。现在, mapKey 和 join 都是通用性函数。Amazing ！

Java8泛型
在Java8函数式框架的解读中，可以明显看到，泛型无处不在。Java8的泛型推导能力也有很大的增强。可以说，如果没有强大的泛型推导支撑，函数接口的威力将会大打折扣。

完整代码示例
package zzz.study.function;
 
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
 
/**
 * Created by shuqin on 17/12/3.
 */
public class FunctionUtil {
 
  public static <T,R> List<R> multiGetResult(List<Function<List<T>, R>> functions, List<T> list) {
    return functions.stream().map(f -> f.apply(list)).collect(Collectors.toList());
  }
 
  public static <K,R> List<R> mergeList(List<R> srcList, List<R> destList ,
                                        Function<R,K> keyFunc,
                                        BinaryOperator<R> mergeFunc) {
    return mergeList(srcList, destList, keyFunc, keyFunc, mergeFunc);
  }
 
  public static <T,S,K,R> List<R> mergeList(List<S> srcList, List<T> destList ,
                                          Function<S,K> skeyFunc, Function<T,K> dkeyFunc,
                                          BiFunction<S,T,R> mergeFunc) {
    return join(destList, mapKey(srcList, skeyFunc)).apply(dkeyFunc, (BiFunction) mergeFunc);
 
  }
 
  public static <T,K> Map<K,T> mapKey(List<T> list, Function<T,K> keyFunc) {
    return list.stream().collect(Collectors.toMap(keyFunc, t -> t, (k1,k2) -> k1));
  }
 
  public static <T,S,K,R> BiFunction<Function<T,K>, BiFunction<S,T,R>, List<R>> join(List<T> destList, Map<K,S> srcMap) {
    return (dkeyFunc,mergeFunc) -> destList.stream().map(
        dest -> {
          K key = dkeyFunc.apply(dest);
          S src = srcMap.get(key);
          return mergeFunc.apply(src, dest);
        }).collect(Collectors.toList());
  }
 
  /** 对给定的值 x,y 应用指定的二元操作函数 */
  public static <T,S,R> Function<BiFunction<T,S,R>, R> op(T x, S y) {
    return opFunc -> opFunc.apply(x, y);
  }
 
  /** 将两个函数使用组合成一个函数，这个函数接受一个二元操作函数 */
  public static <T,S,Q,R> Function<BiFunction<S,Q,R>, R> op(Function<T,S> funcx, Function<T,Q> funcy, T x) {
    return opFunc -> opFunc.apply(funcx.apply(x), funcy.apply(x));
  }
 
  public static <T,S,Q,R> Function<BiFunction<S,Q,R>, Function<T,R>> op(Function<T,S> funcx, Function<T,Q> funcy) {
    return opFunc -> aT -> opFunc.apply(funcx.apply(aT), funcy.apply(aT));
  }
 
  /** 将两个函数组合成一个叠加函数, compose(f,g) = f(g) */
  public static <T> Function<T, T> compose(Function<T,T> funcx, Function<T,T> funcy) {
    return x -> funcx.apply(funcy.apply(x));
  }
 
  /** 将若干个函数组合成一个叠加函数, compose(f1,f2,...fn) = f1(f2(...(fn))) */
  public static <T> Function<T, T> compose(Function<T,T>... extraFuncs) {
    if (extraFuncs == null || extraFuncs.length == 0) {
      return x->x;
    }
    return x -> Arrays.stream(extraFuncs).reduce(y->y, FunctionUtil::compose).apply(x);
  }
 
   public static void main(String[] args) {
     System.out.println(multiGetResult(
         Arrays.asList(
             list -> list.stream().collect(Collectors.summarizingInt(x->x)),
             list -> list.stream().filter(x -> x < 50).sorted().collect(Collectors.toList()),
             list -> list.stream().collect(Collectors.groupingBy(x->(x%2==0? "even": "odd"))),
             list -> list.stream().sorted().collect(Collectors.toList()),
             list -> list.stream().sorted().map(Math::sqrt).collect(Collectors.toMap(x->x, y->Math.pow(2,y)))),
         Arrays.asList(64,49,25,16,9,4,1,81,36)));
 
     List<Integer> list = Arrays.asList(1,2,3,4,5);
     Supplier<Map<Integer,Integer>> mapSupplier = () -> list.stream().collect(Collectors.toMap(x->x, y-> y * y));
 
     Map<Integer, Integer> mapValueAdd = list.stream().collect(Collectors.toMap(x->x, y->y, (v1,v2) -> v1+v2, mapSupplier));
     System.out.println(mapValueAdd);
 
     List<Integer> nums = Arrays.asList(Arrays.asList(1,2,3), Arrays.asList(1,4,9), Arrays.asList(1,8,27))
                                .stream().flatMap(x -> x.stream()).collect(Collectors.toList());
     System.out.println(nums);
 
     List<Integer> fibo = Arrays.asList(1,2,3,4,5,6,7,8,9,10).stream().collect(new FiboCollector());
     System.out.println(fibo);
 
     System.out.println(op(new Integer(3), Integer.valueOf(3)).apply((x,y) -> x.equals(y.toString())));
 
     System.out.println(op(x-> x.length(), y-> y+",world", "hello").apply((x,y) -> x+" " +y));
 
     System.out.println(op(x-> x, y-> y+",world").apply((x,y) -> x+" " +y).apply("hello"));
 
     System.out.println(op(x-> x.toString().length(), y-> y+",world").apply((x,y) -> x+" " +y).apply("hello"));
 
     System.out.println(mergeList(Arrays.asList(1,2), Arrays.asList("an", "a"),
                                  s-> s, t-> t.toString().length(), (s,t) -> s+t));
 
   }
 
}
小结
本文深入学习了Java8函数式编程框架：Function&Stream&Collector，并展示了函数式编程在实际应用中所带来的诸多益处。函数式编程是一把大锋若钝的奇剑。基于函数接口编程，将函数作为数据自由传递，结合泛型推导能力，可编写出精练、通用、易测的代码，使代码表达能力获得飞一般的提升。