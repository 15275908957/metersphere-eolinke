package io.metersphere.platform.domain;


import lombok.Getter;
import lombok.Setter;

import java.util.regex.Pattern;

@Getter
@Setter
public class RequestUrl {
    private String login;
    private String sessionGet;
    private String bugCreate;
    private String createMetaData;
    private String bugUpdate;
    private String bugList;
    private String bugDelete;
    private String bugGet;
    private String storyGet;
    private String userGet;
    private String getTestCase;
    private String getTreeMeun;
    private String getModuleListByType;
    private String getTreeProject;
    private String ajaxGetProductStories;
    private String createModule;
    private String createTestcase;
    private String testcaseView;
    private String updateTestcase;
    private String buildsGet;
    private String buildsGetV17;
    private String fileUpload;
    private String fileDelete;
    private String fileDownload;
    private String replaceImgUrl;
    private String productGet;
    private Pattern imgPattern;
    private String linked2project;
    private String setMenu;
    private String relatedFields;
    private String executionTestcase;
    private String saveState;
    private String getByID;
    private String setProjectSession;
    private String treeBrowse;
}
