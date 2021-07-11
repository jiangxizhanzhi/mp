package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("硬件")
public class Hardware {

    private CPU cpu;

    private RAM ram;

    private Scan scan;

    @ApiModelProperty(value = "主板序列号")
    @JSONField(name = "board_serial")
    private String boardSerial;

    @ApiModelProperty(value = "代理ID")
    @JSONField(name = "agent_id")
    private String agentId;


    @Data
    @ApiModel("cpu")
    public static class CPU {

        @ApiModelProperty(value = "核心数")
        private Integer cores;

        @ApiModelProperty(value = "主频mhz")
        
        private Double mhz;

        @ApiModelProperty(value = "名称")
        private String name;
    }


    @Data
    @ApiModel("ram")
    public static class RAM {

        @ApiModelProperty(value = "空闲内存KB")
        private Long free;

        @ApiModelProperty(value = "内存总大小KB")
        private Long total;

        @ApiModelProperty(value = "使用率(百分比)")
        private Double usage;
    }


}
