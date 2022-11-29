package io.metersphere.platform.impl;


import im.metersphere.plugin.exception.MSPluginException;
import im.metersphere.plugin.utils.JSON;
import im.metersphere.plugin.utils.LogUtil;
import io.metersphere.platform.domain.*;
import io.metersphere.platform.api.AbstractPlatform;
import io.metersphere.base.domain.IssuesWithBLOBs;
import org.springframework.http.ResponseEntity;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class LarkPlatform extends AbstractPlatform {

    protected LarkAbstractClient larkAbstractClient;

    public LarkPlatform(PlatformRequest request) {
        super.key = LarkPlatformMetaInfo.KEY;
        super.request = request;
        larkAbstractClient = new LarkAbstractClient();
        setConfig();
    }

    public LarkConfig setConfig() {
        LarkConfig config = getIntegrationConfig(LarkConfig.class);
        larkAbstractClient.setConfig(config);
        return config;
    }

    protected SimpleDateFormat sdfWithZone = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

    @Override
    public List<DemandDTO> getDemands(String projectConfig) {
        return null;
    }

    @Override
    public IssuesWithBLOBs addIssue(PlatformIssuesUpdateRequest issuesRequest) {
        return null;
    }

    @Override
    public List<SelectOption> getProjectOptions(GetOptionRequest request) {
        return null;
    }

    @Override
    public IssuesWithBLOBs updateIssue(PlatformIssuesUpdateRequest request) {
        return null;
    }

    @Override
    public void deleteIssue(String id) {

    }

    @Override
    public void validateIntegrationConfig() {
        larkAbstractClient.auth();
        larkAbstractClient.checkUserByUserKey();
    }

    @Override
    public void validateProjectConfig(String projectConfig) {
        try {
            LarkProjectConfig object = JSON.parseObject(projectConfig, LarkProjectConfig.class);
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
    }

    public List<SelectOption> getIssueTypes(GetOptionRequest request) {
        List<LarkIssueType> larkIssueTypes = null;
        try {
            larkIssueTypes = larkAbstractClient.getIssueTypes(request.getProjectConfig());
        } catch (Exception e) {
            LogUtil.error(e);
            MSPluginException.throwException(e.getMessage());
        }
        List<SelectOption> selectOptions = larkIssueTypes.stream()
                .map(item ->  new SelectOption(item.getName(), item.getType_key()))
                .collect(Collectors.toList());
        return selectOptions;
    }

    @Override
    public void validateUserConfig(String userConfig) {

    }

    @Override
    public boolean isAttachmentUploadSupport() {
        return false;
    }

    @Override
    public List<PlatformUser> getPlatformUser() {
        return null;
    }

    @Override
    public SyncIssuesResult syncIssues(SyncIssuesRequest request) {
        return null;
    }

    @Override
    public List<PlatformCustomFieldItemDTO> getThirdPartCustomField(String projectConfig) {
        return null;
    }

    @Override
    public ResponseEntity proxyForGet(String url, Class responseEntityClazz) {
        return null;
    }

    @Override
    public void syncIssuesAttachment(SyncIssuesAttachmentRequest request) {

    }

    @Override
    public List<PlatformStatusDTO> getStatusList(String issueKey) {
        return null;
    }
}
