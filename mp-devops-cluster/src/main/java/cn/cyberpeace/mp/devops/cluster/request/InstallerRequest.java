package cn.cyberpeace.mp.devops.cluster.request;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtDocker;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtNormal;
import cn.cyberpeace.mp.devops.cluster.model.common.Resource;
import cn.cyberpeace.mp.devops.cluster.model.common.StartUpParameter;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "服务管理的新增请求参数")
public class InstallerRequest extends BaseRequest {

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
    @ApiModelProperty(value = "分类")
    private List<String> categories;

    /**
     * 服务类型
     */
    @ApiModelProperty(value = "服务类型")
    private Integer deployType;

    /**
     * 对应的docker服务的扩展信息
     */
    @ApiModelProperty(value = "对应的docker服务的扩展信息")
    private ExtDocker extDocker;

    /**
     * 启动参数（包装对象）
     */
    @ApiModelProperty(value = "启动参数")
    private List<StartUpParameter> startUpParameterList;

    /**
     * 对应的windows服务扩展信息
     */
    @ApiModelProperty(value = "对应的windows服务扩展信息")
    private ExtNormal extNormal;

    /**
     * 资源列表
     */
    @ApiModelProperty(value = "资源列表")
    private List<Resource> resourceList;


}
