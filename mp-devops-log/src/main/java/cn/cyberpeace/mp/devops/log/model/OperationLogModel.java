package cn.cyberpeace.mp.devops.log.model;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
import io.swagger.annotations.ApiModelProperty;
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
    private SiteTypeEnums siteType = SiteTypeEnums.MP;

    /**
     * 日志来源,例如
     */
    private String from;

    /**
     * 日志类型：设备日志、系统日志、业务日志、用户日志、场景日志
     */
    private Integer logType;

    /**
     * 操作模块，模块,0:系统，1：工具，2:课程，3：资源，4：账号，5：场景，6：职业体系，7：考试，8：习题集管理，9：夺旗解题，10：理论基础，11：试卷，12：任务，13：流量，14：探针，15：靶标
     */
    private String opModule;

    /**
     * 操作类型，0:创建，1：更新，2：删除，3：登录登出
     */
    private String opType;

    /**
     * 操作对象
     */
    private String opTarget;

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
     * 内容
     */
    private String content;

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
