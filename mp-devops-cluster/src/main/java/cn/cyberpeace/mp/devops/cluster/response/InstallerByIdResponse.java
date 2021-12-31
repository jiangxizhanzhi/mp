package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Resource;
import cn.cyberpeace.mp.devops.cluster.request.DockerInstallerData;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "单个服务返回信息")
public class InstallerByIdResponse extends BaseCloudPlatformResponse{

    /**
     * UUID
     */
    @ApiModelProperty("uuid")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 图标
     */
    @ApiModelProperty(value = "图标")
    private String logo;

    /**
     * 分类
     */
    @ApiModelProperty(value = "分类,以|分割")
    private String categories;

    /**
     * 服务类型
     */
    @ApiModelProperty(value = "服务类型")
    private Integer deployType;

    @ApiModelProperty("docker参数")
    private DockerInstallerData dockerInstallerData;

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表")
    @Builder.Default
    private List<Resource> resourceList = new ArrayList<>();


    /**
     * 关联镜像数量
     */
    @ApiModelProperty(value = "关联镜像数量")
    private Integer deviceCount;
}
