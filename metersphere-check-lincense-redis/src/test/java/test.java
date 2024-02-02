//import SpringConfig.AppConfig;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.ApplicationContext;
//import org.springframework.context.support.ClassPathXmlApplicationContext;
//import org.springframework.data.redis.connection.RedisNode;
//import org.springframework.data.redis.connection.RedisSentinelConfiguration;
//import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
//import org.springframework.data.redis.core.RedisTemplate;
//import org.springframework.data.redis.core.StringRedisTemplate;
//import org.springframework.data.redis.core.types.RedisClientInfo;
//import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
//import org.springframework.data.redis.serializer.RedisSerializer;
//import org.springframework.test.context.ContextConfiguration;
//import org.springframework.test.context.ContextLoader;
//import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
//import redis.clients.jedis.Jedis;
//import redis.clients.jedis.JedisPoolConfig;
//
//import java.util.Set;
//
//@RunWith(SpringJUnit4ClassRunner.class)
//@ContextConfiguration(classes = AppConfig.class)
//public class test {
//    @Autowired
//    private RedisTemplate<String, String> redisTemplate;
//
//    public static void main(String[] args) {
////        ApplicationContext ctx = new ClassPathXmlApplicationContext("applicationContext.xml");
////        ApplicationContext tt = new ClassPathXmlApplicationContext();
//    }
//
////        private void connectRedis(){
////            RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
////            redisStandaloneConfiguration.setHostName("127.0.0.1");
////            redisStandaloneConfiguration.setDatabase(8);
////            redisStandaloneConfiguration.setPort(6379);
////            redisStandaloneConfiguration.setPassword("Fullsee@123");
////
////            LettuceClientConfiguration.LettuceClientConfigurationBuilder lettuceClientConfigurationBuilder = LettuceClientConfiguration.builder();
////            LettuceConnectionFactory factory = new LettuceConnectionFactory(redisStandaloneConfiguration,
////                    lettuceClientConfigurationBuilder.build());
////            factory.afterPropertiesSet();
////            RedisTemplate<String, String> redisTemplate = new RedisTemplate<>();
////            redisTemplate.setConnectionFactory(factory);
////            redisTemplate.afterPropertiesSet();
////            redisTemplate.setHashKeySerializer(new StringRedisSerializer());
////            redisTemplate.setHashValueSerializer(new StringRedisSerializer());
////            redisTemplate.setKeySerializer(new StringRedisSerializer());
////            redisTemplate.setValueSerializer(new StringRedisSerializer());
////
////            System.out.println(redisTemplate.getClientList());
////            Object o = redisTemplate.opsForHash().get("ALARM_LEVEL_MAPPING:around","0001");
////            System.out.println(o);
////        }
//
//    //测试代码区
//    @Test
//    public void test0() {
//        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//        jedisPoolConfig.setTestOnBorrow(true);
//        jedisPoolConfig.setMaxIdle(300);
//        jedisPoolConfig.setMinIdle(100);
//
//        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
//        jedisConnectionFactory.setHostName("10.1.14.202");
//        jedisConnectionFactory.setPassword("Password123@redis");
//        jedisConnectionFactory.setPort(6379);
//        jedisConnectionFactory.setPoolConfig(jedisPoolConfig);
//
//        StringRedisTemplate stringRedisTemplate = new StringRedisTemplate();
//        stringRedisTemplate.setConnectionFactory(jedisConnectionFactory);
//        ClassLoader classLoader = AppConfig.class.getClassLoader();
//
//        stringRedisTemplate.setBeanClassLoader(classLoader);
//        stringRedisTemplate.afterPropertiesSet();
//
//        RedisSentinelConfiguration j = new RedisSentinelConfiguration();
//        RedisNode redisNode = new RedisNode("10.1.14.202", 6379);
//        j.addSentinel(redisNode);
////        Set<String> ttt = stringRedisTemplate.keys("*");
////        stringRedisTemplate.setBeanClassLoader();
//        System.out.println(redisTemplate);
//
////        System.out.println(stringRedisTemplate.isExposeConnection());
//
//        Jedis jedis = new Jedis("10.1.14.202", 6379);
//        jedis.auth("Password123@redis");
//        RedisSerializer stringSerializer = new JdkSerializationRedisSerializer();
//
//        byte[] tt = stringSerializer.serialize("LICENSE");
//
////        String key = stringSerializer.deserialize(tt).toString();
//        String key = new String(tt);
//        System.out.println(jedis.isConnected());
//
//        String a = jedis.get(key);
//        Set<String> tset = jedis.keys("*");
//
//        System.out.println(a);
////
////        stringRedisTemplate.afterPropertiesSet();
////
////        System.out.println(jedis.isConnected());
////        System.out.println(stringRedisTemplate.isExposeConnection());
//
////        redisConnection.get
//        //存入数据
////        Set<String> a = stringRedisTemplate.keys("*");
////        System.out.println(a);
////        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
//
////        RedisSerializer t = new StringRedisSerializer();
////        byte[] ww = stringSerializer.serialize("\\xAC\\xED\\x00\\x05t\\x00\\x07LICENSE");
////        System.out.println(t.deserialize(ww));
//        redisTemplate.setKeySerializer(stringSerializer);
////        redisTemplate.
//        //设置序列化Value的实例化对象
////        redisTemplate.setValueSerializer(t);
////        redisTemplate.opsForValue().setBit("bit", 0 , true);
////        redisTemplate.afterPropertiesSet();
////        redisTemplate.opsForValue().set("123name一", "eee");
//
////        byte[] ttt = stringSerializer.serialize("*");
////        Object aww = stringSerializer.deserialize(ttt);
////        Set<String> a = redisTemplate.keys("*");
////        stringSerializer.deserialize();
//        String license = redisTemplate.opsForValue().get("LICENSE");
//
//        System.out.println(license);
//
////        for(String item : a){
////            System.out.println(redisTemplate.opsForValue().get(item));
////            byte[] bytes = t.serialize(item);
////            String b = new String(bytes,StandardCharsets.UTF_8);
////            System.out.println(b);
////        }
////        redisTemplate.opsForValue().
//        //查询数据
////        String name = (String) redisTemplate.opsForValue().get("name");
////        System.out.println(name);
//    }
//
//}

import Lincense.CheckLicense;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class test{
    @Test
    public void aa (){
        String a = "800.00";
        System.out.println(Double.parseDouble(a));
//        List a = new ArrayList<>();
//        System.out.println(CheckLicense.getEnv());;
    }
}