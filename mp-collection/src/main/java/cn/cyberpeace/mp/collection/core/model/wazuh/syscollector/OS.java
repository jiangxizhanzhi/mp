package cn.cyberpeace.mp.collection.core.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/25 09:32
 */
@Data
public class OS {

    private OSDetail os;

    private Scan scan;

    @JSONField(name = "version")
    private String version;

    @JSONField(name = "hostname")
    private String hostname;

    private String release;

    private String architecture;

    private String sysname;

    @JSONField(name = "agent_id")
    private String agentId;

    @Data
    public static class OSDetail {

        private String major;

        private String minor;

        private String name;

        private String platform;

        private String version;
    }

}
