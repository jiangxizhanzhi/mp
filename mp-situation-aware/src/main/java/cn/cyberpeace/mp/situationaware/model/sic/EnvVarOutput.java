package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/04/11 10:41
 */
@Data
public class EnvVarOutput {

    // 是否有变动，综合
    @JSONField(name = "changed_count")
    private Integer changedCount;

    @JSONField(name = "added_count")
    private Integer addedCount;

    @JSONField(name = "values_count")
    private Integer valuesCount;

    @JSONField(name = "deleted_count")
    private Integer deletedCount;

    @JSONField(name = "modified_count")
    private Integer modifiedCount;

    @JSONField(name = "data")
    private String data;

}
