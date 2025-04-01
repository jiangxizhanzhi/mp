package cn.cyberpeace.mp.sceneplugincloud.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 微软云用户管理
 */
@Data
@Table("mp_auth_cloud_user")
public class AzureUserModel extends BaseModel {

    @ApiModelProperty(value = "主键")
    private Integer pk;

    @ApiModelProperty(value = "连接地址")
    private String connectIp;

    @ApiModelProperty(value = "云类型")
    private Integer cloudType;

    @ApiModelProperty(value = "tenant_id")
    private String azureTenantId;

    @ApiModelProperty(value = "subscription_id")
    private String azureSubscriptionId;

    @ApiModelProperty(value = "client_id")
    private String azureClientId;

    @ApiModelProperty(value = "client_secret")
    private String azureClientSecret;

    @ApiModelProperty(value = "区域")
    private String area;

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "版本号")
    private Integer version;

    @ApiModelProperty(value = "过期时间")
    private LocalDateTime expireTime;
}
