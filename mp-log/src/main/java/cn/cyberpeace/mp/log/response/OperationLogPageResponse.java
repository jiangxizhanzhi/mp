package cn.cyberpeace.mp.log.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(value = "日志分页返回信息")
public class OperationLogPageResponse {

    
    @ApiModelProperty(value = "操作主题")
    private String title;

    
    @ApiModelProperty(value = "模块 (0:系统; 1:工具; 2:课程; 3:资源; 4:账号; 5:场景; 6:职业体系; " +
            "8:习题集管理; 9:夺旗解题; 10:理论基础; 11:试卷; 12:任务; 13:流量; 14:探针; 15:靶标；16:红蓝对抗; 17:单兵训练; 18:团队训练;" +
            "19:赛场, 20:理论赛, 21:运维赛, 22:夺旗赛, 23:攻防赛, 24:渗透赛, 25:战队)")
    private String opModule;

    @ApiModelProperty(value = "模块ID")
    private Integer opModuleID;

    
    @ApiModelProperty(value = "操作类型 (0:登入 1:登出 2:创建 3:更新 4:删除 5:学习 6:练习 7:考试 8:训练 9:测评 10:参赛 11:停止 12:暂停 13:启动 14:恢复)")
    private String opType;

    @ApiModelProperty(value = "操作类型ID")
    private Integer opTypeID;

    
    @ApiModelProperty(value = "操作对象")
    private String opTarget;

    @ApiModelProperty(value = "操作对象ID")
    private Integer opTargetID;

    
    @ApiModelProperty(value = "用户ID")
    private String userId;

    
    @ApiModelProperty(value = "用户名称")
    private String userName;

    
    @ApiModelProperty(value = "内容")
    private String content;


    
    @ApiModelProperty(value = "描述")
    private String description;

    
    @ApiModelProperty(value = "操作时间/创建时间")
    private LocalDateTime opTime;

    
    @ApiModelProperty(value = "多少秒之前操作的")
    private Long opMillSecondsBefore;

    
    @ApiModelProperty(value = "操作人IP")
    private String opIp;

}
