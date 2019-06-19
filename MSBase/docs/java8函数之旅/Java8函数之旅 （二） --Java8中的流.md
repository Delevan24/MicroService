Java8函数之旅 （二） --Java8中的流
流与集合
   众所周知，日常开发与操作中涉及到集合的操作相当频繁，而java中对于集合的操作又是相当麻烦。这里你可能就有疑问了，我感觉平常开发的时候操作集合时不麻烦呀？那下面我们从一个例子说起。

计算从伦敦来的艺术家的人数
请注意这个问题例子在本篇博客中会经常提到，希望你能记住这个简单的例子
这个问题看起来相当的简单，那么使用for循环进行计算

    int count = 0;
    for(Artist artist: allArtists){
        if(artisst.isFrom("London")){
            count++;
        }
    }
标准的写法如上图，当然是没有问题的了，尽管这样的操作是可以的，但依旧存在着问题。

每次需要迭代集合类的的时候，我都要写这样的5行代码或者更多，并且将这样的代码想要改成并行运行的方式也十分的麻烦，需要修改每个for循环才能够实现。
第二个问题就是在于这样的写法本身就是阅读性很差的，什么？我很容易就看的懂呀，但事实上，你不得不承认，其他人必须要阅读了整个循环体，然后再思考一会，才能得出：哦！这段代码是做这个的，当然了，这个例子相当简单，你可能几秒钟就看理解了，但是面对一个多层循环嵌套的集合迭代操作，想看明白，那就相当头疼了。
第三个问题在于，for循环从本质上来讲是一种串行化的操作，从总体来看的话，使用for循环会将行为和方法混为一谈。
外部迭代与内部迭代
   上文所用到的for循环是来自java5的增强for循环，本质上是属于iterator迭代器的语法糖，这种使用迭代器的迭代集合的方式，称之为外部迭代，说的通俗一点，就是需要我们程序猿手动的对这个集合进行种种操作才能得到想要结果的迭代方式，叫做外部迭代。
   与外部迭代所对应的，则是内部迭代，内部迭代与之相反，是集合本身内部通过流进行了处理之后，程序猿们只需要直接取结果就行了，这种迭代称为内部迭代。
   那么问题来了，用内部迭代怎么解决上面的问题呢？

    long count = allArtists.stream()//进行流操作
                           .filter(artist -> artist.isFrom("London"))//选出所有来自伦敦的艺术家
                           .count();//统计他们的数量
ok，也许你还暂时还不了解关于stream()流的相关操作，别着急，下文会对这些api语法作说明。与上文对应，这里同样针对上文列举出三条好处。

每次需要迭代的时候，并不需要写同样的代码块，说出来你可能不信，这样的代码只有一行，分成三行来表示只是为了方便阅读，改成并行操作的方式也简单的惊人，只需要将第一行的stream()改为parallelStream()就可以了
第二个好处就是可阅读性，相信即使你现在暂时不懂得流的相关api，也能看懂上文的操作，仔细想想这个问题:计算从伦敦来的艺术家的人数，那不就是两步吗？第一步筛选出所有来自伦敦的艺术家，第二步统计他们的人数，现在你回头看上文的代码，第一行使用流对集合进行内部操作，第二步筛选出来自伦敦的艺术家，第三步计数，简单明了，没有令人头疼的循环，也不需要看完整段代码才理解这一行是做什么的。
第三个好处其实第一点已经提到的，轻松的并行化，并且既然是涉及到集合的相关操作，就让集合自己去完成，何必劳驾宝贵的程序员的其他时间呢？
常用流的api
1.获取流对象、
要进行相应的流操作，必然要先获得流对象，首先介绍的就是如何获得一个流的对象。

对于集合来说,直接通过stream()方法即可获取流对象
		List<Person> list = new ArrayList<Person>(); 
		Stream<Person> stream = list.stream();
对于数组来说,通过Arrays类提供的静态函数stream()获取数组的流对象
		String[] names = {"chaimm","peter","john"};
		Stream<String> stream = Arrays.stream(names);
直接将几个普通的数值变成流对象
	Stream<String> stream = Stream.of("chaimm","peter","john");
