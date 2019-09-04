# Spring Data Redis

## 简介
`SpringData`提供了针对数据库（包括`SQL`和`NOSQL`）的整合方案，对`Hibernate` `JPA`、`Jedis`等工具的api进行高级的封装，为我们提供简单方便地操作接口。  

`Spring Data Redis` 是`Spring Data`的项目下的一个模块。整合了`jedis`，并对API进行了封装，让我们可以较方便地操作`redis`。  

学习`Spring Data Redis`主要包括以下内容：  

1. 单机和集群的配置  

2. 使用`RedisTemplate`操作分别操作不同的类型数据：`string`、`list`、`set`、`sorted set`、`hash`.  

3. 使用`RedisTemplate`操作事务。  

项目测试中发现两个比较大的坑：  

1. **Spring Data Redis 涉及到返回Boolean的方法，不一定只是返回true或者false，还会返回null；**  

2. **事务过程中，读可以马上执行，而写需要提交后一起执行。所以会出现事务中存入值，但是读出来是null的情况。**  


## 项目实现的需求

1. 使用`Spring Boot`整合`Spring Data Redis`，以`json`格式存取不同类型数据：`string`、`list`、`set`、`sorted set`、`hash`  

2. 测试`Spring Data Redis`的事务管理。  

## 项目工程环境

JDK：1.8.0_201  

maven：3.6.1  

IDE：Spring Tool Suites4 for Eclipse：4.12   

Redis：3.2.100（windows版）  

Spring Data Redis：2.1.10.RELEASE  

Spring Boot：2.1.7.RELEASE  

## 引入依赖
```xml
<parent>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-parent</artifactId>
	<version>2.1.7.RELEASE</version>
	<relativePath /> <!-- lookup parent from repository -->
</parent>

<properties>
	<java.version>1.8</java.version>
</properties>

<dependencies>
   <!-- springbootredis启动器 -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-data-redis</artifactId>
	</dependency>
	<!-- jackson -->
	<dependency>
		<groupId>com.fasterxml.jackson.core</groupId>
		<artifactId>jackson-databind</artifactId>
	</dependency>
	<!-- 测试 -->
	<dependency>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-test</artifactId>
		<scope>test</scope>
	</dependency>
</dependencies>
```

## application.properties的配置

这里主要注意2个要点：  

1. 序列化器：一般有以下5种。默认是采用`StringRedisSerializer`，这种序列器不支持对象存储，使用过程中需要手动抓换序列化器。所以我统一把value的序列化器都改成`GenericJackson2JsonRedisSerializer`  

|Serializer实现	 |描述            |
| -------------- | -------------- |
|StringRedisSerializer	|String/byte[]转换，速度快|
|JdkSerializationRedisSerializer	|JDK自带序列化|
|OxmSerializer	|XML序列化，占空间，速度慢|
|Jackson2JsonRedisSerializer	|JSON序列化，需要定义JavaType|
|GenericJackson2JsonRedisSerializer	|JSON序列化，无需定义JavaType|

2. 事务配置。这里必须开启事务，如果不配置也行，但使用时需要频繁地手动地开启。 

而这两个在`application.properties`里好像都配置不了。所以我选择创建一个`RedisConfig`来配置。  

```properties
#############基本参数#############
# ip地址
spring.redis.host=127.0.0.1
# 端口号
spring.redis.port=6379
# 密码
spring.redis.password=root
# Redis数据库索引（默认为0）  
spring.redis.database=0 
# 连接超时时间（毫秒）  
spring.redis.timeout=3000
#############资源数控制参数#############
# 资源池的最大连接数。默认值8
spring.redis.jedis.pool.max-active=8
# 资源池允许最大空闲连接数。默认值8。建议=maxTotal
spring.redis.jedis.pool.max-idle=8
# 资源池确保最少空闲连接数。默认0。
spring.redis.jedis.pool.min-idle=0
# 连接池最大阻塞等待时间（使用负值表示没有限制） 
spring.redis.jedis.pool.max-wait=-1
```
其实这里我们不配置，`SpringBoot`也会自动帮我们在容器中生成了一个`RedisTemplate`和一个`StringRedisTemplate`。其中`RedisTemplate`属于`<Object,Object>`类型，使用过程需要频繁类型转换。  

具体见：`org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration`
这里我们使用自定义`RedisTemplate`方式：  

