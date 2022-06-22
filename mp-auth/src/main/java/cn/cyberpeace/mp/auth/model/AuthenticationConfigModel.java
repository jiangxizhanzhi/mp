package cn.cyberpeace.mp.auth.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/06/21 15:15
 */
@Data
@NoArgsConstructor
@Table("mp_auth_authentication_config")
public class AuthenticationConfigModel extends BaseModel {

    private String jwtSecret;

    public AuthenticationConfigModel(String jwtSecret) {
        this.jwtSecret = jwtSecret;
    }
}
