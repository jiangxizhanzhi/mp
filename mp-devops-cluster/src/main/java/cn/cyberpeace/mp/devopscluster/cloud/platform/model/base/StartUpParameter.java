package cn.cyberpeace.mp.devopscluster.cloud.platform.model.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("启动参数")
public class StartUpParameter implements Serializable {

    
    @ApiModelProperty("参数类型：环境变量/命令参数/端口映射")
    private String type;

    
    @ApiModelProperty("参数名称")
    private String name;

    
    @ApiModelProperty("参数说明")
    private String desc;

    
    @ApiModelProperty("默认值")
    private String defaultVal;

    
    @ApiModelProperty("是否可选")
    private String required;

    
    @ApiModelProperty("容器端口")
    private String containerPort;

    
    @ApiModelProperty("主机端口")
    private String hostPort;

    
    @ApiModelProperty("命令")
    private String cmd;
}
