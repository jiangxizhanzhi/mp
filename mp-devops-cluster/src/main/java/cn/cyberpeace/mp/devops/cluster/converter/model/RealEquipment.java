package cn.cyberpeace.mp.devops.cluster.converter.model;

import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.model.common.ExtSystemInfo;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RealEquipment extends BasePageConvertModel{

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
    private ExtSystemInfo extSystemInfo;

    /**
     * 实体信息
     */
    private ExtEntity extEntity;

    /**
     * 描述
     */
    private String description;

    /**
     * 网关信息
     */
    private ExtGateway extGateway;

    /**
     * 在位状态
     */
    private StatusEnum inPosition;

    /**
     * 使用状态
     */
    private StatusEnum useStatus;

    /**
     * 引用状态
     */
    private Integer referenceStatus;
}
