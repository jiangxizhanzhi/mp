package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 边界网络设备
 *
 * @since 2024-03-28 16:41:10
 */
@Data
@Table("mp_devops_boundary_network_equipment")
public class MpDevopsBoundaryNetworkEquipment extends BaseModel {
    /**
     * 主键
     */
    private Integer pk;
    /**
     * ID
     */
    private String id;
    /**
     * 设备类型
     */
    private String equipmentType;
    /**
     * 别名
     */
    private String otherName;
    /**
     * 名称
     */
    private String name;
    /**
     * ip连接地址
     */
    private String ipConnect;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}