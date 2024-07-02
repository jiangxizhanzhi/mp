package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @ClassName PortConfig
 * @Description 端口配置
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see Serializable
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("端口配置")
public class PortConfig implements Serializable {

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
