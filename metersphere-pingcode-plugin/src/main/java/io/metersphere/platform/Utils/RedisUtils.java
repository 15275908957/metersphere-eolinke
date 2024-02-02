package io.metersphere.platform.Utils;

//import redis.clients.jedis.Jedis;
import java.util.Map;

public class RedisUtils {
    private static RedisUtils redisUtils;
//    private static Jedis jedis;
    private static String client_id;
    private static String client_Secret;

    private RedisUtils (){

    }

    public static RedisUtils getRedisUtils(String id, String secret){
        if(redisUtils == null){
            client_id = id;
            client_Secret = secret;
            redisUtils = new RedisUtils();
        }
//        loadJedis();
        return redisUtils;
    }

//    private static void loadJedis(){
//        Map<String, String> envMap = System.getenv();
//        //本地测试数据
//        String redisHost = envMap.get("REDIS_HOST");
//        String redisPort = envMap.get("REDIS_PORT");
//        String redisPasswrod = envMap.get("REDIS_PASSWORD");
//        try{
//            jedis = new Jedis(redisHost, Integer.parseInt(redisPort));
//            jedis.auth(redisPasswrod);
//        }catch (Exception e) {
//            //本地测试数据
//            jedis = new Jedis("127.0.0.1", 6379);
//            jedis.auth("fit2cloud");
//            e.printStackTrace();
//        }
//    }

    private String getKey(){
        return client_id+"_"+client_Secret;

    }

//    public String getToken(){
//        try {
//            System.out.println("获取token key " + getKey());
//        }catch (Exception e){
//
//        }finally {
//            jedis.close();
//        }
//        return jedis.get(getKey());
//    }
//
//    public void setToken(String token, int time){
//        // time 秒
//        try {
//
//        }catch (Exception e){
//            System.out.println("添加token key : " + getKey() + " token : " + token + " time : " + time);
//            jedis.setex(getKey(), time, token);
//        }finally {
//            jedis.close();
//        }
//
//    }

}
