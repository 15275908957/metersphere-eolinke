package io.metersphere.platform.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Getter
@Setter
public class PingCodeIssue {
    private String id;
    private String identifier;
    private String type;
    private PingCodeField.Priority priority;
    private String title;
    private String description;
    private String assignee;
    private String start_at;
    private String endAt;
    private Integer created_at;
    private Integer updated_at;

    private Integer is_archived;
    private Integer is_deleted;
    private String completed_at;

    private String version;
    private PingCodeField.Sprint sprint;
    private String board;
    private String entry;
//    private String parent;

    private String parent_id;
    private String story_points;
    private String estimated_workload;
    private String remaining_workload;
    private Map<String, Object> properties;


}
