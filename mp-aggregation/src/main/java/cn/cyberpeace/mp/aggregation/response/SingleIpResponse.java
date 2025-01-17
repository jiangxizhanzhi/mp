package cn.cyberpeace.mp.aggregation.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 单IP响应
 *
 * @author 二竹
 * @date 2025/01/17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel("单ip配置")
public class SingleIpResponse {

    @ApiModelProperty(value = "是否开启单ip")
    private Boolean publicNetProxy;

    @ApiModelProperty(value = "单ip模式下配置的ip地址")
    private String publicNetProxyIp;
}
