package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@Table("mp_auth_client_details")
public class ClientDetailsModel extends BaseModel implements Serializable {
    
    public static final Integer CLIENT_TYPE_OAUTH2 = 1;
    
    public static final Integer CLIENT_TYPE_UNAUTH = 2;

    @ApiModelProperty("客户端ID")
    private String clientId;
    @ApiModelProperty("资源ID")
    private String resourceIds;
    @ApiModelProperty("客户端秘钥")
    private String clientSecret;
    @ApiModelProperty("类型")
    private Integer type;
    @ApiModelProperty("权限范围")
    private String scope;
    @ApiModelProperty("授权类型")
    private String authorizedGrantTypes;
    @ApiModelProperty("跳转地址")
    private String webServerRedirectUri;
    @ApiModelProperty("权限集合")
    private String authorities;
    @ApiModelProperty("accesstoken有效期")
    private Integer accessTokenValidity;
    @ApiModelProperty("refreshtoken有效期")
    private Integer refreshTokenValidity;
    @ApiModelProperty("附加信息")
    private String additionalInformation;
    @ApiModelProperty("是否自动批准")
    private String autoapprove;
}
