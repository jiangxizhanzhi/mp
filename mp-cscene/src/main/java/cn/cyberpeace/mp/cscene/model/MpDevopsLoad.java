package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 载荷同步表(MpDevopsLoad)实体类
 *
 * @since 2024-03-28 16:41:10
 */
@Data
@Table("mp_devops_load")
public class MpDevopsLoad extends BaseModel {
    /**
     * 主键
     */
    private Integer pk;
    /**
     * ID
     */
    private String id;
    /**
     * 工具名称
     */
    private String toolName;
    /**
     * 工具描述
     */
    private String toolDescription;
    /**
     * 工具平台
     */
    private String toolPlatform;
    /**
     * 作用类型
     */
    private String actionType;
    /**
     * 运行平台
     */
    private String runTable;
    /**
     * 控制类型
     */
    private String controlType;
    /**
     * 控制端口
     */
    private String controlPort;
    /**
     * 来源
     */
    private String source;
    /**
     * 帮助文档
     */
    private String readMe;
    /**
     * 封面
     */
    private String cover;
    /**
     * 默认命令
     */
    private String defaultCmd;
    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
    /**
     * 创建时间
     */
    private LocalDateTime createTime;
    /**
     * 同步时间
     */
    private LocalDateTime synchronizationTime;
    /**
     * 创建人Id
     */
    private String creatorId;
    /**
     * 漏洞情报
     */
    private List Loopholes;
}