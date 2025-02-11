package cn.cyberpeace.mp.cscene.controller.request;


import java.util.List;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceCheckRequest {

    @ApiModelProperty(value = "场景id")
    private String csceneId;

    @ApiModelProperty(value = "check节点id")
    String nodeId;

    @ApiModelProperty(value = "checker脚本文件绝对路径")
    String scriptFilePath;

    @ApiModelProperty(value = "脚本参数")
    List<String> scriptParams;

}
