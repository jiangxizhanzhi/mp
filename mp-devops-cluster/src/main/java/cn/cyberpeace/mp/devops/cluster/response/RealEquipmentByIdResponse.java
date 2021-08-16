package cn.cyberpeace.mp.devops.cluster.response;

import cn.cyberpeace.mp.devops.cluster.converter.model.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.converter.model.SystemInfo;
import io.swagger.annotations.ApiModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "实装分页返回信息")
public class RealEquipmentByIdResponse extends BaseCloudPlatformResponse{

    /**
     * UUID
     */
    private String id;

    /**
     * 名称
     */
    private String name;

    /**
     * 图标
     */
    private String logo;

    /**
     * 角色
     */
    private Integer role;

    /**
     * 分类
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 系统信息
     */
    private SystemInfo systemInfo;

    /**
     * 实体信息
     */
    private ExtEntity extEntity;

    /**
     * 描述
     */
    private String description;



}
