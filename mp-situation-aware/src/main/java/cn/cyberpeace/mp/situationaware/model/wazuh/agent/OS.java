package cn.cyberpeace.mp.situationaware.model.wazuh.agent;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("操作系统")
public class OS {

    @ApiModelProperty(value = "构建版本")
    private String build;

    @ApiModelProperty(value = "架构")
    private String arch;

    @ApiModelProperty(value = "主版本号")
    private String major;

    @ApiModelProperty(value = "次版本号")
    private String minor;

    @ApiModelProperty(value = "名称")
    private String name;

    @ApiModelProperty(value = "平台")
    private String patform;

    @ApiModelProperty(value = "uname")
    private String uname;

    @ApiModelProperty(value = "版本")
    private String version;


}
