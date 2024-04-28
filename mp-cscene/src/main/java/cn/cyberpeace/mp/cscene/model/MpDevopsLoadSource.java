package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 载荷同步设置表(MpDevopsLoadSource)实体类
 *
 * @since 2024-03-28 16:41:10
 */
@Data
@Table("mp_devops_load")
public class MpDevopsLoadSource extends BaseModel {
    /**
     * 主键
     */
    private Integer pk;
    /**
     * 载荷主机
     */
    private String ip;
    /**
     * 同步协议
     */
    private String synchronizationProtocol;
    /**
     * 认证方式(1:密码认证 2:cookie)
     */
    private String authenticationType;
    /**
     * 用户名
     */
    private String userName;
    /**
     * 密码
     */
    private String password;
    /**
     * cookie
     */
    private String cookie;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 创建人Id
     */
    private String creatorId;
}