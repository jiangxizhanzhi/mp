package cn.cyberpeace.mp.situationaware.model.wazuh.syscollector;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/18 10:58
 */
@Data
@ApiModel("网卡信息")
public class Netiface {

    @ApiModelProperty(value = "接收包")
    private X rx;

    @ApiModelProperty(value = "发送包")
    private X tx;

    private Scan scan;

    @ApiModelProperty(value = "类型")
    private String type;

    @ApiModelProperty(value = "mtu")
    private Integer mtu;

    @ApiModelProperty(value = "状态")
    private String state;

    @ApiModelProperty(value = "mac地址")
    private String mac;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "探针ID")
    @JSONField(name = "agent_id")
    private String agentId;


    /**
     * 接收或者发送的包
     */
    @Data
    @ApiModel("X")
    public static class X {

        @ApiModelProperty(value = "核心数")
        private Integer bytes;

        @ApiModelProperty(value = "丢弃")
        private Integer dropped;

        @ApiModelProperty(value = "错误")
        private String errors;

        @ApiModelProperty(value = "包数量")
        private String packets;
    }


}
