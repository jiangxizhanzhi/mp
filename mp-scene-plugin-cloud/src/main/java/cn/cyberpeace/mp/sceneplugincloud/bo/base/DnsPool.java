package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DnsPool {
    @ApiModelProperty(value = "节点id")
    private String nodeId;
    @ApiModelProperty(value = "域名")
    private String domain;
}
