package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.util.UserUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/2 11:15
 */
@Service
public class OperatorAuthorityCheckService {

    @Autowired
    private UserService userService;

    public void check() {
        UserModel currentUser = UserUtils.getLoginUser();
        if (Objects.nonNull(currentUser)) {
            UserDto userDto = userService.userModel2UserDtoWithoutOrg(currentUser);
            boolean isSupserUser = Objects.equals(userDto.getIsSuperUser(), 1) ? true : false;
            boolean isAdmin = Objects.equals(userDto.getIsAdmin(), 1) ? true : false;

        }
    }

}
