package io.metersphere.platform.domain.EOlinker;

import lombok.Data;

import java.util.List;

@Data
public class MockInfoEntity {
    private String mockResult;
    private List<MockRuleEntity> mockRule;
    private MockConfigEntity mockConfig;
}
