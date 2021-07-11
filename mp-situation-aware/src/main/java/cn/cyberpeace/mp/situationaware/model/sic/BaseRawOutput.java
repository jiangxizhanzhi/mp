package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;


@Data
public class BaseRawOutput {

    @JSONField(name = "raw_output")
    private String rawOutput;


}
