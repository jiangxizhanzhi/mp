package cn.cyberpeace.mp.collection.core.model.es.sic;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/3/25 10:43
 */
@Data
public class BaseInput {
    @JSONField(name = "output_format")
    private String outputFormat;

    private String extra;

    private String id;

    private String matcher;

    private String command;

    private String group;
}
