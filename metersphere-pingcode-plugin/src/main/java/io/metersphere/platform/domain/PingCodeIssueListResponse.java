package io.metersphere.platform.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PingCodeIssueListResponse {
    private int page_index;
    private int page_size;
    private int total;
    private List<PingCodeIssue> values;
}
