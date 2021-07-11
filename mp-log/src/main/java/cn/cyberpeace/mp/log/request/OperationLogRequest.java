package cn.cyberpeace.mp.log.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("操作日志请求体")
public class OperationLogRequest {

    
    @ApiModelProperty(value = "日志来源")
    private String from;

    
    @ApiModelProperty(value = "日志类型：0：系统日志、1：用户日志、2：场景日志,3:业务日志")
    private Integer logType;

    
    @ApiModelProperty(value = "模块 0:系统, 1:工具, 2:课程, 3:资源, 4:账号, 5:场景, 6:职业体系, 8:习题集管理, " +
            "9:夺旗解题, 10:理论基础, 11:试卷, 12:任务, 13:流量, 14:探针, 15:靶标, 16:红蓝对抗, 17:单兵训练, 18:团队训练, " +
            "19:赛场, 20:理论赛, 21:运维赛, 22:夺旗赛, 23:攻防赛, 24:渗透赛, 25:战队", required = true)
    @NotNull
    private Integer opModule;

    
    @ApiModelProperty(value = "操作类型 0:登入, 1:登出, 2:创建, 3:更新, 4:删除, 5:学习, 6:练习, 7:考试, 8:训练, 9:测评, 10:参赛, 11:停止, 12:暂停, 13:启动, 14:恢复")
    private Integer opType;

    
    @ApiModelProperty(value = "操作对象，例如课程、用户等")
    private String opTarget;

    
    @ApiModelProperty(value = "操作主题")
    private String title;

    
    @ApiModelProperty(value = "用户ID",required = true)
    @NotBlank
    private String userId;

    
    @ApiModelProperty(value = "用户名称",required = true)
    @NotBlank
    private String userName;

    
    @ApiModelProperty(value = "用户类型（用户，客户端）")
    private String userType;

    
    @ApiModelProperty(value = "内容")
    private String content;

    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "操作人IP")
    private String opIp;

}
