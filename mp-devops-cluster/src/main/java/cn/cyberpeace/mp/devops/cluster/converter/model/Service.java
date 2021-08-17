package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.converter.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.converter.model.common.SystemInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Service extends BasePageConvertModel{

    /**
     * UUID
     */
    private String uuid;

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
