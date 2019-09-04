package cn.zzs.springdata.test;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ListOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.test.context.junit4.SpringRunner;

import cn.zzs.springdata.pojo.User;

/**
 * 测试redis
 * @author zzs
 *
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class RedisTest {
	@Autowired
	@Qualifier("redisTemplate")
	private RedisTemplate<String, Object> redisTemplate;

	Logger logger = LoggerFactory.getLogger(RedisTest.class);

	private User user1 = new User("zzs001", 18, new Date(), new Date());
	private User user2 = new User("zzs002", 19, new Date(), new Date());
	private User user3 = new User("zzs003", 20, new Date(), new Date());
	private User user4 = new User("zzs004", 21, new Date(), new Date());
	private User user5 = new User("zzs005", 22, new Date(), new Date());

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
}
