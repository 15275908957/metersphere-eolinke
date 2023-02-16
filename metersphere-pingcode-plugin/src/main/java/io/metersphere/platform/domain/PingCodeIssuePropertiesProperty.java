package io.metersphere.platform.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PingCodeIssuePropertiesProperty {
    private String url;
    private String id;
    private String name;
    private String type;
    private List<PingCodeField.Option> sptions = new ArrayList<>();
    private List<PingCodeField.Option> options = new ArrayList<>();

    public List<SelectOption> getMSOptionList(){
        List<SelectOption> selectOptionList = new ArrayList<>();
        if(this.options != null && this.options.size() != 0){
            for(PingCodeField.Option item : options){
                selectOptionList.add(new SelectOption(item.getText(), item.get_id()));
            }
        }
        return selectOptionList;
    }

}
