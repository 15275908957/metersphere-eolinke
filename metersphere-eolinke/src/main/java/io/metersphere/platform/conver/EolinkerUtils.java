package io.metersphere.platform.conver;

import io.metersphere.platform.commons.RequestTypeEnum;
import io.metersphere.platform.domain.EOlinker.*;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class EolinkerUtils {

    public static List<ApiInfoEntity> getEolinkerAPIList(List<ApiGroupInfoEntity> apiGroupInfoEntities){
        List<ApiInfoEntity> list = new ArrayList<>();
        if(apiGroupInfoEntities != null && apiGroupInfoEntities.size() != 0){
            for(ApiGroupInfoEntity item: apiGroupInfoEntities){
//                if(StringUtils.equals("preaudit",item.getGroupName())){
//                    System.out.println(item.getGroupName());
//                }
//                if(StringUtils.equals("智慧V2",item.getGroupName())){
//                    System.out.println(item.getGroupName());
//                }
                List<ApiInfoEntity> apiInfoEntities = item.getApiList();
                if(StringUtils.isNotBlank(item.getGroupName()) && item.getIsChild() != null){
                    for(ApiInfoEntity api : apiInfoEntities){
                        api.getBaseInfo().setGroupId(item.getGroupID());
                    }
                    list.addAll(apiInfoEntities);
                }
                List<ApiGroupInfoEntity> apiGroupInfoEntityList = item.getApiGroupChildList();
                List<ApiInfoEntity> listTemp = getEolinkerAPIList(apiGroupInfoEntityList);
                list.addAll(listTemp);
            }
        }
        return list;
    }

    public static Map<String, ApiGroupInfoEntity> getTagsListByAPIFroup(List<ApiGroupInfoEntity> apiGroupInfoEntities) {
        Map<String, ApiGroupInfoEntity> map = new HashMap<>();
        if(apiGroupInfoEntities != null && apiGroupInfoEntities.size() != 0){
            for(ApiGroupInfoEntity item: apiGroupInfoEntities){
                if(item.getGroupID() != null){
                    map.put(item.getGroupID()+"",item);
                }
                map.putAll(getTagsListByAPIFroup(item.getApiGroupChildList()));
            }
        }
        return map;
    }

    public static List<String> getTagByGroupId(Long groupId, Map<String, ApiGroupInfoEntity> apiGroupMap) {
        List<String> tag = new ArrayList<>();
        ApiGroupInfoEntity temp = apiGroupMap.get(groupId+"");
        tag.add(temp.getGroupName());
        while (true){
            temp = apiGroupMap.get(temp.getParentGroupID()+"");
            if(temp != null){
                tag.add(temp.getGroupName());
            } else{
                break;
            }
        }
        Collections.reverse(tag);
        return tag;
    }
}
