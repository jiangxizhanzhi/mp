package cn.cyberpeace.mp.situationaware.model.wazuh.decoder;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/4/18 20:26
 */
@Data
public class Decoder {

    private String filename;

    @JSONField(name = "relative_dirname")
    private String relativeDirname;

    private String status;

    private String name;

    private int position;

    private Details details;

    @Data
    public static class Details {

        private String parent;

        private Prematch prematch;

        private Regex regex;

        private String order;
    }

    @Data
    public static class Prematch {

        private String pattern;

        private String offset;

    }

    @Data
    public static class Regex {

        private String pattern;

        private String offset;

    }

}
