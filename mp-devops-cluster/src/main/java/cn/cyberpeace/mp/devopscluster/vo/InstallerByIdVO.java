package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.Resource;
import cn.cyberpeace.mp.devopscluster.request.DockerInstallerData;
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
public class InstallerByIdVO extends BaseVO {

    
    @ApiModelProperty("uuid")
    private String uuid;

    
    @ApiModelProperty(value = "名称")
    private String name;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "图标")
    private String logo;

    
    @ApiModelProperty(value = "分类,以|分割")
    private String categories;

    
    @ApiModelProperty(value = "服务类型")
    private Integer deployType;

    @ApiModelProperty("docker参数")
    private DockerInstallerData dockerInstallerData;

    
    @ApiModelProperty(value = "资源列表")
    @Builder.Default
    private List<Resource> resourceList = new ArrayList<>();


    
    @ApiModelProperty(value = "关联镜像数量")
    private Integer deviceCount;
}
