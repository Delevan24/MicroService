package com.eternity.microservice.base.dontlook;

public class HashMapRead {
	
	/*
	
	transient Node<K,V>[] table;　　　　　　　 //HashMap的哈希桶数组，非常重要的存储结构，用于存放表示键值对数据的Node元素。
	
	transient Set<Map.Entry<K,V>> entrySet;  //HashMap将数据转换成set的另一种存储形式，这个变量主要用于迭代功能。
	
	transient int size;　　　　　　　　　　　　　//HashMap中实际存在的Node数量，注意这个数量不等于table的长度，甚至可能大于它，因为在table的每个节点上是一个链表（或RBT）结构，可能不止有一个Node元素存在。
	
	transient int modCount;　　　　　　　　　　 //HashMap的数据被修改的次数，这个变量用于迭代过程中的Fail-Fast机制，其存在的意义在于保证发生了线程安全问题时，能及时的发现（操作前备份的count和当前modCount不相等）并抛出异常终止操作。
	
	int threshold;　　　　　　　　　　　　　　　　//HashMap的扩容阈值，在HashMap中存储的Node键值对超过这个数量时，自动扩容容量为原来的二倍。
	
	final float loadFactor;　　　　　　　　　　　//HashMap的负载因子，可计算出当前table长度下的扩容阈值：threshold = loadFactor * table.length。
	
	
	显然，HashMap的底层实现是基于一个Node的数组，那么Node是什么呢？在HashMap的内部可以看见定义了这样一个内部类：
	
	static class Node<K,V> implements Map.Entry<K,V> {
	　　final int hash;
	　　final K key;
		V value;
		Node<K,V> next;
		
		Node(int hash, K key, V value, Node<K,V> next) {
			this.hash = hash;
			this.key = key;
			this.value = value;
			this.next = next;
		}
		
		public final K getKey()        { return key; }
		public final V getValue()      { return value; }
		public final String toString() { return key + "=" + value; }
		
		public final int hashCode() {
			return Objects.hashCode(key) ^ Objects.hashCode(value);
		}
		
		public final V setValue(V newValue) {
			V oldValue = value;
			value = newValue;
			return oldValue;
		}
		
		public final boolean equals(Object o) {
			if (o == this)
				return true;
			if (o instanceof Map.Entry) {
				Map.Entry<?,?> e = (Map.Entry<?,?>)o;
				if (Objects.equals(key, e.getKey()) &&
						Objects.equals(value, e.getValue()))
					return true;
			}
			return false;
		}
	}
	
	
	我们大体看一下这个内部类就可以知道，它实现了Map.Entry接口。其内部的变量含义也很明确，hash值、key\value对和实现链表和红黑树所需要的指针索引。

　　既然知道了HashMap的基本结构，那么这些变量的默认值都是多少呢？我们再看一下HashMap定义的一些常量：


	//默认的初始容量为16，必须是2的幂次
	static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
	
	//最大容量即2的30次方
	static final int MAXIMUM_CAPACITY = 1 << 30;
	
	//默认加载因子
	static final float DEFAULT_LOAD_FACTOR = 0.75f;
	
	//当put一个元素时，其链表长度达到8时将链表转换为红黑树
	static final int TREEIFY_THRESHOLD = 8;
	
	//链表长度小于6时，解散红黑树
	static final int UNTREEIFY_THRESHOLD = 6;
	
	//默认的最小的扩容量64，为避免重新扩容冲突，至少为4 * TREEIFY_THRESHOLD=32，即默认初始容量的2倍
	static final int MIN_TREEIFY_CAPACITY = 64;
	
	
	TIP : 在HashMap内部定义的几个变量，包括桶数组本身都是transient修饰的，这代表了他们无法被序列化，而HashMap本身是实现了Serializable接口的。这很容易产生疑惑：HashMap是如何序列化的呢？查了一下源码发现，
	HashMap内有两个用于序列化的函数 readObject(ObjectInputStream s) 和 writeObject（ObjectOutputStreams），通过这个函数将table序列化。
 	HashMap 的 put 方法解析
　　以上就是我们对HashMap的初步认识，下面进入正题，看看HashMap是如何添加、查找与删除数据的。

　　首先来看put方法，我尽量在每行都加注释阐明这一行的含义，让阅读起来更容易理解。

	
	
	public V put(K key, V value) {
		return putVal(hash(key), key, value, false, true);
	}
	//这里onlyIfAbsent表示只有在该key对应原来的value为null的时候才插入，也就是说如果value之前存在了，就不会被新put的元素覆盖。evict参数用于LinkedHashMap中的尾部操作，这里没有实际意义。
　　final V putVal(int hash, K key, V value, boolean onlyIfAbsent, boolean evict) {

		//定义变量tab是将要操作的Node数组引用，p表示tab上的某Node节点，n为tab的长度，i为tab的下标。
		Node<K,V>[] tab; Node<K,V> p; int n, i;　　
		
		//判断当table为null或者tab的长度为0时，即table尚未初始化，此时通过resize()方法得到初始化的table。　　　　　　　　　　　　　　　　　　
		if ((tab = table) == null || (n = tab.length) == 0)　　
		
		//这种情况是可能发生的，HashMap的注释中提到：The table, initialized on first use, and resized as necessary。　                             　　　　　　　　　　　
		n = (tab = resize()).length;　
		
		//此处通过（n - 1） & hash 计算出的值作为tab的下标i，并另p表示tab[i]，也就是该链表第一个节点的位置。并判断p是否为null。　　　　　　　　　　　　　　　　　　　　　　
		if ((p = tab[i = (n - 1) & hash]) == null)
		
		    //当p为null时，表明tab[i]上没有任何元素，那么接下来就new第一个Node节点，调用newNode方法返回新节点赋值给tab[i]。
			tab[i] = newNode(hash, key, value, null);　　　
			　
		//下面进入p不为null的情况，有三种情况：p为链表节点；p为红黑树节点；p是链表节点但长度为临界长度TREEIFY_THRESHOLD，再插入任何元素就要变成红黑树了。	　　　　　　　　　　　　
        else {　
        
        　　//定义e引用即将插入的Node节点，并且下文可以看出 k = p.key。　　　　　　　　　　　　　　　　　　　　　　　　　　　
			Node<K,V> e; K k;　　
			
			//HashMap中判断key相同的条件是key的hash相同，并且符合equals方法。这里判断了p.key是否和插入的key相等，如果相等，则将p的引用赋给e。　　　　　　　　　　　　　　　　　　　　　　　　　　　　
			if (p.hash == hash &&
			
			//这一步的判断其实是属于一种特殊情况，即HashMap中已经存在了key，于是插入操作就不需要了，只要把原来的value覆盖就可以了。　　　　　　　　　　　　　　　　　　　　　　　　　　　　
			((k = p.key) == key || (key != null && key.equals(k))))
			
			//这里为什么要把p赋值给e，而不是直接覆盖原值呢？答案很简单，现在我们只判断了第一个节点，后面还可能出现key相同，所以需要在最后一并处理。
			e = p;　
			
			//现在开始了第一种情况，p是红黑树节点，那么肯定插入后仍然是红黑树节点，所以我们直接强制转型p后调用TreeNode.putTreeVal方法，返回的引用赋给e。　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
            else if (p instanceof TreeNode)
            	
            	//你可能好奇，这里怎么不遍历tree看看有没有key相同的节点呢？其实，putTreeVal内部进行了遍历，存在相同hash时返回被覆盖的TreeNode，否则返回null。
				e = ((TreeNode<K,V>)p).putTreeVal(this, tab, hash, key, value);
			
			//接下里就是p为链表节点的情形，也就是上述说的另外两类情况：插入后还是链表/插入后转红黑树。另外，上行转型代码也说明了TreeNode是Node的一个子类。
			else {　　　　
				
				//我们需要一个计数器来计算当前链表的元素个数，并遍历链表，binCount就是这个计数器。　　　　　　　　       　　　　　　　　
				for (int binCount = 0; ; ++binCount) {　
					
					//遍历过程中当发现p.next为null时，说明链表到头了，直接在p的后面插入新的链表节点，即把新节点的引用赋给p.next，插入操作就完成了。注意此时e赋给p。　　　　　　　　　　　　　　　
					if ((e = p.next) == null) {　
					
						//最后一个参数为新节点的next，这里传入null，保证了新节点继续为该链表的末端。　　　　　　　　　　　　　　　　　　　
						p.next = newNode(hash, key, value, null);
						
						//插入成功后，要判断是否需要转换为红黑树，因为插入后链表长度加1，而binCount并不包含新节点，所以判断时要将临界阈值减1。　　　　　　　　　　
						if (binCount >= TREEIFY_THRESHOLD - 1) // -1 for 1st 　
							
							//当新长度满足转换条件时，调用treeifyBin方法，将该链表转换为红黑树。　
							treeifyBin(tab, hash);
						
						//当然如果不满足转换条件，那么插入数据后结构也无需变动，所有插入操作也到此结束了，break退出即可。　　　　　　　　　　　　　　　　
						break;　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
					}
					
					 //在遍历链表的过程中，我之前提到了，有可能遍历到与插入的key相同的节点，此时只要将这个节点引用赋值给e，最后通过e去把新的value覆盖掉就可以了。
					if (e.hash == hash &&
					
					//老样子判断当前遍历的节点的key是否相同。　　　　　　　　　　　　　　　　　　　　　　　　
					((k = e.key) == key || (key != null && key.equals(k))))
					
					//找到了相同key的节点，那么插入操作也不需要了，直接break退出循环进行最后的value覆盖操作。　　
					break;
					
					//在第21行我提到过，e是当前遍历的节点p的下一个节点，p = e 就是依次遍历链表的核心语句。每次循环时p都是下一个node节点。　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
					p = e;　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
				}
			}
			
			//左边注释为jdk自带注释，说的很明白了，针对已经存在key的情况做处理。
			if (e != null) { // existing mapping for key　
				
				//定义oldValue，即原存在的节点e的value值。　　　　　　　　　　　　　　　
				V oldValue = e.value;
				
				//前面提到，onlyIfAbsent表示存在key相同时不做覆盖处理，这里作为判断条件，可以看出当onlyIfAbsent为false或者oldValue为null时，进行覆盖操作。　　　　　　　　　　　　　　　　　　　　　　　　　　　
				if (!onlyIfAbsent || oldValue == null)
				
				//覆盖操作，将原节点e上的value设置为插入的新value。　　　　　　　　　　　　　　　　　
				e.value = value;
				
				//这个函数在hashmap中没有任何操作，是个空函数，他存在主要是为了linkedHashMap的一些后续处理工作。　　　　　　　　　　　　　　　　　　　　　　　　
				afterNodeAccess(e);
				
				//这里很有意思，他返回的是被覆盖的oldValue。我们在使用put方法时很少用他的返回值，甚至忘了它的存在，这里我们知道，他返回的是被覆盖的oldValue。　　　　　　　　　　　　　　　　　　　　　　　　　　　　
				return oldValue;　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
			}
		}　
		
		//收尾工作，值得一提的是，对key相同而覆盖oldValue的情况，在前面已经return，不会执行这里，所以那一类情况不算数据结构变化，并不改变modCount值。　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
		++modCount;
		
		//同理，覆盖oldValue时显然没有新元素添加，除此之外都新增了一个元素，这里++size并与threshold判断是否达到了扩容标准。　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
		if (++size > threshold)　
		
		//当HashMap中存在的node节点大于threshold时，hashmap进行扩容。　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
		resize();
		
		//这里与前面的afterNodeAccess同理，是用于linkedHashMap的尾部操作，HashMap中并无实际意义。1　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
		afterNodeInsertion(evict);　
		
		//最终，对于真正进行插入元素的情况，put函数一律返回null。　　　　　　　　　　　　　　　　　　　　　　　　　　　　
		return null;　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　   　　　
	}
	
	
	
	
	
	
	
	
	
	在上述代码中的第十行，HashMap根据 (n - 1) & hash 求出了元素在node数组的下标。这个操作非常精妙，下面我们仔细分析一下计算下标的过程，主要分三个阶段：计算hashcode、高位运算和取模运算。

　　首先，传进来的hash值是由put方法中的hash(key)产生的（上述第2行），我们来看一下hash()方法的源码：
	
	static final int hash(Object key) {
         int h;
         return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
     }
	
	
	这里通过key.hashCode()计算出key的哈希值，然后将哈希值h右移16位，再与原来的h做异或^运算——这一步是高位运算。设想一下，如果没有高位运算，那么hash值将是一个int型的32位数。而从2的-31次幂到2的31次幂之间，有将近几十亿的空间，如果我们的HashMap的table有这么长，内存早就爆了。所以这个散列值不能直接用来最终的取模运算，而需要先加入高位运算，将高16位和低16位的信息"融合"到一起，也称为"扰动函数"。这样才能保证hash值所有位的数值特征都保存下来而没有遗漏，从而使映射结果尽可能的松散。最后，根据 n-1 做与操作的取模运算。这里也能看出为什么HashMap要限制table的长度为2的n次幂，因为这样，n-1可以保证二进制展示形式是（以16为例）0000 0000 0000 0000 0000 0000 0000 1111。在做"与"操作时，就等同于截取hash二进制值得后四位数据作为下标。这里也可以看出"扰动函数"的重要性了，如果高位不参与运算，那么高16位的hash特征几乎永远得不到展现，发生hash碰撞的几率就会增大，从而影响性能。

　　HashMap的put方法的源码实现就是这样了，整理思路非常连贯。这里面有几个函数的源码（比如resize、putTreeValue、newNode、treeifyBin）限于篇幅原因，就不贴了，后面应该还会更新在其他博客里，有兴趣的同学也可以自己挖掘一下。

	HashMap 的 get 方法解析
　　 读完了put的源码，其实已经可以很清晰的理清HashMap的工作原理了。接下来再看get方法的源码，就非常的简单：



	public V get(Object key) {
        Node<K,V> e;
        
        //根据key及其hash值查询node节点，如果存在，则返回该节点的value值。
        return (e = getNode(hash(key), key)) == null ? null : e.value;　　　　　
    }

	
	//根据key搜索节点的方法。记住判断key相等的条件：hash值相同 并且 符合equals方法。
    final Node<K,V> getNode(int hash, Object key) {　　　　　　　　　　　　　　　　
        Node<K,V>[] tab; Node<K,V> first, e; int n; K k;
        
        //根据输入的hash值，可以直接计算出对应的下标（n - 1）& hash，缩小查询范围，如果存在结果，则必定在table的这个位置上。
        if ((tab = table) != null && (n = tab.length) > 0 && (first = tab[(n - 1) & hash]) != null) {
            if (first.hash == hash && // always check first node
            
            	//判断第一个存在的节点的key是否和查询的key相等。如果相等，直接返回该节点。
                ((k = first.key) == key || (key != null && key.equals(k))))　　　
                return first;
            
            //遍历该链表/红黑树直到next为null。
            if ((e = first.next) != null) {　　　　　　　　　　　　　　　　　　　　　　
                if (first instanceof TreeNode)　　
                　　
                 	//当这个table节点上存储的是红黑树结构时，在根节点first上调用getTreeNode方法，在内部遍历红黑树节点，查看是否有匹配的TreeNode。
                    return ((TreeNode<K,V>)first).getTreeNode(hash, key);
                do {
                
                	//当这个table节点上存储的是链表结构时，用跟第11行同样的方式去判断key是否相同。
                    if (e.hash == hash && ((k = e.key) == key || (key != null && key.equals(k))))
                        return e;
                        
                  //如果key不同，一直遍历下去直到链表尽头，e.next == null。
                } while ((e = e.next) != null);　　　　　　　　　　　　　　　　　　  　
            }
        }
        return null;
    }
	
	
	因为查询过程不涉及到HashMap的结构变动，所以get方法的源码显得很简洁。核心逻辑就是遍历table某特定位置上的所有节点，分别与key进行比较看是否相等。

　　以上便是HashMap最常用API的源码分析，除此之外，HashMap还有一些知识需要重点学习：扩容机制、并发安全问题、内部红黑树的实现。这些内容我也会在之后陆续发文分析，希望可以帮读者彻底理解HashMap的原理。.




	
	 final Node<K,V>[] resize() {
	 
	 	//首次初始化后table为Null
        Node<K,V>[] oldTab = table;
        int oldCap = (oldTab == null) ? 0 : oldTab.length;
        
        //默认构造器的情况下为0
        int oldThr = threshold;
        int newCap, newThr = 0;
        
        //table扩容过
        if (oldCap > 0) {
        
             //当前table容量大于最大值得时候返回当前table
             if (oldCap >= MAXIMUM_CAPACITY) {
                threshold = Integer.MAX_VALUE;
                return oldTab;
            }
            else if ((newCap = oldCap << 1) < MAXIMUM_CAPACITY &&
                     oldCap >= DEFAULT_INITIAL_CAPACITY)
            //table的容量乘以2，threshold的值也乘以2           
            newThr = oldThr << 1; // double threshold
        }
        else if (oldThr > 0) // initial capacity was placed in threshold
        //使用带有初始容量的构造器时，table容量为初始化得到的threshold
        newCap = oldThr;
        else {  //默认构造器下进行扩容
             // zero initial threshold signifies using defaults
            newCap = DEFAULT_INITIAL_CAPACITY;
            newThr = (int)(DEFAULT_LOAD_FACTOR * DEFAULT_INITIAL_CAPACITY);
        }
        if (newThr == 0) {
        //使用带有初始容量的构造器在此处进行扩容
            float ft = (float)newCap * loadFactor;
            newThr = (newCap < MAXIMUM_CAPACITY && ft < (float)MAXIMUM_CAPACITY ?
                      (int)ft : Integer.MAX_VALUE);
        }
        threshold = newThr;
        @SuppressWarnings({"rawtypes","unchecked"})
            Node<K,V>[] newTab = (Node<K,V>[])new Node[newCap];
        table = newTab;
      if (oldTab != null) {
        //对新扩容后的table进行赋值，条件中的代码删减
        }
        return newTab;
	}


	*/
	
	
	
	
}
