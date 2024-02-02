package entity;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MSConverEntity {
    private String msUrl;
    private String accessKey;
    private String secretKey;
    private String account;
    private String password;
    private String msVersion;

    public MSConverEntity(){}

    public MSConverEntity(String msUrl, String accessKey, String secretKey,String account, String password){
        this.msUrl = msUrl;
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.account = account;
        this.password = password;
    }

    public String toProperties(){
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(this));
        String txt = "";
        for(String key: jsonObject.keySet()){
            if(jsonObject.getString(key) != null) txt += key+"="+jsonObject.getString(key)+"\n";
        }
        txt += "importType=不覆盖\n";
        return txt;
    }
}
