package cn.cyberpeace.mp.devopscluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网络分页请求信息
 */
@Data
public class OfflineUpgradeRequest {

    @ApiModelProperty(value = "镜像uuid")
    private String uuid;

    @ApiModelProperty(value = "当前镜像名称")
    private String currentImageName;

    @ApiModelProperty(value = "上传镜像的名称")
    private String uploadFilename;

    @ApiModelProperty(value = "上传方式")
    private String uploadType;

}
