package cn.cyberpeace.mp.devops.log.request;

import cn.cyberpeace.mp.devops.log.enums.SiteTypeEnums;
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
public class OperationLogPageRequest extends BasePageRequest{

    /**
     * 站点类型，0：中台
     */
    @ApiModelProperty(value = "0:中台",hidden = true)
    private SiteTypeEnums siteType = SiteTypeEnums.MP;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;

    @ApiModelProperty(value = "用户id")
    private String userId;

    @ApiModelProperty(value = "模块")
    private String module;

    @ApiModelProperty(value = "操作类型")
    private String opType;

}
