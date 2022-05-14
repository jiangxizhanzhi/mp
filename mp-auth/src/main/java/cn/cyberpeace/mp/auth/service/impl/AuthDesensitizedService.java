package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.OperatePermissionService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.auth.util.UserUtils;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.DesensitizedUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/5/10 14:40
 */
@Slf4j
@Service
public class AuthDesensitizedService {

    @Autowired
    private UserService userService;

    @Autowired
    private OperatePermissionService operatePermissionService;

    public void desensitiveUserModels(List<UserModel> userModels) {
        if (!operatePermissionService.isLoginUserAdmin()) {
            // 普通用户查询信息需要部分字段脱敏，管理员不用
            userModels.forEach(u -> {
                doDesensitization(u);
            });
        }
    }


    public void desensitiveUserDtos(List<UserDto> userDtos) {
        if (CollectionUtil.isNotEmpty(userDtos) && !operatePermissionService.isLoginUserAdmin()) {
            // 普通用户查询信息需要部分字段脱敏，管理员不用
            userDtos.forEach(u -> {
                doDesensitization(u);
            });
        }
    }

    private void doDesensitization(UserModel userModel) {
        if (Objects.isNull(userModel)) {
            return;
        }
        // 地址
        //userModel.setAddress(DesensitizedUtil.desensitized(userModel.getAddress(), DesensitizedUtil.DesensitizedType.ADDRESS));
        // 手机
        //userModel.setMobile(DesensitizedUtil.desensitized(userModel.getMobile(), DesensitizedUtil.DesensitizedType.MOBILE_PHONE));
        // 身份证
        //userModel.setIdCard(DesensitizedUtil.desensitized(userModel.getIdCard(), DesensitizedUtil.DesensitizedType.ID_CARD));
        // 邮箱
        userModel.setEmail(DesensitizedUtil.desensitized(userModel.getEmail(), DesensitizedUtil.DesensitizedType.EMAIL));
        // 密码
        userModel.setPassword(DesensitizedUtil.desensitized(userModel.getPassword(), DesensitizedUtil.DesensitizedType.PASSWORD));
    }

}
