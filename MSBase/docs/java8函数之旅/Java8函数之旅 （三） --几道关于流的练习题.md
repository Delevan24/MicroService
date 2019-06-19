#Java8函数之旅 （三） --几道关于流的练习题
为什么要有练习题？

   所谓学而不思则罔，思而不学则殆，在系列第一篇就表明我认为写博客，既是分享，也是自己的巩固，我深信"纸上得来终觉浅，绝知此事要躬行"的道理，因此之后的几篇博客都会在知识点后面附上几道练习题，不会单独开一篇来说练习题。
   大部分题练习题来自于Richard Warburton的《Java8 函数式编程》,练习题的难度不会很难，大部分都十分的基础（可能偶尔会有一两道进阶题）,并且我在后面也会附上可供参考的思路与代码，但是我认为想要学习的人（包括我）应当首先对问题沉下心来思考，然后再查看参考答案，当然如果看博客的你没有这么多时间我的建议是记录下来，闲暇时光在脑袋里想一想也是好的，我认为鲁迅的说的时间就像海绵，挤挤就有了是十分有道理的，其实大家都没有自己想象的那么忙。

基础题

Question 1 (常用流操作)
编写一个求和函数，计算流中所有数的和。例int addUp(Stream numbers)
编写一个函数，参数为艺术家集合，返回一个字符串集合，其中包含了艺术家的姓名与国籍。(艺术家类名为Artist,里面有获得姓名与国籍的get方法getName()与getNationality())
编写一个函数，参数为专辑集合，返回一个由 最多包含3首歌曲的专辑 组成的集合。(专辑类名为Album，里面包含了一个返回本专辑所有歌曲的集合的方法getTrackList())
请耐心思考一会，看看能不能自己写出来，下面给一些参考的思路，具体的方法确实是有很多种的。

Question 1 的参考思路

编写一个求和函数，计算流中所有数的和。
这道练习题要求计算流中所有的数的和，参数为流，返回他们的和，这其实就是要求我们重复实现类库中的sum()方法，那系列第二篇提到的reduce操作里面已经提到了，类库中的sum,max,min等一系列操作都是用reduce完成的，因此在这里我们也可以用reduce进行操作。参考代码如下
    public static int addUp(Stream<Integer> numbers){
        reutrn numbres.reduce(0,(x,y) -> x+y);
    }
    
编写一个函数，参数为艺术家集合，返回一个字符串集合，其中包含了艺术家的姓名与国籍。
这道题参数为艺术家集合，返回值为字符串，那么显然这是涉及到映射map的操作，但是要将一个艺术家元素映射成两个字符串（分别是这个艺术家的姓名与这个艺术家的国籍），那么就要涉及到flatMap了,flatMap方法可以用Stream替换值，然后在将多个Stream链接成一个Stream，对于本题来说，使用艺术家姓名与艺术家国籍的流替换掉艺术家，再将这些流链接到一起即可。
更具体一点的说法，假设有2个艺术家，分别是来自中国的张三与来自美国的jack，那么直接使用map映射返回的结果是2个流，格式大约为这样：[张三,中国],[jack，美国]，使用flatMap就可以将这两个流合并,于是格式就变成了[张三，中国，jack，美国]，符合题目中的返回一个字符串集合。
参考代码如下

    public static List<String> getArtistNamesAndNations(List<Artist> artists){
        return artists.stream()
                .flatMap(artist -> Stream.of(artist.getName(),artist.getNationality()))
                .collect(toList());
    }
    
注:这里最后一行要说明一下，事实上标准的写法应该是collect(Collectors.toList())，但是因为这个方法十分常用，所以在类开头导入了静态方法
import static java.util.stream.Collectors.toList
这是来自于java5的特性，可以方便静态属性与方法的调用。


编写一个函数，参数为专辑集合，返回一个由 最多包含3首歌曲的专辑 组成的集合。(专辑类名为Album，里面包含了一个返回本专辑所有歌曲的集合的方法getTrackList())
这道题很明显参数为专辑，返回值也是专辑，中间多了一个条件，因此使用过滤器即可。参考代码如下

    public static List<Album> getAlbumsWithMostThreeTracks(List<Album> albums){
        return albums.stream()
                     .filter(album -> album.getTrackList().size <= 3)
                     .collect(toList());
    }
    
Question 2 (关于迭代)

修改下面的代码，将外部迭代转换成内部迭代

为了保证不理解错误，说明一下,艺术家有的是个人，有的是团体或者乐队,getMembers()方法是获得该艺术家团体的所有人数，如果是个人艺术家,人数自然就是一个人。
并且注意下面这段代码的最后一行有效行,members使用了count()方法来计数，因此该方法的返回值是一个stream流，想要获得数量要用count()方法。
    
    int totalMembers = 0;
    for(Artist artist: artists){
        Stream<Artist> members = artist.getMembers();
        totalMembers += members.count();
    }
    
附:getMembers方法

    public Stream<Artist> getMembers() {
        return members.stream();
    }
Question 2 的参考思路

