package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeFileActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "文件路径(fdfs路径)")
    private String filePath;
    @ApiModelProperty(value = "目标路径")
    private String destPath;
}
