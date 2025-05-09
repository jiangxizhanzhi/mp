package cn.cyberpeace.mp.base.snmp_device.netgear;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.math.NumberUtils;

@Getter
@Setter
public class CommandTopPort {
    @ApiModelProperty(value = "本地端口")
    private Local local;

    @ApiModelProperty(value = "远程端口")
    private Remote remote;

    @ApiModelProperty(value = "索引节点")
    private Integer inode;

    @ApiModelProperty(value = "父进程ID")
    private Integer pid;

    @ApiModelProperty(value = "接收队列中的数据包")
    @JSONField(name = "rx_queue")
    private Integer rxQueue;

    @ApiModelProperty(value = "待传输的数据包")
    @JSONField(name = "tx_queue")
    private String txQueue;

    @ApiModelProperty(value = "协议")
    private String protocol;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "进程")
    private String process;

    @ApiModelProperty(value = "代理ID")
    @JSONField(name = "agent_id")
    private String agentId;


    @Getter
    @Setter
    @ApiModel("本地端口")
    public static class Local {

        @ApiModelProperty("本地IP")
        private String ip;

        @ApiModelProperty("本地端口")
        private String portStr;

        private Integer port ;

        public void setPortStr(String portStr) {
            this.portStr = portStr;
            if (portStr != null && NumberUtils.isNumber(portStr)) {
                this.port = NumberUtils.toInt(portStr);
            }
        }
    }

    @Data
    @ApiModel("远程端口")
    public static class Remote {

        @ApiModelProperty("远程IP")
        private String ip;

        @ApiModelProperty("远程端口")
        private String portStr;

        private Integer port ;

        public void setPortStr(String portStr) {
            this.portStr = portStr;
            if (portStr != null && NumberUtils.isNumber(portStr)) {
                this.port = NumberUtils.toInt(portStr);
            }
        }
    }
}