2.collect(toList())
  collect（Collectors.toList()）方法是将stream里的值生成一个列表，也就是将流再转化成为集合，是一个及早求值的操作。
  关于惰性求值与及早求值，这里简单说明一下，这两者最重要的区别就在于看操作有没有具体的返回值(或者说是否产生了具体的数值)，比如上文的的统计来自英国艺术家人数的代码，第二行代码的操作是首先筛选出来自英国的艺术家，这个操作并没有实际的数值产生，因此这个操作就是惰性求值，而最后的count计数方法，产生了实际的数值，因此是及早求值。惰性求值是用于描述stream流的，因此返回值是stream，而几乎所有对于流的链式操作都是进行各种惰性求值的链式操作，最后加上一个及早求值的方法返回想要的结果。
  你可以用建造者的设计模式去理解他，建造者模式通过一系列的操作进行设置与配置操作，最后调用一个build方法，创建出相应的对象。对于这里也是同样，调用各种惰性求值的方法，返回一个stream流，最后一步调用一个及早求值的方法，得到最终的结果。
那么现在对于这个collect(toList())，使用方法就十分明了了。

    list.stream()//将集合转化成流
        .???()//一系列惰性求值的操作,返回值为stream
        .collect(toList())//及早求值，这个及早求值的方法返回值为集合,再将流转化为集合
3. 筛选filter
  你如果有耐心，看到了这里对于这个操作应该不陌生了，filter函数接收一个Lambda表达式作为参数，该表达式返回boolean，在执行过程中，流将元素逐一输送给filter，并筛选出执行结果为true的元素。
还是上文的例子:筛选出来自英国的艺术家

		long count = allArtists.stream()
							   .filter(artist -> artist.isFrom("London"))//惰性求值筛选
							   .count();//及早求值统计
4.去重distinc
		long count = allArtists.stream()
							   .filter(artist -> artist.isFrom("London"))
							   .distinct()
							   .count();
这样只增加了一行，便达到了筛选出所有来自英国的艺术家，并且去掉重复的名字之后的统计数量的目的
你看，符合了上文所说的，简单，易懂，可读性强。
相信下面我说的几个方法你一看就懂。

5.截取limit
截取流的前N个元素

    long count = allArtists.stream()
                           .filter(artist -> artist.isFrom("London"))
                           .limit(N)
                           .count();
6. 跳过skip
跳过流的前N个元素：

		long count = allArtists.stream()
							   .filter(artist -> artist.isFrom("London"))
							   .skip(N)
							   .count();
7. 映射map
如果有一个函数可以将一种类型的值转换成另外一种类型,map操作就可以使用该函数，将一个流中的值转换成一个新的流。
映射这个操作其实在大家编程的过程中都经常用到，也就是将A映射成B A->B
还是用艺术家的例子，现在要获得一个包含所有来自伦敦艺术家的名字的集合

		List<String> artistNames = allArtists.stream()
											 .filter(artist -> artist.isFrom("London"))
											 .map(artist -> artist.getArtistName())//将艺术家集合映射成了包含艺术家名字的集合
											 .collect(Collects.toList());
请注意，这里的传递的Lambda表达式必须是Function接口的一个实例，Function接口是只包含一个参数的普通函数接口。

8. flatMap
上一条已经介绍过map操作，它可以用一个新的值代替stream里的值，但有时候，用户希望让map操作有点变化，生成一个新的steram对象取而代之，用户通常不希望结果是一连串的流，此时flatMap能够派上用场。
通俗的一点的说法是，他可以将一条一条的小流，汇聚成一条大流，好比海纳百川的感觉。
用一个简单的例子就很容易理解了
假设有一个包含多个集合的流，现在希望得到所有数字的序列，利用flatMap解决办法如下

			List <Integer> together = Stream.of(asList(1,2),asList(3,4))
											.flatMap(numbers -> numbers.stream())
											.collect(toList());
			together.forEach(n -> System.out.println(n));
输出结果为1,2,3,4
你看，2条小流被整合成了一条流！（这就是为什么这个类库叫做stream，流的意思，十分的形象化）
steram流，在java8里，你可以理解成流水线，流水线的上的商品就是集合里一个个的元素，而这些对于流的各种各样的流操作，就是流水线上加工这些商品的机器。所以呢，stream流的相关特性与之也符合

不可逆，无论是河流，水流，还是流水线，没听过有倒流的，因此java8中的流也同样如此，你不可能在操作完第一个元素之后回头再重新操作，这在流操作里是无法完成的。
另一个特性就是内部迭代，这在一开始已经讲述过了。
为什么到这里我才做不可逆的特性说明呢，因为我觉得flatMap很能符合流的特点，水流嘛，海纳百川，不可逆流，你看，完美符合java8的流特性。

