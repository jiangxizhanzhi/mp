package cn.cyberpeace.mp.devopscluster.po;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_cloud_target_container")
public class DeviceCloudTargetContainer extends BaseModel {

    /**
     * 主键
     */
    private Integer pk;
    /**
     * 服务ID
     */
    private String linkId;
    /**
     * 服务名称
     */
    private String name;
    /**
     * 云类型
     */
    private String cloudType;
    /**
     * 源类型(1:云平台 2:用户上传)
     */
    private Integer sourceType;
    /**
     * 容器类型(容器基础结构 容器管理 容器化应用程序)
     */
    private String containerType;
    /**
     * 子类型(容器实例 容器注册表 Kubernetes服务)
     */
    private String subType;
    /**
     * 用户上传镜像名称
     */
    private String uploadTargetName;
    /**
     * 操作系统
     */
    private String operatingSystem;
    /**
     * 操作系统状态
     */
    private String operatingSystemStatus;
    /**
     * 是否公开，0：私有，1：自定义，2：公开
     */
    private Integer isPublic;
    /**
     * 是否支持快速构建，0：不支持，1：支持
     */
    private Integer quickCreateSupport;
    /**
     * 是否导入，0：false,1:true
     */
    private Integer isImport;
    /**
     * 是否隐藏，0：false,1:true
     */
    private Integer isHide;
    /**
     * 是否自定义
     */
    @DefaultInt(0)
    private Integer isCustom;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 图标地址
     */
    private String logo;
    /**
     * 版本号
     */
    private String version;
    /**
     * 代系
     */
    private String generation;
}
