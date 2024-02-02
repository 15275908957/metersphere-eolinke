package Lincense;

import com.alibaba.fastjson2.JSON;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.JdkSerializationRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class CheckLicense {

    public static Map<String,String> getEnv(){
        Map<String,String> envMap = System.getenv();
        return envMap;
    }

    public static String getEnvStr(){
        Map<String,String> envMap = getEnv();
        return JSON.toJSONString(envMap);
    }

    public static String getString(String str){
        return str + "}}";
    }

    public static RedisTemplate<String, String> loadRedis(){
        Map<String,String> envMap = getEnv();
        String redisHost = envMap.get("REDIS_HOST");
        String redisPort = envMap.get("REDIS_PORT");
        String redisPasswrod = envMap.get("REDIS_PASSWORD");

        RedisTemplate<String, String> template = new RedisTemplate<>();
        //单机模式
        JedisConnectionFactory fac = new JedisConnectionFactory();
        fac.setHostName(redisHost);
        fac.setPassword(redisPasswrod);
        fac.setPort(Integer.parseInt(redisPort));

        fac.afterPropertiesSet();
        template.setDefaultSerializer(new StringRedisSerializer());
        template.setConnectionFactory(fac);
        template.afterPropertiesSet();

        RedisSerializer stringSerializer = new JdkSerializationRedisSerializer();
        template.setKeySerializer(stringSerializer);
        return template;
    }

    public static String getLicense(){
        RedisTemplate<String, String> template = loadRedis();
        String license = template.opsForValue().get("LICENSE");
        return license;
    }

    public static String getValue(String key){
        RedisTemplate<String, String> template = loadRedis();
        String license = template.opsForValue().get(key);
        return license;
    }

    public static void setValue(String key, String value, Long time,TimeUnit unit ){
        RedisTemplate<String, String> template = loadRedis();
        template.opsForValue().set(key, value, time, unit);
    }

    public static void checkLicenseByRedis(){
        String license = getLicense();
        if(license.indexOf("valid") == -1){
            System.out.println("许可过期");
            throw new RuntimeException("许可校验失败");
        }
    }

    public static long getTimeByDateStr(String dateStr) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date apiTime = format.parse(dateStr);
            return apiTime.getTime();
        } catch (Exception e) {

        }
        return 0l;
    }


}
