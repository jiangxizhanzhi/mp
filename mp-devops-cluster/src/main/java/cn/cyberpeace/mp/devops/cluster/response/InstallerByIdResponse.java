package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtDocker;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtNormal;
import cn.cyberpeace.mp.devops.cluster.model.common.Resource;
import cn.cyberpeace.mp.devops.cluster.model.common.StartUpParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

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
    @ApiModelProperty("名称")
    private String name;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String logo;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private String categories;

    /**
     * 服务类型：windows服务/docker服务
     */
    @ApiModelProperty("服务类型")
    private Integer deployType;

    /**
     * 对应的docker服务的扩展信息
     */
    @ApiModelProperty("对应的docker服务的扩展信息")
    private ExtDocker extDocker;

    /**
     * 启动参数（包装对象）
     */
    @ApiModelProperty(value = "启动参数")
    private List<StartUpParameter> startUpParameterList;

    /**
     * 对应的windows服务扩展信息
     */
    @ApiModelProperty("对应的windows服务的扩展信息")
    private ExtNormal extNormal;

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表")
    private List<Resource> resourceList;
}
