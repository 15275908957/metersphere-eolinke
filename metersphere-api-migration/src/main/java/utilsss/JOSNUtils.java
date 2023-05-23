package utilsss;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.micrometer.common.util.StringUtils;
import java.util.ArrayList;
import java.util.List;

public class JOSNUtils {

    public static String getArrayStrByStr(String str){
        String[] aa = str.split(",");
        String a = "";
        for(int i = 0 ; i < aa.length; i++){
            a += ",\""+aa[i]+"\"";
        }
        return "["+a.substring(1,a.length())+"]";
    }

    public static String getProjectID(String data ,String projectName){
        List<JSONArray> jsonArrayPro = JSON.parseArray(data, JSONArray.class);
        for(JSONArray item: jsonArrayPro){
            for(int i = 0 ; i < item.size(); i++){
                JSONObject jsonObject = JSONObject.parseObject(item.getString(i));
                String nameTemp = jsonObject.getString("name");
                if(StringUtils.isNotBlank(nameTemp) && nameTemp.equals(projectName)){
                    return jsonObject.getString("id");
                }
            }
        }
        return null;
    }

    public static String getModuleIDByArrayStr(String data, String names, String projectID){
        List<String> nameList = JSON.parseArray(names, String.class);
        List<String> ids = new ArrayList<>();
        for(String item : nameList){
            String id = getModuleID(data, item, projectID);
            ids.add(id);
        }
//        Collections.reverse(ids);
        return JSON.toJSONString(ids);
    }

    public static Object getModuleIDByArray(String data, String names, String projectID){
        List<String> nameList = JSON.parseArray(names, String.class);
        List<String> ids = new ArrayList<>();
        for(String item : nameList){
            String id = getModuleID(data, item, projectID);
            ids.add(id);
        }
//        Collections.reverse(ids);
        return JSON.toJSONString(ids);
    }



    public static String getModuleID(String data, String name, String projectID){
        List<JSONArray> jsonArray = JSON.parseArray(data, JSONArray.class);
        for(JSONArray item : jsonArray){
            for(int i = 0 ; i < item.size(); i++){
                JSONObject jsonObject = JSONObject.parseObject(item.getString(i));
                String projectIdTemp = jsonObject.getString("projectId");
                if(!projectIdTemp.equals(projectID)) continue;
                String nameTemp = jsonObject.getString("name");
                if(StringUtils.isNotBlank(nameTemp) && nameTemp.equals(name)){
                    return jsonObject.getString("id");
                }
                JSONArray children = jsonObject.getJSONArray("children");
                if(children != null){
                    String id = getSubModuleID(children, name);
                    if(id != null){
                        return id;
                    }
                }
            }
        }
        return null;
    }

    private static String getSubModuleID(JSONArray item, String name){
        for(int i = 0 ; i < item.size(); i++){
            JSONObject jsonObject = JSONObject.parseObject(item.getString(i));
            String nameTemp = jsonObject.getString("label");
            if(StringUtils.isNotBlank(nameTemp) && nameTemp.equals(name)){
                return jsonObject.getString("id");
            }
            JSONArray children = jsonObject.getJSONArray("children");
            if(children != null){
                String mName = getSubModuleID(children, name);
                if(mName != null){
                    return mName;
                }
            }
        }
        return null;
    }

}