9. max和min
例子：获得所有艺术家中，年龄最大的艺术家
想一想，采用原始的外部迭代，要达到这么简单的要求是不是忽然感觉很麻烦？排个序？还是写一个交替又或者是选择比较的算法？何必这么麻烦！使用流操作采用内部迭代就好了，这不是我们程序猿应该专门写一段外部程序来解决的问题！
Stream上常用的操作之一是求最大值和最小值，事实上通过流操作来完成最大值最小值的方式有很多很多种，这里介绍的max和min的方法是stream类里的直接附带的方法，事实上在实际操作的时候我并不会选择这种操作方式（关于这点，在后面的章节会提到，这里提前做一个记号，以后增加超链接过去）
使用流操作如下:
	
		Artist theMaxAgeArtist = allArtists.stream()
										   .max(Comparator.comparing(artist -> artist.getAge()))
										   .get();
我们一行一行地说

第一行，转化为流对象，读到这里的你相信已经十分理解了，因此以后对于这一行不再说明了
第二行，查找Stream中最大或最小的元素，首先要考虑的是用什么作为排序的条件，这里显然是根据艺术家的年龄作为指标,为了让Stream对象按照艺术家的年龄进行排序，需要传给它一个Comparator对象，java8提供了一个新的静态方法comparing，使用它可以方便的实现一个比较器。放在以前，我们需要比较两个对象的某个属性的值，现在只需要提供一个get方法就可以了。
这个comparing方法很有意思，这个方法接受一个函数作为参数，并且返回另一个函数。这在其他语言里听起来像是废话，然而在java里可不能这么认为，这种方法早就该引入Java的标准类库，然而之前的实现方式只能是匿名内部类的实现，无论是看起来，还是写起来，都是相当的难受，所以一直就没有实现，但是现在有了Lambda表达式，就变得很简洁啦。
第三行，max()方法返回的是一个Optional对象，这个对象对我们来确实有点陌生，第11条我会专门对这个对象进行介绍，在这里需要记住的是，通过get方法可以取得Optional对象中的值。
10.归约reduce
reduce操作可以实现从一组值中生成一个值。在上述例子中用到的count，min，max方法，因为经常使用，所以被纳入了标准库里，实际上，这些方法都是由reduce操作实现的。
reduce函数接收两个参数：

初始值
进行归约操作的Lambda表达式
举个例子 使用reduce进行求和

    int count = Stream.of(1,2,3)
                      .reduce(0,(acc,element) -> acc + element);
reduce的第一个参数表示初始值为0；
reduce的第二个参数为需要进行的归约操作，它接收一个拥有两个参数的Lambda表达式，以上代码acc参数代表当前的数值总和,element代表下一个元素，reduce会把流中的元素两两输给Lambda表达式，最后将计算出累加之和。
也就是说每次acc+element的返回值都会赋给acc
在上述求和例子中，计算过程是这样的 初始值为0

0 + 1 = 1
1 + 2 = 3
3 + 3 = 6
以上三行就是 acc + elment = acc ,其中acc的初始值为reduce的第一个参数(在本例中初始值为0)
上面的方法中我们自己定义了Lambda表达式实现求和运算，如果当前流的元素为数值类型，那么可以使用Integer提供了sum函数代替自定义的Lambda表达式，如：

int age = list.stream().reduce(0, Integer::sum);
Integer类还提供了min、max等一系列数值操作，当流中元素为数值类型时可以直接使用。

注: 上面的Integer::sum如果不理解的话，这是java8中引用的方法，是一种简写语法，属于语法糖。
一般格式为类名(或者是类的实例对象) :: 方法名（注意这里只是方法名，没有括号）,这里引用了Integer里的sum函数(java8里新增的)，下面是Integer里的sum函数源码

    /**
     * Adds two integers together as per the + operator.
     *
     * @param a the first operand
     * @param b the second operand
     * @return the sum of {@code a} and {@code b}
     * @see java.util.function.BinaryOperator
     * @since 1.8
     */
    public static int sum(int a, int b) {
        return a + b;
    }
11. Optional对象
Optional是Java8新加入的一个容器，这个容器只存1个或0个元素，它用于防止出现NullpointException，它提供如下方法：

isPresent()
判断容器中是否有值。
ifPresent(Consume lambda)
容器若不为空则执行括号中的Lambda表达式。
T get()
获取容器中的元素，若容器为空则抛出NoSuchElement异常。
T orElse(T other)
获取容器中的元素，若容器为空则返回括号中的默认值。
值得注意的是，Optional对象不仅可以用于新的Java 8 API,也可用于具体领域类中，和普通的类并没有什么区别，当试图避免空值相关的缺陷，如捕获的异常时，可以考虑一下是否可使用Optional对象。

本篇小结
  本篇以一个艺术家的例子介绍了流与基本流的相关操作，目的是为了让看到本篇博客的人尝试着使用这样的函数式方法，并开始理解什么是java8中的流。