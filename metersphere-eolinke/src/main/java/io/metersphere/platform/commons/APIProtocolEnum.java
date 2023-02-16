package io.metersphere.platform.commons;

import org.apache.commons.lang3.StringUtils;

public enum APIProtocolEnum {
    HTTP("0", "HTTP"),
    HTTPS("1", "HTTP");

    private String tpyeCode;
    private String msProtocol;

    APIProtocolEnum(String tpyeCode, String msProtocol) {
        this.tpyeCode = tpyeCode;
        this.msProtocol = msProtocol;
    }

    public static String getMsProtocolByTypeCode(String typeCode){
        APIProtocolEnum[] arr = APIProtocolEnum.values();
        for(int i = 0; i < arr.length ;i++){
            if(StringUtils.equals(typeCode, arr[i].tpyeCode)){
                return arr[i].msProtocol;
            }
        }
        return HTTP.msProtocol;
    }

}
