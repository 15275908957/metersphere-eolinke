package io.metersphere.platform.domain;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Data
public class PingCodeGetIssue {
    private String id;
    private String url;
    private Project project;
    private String identifier;
    private String type;
    private State state;
    private Priority priority;
    private String title;
    private String description;
    private Assignee assignee;
    private long start_at;
    private long end_at;
    private long created_at;
    private long updated_at;
    private Created_by created_by;
    private int is_archived;
    private int is_deleted;
    private String completed_at;
    private List<Participants> participants;
    private Map<String,Object> properties;
    private List<String> tags;
    private Version version;
    private Sprint sprint;
    private String board;
    private String entry;
    private String swimlane;
    private Parent parent;
    private double story_points;
    private double estimated_workload;
    private double remaining_workload;
    @Data
    public static class Project{
        private String id;
        private String url;
        private String name;
        private String type;
        private String identifier;
    }
    @Data
    public static class State{
        private String id;
        private String url;
        private String name;
        private String type;
        private String color;
    }
    @Data
    public static class Priority{
        private String id;
        private String url;
        private String name;
    }

    @Data
    public static class Assignee{
        private String url;
        private String id;
        private String name;
        private String display_name;
        private String avatar;
    }
    @Data
    public static class Created_by{
        private String url;
        private String id;
        private String name;
        private String display_name;
        private String avatar;
    }
    @Data
    public static class User{
        private String url;
        private String id;
        private String name;
        private String display_name;
        private String avatar;
    }
    @Data
    public static class Participants{
        private String url;
        private String id;
        private User user;
    }
    @Data
    public static class Version{
        private String id;
        private String url;
        private String name;
        private long start_at;
        private long end_at;
    }
    @Data
    public static class Sprint{
        private String id;
        private String url;
        private String name;
        private long start_at;
        private long end_at;
        private String status;
    }
    @Data
    public static class Parent{
        private String id;
        private String url;
        private String identifier;
        private String type;
        private String title;
        private String start_at;
        private String end_at;
    }
}


