package cn.cyberpeace.mp.cscene.controller.request;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceCheckRequest {

    @ApiModelProperty(value = "场景id")
    private String mpScene;

    @ApiModelProperty(value = "check节点id")
    private String nodeId;

    @ApiModelProperty(value = "checker脚本文件绝对路径")
    private String scriptFilePath;

    @ApiModelProperty(value = "脚本参数")
    private String scriptParams;

}
