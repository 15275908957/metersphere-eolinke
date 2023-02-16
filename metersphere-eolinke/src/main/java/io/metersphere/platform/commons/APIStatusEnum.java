package io.metersphere.platform.commons;

import org.apache.commons.lang3.StringUtils;

public enum APIStatusEnum {
    QIYONG("0", "Underway"),
    WEIHU("1", "Prepare"),
    FEIQI("2", "false");

    private String tpyeCode;
    private String msStatus;

    APIStatusEnum(String tpyeCode, String msStatus) {
        this.tpyeCode = tpyeCode;
        this.msStatus = msStatus;
    }

    public static String getMsStatusByTypeCode(String typeCode){
        APIStatusEnum[] arr = APIStatusEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].msStatus;
            }
        }
        return QIYONG.msStatus;
    }

}
