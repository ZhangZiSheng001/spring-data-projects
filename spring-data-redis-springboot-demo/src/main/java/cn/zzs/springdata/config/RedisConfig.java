package cn.zzs.springdata.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

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
