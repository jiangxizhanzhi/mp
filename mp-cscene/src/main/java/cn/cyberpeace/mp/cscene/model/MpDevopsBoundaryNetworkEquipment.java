package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

import java.time.LocalDateTime;

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
     * 图标
     */
    private String logo;
    /**
     * ip连接地址
     */
    private String ipConnect;
    /**
     * 端口号
     */
    private Integer port;
    /**
     * 接入协议
     */
    private String protocol;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 镜像类型
     */
    private String imageType;
}