package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName ISOFileRequest
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
public class ISOFileRequest {
    @ApiModelProperty("临时文件uuid")
    private String tmpdirUuid;
    @ApiModelProperty("文件名称")
    private String fileName;
}
