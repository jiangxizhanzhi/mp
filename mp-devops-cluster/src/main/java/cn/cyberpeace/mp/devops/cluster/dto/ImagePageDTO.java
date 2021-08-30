package cn.cyberpeace.mp.devops.cluster.dto;

import cn.cyberpeace.mp.devops.cluster.enums.PublicEnum;
import cn.cyberpeace.mp.devops.cluster.enums.QuickCreateSupportEnum;
import cn.cyberpeace.mp.devops.cluster.enums.RoleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImagePageDTO extends BasePageDTO{

    /**
     * 角色
     */
    private RoleEnum role;

    /**
     * 系统类型
     */
    private Integer mainCategory;

    /**
     * 子分类
     */
    private Integer subCategory;

    /**
     * 镜像类型
     */
    private String imageType;

    /**
     * 镜像名称
     */
    private String name;

    /**
     * 用户ID
     */
    private String owner;

    /**
     * 是否公开
     */
    private PublicEnum isPublic;

    /**
     * 是否支持快速创建
     */
    private QuickCreateSupportEnum quickCreateSupport;


}
