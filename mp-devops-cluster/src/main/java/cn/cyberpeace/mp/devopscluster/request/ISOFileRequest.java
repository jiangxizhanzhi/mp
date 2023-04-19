package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class ISOFileRequest {
    @ApiModelProperty("临时文件uuid")
    private String tmpdirUuid;
    @ApiModelProperty("文件名称")
    private String fileName;
}
