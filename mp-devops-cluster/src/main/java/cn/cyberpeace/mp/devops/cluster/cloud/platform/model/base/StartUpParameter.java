package cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 启动参数
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("启动参数")
public class StartUpParameter implements Serializable {

    /**
     * 参数类型：环境变量/命令参数/端口映射
     */
    @ApiModelProperty("参数类型：环境变量/命令参数/端口映射")
    private String type;

    /**
     * 参数名称
     */
    @ApiModelProperty("参数名称")
    private String name;

    /**
     * 参数说明
     */
    @ApiModelProperty("参数说明")
    private String desc;

    /**
     * 默认值
     */
    @ApiModelProperty("默认值")
    private String defaultVal;

    /**
     * 是否可选
     */
    @ApiModelProperty("是否可选")
    private String required;

    /**
     * 容器端口
     */
    @ApiModelProperty("容器端口")
    private String containerPort;

    /**
     * 主机端口
     */
    @ApiModelProperty("主机端口")
    private String hostPort;

    /**
     * 命令
     */
    @ApiModelProperty("命令")
    private String cmd;
}
