package cn.cyberpeace.mp.auth.dto.user;

import cn.cyberpeace.mp.auth.model.UserModel;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/22 09:43
 */
@Data
public class UserTmpDto extends UserModel {

    private String orgs;

    private String roles;

}
