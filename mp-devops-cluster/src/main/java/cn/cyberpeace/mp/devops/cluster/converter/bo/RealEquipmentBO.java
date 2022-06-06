package cn.cyberpeace.mp.devops.cluster.converter.bo;

import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtEntity;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtEntityAccess;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtGateway;
import cn.cyberpeace.mp.devops.cluster.cloud.platform.model.base.ExtSystemInfo;
import cn.cyberpeace.mp.devops.cluster.converter.model.BasePageConvertModel;
import cn.cyberpeace.mp.devops.cluster.enums.StatusEnum;
import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class RealEquipmentBO extends BasePageConvertModel {

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
     * 三级分类
     */
    private Integer thirdCategory;

    /**
     * 系统信息
     */
    private ExtSystemInfo extSystemInfo;

    /**
     * 实体信息
     */
    private ExtEntity extEntity;

    /**
     * 交换机
     */
    private List<ExtEntityAccess> extEntityAccess;

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
