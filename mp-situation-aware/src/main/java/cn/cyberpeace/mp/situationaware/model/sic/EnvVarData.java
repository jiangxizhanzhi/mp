package cn.cyberpeace.mp.situationaware.model.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.util.Map;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/04/11 10:41
 */
@Data
public class EnvVarData {

    @JSONField(name = "deleted")
    private Map<String, String> deleted;

    @JSONField(name = "added")
    private Map<String, String> added;

    @JSONField(name = "modified")
    private Map<String, String> modified;

    @JSONField(name = "env")
    private Map<String, String> env;


}
