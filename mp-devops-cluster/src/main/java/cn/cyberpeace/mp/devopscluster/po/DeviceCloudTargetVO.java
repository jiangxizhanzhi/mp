package cn.cyberpeace.mp.devopscluster.po;

import cn.cyberpeace.mp.base.model.BaseModel;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeviceCloudTargetVO extends BaseModel {

    /**
     * 主键
     */
    private Integer pk;
    /**
     * ID
     */
    private String id;
    /**
     * 服务名称
     */
    private String name;
    /**
     * 云类型
     */
    private String cloudType;
    /**
     * 别名
     */
    private String otherName;
    /**
     * 云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储 )
     */
    private String cloudServiceType;
    /**
     * 子类型
     */
    private String subType;
    /**
     * 操作系统
     */
    private String operatingSystem;
    /**
     * 操作系统状态
     */
    private String operatingSystemStatus;
    /**
     * 源类型(1:云平台 2:用户上传)
     */
    private Integer sourceType;


    /**
     * 网络类型
     */
    private String netType;
    /**
     * 网络服务名称
     */
    private String netServiceName;


    /**
     * 计算类型
     */
    private String calculateType;
    /**
     * publisher
     */
    private String publisher;
    /**
     * offer
     */
    private String offer;
    /**
     * sku
     */
    private String sku;


    /**
     * 容器类型
     */
    private String containerType;
    /**
     * 用户上传镜像名称
     */
    private String uploadTargetName;


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
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}
