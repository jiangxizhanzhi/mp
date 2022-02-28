package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/2/25 09:32
 */
@Data
public class Hardware {

    private CPU cpu;

    private RAM ram;

    private Scan scan;

    @JSONField(name = "board_serial")
    private String boardSerial;

    @JSONField(name = "agent_id")
    private String agentId;


    @Data
    public static class CPU {

        private Integer cores;

        // Double?
        private Long mhz;

        private String name;
    }


    @Data
    public static class RAM {

        private Long free;

        private Long total;

        private Long usage;
    }


}
