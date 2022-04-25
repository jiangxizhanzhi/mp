package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.Resource;
import cn.cyberpeace.mp.devops.cluster.request.BaseRequest;
import cn.cyberpeace.mp.devops.cluster.request.DockerInstallerData;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstallerDTO extends BaseRequest {

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
    private List<Resource> resourceList;


}
