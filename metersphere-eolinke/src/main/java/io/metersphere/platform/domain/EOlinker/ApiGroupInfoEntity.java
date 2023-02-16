package io.metersphere.platform.domain.EOlinker;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ApiGroupInfoEntity {
    private Long groupID;
    private String groupName;
    private Long projectID;
    private Long parentGroupID;
    private Integer isChild;
    private String apiList;
    private String apiGroupChildList;

    public List<ApiGroupInfoEntity> getApiGroupChildList() {
        List<ApiGroupInfoEntity> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        try{
            jsonArray = JSONArray.parseArray(this.apiGroupChildList);
        }catch (Exception e){
            JSONObject jsonObject = JSONObject.parseObject(this.apiGroupChildList);
            for(String key: jsonObject.keySet()){
                JSONObject temp = jsonObject.getJSONObject(key);
                jsonArray.add(temp);
            }
        }
        if(jsonArray != null){
            for(int i = 0 ; i < jsonArray.size(); i++){
                String apiInfoEntityStr = jsonArray.getString(i);
                ApiGroupInfoEntity apiInfoEntity = JSONObject.parseObject(apiInfoEntityStr, ApiGroupInfoEntity.class);
                list.add(apiInfoEntity);
            }
        }
        return list;
    }
    public List<ApiInfoEntity> getApiList(){
        List<ApiInfoEntity> list = new ArrayList<>();
        JSONArray jsonArray = new JSONArray();
        try{
            jsonArray = JSONArray.parseArray(this.apiList);
        }catch (Exception e){
            JSONObject jsonObject = JSONObject.parseObject(this.apiList);
            for(String key: jsonObject.keySet()){
                JSONObject temp = jsonObject.getJSONObject(key);
                jsonArray.add(temp);
            }
        }
        if(jsonArray != null){
            for(int i = 0 ; i < jsonArray.size(); i++){
                String apiInfoEntityStr = jsonArray.getString(i);
                ApiInfoEntity apiInfoEntity = JSONObject.parseObject(apiInfoEntityStr, ApiInfoEntity.class);
                list.add(apiInfoEntity);
            }
        }
        return list;
    }
}
