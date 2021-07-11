package cn.cyberpeace.mp.flow.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量上传返回信息")
public class FlowInfoUploadResponse {

    
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    
    @ApiModelProperty(value = "文件类型")
    private String fileType;

    
    @ApiModelProperty(value = "文件url")
    private String fileUrl;

    
    @ApiModelProperty(value = "第三方id")
    private String thirdId;

    
    @ApiModelProperty(value = "第三方文件名称")
    private String thirdName;

}
