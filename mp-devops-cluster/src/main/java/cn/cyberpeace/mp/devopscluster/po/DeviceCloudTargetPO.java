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
     * 别名
     */
    private String otherName;
    /**
     * 云类型
     */
    private String cloudType;
    /**
     * 云服务类型(1:网络,2:计算,3:容器,4:数据库,5:分析,6:安全性,7:存储 )
     */
    private String cloudServiceType;
    /**
     * 操作系统
     */
    private String operatingSystem;
    /**
     * 操作系统状态
     */
    private String operatingSystemStatus;
    /**
     * 版本号
     */
    private String version;
    /**
     * 代系
     */
    private String generation;
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
}
