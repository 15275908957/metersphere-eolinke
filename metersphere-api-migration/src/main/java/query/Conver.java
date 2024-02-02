package query;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import entity.*;
import utilsss.JOSNUtils;
import utilsss.MSClient;
import utilsss.UseFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Conver {

    private static MSClient client;
    private static Conver conver = new Conver();

    private Conver() {}

    public Conver (String host, String ak, String sig){
//        CheckLicense.checkLicenseByRedis();
        client = MSClient.getMsClientAKSK(host, ak, sig);
    }

//    public static Conver getInstanceAKSK(String host, String ak, String sig){
//        LincenseAPI.CheckLicense.checkLicenseByUrl(host);
//        client = MSClient.getMsClientAKSK(host, ak, sig);
//        return conver;
//    }

    public static Conver getInstanceAccountPassword(String host, String token, String session, String version){
//        LincenseAPI.CheckLicense.checkLicenseByUrl(host);
        client = MSClient.getMsClientAccountPassword(host, token, session,version);
        return conver;
    }

    public String converReqeust(String requestStr, String projectData, String moduleData){
        JSONObject json = JSONObject.parseObject(requestStr);
        String fg = json.getString("modeId");
        if(fg != null){
            if(fg.equals("覆盖")){
                json.put("modeId", "fullCoverage");
            }else{
                json.put("modeId", "incrementalMerge");
            }
        }
        String projectId = json.getString("projectId");
        if(projectId != null){
            json.put("projectId", JOSNUtils.getProjectID(projectData, projectId));
        }
        String moduleId = json.getString("moduleId");
        if(moduleId != null){
            json.put("moduleId", JOSNUtils.getModuleID(moduleData, moduleId, json.getString("projectId")));
        }
        return json.toString();
    }

    public String apiConver(String APIData, String toProjectID, String toModuleID, String requestFileStr,String apiFilePath){
        if(apiFilePath == null){
            apiFilePath = "/opt/metersphere/data/file/"+toProjectID+"/"+toModuleID;
        }
//        APIData = APIData.replaceAll(projectID, toProjectID);
        APIFileEntity apiEntity = JSON.parseObject(APIData, APIFileEntity.class);
       String fileName = "MeterSphereAPI.json";
        UseFile.writeTxt(apiFilePath, fileName, JSON.toJSONString(apiEntity));
        String response = null;
        try{
            response = client.importAPI(apiFilePath, fileName, requestFileStr);
        }catch (Exception e){

        }
        List<CaseEntity> caseEntityList = apiEntity.getCases();
        List<ApiEntity> apiEntityList = apiEntity.getData();

        Map<String, CaseEntity> caseMap = new HashMap<>();
        Map<String, ApiEntity> apiMap = new HashMap<>();

        caseEntityList.forEach( item -> {
            ApiEntity api = JSONObject.parseObject(item.getRequest(), ApiEntity.class);
            caseMap.put(api.getPath(), item);
        });

        apiEntityList.forEach(item -> {
            apiMap.put(item.getPath(), item);
        });

        ResultEntity resultEntity = JSONObject.parseObject(response, ResultEntity.class);
        APIFileEntity newApiEntity = JSON.parseObject(JSONObject.toJSONString(resultEntity.getData()), APIFileEntity.class);
        if(newApiEntity.getData() != null && newApiEntity.getData().size() != 0){
            for(ApiEntity item: newApiEntity.getData()){
                ApiEntity temp = apiMap.get(item.getPath());
                if(temp != null){
                    item.setOldID(temp.getId());
                }
            }
        }
        if(newApiEntity.getCases() != null && newApiEntity.getCases().size() != 0){
            for(CaseEntity item: newApiEntity.getCases()){
                ApiEntity temp = JSONObject.parseObject(item.getRequest(), ApiEntity.class);
                CaseEntity caseTemp = caseMap.get(temp.getPath());
                if(caseTemp != null){
                    item.setOldID(caseTemp.getId());
                }
            }
        }
        return JSONObject.toJSONString(newApiEntity);
    }


//    public String apiConver(String APIData, String toProjectID, String toModuleID){
//        RequestFileEntity requestFileEntity = new RequestFileEntity();
//        requestFileEntity.setModuleId(toModuleID);
//        requestFileEntity.setProjectId(toProjectID);
//        requestFileEntity.setProtocol("HTTP");
//        requestFileEntity.setSaved(true);
//        requestFileEntity.setPlatform("Metersphere");
//        requestFileEntity.setModeId("fullCoverage");
//        return apiConver(APIData, toProjectID, toModuleID, JSON.toJSONString(requestFileEntity));
//    }

    public String contextConver(String contextData, String apiData, String requestFileStr, String toProjectID, String toModuleID, String apiFilePath) throws Exception{
//        contextData = contextData.replaceAll(projectID,toProjectID);
        if(apiFilePath == null){
            apiFilePath = "/opt/metersphere/data/file/"+toProjectID+"/"+toModuleID;
        }
        APIFileEntity newApiEntity = JSON.parseObject(apiData, APIFileEntity.class);
        Map<String, ApiEntity> apiEntityMap = new HashMap<>();
        Map<String, CaseEntity> caseEntityMap = new HashMap<>();
        for(CaseEntity item: newApiEntity.getCases()){
            if(item.getOldID() != null && item.getId() != null) contextData = contextData.replaceAll(item.getOldID(), item.getId());
        }
        for(ApiEntity item: newApiEntity.getData()){
            if(item.getOldID() != null && item.getId() != null) contextData = contextData.replaceAll(item.getOldID(), item.getId());
        }
        CentextEntity centextEntity = JSONObject.parseObject(contextData, CentextEntity.class);

//        if(newApiEntity.getData() != null && newApiEntity.getData().size() != 0){
//            newApiEntity.getData().forEach(item -> {
//                apiEntityMap.put(item.getOldID(),item);
//            });
//        }
//        if(newApiEntity.getCases() != null && newApiEntity.getCases().size() != 0){
//            newApiEntity.getCases().forEach(item -> {
////                ApiEntity apiEntity = JSON.parseObject(item.getRequest(), ApiEntity.class);
//                caseEntityMap.put(item.getOldID(), item);
//            });
//        }
        centextEntity.setProjectId(toProjectID);
//        for(CentextDataEntity item: centextEntity.getData()){
//            item.setProjectId(toProjectID);
//            JSONObject jsonObject = JSONObject.parseObject(item.getScenarioDefinition());
//            JSONArray jsonArray = jsonObject.getJSONArray("hashTree");
//            for(int i = 0 ; i < jsonArray.size(); i++){
//                JSONObject jsonTemp = jsonArray.getJSONObject(i);
//                updateId(jsonTemp, apiEntityMap, caseEntityMap);
//            }
//            item.setScenarioDefinition(jsonObject.toString());
//        }
//        String apiFilePath = "/opt/metersphere/data/file/"+toProjectID+"/"+toModuleID;
        String fileName = "MeterSphereScenario.json";
        UseFile.writeTxt(apiFilePath,fileName, JSONObject.toJSONString(centextEntity));
        return client.importScenario(apiFilePath, fileName, requestFileStr);
    }

    public void updateId(JSONObject jsonTemp, Map<String, ApiEntity> apiEntityMap, Map<String, CaseEntity> caseEntityMap){
        String refType = jsonTemp.getString("refType");
        String id = jsonTemp.getString("id");
        if(refType != null && refType.equals("CASE")){
            CaseEntity caseEntity =  caseEntityMap.get(id);
            if(caseEntity == null) return;
            jsonTemp.put("id",caseEntity.getId());
        } else if(refType != null && refType.equals("API")){
            ApiEntity apiEntity =  apiEntityMap.get(id);
            if(apiEntity == null) return;
            jsonTemp.put("id",apiEntity.getId());
        } else {
            //场景
            String type = jsonTemp.getString("type");
            if(type != null && "scenario".equals(type)){
                JSONArray jsonArray = jsonTemp.getJSONArray("hashTree");
                for(int i = 0 ; i < jsonArray.size(); i++){
                    JSONObject temp = jsonArray.getJSONObject(i);
                    updateId(temp, apiEntityMap, caseEntityMap);
                }
            }
        }
    }
}
