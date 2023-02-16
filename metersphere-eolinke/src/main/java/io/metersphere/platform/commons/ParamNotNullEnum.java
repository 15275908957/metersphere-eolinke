package io.metersphere.platform.commons;

import org.apache.commons.lang3.StringUtils;

public enum ParamNotNullEnum {
    TRUE("0", "true",true),
    FALSE("1", "false", false);

    private String tpyeCode;
    private String required;
    private boolean requiredBoolean;

    ParamNotNullEnum(String tpyeCode, String required, boolean requiredBoolean) {
        this.tpyeCode = tpyeCode;
        this.required = required;
        this.requiredBoolean = requiredBoolean;
    }

    public static String getRequiredByTypeCode(String typeCode){
        ParamNotNullEnum[] arr = ParamNotNullEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].required;
            }
        }
        return "false";
    }

    public static boolean getRequiredBooleanByTypeCode(String typeCode){
        ParamNotNullEnum[] arr = ParamNotNullEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].requiredBoolean;
            }
        }
        return false;
    }

    public String getTpyeCode() {
        return tpyeCode;
    }

    public void setTpyeCode(String tpyeCode) {
        this.tpyeCode = tpyeCode;
    }
}