不拘泥于具体实现方式，首先了解清楚这段代码想做什么，有几步。这段代码想做的就是统计出所有艺术家的人数，因为有的艺术家是团体，有的艺术家是个人，个人的艺术家就算一个人，团体的话就统计出该团体的所有人数。那么相应的步骤就显而易见了。
第一步统计出每个艺术家个人或者团体的人数，第二步将他们求和。对于流操作第一步是一个惰性求值,首先将艺术家映射成他们的团体人数,第二步是一个及早求值，对这些人数进行求和。
参考代码如下


public static int countAllArtistMembers(List<Aritst> artists){
    return artists.stream()
                  .map(artist -> artist.getMembers().count())
                  .reduce(0,Integer::sum);
}

当然目前还没有介绍一些属于高阶流的例如收集器等，事实上下面这段使用数值流的代码看起来更舒服一些，阅读性也更强


public static int countAllArtistMembers(List<Aritst> artists){
    return artists.stream()
                  .mapToInt(artist -> artist.getMembers().count())//统计人数,转换成数值流
                  .sum();//对人数求和
                  
Question 3 (判断求值方式)

-依据以下两个方法的方法签名，判断下面两个方法是属于惰性求值还是及早求值
1.boolean anyMatch(Predicate<? super T> predicate);
2.Stream limit(long maxSize);
注:第一题的Predicate是java8的函数接口，看不明白并没有关系，后面会进行介绍，题目与之也没有什么联系。

Question 3 的参考思路

之前系列博客上一篇将惰性求值与及早求值的区别已经介绍过，这两者最大的区别的就是惰性求值返回的值是Stream，而及早求值是直接返回具体的数据，那么结果就显而易见了
第一个方法返回了具体的boolean数值，第二个方法返回的则是Steam流，因此第一个方法属于及早求值，第二个方法属于惰性求值。

Question 4 （字符串练习题两道）

计算一个字符串中小写字母的个数
注:可以参考Java8 string中新引入的chars方法

在一个字符串集合中，找出包含最多小写字母的字符串。

对于还没有熟悉流操作的同学来说，第一反应思路是for循环,if语句之类的，其实这很正常，那么不妨试着先写出你第一反应程序的伪代码，接着再使用流操作对它进行重构，接着再想一想重构的过程中，提取出真正的几步需求，也就是我要干什么，总共有几步，分解成流操作可以怎么样，是过滤器？还是收集器？又还是映射？还是比较器？，慢慢的这样思考，相信以后工作中碰到相应的问题第一反应就会是流操作啦:)

Question 4 的参考思路

计算一个字符串中小写字母的个数
首先引入一段java8 String类的chars方法的api

    /**
     * Returns a stream of {@code int} zero-extending the {@code char} values
     * from this sequence.  Any char which maps to a <a
     * href="{@docRoot}/java/lang/Character.html#unicode">surrogate code
     * point</a> is passed through uninterpreted.
     *
     * <p>If the sequence is mutated while the stream is being read, the
     * result is undefined.
     *
     * @return an IntStream of char values from this sequence
     * @since 1.8
     */
    public default IntStream chars() 
    
简单点说呢，这个方法返回一个由string里字符组成的数值流，数值流，当然也是属于流的一种了。
那么这个问题的思路是这样的，第一步获得所有字母的流，这点通过chars方法已经得到了，第二步选出所有的小写字母,这点的话使用过滤器filter也可以轻松做到（也就是第一反应的if），第三部统计个数，这个就不用说了吧,count()方法搞定。
下面是参考代码

    public class Question4 {
        public static int countStringLowercaseLetters(String string){
            return string.chars()//获得字母流
                         .filter(Character::isLowerCase)//筛选出所有小写字母
                         .count;//统计数量
        }
    }
这里注意到参考代码里给了一个类，那是因为后面一道题要用到这个方法，为了减少代码量，这里给一个类，方便到时候引用。

在一个字符串集合中，找出包含最多小写字母的字符串。
同样的理清思路，第一步统计出每一个字符串小写字母的数量（这一步上一题已经做完了）并将他们映射成数值流，第二步选出这些数值中最大的。
这里依旧给两个参考代码，一个是使用了Comparator比较器，返回的是optional对象,使用get()方法获得里面的值，另一个是直接映射成数值流（个人推荐第二种啦）

使用比较器

    public static Optional<String> mostLowcaseLetters(List<String> strings){
        return strings.stream()
                      .max(Comparator.comparing(Question4::countStringLowercaseLetters));
    } 
    
使用数值流

    public static int mostLowcaseLetters(List<String> strings){
        return strings.stream()
                      .mapToInt(Question4::countStringLowercaseLetters))
                      .max();
    }
    
思考题

试着只用reduce和Lambda表达式写出实现Stream上的filter操作的代码
试着只用reduce和Lambda表达式写出实现Stream上的map操作的代码
注:使用collect收集器会更容易一些，不过目前本系列博客还没有介绍。当然只是用reduce与lambda也是完全可以做到的，可以试着看一看java8 filter与map的源码。

思考题答案放在下一篇博客里，当然实现的方式有很多，这里所有的答案都只是一个参考，仅提供一种思路。