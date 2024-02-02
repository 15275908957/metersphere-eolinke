package io.metersphere.platform.domain;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import io.metersphere.plugin.utils.JSON;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.util.List;

@Data
public class MSCase {
    private String id;
    private Integer zentaoId;
    private String zentaoModuleId;
    private String nodeId;
    private String testId;
    private String nodePath;
    private String projectId;
    private String name;
    private String type;
    private String maintainer;
    private String priority;
    private String method;
    private long createTime;
    private long updateTime;
    private String sort;
    private long num;
    private String reviewStatus;
    private String tags;
    private String demandId;
    private String demandName;
    private String status;
    private String stepModel;
    private String customNum;
    private String createUser;
    private String originalStatus;
    private String deleteTime;
    private String deleteUserId;
    private String order;
    private String casePublic;
    private String versionId;
    private String refId;
    private boolean latest;
    private String lastExecuteResult;
    private String prerequisite;
    private String remark;
    private String steps;
    private String stepDescription;
    private String expectedResult;
    private String customFields;
    private String maintainerName;
    private String apiName;
    private String lastResultId;
    private String projectName;
    private String createName;
    private String versionName;
    private List<Fields> fields;
    private List<String> caseTags;
    private List<String> issueList;
    private List<File> files;
    private List<CaseCorrelation> casePost;
    private List<CaseCorrelation> casePre;
    private List<CaseComment> caseComments;

    public void setFilesByJSONObject(JSONObject jsonObject){
        if(jsonObject == null) return;
        String filesStr = jsonObject.getString("fileDataList");
        if(StringUtils.isNotEmpty(filesStr)){
            this.files = JSON.parseArray(filesStr, File.class);
        }
    }

    public void setCasePostByJSONObject(JSONObject jsonObject){
        if(jsonObject == null) return;
        String caseItemPOST = jsonObject.getString("caseItemPOST");
        if(StringUtils.isNotEmpty(caseItemPOST)){
            this.casePost = JSON.parseArray(caseItemPOST, CaseCorrelation.class);
        }
    }

    public void setCasePreByJSONObject(JSONObject jsonObject){
        if(jsonObject == null) return;
        String caseItemPRE = jsonObject.getString("caseItemPRE");
        if(StringUtils.isNotEmpty(caseItemPRE)){
            this.casePre = JSON.parseArray(caseItemPRE, CaseCorrelation.class);
        }
    }

    public void setCaseCommentsByJSONObject(JSONObject jsonObject){
        if(jsonObject == null) return;
        String caseItemComment = jsonObject.getString("caseItemComment");
        if(StringUtils.isNotEmpty(caseItemComment)){
            this.caseComments = JSON.parseArray(caseItemComment, CaseComment.class);
        }
    }

    @Data
    public static class CaseComment{
        private String id;
        private String caseId;
        private String author;
        private long createTime;
        private long updateTime;
        private String status;
        private String type;
        private String belongId;
        private String description;
        private String authorName;
    }

    @Data
    public static class CaseCorrelation{
        private String sourceId;
        private String targetId;
        private String type;
        private String graphId;
        private String creator;
        private long createTime;
        private String targetName;
        private long targetNum;
        private String targetCustomNum;
        private String status;
        private String versionId;
        private String versionName;
    }

    @Data
    public static class File{
        private String id;
        private String name;
        private String type;
        private int size;
        private long createTime;
        private long updateTime;
        private String creator;
        private String filePath;
        private boolean isLocal;
        private boolean isRelatedDeleted;
    }

    @Data
    public static class Fields{
        private String id;
        private String name;
        private String scene;
        private String type;
        private String remark;
        private String system;
        private String global;
        private String createTime;
        private String updateTime;
        private String createUser;
        private String projectId;
        private String thirdPart;
        private String options;
        private String required;
        private String order;
        private String defaultValue;
        private String textValue;
        private String value;
        private String customData;
        private String originGlobalId;
        private String key;
    }
}