```java
/**
 * 
 * @ClassName: RedisConfig
 * @Description: redis配置类,一般用于配置RedisTemplate
 * @author: zzs
 * @date: 2019年9月4日 下午7:49:06
 */
@Configuration
public class RedisConfig {
	/**
	 * 配置RedisTemplate
	 * @param factory
	 * @return
	 */
	@Bean
	@SuppressWarnings("all")
	public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory factory) {

		//创建RedisTemplate并设置连接工厂
		RedisTemplate<String, Object> template = new RedisTemplate<String, Object>();
		template.setConnectionFactory(factory);

		//创建序列化器
		GenericJackson2JsonRedisSerializer jackson2JsonRedisSerializer = new GenericJackson2JsonRedisSerializer();
		StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();

		// key采用String的序列化方式
		template.setKeySerializer(stringRedisSerializer);

		// value序列化方式采用jackson
		template.setValueSerializer(jackson2JsonRedisSerializer);

		// hash的key也采用String的序列化方式
		template.setHashKeySerializer(stringRedisSerializer);

		// hash的value序列化方式采用jackson
		template.setHashValueSerializer(jackson2JsonRedisSerializer);

		//设置支持事务
		template.setEnableTransactionSupport(true);
		return template;
	}
}
```

## 测试操作String类型数据
**Spring Data Redis 涉及到返回Boolean的方法，不一定只是返回true或者false，还会返回null，所以不能直接用于if判断** 

```java
/**
 * @Title: testKey
 * @Description: 测试操作String的方法
 * @author: zzs
 * @date: 2019年9月1日 下午9:30:43
 * @return: void
 * @throws Exception 
 */
@Test
public void testString() {
	//定义key
	String key = "redis-demo:testString";
	//key如果存在，先清除
	redisTemplate.delete(key);
	//获取String类型的操作对象
	ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
	//添加键值对
	opsForValue.set(key, user1);
	//修改键值对
	opsForValue.set(key, user2);
	//判断键值对是否存在
	System.err.println(redisTemplate.hasKey(key));
	//获取键值对
	System.err.println(opsForValue.get(key));
}
```


## 测试操作List类型数据
```java
/**
 * @Title: testList
 * @Description: 测试操作List的方法
 * @author: zzs
 * @date: 2019年9月1日 下午9:30:43
 * @return: void
 * @throws Exception 
 */
@Test
public void testList() {
	//定义key
	String key = "redis-demo:testlist";
	//key如果存在，先清除
	redisTemplate.delete(key);
	//获取List类型的操作对象
	ListOperations<String, Object> opsForList = redisTemplate.opsForList();
	//从左边批量添加元素
	opsForList.leftPushAll(key, user1, user2, user3, user4);
	//修改元素：只能根据索引删除
	opsForList.set(key, 0, user5);
	//从右边删除第一个元素：只能删除第一个或最后一个
	System.err.println("弹出并删除元素：" + opsForList.rightPop(key));
	//遍历元素
	List<Object> list = opsForList.range(key, 0, -1);
	Iterator<Object> iterator = list.iterator();
	while (iterator.hasNext()) {
		System.err.println(iterator.next());
	}
}
```

## 测试操作Set类型数据
```java
/**
 * @Title: testSet
 * @Description: 测试操作Set的方法
 * @author: zzs
 * @date: 2019年9月1日 下午9:30:43
 * @return: void
 * @throws Exception 
 */
@Test
public void testSet() {
	//定义key
	String key = "redis-demo:testSet";
	//key如果存在，先清除
	redisTemplate.delete(key);
	//获取Set类型的操作对象
	SetOperations<String, Object> opsForSet = redisTemplate.opsForSet();
	//添加元素
	opsForSet.add(key, user1, user2, user3, user4);
	//更新元素：不能更新元素
	//删除元素
	opsForSet.remove(key, user1);
	//判断元素是否存在
	System.err.println(opsForSet.isMember(key, user1));
	//遍历元素
	Set<Object> members = opsForSet.members(key);
	Iterator<Object> iterator = members.iterator();
	while (iterator.hasNext()) {
		System.err.println(iterator.next());
	}
}

```
## 测试操作Sorted Set类型数据
```java
/**
 * @Title: testSortedSet
 * @Description: 测试操作Sorted Set的方法
 * @author: zzs
 * @date: 2019年9月1日 下午9:30:43
 * @return: void
 * @throws Exception 
 */
@Test
public void testSortedSet() throws Exception {
	//定义key
	String key = "redis-demo:testSortedSet";
	//key如果存在，先清除
	redisTemplate.delete(key);
	//获取Sorted Set类型的操作对象
	ZSetOperations<String, Object> opsForZSet = redisTemplate.opsForZSet();
	//添加元素
	opsForZSet.add(key, user1, 1D);
	opsForZSet.add(key, user2, 3D);
	opsForZSet.add(key, user3, 4D);
	opsForZSet.add(key, user4, 2D);
	//更新元素
	opsForZSet.add(key, user5, 1D);
	//删除元素
	opsForZSet.remove(key, user3);
	//查找指定元素的score
	System.err.println(opsForZSet.score(key, user2));
	//查找指定指定元素的排名
	System.err.println(opsForZSet.rank(key, user2));
	//遍历元素
	Set<Object> set = opsForZSet.range(key, 0, -1);
	Iterator<Object> iterator = set.iterator();
	while (iterator.hasNext()) {
		System.err.println(iterator.next());
	}
}
```

