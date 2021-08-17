package cn.cyberpeace.mp.devops.log.response;

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

    /**
     * 操作主题
     */
    @ApiModelProperty(value = "操作主题")
    private String title;

    /**
     * 操作模块，模块,0:系统，1：工具，2:课程，3：资源，4：账号，5：场景，6：职业体系，7：考试，8：习题集管理，9：夺旗解题，10：理论基础，11：试卷，12：任务，13：流量，14：探针，15：靶标
     */
    @ApiModelProperty(value = "模块",notes = "cn.cyberpeace.mp.devops.log.enums.OpModuleEnums")
    private String opModule;

    /**
     * 操作类型，0:创建，1：更新，2：删除，3：登录登出
     */
    @ApiModelProperty(value = "操作类型",notes = "cn.cyberpeace.mp.devops.log.enums.OpTypeEnums")
    private String opType;

    /**
     * 操作对象
     */
    @ApiModelProperty(value = "操作对象")
    private String opTarget;


    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;

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
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间/创建时间")
    private LocalDateTime opTime;

    /**
     * 操作人IP
     */
    @ApiModelProperty(value = "操作人IP")
    private String opIp;

}
