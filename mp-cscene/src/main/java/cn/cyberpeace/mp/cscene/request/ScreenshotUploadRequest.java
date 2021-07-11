package cn.cyberpeace.mp.cscene.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("上传截屏文件请求")
public class ScreenshotUploadRequest {

    
    @ApiModelProperty(required = true, value = "场景名称")
    @NotBlank(message = "sceneName can not be empty")
    private String sceneName;

    
    @ApiModelProperty(value = "文件")
    @NotNull(message = "file can not be empty")
    private MultipartFile file;

    
    @ApiModelProperty(required = true, value = "场景id")
    private String scene;

    
    @ApiModelProperty(required = true, value = "节点id")
    private String nodeId;

}