## 测试操作Hash类型数据
```java
/**
 * @Title: testHash
 * @Description: 测试操作Hash的方法
 * @author: zzs
 * @date: 2019年9月1日 下午9:30:43
 * @return: void
 * @throws Exception 
 */
@Test
public void testHash() throws Exception {
	//定义key
	String key = "redis-demo:testHash";
	//key如果存在，先清除
	redisTemplate.delete(key);
	//获取hash类型的操作对象
	HashOperations<String, Object, Object> opsForHash = redisTemplate.opsForHash();
	//添加元素
	opsForHash.put(key, "name", user1.getName());
	opsForHash.put(key, "age", user1.getAge());
	opsForHash.put(key, "create", user1.getCreate());
	opsForHash.put(key, "modified", user1.getModified());
	//更新元素
	opsForHash.put(key, "age", 22);
	//删除元素
	opsForHash.delete(key, "modified");
	//获取指定元素的值
	System.err.println(opsForHash.get(key, "name"));
	//遍历元素
	Map<Object, Object> map = opsForHash.entries(key);
	for (Entry<Object, Object> entry : map.entrySet()) {
		System.err.println(entry.getKey() + "=" + entry.getValue());
	}
}
```

## 测试操作Key
```java
/**
 * @Title: testKey
 * @Description: 测试操作key的方法
 * @author: zzs
 * @date: 2019年9月1日 下午9:30:43
 * @return: void
 * @throws Exception 
 */
@Test
public void testKey() throws Exception {
	//定义key
	String key = "redis-demo:testKey";
	//key如果存在，先清除
	if (redisTemplate.hasKey(key)) {
		redisTemplate.delete(key);
	}
	//添加键值对
	redisTemplate.opsForValue().set(key, "test001");
	//设置key的过期时间
	redisTemplate.expire(key, 5, TimeUnit.SECONDS);
	Thread.sleep(3000);
	//查询key的剩余时间
	System.err.println(redisTemplate.getExpire(key, TimeUnit.SECONDS));
	//清除key的过期时间
	redisTemplate.persist(key);
	System.err.println(redisTemplate.getExpire(key, TimeUnit.SECONDS));
}
```

## 测试事务管理
**注意：事务过程中，读可以马上执行，而写需要提交后一起执行。所以会出现事务中存入值，但是读出来是null的情况。**  

```java
/**
 * @Title: testTransaction
 * @Description: 测试操作事务操作
 * @author: zzs
 * @date: 2019年9月1日 下午9:30:43
 * @return: void
 * @throws Exception 
 */
@Test
public void testTransaction() {
	//定义key
	String key = "redis-demo:testString";
	//开启对key修改的监视，如果事务执行期间key被修改，那么事务将不会提交。
	redisTemplate.watch(key);
	//开启事务
	redisTemplate.multi();
	try {
		//如果key存在先删除
		redisTemplate.delete(key);
		//获取String类型的操作对象
		ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
		//添加键值对
		opsForValue.set(key, user1);
		//判断键值对是否存在:null。这时key还没存进redis，而且竟然不是false
		System.err.println(redisTemplate.hasKey(key));
		//提交事务
		//int i = 2/0;
		redisTemplate.exec();
		//判断键值对是否存在:true。这时事务已经提交，所以可以找到。
		System.err.println(redisTemplate.hasKey(key));
	} catch (Exception e) {
		logger.error("测试jedis存取String类型出错，事务回滚", e);
		//回滚事务
		redisTemplate.discard();
	}
}
```

## 项目路径：   
[spring-data-redis-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-redis-demo)   

[spring-data-redis-springboot-demo](https://github.com/ZhangZiSheng001/spring-data-projects/tree/master/spring-data-redis-springboot-demo)   


> 学习使我快乐！！
