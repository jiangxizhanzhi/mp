package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class CsceneInstanceNodeUploadFileActionRequest extends CsceneInstanceNodeActionRequest {
    @ApiModelProperty(value = "上传后的文件名")
    private String fileName;
    @ApiModelProperty(value = "文件下载地址")
    private String fileUrl;
    @ApiModelProperty(value = "上传的目录")
    private String targetDir;
}
