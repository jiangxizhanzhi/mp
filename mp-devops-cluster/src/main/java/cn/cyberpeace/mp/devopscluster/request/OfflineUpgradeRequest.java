package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * 网络分页请求信息
 */
@Data
public class OfflineUpgradeRequest {

    @NotEmpty
    @ApiModelProperty(value = "镜像uuid", required = true)
    private String uuid;

    @NotEmpty
    @ApiModelProperty(value = "当前镜像名称", required = true)
    private String currentImageName;

    @ApiModelProperty(value = "上传镜像的名称")
    private String uploadFilename;

    @ApiModelProperty(value = "上传方式")
    private String uploadType;

}
