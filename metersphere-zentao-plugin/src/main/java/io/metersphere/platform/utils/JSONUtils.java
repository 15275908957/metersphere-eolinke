package io.metersphere.platform.utils;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;

public class JSONUtils {

    public static List<String> addIds(Object idsOld,Object ids){


        String idsOldStr = JSON.toJSONString(idsOld);
        String[] a = idsOldStr.split(",");
        String idsStr = JSON.toJSONString(ids);
        if(idsOldStr == null || idsOldStr.equals("")){
            List<String> re = JSON.parseArray(idsStr, String.class);
            return re;
        }
        List<String> idsOldList = JSON.parseArray(idsOldStr, String.class);
        List<String> idsList = JSON.parseArray(idsStr, String.class);
        idsOldList.addAll(idsList);
        return idsOldList;
    }

    public static String addObject(String objectOld,String object){
        List<Object> idsList = JSON.parseArray(object);
        if(objectOld == null || objectOld.equals("")){
            return object;
        }
        List<Object> idsOldList = JSON.parseArray(objectOld);
        idsOldList.addAll(idsList);
        return JSON.toJSONString(idsOldList);
    }

    public static String getLocalhostIP() throws UnknownHostException {
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        return hostAddress;
    }

    public static String getJsonValueByP(String str, String p){
        JSONObject jsonObject = JSONObject.parseObject(str);
        return jsonObject.getString(p);
    }

    public static String getJsonValueByArrayP(String str, String p, String key){
        String value = "";
        JSONArray jsonArray = JSONArray.parseArray(str);
        for(int i = 0 ; i < jsonArray.size(); i++){
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String keyValue = jsonObject.getString(p);
            if(StringUtils.equals(keyValue, key)){
                value = jsonObject.toString();
                break;
            }
        }
        return value;
    }

    public static JSONObject getFileJSONObject(String str){
        JSONArray jsonArray = JSONArray.parseArray(str);
        JSONObject jsonObject = new JSONObject();
        for(int i = 0 ; i < jsonArray.size(); i++){
            String caseIDTemp = jsonArray.getJSONObject(i).getString("caseID");
            jsonObject.put(caseIDTemp, jsonArray.getJSONObject(i));
        }
        return jsonObject;
    }

    public static String getFileStrByCaseID(JSONObject jsonObject, String caseID){
        return jsonObject.getJSONObject(caseID).toString();
    }
//
//    public static void main(String[] args) {
//        String msCasesFile = "[{\"caseID\":\"e820884c-7b4c-4120-bbb1-c7270c3b97a7\",\"fileDataList\":[{\"id\":\"888f781d-1051-46ef-903f-43aeec7d4743\",\"name\":\"frontend.json\",\"type\":\"JSON\",\"size\":3037,\"createTime\":1678674239841,\"updateTime\":1678674239841,\"creator\":\"Administrator\",\"filePath\":\"\\/opt\\/metersphere\\/data\\/attachment\\/testcase\\/e820884c-7b4c-4120-bbb1-c7270c3b97a7\",\"isLocal\":true,\"isRelatedDeleted\":false}]},{\"caseID\":\"ca8c9d46-5723-4c85-8136-e65c1549d365\",\"fileDataList\":[]},{\"caseID\":\"b9c53ae5-4e5b-415a-abfa-27297833b274\",\"fileDataList\":[]},{\"caseID\":\"9fe9c889-4d7f-4f60-9b89-05f46e1aa5c8\",\"fileDataList\":[]},{\"caseID\":\"21bdee55-ec33-4f8e-8674-5c7c0da84feb\",\"fileDataList\":[]},{\"caseID\":\"1e27d1a6-5a9c-4362-899e-4f87e1518e74\",\"fileDataList\":[]},{\"caseID\":\"770ad456-fc09-461f-bafd-f0474bb0da4a\",\"fileDataList\":[]},{\"caseID\":\"d3be9941-9ab3-40c3-8549-05134e30fc52\",\"fileDataList\":[]},{\"caseID\":\"4deb789d-696c-4b17-93a0-d9aa4295efe9\",\"fileDataList\":[]},{\"caseID\":\"e5477bc1-cc52-4b67-922b-d9b67da6dbe6\",\"fileDataList\":[]},{\"caseID\":\"5d7f760b-9d4a-463b-9c73-acad83b62ef5\",\"fileDataList\":[]},{\"caseID\":\"1c011eb0-a261-4d75-b136-718735b3ca0d\",\"fileDataList\":[]}]";
////        String aa = getJsonValueByArrayP(msCasesFile, "caseID", "e820884c-7b4c-4120-bbb1-c7270c3b97a7");
//        JSONObject js = getFileJSONObject(msCasesFile);
//        String aa = getFileStrByCaseID(js, "e820884c-7b4c-4120-bbb1-c7270c3b97a7");
//        System.out.println(aa);
//    }
}
