package cn.cyberpeace.mp.devops.log.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * 操作日志实体类
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class OperationLogRequest {

    /**
     * 日志来源
     */
    @ApiModelProperty(value = "日志来源")
    private String from;

    /**
     * 日志类型：设备日志、系统日志、业务日志、用户日志、场景日志
     */
    @ApiModelProperty(value = "日志类型：0：系统日志、1：用户日志、2：场景日志,3:业务日志")
    private Integer logType;

    /**
     * 操作模块
     * 对应mp_operate_log索引中的opModuleID字段
     */
    @ApiModelProperty(value = "模块 0:系统, 1:工具, 2:课程, 3:资源, 4:账号, 5:场景, 6:职业体系, 7:考试, 8:习题集管理, 9:夺旗解题, 10:理论基础, 11:试卷, 12:任务, 13:流量, 14:探针, 15:靶标, 16:红蓝对抗, 17:单兵训练, 18:团队训练",required = true)
    @NotNull
    private Integer opModule;

    /**
     * 操作类型：新增、更新、查询、删除
     * 对应mp_operate_log索引中的opTypeID字段
     */
    @ApiModelProperty(value = "操作类型 0:登入, 1:登出, 2:创建, 3:更新, 4:删除, 5:学习, 6:练习, 7:考试, 8:训练, 9:测评, 10:参赛, 11:停止, 12:暂停, 13:启动, 14:恢复")
    private Integer opType;

    /**
     * 操作对象
     * 对应mp_operate_log索引中的opTarget.keyword字段
     */
    @ApiModelProperty(value = "操作对象，例如课程、用户等")
    private String opTarget;

    /**
     * 操作主题
     */
    @ApiModelProperty(value = "操作主题")
    private String title;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID",required = true)
    @NotBlank
    private String userId;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称",required = true)
    @NotBlank
    private String userName;

    /**
     * 用户类型（用户，客户端）
     */
    @ApiModelProperty(value = "用户类型（用户，客户端）")
    private String userType;

    /**
     * 内容
     */
    @ApiModelProperty(value = "内容")
    private String content;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String description;

    /**
     * 操作人IP
     */
    @ApiModelProperty(value = "操作人IP")
    private String opIp;

}
