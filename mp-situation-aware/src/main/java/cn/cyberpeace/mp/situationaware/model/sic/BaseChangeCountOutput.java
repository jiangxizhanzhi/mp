package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


@Data
public class BaseChangeCountOutput {

    
    @JSONField(name = "changed_count")
    private String changedCount;

    @JSONField(name = "added_count")
    private String addedCount;

    @JSONField(name = "values_count")
    private String valuesCount;

    @JSONField(name = "deleted_count")
    private String deletedCount;

    @JSONField(name = "modified_count")
    private String modifiedCount;

    @JSONField(name = "data")
    private String data;

}
