package utilsss;

import com.alibaba.fastjson2.JSONObject;

import java.io.*;
import java.util.Properties;


public class PropertiesUtils {

    private static PropertiesUtils inst = null;
    private PropertiesUtils() {}
    public static PropertiesUtils getInst() {
        if(inst == null){
            inst = new PropertiesUtils();
            loadMap();
        }
        return inst;
    }
    private static Properties prop = null;

    public static String getValue(String key){
        return prop.getProperty(key);
    }

    public static String getKeyAll(){
        return JSONObject.toJSONString(prop.keySet());
    }

    private static void loadMap() {
        try {
            String path = System.getProperty("user.dir")+"/loninMS.properties";
            InputStream inputStream = new BufferedInputStream(new FileInputStream(new File(path))); //方法1
            prop = new Properties();
            prop.load(new InputStreamReader(inputStream));
        } catch (FileNotFoundException e) {
            System.out.println("properties文件路径有误！");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
