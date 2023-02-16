package io.metersphere.platform.commons;

public enum RequestParamTypeEnum {
    FORM_DATA("0"),
    REST_FUL("2"),
    RAW("1");

    private String tpyeCode;

    RequestParamTypeEnum(String tpyeCode) {
        this.tpyeCode = tpyeCode;
    }


    public String getTpyeCode() {
        return tpyeCode;
    }

    public void setTpyeCode(String tpyeCode) {
        this.tpyeCode = tpyeCode;
    }
}
