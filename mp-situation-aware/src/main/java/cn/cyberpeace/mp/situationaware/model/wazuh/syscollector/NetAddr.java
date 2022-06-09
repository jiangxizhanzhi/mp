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
public class NetAddr {

    private Scan scan;

    @ApiModelProperty(value = "广播地址")
    private String broadcast;

    @ApiModelProperty(value = "协议")
    private String proto;

    @ApiModelProperty(value = "地址")
    private String address;

    @ApiModelProperty(value = "网卡")
    private String iface;

    @ApiModelProperty(value = "掩码")
    private String netmask;

    @ApiModelProperty(value = "探针ID")
    @JSONField(name = "agent_id")
    private String agentId;


}
