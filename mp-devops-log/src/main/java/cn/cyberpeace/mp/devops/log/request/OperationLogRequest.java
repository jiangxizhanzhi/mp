package cn.cyberpeace.mp.devops.log.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
     */
    @ApiModelProperty(value = "模块",notes = "cn.cyberpeace.mp.devops.log.enums.OpModuleEnums")
    private Integer opModule;

    /**
     * 操作类型：新增、更新、查询、删除
     */
    @ApiModelProperty(value = "操作类型",notes = "cn.cyberpeace.mp.devops.log.enums.OpTypeEnums")
    private Integer opTypeEnums;

    /**
     * 操作对象
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
    @ApiModelProperty(value = "用户ID")
    private String userId;

    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    private String userName;

    /**
     * 用户类型（用户，客户端）
     */
    @ApiModelProperty(value = "用户类型（用户，客户端）")
    private String userType;

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
