package Lincense;

import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


public class test {
    public static RedisTemplate<String, String> loadRedis(){

        RedisTemplate<String, String> template = new RedisTemplate<>();
        //单机模式
        JedisConnectionFactory fac = new JedisConnectionFactory();
        fac.setHostName("127.0.0.1");
        fac.setPassword("feizhiyun");
        fac.setPort(6379);

        fac.afterPropertiesSet();
        template.setDefaultSerializer(new StringRedisSerializer());
        template.setConnectionFactory(fac);
        template.afterPropertiesSet();

        RedisSerializer stringSerializer = new JdkSerializationRedisSerializer();
        template.setKeySerializer(stringSerializer);
        return template;
    }

    public static void main(String[] args) {
        RedisTemplate<String, String> template = loadRedis();
        System.out.println(template.opsForValue().get("LICENSE"));
    }
}
