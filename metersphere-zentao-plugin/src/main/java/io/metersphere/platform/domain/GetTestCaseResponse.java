package io.metersphere.platform.domain;

import lombok.Data;

@Data
public class GetTestCaseResponse {
    private Integer id;
    private String project;
    private String product;
    private String execution;
    private String branch;
    private String lib;
    private String module;
    private String modulePath;
    private String path;
    private String story;
    private String storyVersion;
    private String title;
    private String precondition;
    private String keywords;
    private String pri;
    private String type;
    private String auto;
    private String frame;
    private String stage;
    private String howRun;
    private String scriptedBy;
    private String scriptedDate;
    private String scriptStatus;
    private String scriptLocation;
    private String status;
    private String subStatus;
    private String color;
    private String frequency;
    private String order;
    private String openedBy;
    private String openedDate;
    private String reviewedBy;
    private String reviewedDate;
    private String lastEditedBy;
    private String lastEditedDate;
    private String version;
    private String linkCase;
    private String fromBug;
    private String fromCaseID;
    private String fromCaseVersion;
    private String deleted;
    private String lastRunner;
    private String lastRunDate;
    private String lastRunResult;
    private String storyTitle;
    private int bugs;
    private int results;
    private int caseFails;
    private String stepNumber;
}
