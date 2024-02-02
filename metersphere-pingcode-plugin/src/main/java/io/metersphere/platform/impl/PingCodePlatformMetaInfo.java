package io.metersphere.platform.impl;

import io.metersphere.platform.api.AbstractPlatformMetaInfo;

public class PingCodePlatformMetaInfo extends AbstractPlatformMetaInfo {

    public static final String KEY = "PingCode";

    public PingCodePlatformMetaInfo() {
        super(PingCodePlatformMetaInfo.class.getClassLoader());
    }

    @Override
    public String getVersion() {
        return "2.10.1";
    }

    @Override
    public String getKey() {
        return KEY;
    }

    @Override
    public boolean isThirdPartTemplateSupport() {
        return true;
    }
}
