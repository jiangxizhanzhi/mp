package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_auth_client_details")
public class ClientDetailsModel extends BaseModel {
    // Oauth2渠道
    public static final Integer CLIENT_TYPE_OAUTH2 = 1;
    // 免认证渠道
    public static final Integer CLIENT_TYPE_UNAUTH = 2;

    private String clientId;
    private String resourceIds;
    private String clientSecret;
    private Integer type;
    private String scope;
    private String authorizedGrantTypes;
    private String webServerRedirectUri;
    private String Authorities;
    private Integer accessTokenValidity;
    private Integer refreshTokenValidity;
    private String additionalInformation;
    private String autoapprove;
}
