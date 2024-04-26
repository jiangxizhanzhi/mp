package cn.cyberpeace.mp.devopscluster.po;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultBool;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_devops_cloud_target")
public class DeviceCloudTargetPO extends BaseModel {

    /**
     * 主键
     */
    private Integer pk;
    /**
     * 服务ID
     */
    private String id;
    /**
     * 服务名称
     */
    private String name;
    /**
     * 云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储 )
     */
    private Integer cloudServiceType;
    /**
     * 源类型(1:云平台 2:用户上传)
     */
    private Integer sourceType;
    /**
     * publisherName
     */
    private String publisherName;
    /**
     * offerName
     */
    private String offerName;
    /**
     * sku
     */
    private String sku;
    /**
     * 操作系统
     */
    private String operatingSystem;
    /**
     * 操作系统状态
     */
    private String operatingSystemStatus;
    /**
     * 用户上传镜像名称
     */
    private String uploadTargetName;
    /**
     * 镜像类型：vm，container,simple, entity
     */
    private String imageType;
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
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 靶标创建用户
     */
    private String createUser;
    /**
     * 靶标实例创建用户
     */
    private String instanceCreateUser;
    /**
     * 角色
     */
    private Integer role;
    /**
     * 图标地址
     */
    private String logo;
}
