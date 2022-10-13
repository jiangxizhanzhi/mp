package cn.cyberpeace.mp.log.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 审计日志
 * @Author shgang
 * @Date 2022/9/13
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table("mp_devops_log_operation")
public class OperateLogSqlModel extends BaseModel {

    /**
     * 站点类型：应用、中台、前端、平台
     */
    @DefaultInt
    private Integer siteType;

    /**
     * 日志来源,例如
     */
    @DefaultStr
    private String from;

    /**
     * 日志类型：设备日志、系统日志、业务日志、用户日志、场景日志
     */
    @DefaultInt
    private Integer logType;

    /**
     * 操作模块，模块,0:系统，1：工具，2:课程，3：资源，4：账号，5：场景，6：职业体系，7：考试，8：习题集管理，9：夺旗解题，10：理论基础，11：试卷，12：任务，13：流量，14：探针，15：靶标
     */
    @DefaultStr
    private String opModule;

    /**
     * 跟opModule一一对应，opModule存储国际化之后的字符串，opModuleID用来存储对应的数字，用于检索
     */
    @DefaultInt(-1)
    private Integer opModuleId;

    /**
     * 操作类型，0:创建，1：更新，2：删除，3：登录登出
     */
    @DefaultStr
    private String opType;

    /**
     * 跟opType一一对应，opType存储国际化之后的字符串，opTypeID用来存储对应的数字，用于检索
     */
    @DefaultInt(-1)
    private Integer opTypeId;

    /**
     * 操作对象
     */
    @DefaultStr
    private String opTarget;

    /**
     * 跟opTarget一一对应，opTarget存储国际化之后的字符串，opTargetID用来存储对应的数字，用于检索
     */
    @DefaultInt(-1)
    private Integer opTargetId;

    /**
     * 操作主题
     */
    @DefaultStr
    private String title;

    /**
     * 用户ID
     */
    @DefaultStr
    private String userId;

    /**
     * 用户名称
     */
    @DefaultStr
    private String userName;

    /**
     * 用户类型（用户，客户端）
     */
    @DefaultStr
    private String userType;

    /**
     * 内容
     */
    @DefaultStr
    private String content;

    /**
     * 描述
     */
    @DefaultStr
    private String description;

    /**
     * 操作时间
     */
    private LocalDateTime opTime = LocalDateTime.now();

    /**
     * 操作人IP
     */
    @DefaultStr
    private String opIp;

}
