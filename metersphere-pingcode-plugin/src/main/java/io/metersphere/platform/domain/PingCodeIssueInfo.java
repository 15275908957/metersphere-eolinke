package io.metersphere.platform.domain;

import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class PingCodeIssueInfo {
    private String id;

    private String project_id;
    //项目的id。

    private String title;
    //工作项的标题。

    private String state_id;
        //可选
    //工作项状态的id。

    private Number start_at;
        //可选
    //工作项的开始时间。

    private Number end_at;
        //可选
    //工作项的截止时间。

    private String description;
        //可选
    //工作项的描述。

    private String sprint_id;
        //可选
    //迭代的id。该字段只有项目类型为scrum时有效。

    private String board_id;
    //可选
    //看板的id。该字段只有项目类型为kanban时有效。

    private String entry_id;
    //可选
    //看板栏的id。该字段只有项目类型为kanban时有效。

    private String swimlane_id;
    //可选	String
    //泳道的id。该字段只有项目类型为kanban时有效。

    private String version_id;
    //可选
    //发布的id。

    private String priority_id;
    //可选
    //优先级的id。

    private String assignee_id;
    //可选
    //工作项负责人的id。

    private String parent_id;
    //可选
    //父工作项的id，缺陷的父工作项为用户故事。

    private List<String> participant_ids;
    //可选	String[]
    //工作项关注人的id列表。

    private Number story_points;
    //可选
    //工作项的故事点。

    private Number estimated_workload;
    //可选
    //工作项的预估工时。

    private Number remaining_workload;
    //可选
    //工作项的剩余工时。

    private Map<String, Object> properties = new HashMap<>();
    //可选
    //工作项属性的键值对集合，需要注意的是，当前工作项类型对应的工作项属性方案需要包含这些工作项属性，例如工作项属性方案中包含prop_a和prop_b，那么为这两个工作项属性赋值时，可以指定{ "prop_a": "prop_a_value", "prop_b": "prop_b_value" }。
}
