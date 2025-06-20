package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel("实体USB对象")
public class DockerParams {
    @ApiModelProperty("容器 run 参数")
    private Boolean run;
    @ApiModelProperty("容器 interactive 参数")
    private Boolean interactive;
    @ApiModelProperty("容器 privileged 参数")
    private Boolean privileged;
    @ApiModelProperty("容器 环境变量 参数")
    private String environmentStr;
    @ApiModelProperty("容器 tty 参数")
    private Boolean tty;
    @ApiModelProperty("容器 其余自定义 参数")
    private String hostConfigExStr;
    @ApiModelProperty("高级选项的开关")
    private Boolean options;
    /**
     * 镜像来源，1：基础镜像，2：CP-CLOUD已有镜像，3：自定义上传
     * 用来给前端判断，是否要展示高级选项相关的输入框
     */
    @ApiModelProperty("镜像来源，1：基础镜像，2：CP-CLOUD已有镜像，3：自定义上传 用来给前端判断，是否要展示高级选项相关的输入框")
    private Integer imageSource;
}
