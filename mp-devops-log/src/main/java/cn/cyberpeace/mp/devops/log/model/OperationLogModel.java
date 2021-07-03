package cn.cyberpeace.mp.devops.log.model;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 操作日志实体类
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OperationLogModel {

    /**
     * 站点类型：应用、中台、前端、平台
     */
    private SiteTypeEnums siteType;

    /**
     * 日志类型：设备日志、系统日志、业务日志、用户日志、场景日志
     */
    private String logType;

    /**
     * 操作模块
     */
    private String opModule;

    /**
     * 操作类型：新增、更新、查询、删除
     */
    private String opType;

    /**
     * 操作主题
     */
    private String title;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名称
     */
    private String userName;

    /**
     * 用户类型（用户，客户端）
     */
    private String userType;

    /**
     * 描述
     */
    private String description;

    /**
     * 操作时间
     */
    private LocalDateTime opTime = LocalDateTime.now();

    /**
     * 操作人IP
     */
    private String opIp;

}
