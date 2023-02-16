package io.metersphere.platform.conver;

import com.alibaba.fastjson2.JSONObject;
import io.metersphere.platform.domain.EOlinker.ApiGroupInfoEntity;
import io.metersphere.platform.domain.EOlinker.ApiInfoEntity;
import io.metersphere.platform.domain.EOlinker.PostmanModel;
import io.metersphere.platform.domain.EOlinker.ProjectAPIEntity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EolinkerConverPostMan {
    @Deprecated
    public static String toPostmanByEolinker(ProjectAPIEntity projectAPIEntity) {
        List<PostmanModel> postmanModels = new ArrayList<>();
        List<ApiGroupInfoEntity> apiGroupInfoEntities = projectAPIEntity.getApiGroupList();
        for(ApiGroupInfoEntity item: apiGroupInfoEntities){
            PostmanModel postmanModel = new PostmanModel();
            postmanModel.setName(item.getGroupName());
            List<ApiInfoEntity> apiInfoEntity = item.getApiList();
            List<PostmanModel.ItemBean> itemBeanList = new ArrayList<>();
            for(ApiInfoEntity api: apiInfoEntity){
                PostmanModel.ItemBean itemBean = new PostmanModel.ItemBean();
                itemBean.setName(api.getBaseInfo().getApiName());
                PostmanModel.ItemBean.RequestBean requestBean = new PostmanModel.ItemBean.RequestBean();
                PostmanModel.ItemBean.RequestBean.HeaderBean headerBean = new PostmanModel.ItemBean.RequestBean.HeaderBean();

//                requestBean.setHeader(api.getHeaderInfo());

                itemBean.setRequest(requestBean);
                itemBeanList.add(itemBean);
            }
            postmanModel.setItem(itemBeanList);
        }
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("item", postmanModels);
        JSONObject info = new JSONObject();
        info.put("schema", "https://schema.getpostman.com/json/collection/v2.1.0/collection.json");
        String dateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
//        String exportName = StringUtils.isNotBlank(appSettingService.getState().getExportModuleName()) ? appSettingService.getState().getExportModuleName() : files.get(0).getProject().getName();
        info.put("name", "cjgTestPostmanjson.json");
        info.put("description", "exported at " + dateTime);
        info.put("_postman_id", UUID.randomUUID().toString());
        jsonObject.put("info", info);
        return jsonObject.toJSONString();
    }
}
