package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 端口配置
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("端口配置")
public class PortConfig {

    /**
     * 管理口/Wan口/Lan口
     */
    @ApiModelProperty("配置类型")
    private Integer portType;

    /**
     * 端口名称
     */
    @ApiModelProperty("端口名称")
    private String portName;

    /**
     * 端口网段
     */
    @ApiModelProperty("端口网段")
    private String portIps;

    @ApiModelProperty("接入协议")
    private List<AccessProtocol> accessProtocols;
}
