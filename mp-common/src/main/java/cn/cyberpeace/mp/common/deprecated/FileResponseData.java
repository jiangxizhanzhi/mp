package cn.cyberpeace.mp.common.deprecated;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class FileResponseData {

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "编码")
    private String code;

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "信息")
    private String message;

    
    @ApiModelProperty(value = "成功与否")
    private boolean success = true;

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件路径")
    private String filePath;

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件类型")
    private String fileType;

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件下载url")
    private String httpUrl;

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件下载内部url")
    private String innerHttpUrl;

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "token")
    private String token;

    
    @JsonInclude(Include.NON_NULL)
    @ApiModelProperty(value = "文件大小")
    private Long fileSize;


    public FileResponseData() {
    }

    public FileResponseData(boolean success) {
        this.success = success;
    }

}
