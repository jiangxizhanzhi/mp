package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "实装返回信息")
public class RealEquipmentByIdResponse extends BaseCloudPlatformResponse{

    /**
     * UUID
     */
    @ApiModelProperty("UUID")
    private String uuid;

    /**
     * 名称
     */
    @ApiModelProperty("名称")
    private String name;

    /**
     * 图标
     */
    @ApiModelProperty("图标")
    private String logo;

    /**
     * 角色
     */
    @ApiModelProperty("角色")
    private Integer role;

    /**
     * 分类
     */
    @ApiModelProperty("分类")
    private Integer mainCategory;

    /**
     * 子分类
     */
    @ApiModelProperty("子分类")
    private Integer subCategory;

    /**
     * 系统信息
     */
    @ApiModelProperty("系统信息")
    private ExtSystemInfo extSystemInfo;

    /**
     * 实体信息
     */
    @ApiModelProperty("实体信息")
    private ExtEntity extEntity;

    /**
     * 扩展网关信息
     */
    private ExtGateway extGateway;

    /**
     * 描述
     */
    @ApiModelProperty("描述")
    private String description;



}
