 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ClientDetailsModel.java.
 // File size 1682.
 // File create date Fri Jan 17 09:57:15 CST 2025.
 // File update date Fri Jan 17 11:23:12 CST 2025.
 // @Version 1.0.0
package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

 /**
  * 客户端详细信息模型
  *
  * @author 二竹
  * @date 2025/01/17
  */
 @Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
@Table("mp_auth_client_details")
public class ClientDetailsModel extends BaseModel implements Serializable {
     private static final long serialVersionUID = 1L;
    // Oauth2渠道
    public static final Integer CLIENT_TYPE_OAUTH2 = 1;
    // 免认证渠道
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
