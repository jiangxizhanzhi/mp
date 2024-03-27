package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 微软云用户管理
 */
@Data
@Table("mp_auth_cloud_user")
public class MicrosoftAzureUserModel extends BaseModel {

    @ApiModelProperty(value = "主键")
    private Integer pk;

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

    @ApiModelProperty(value = "创建人")
    private String creator;

    @ApiModelProperty(value = "版本号")
    private Integer version;
}
