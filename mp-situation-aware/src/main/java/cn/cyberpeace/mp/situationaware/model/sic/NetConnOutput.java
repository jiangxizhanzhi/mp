package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/3/25 10:41
 */
@Data
public class NetConnOutput {

    @JSONField(name = "raw_output")
    private String rawOutput;


}
