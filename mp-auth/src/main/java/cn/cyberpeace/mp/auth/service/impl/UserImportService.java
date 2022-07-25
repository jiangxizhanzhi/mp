package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserRoleModel;
import cn.cyberpeace.mp.auth.service.OrgUserService;
import cn.cyberpeace.mp.auth.service.UserCacheService;
import cn.cyberpeace.mp.auth.service.UserRoleService;
import cn.cyberpeace.mp.auth.service.UserService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/7/7 17:14
 */
@Slf4j
@Service
public class UserImportService {

    @Autowired
    private UserService userService;

    @Autowired
    private OrgUserService orgUserService;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private UserCacheService userCacheService;


    /**
     * 导入用户
     * @param userModels
     * @param userRoleModels
     * @param orgUserModels
     */
    @Transactional(rollbackFor = Exception.class)
    public void doImport(List<UserModel> userModels, List<UserRoleModel> userRoleModels, List<OrgUserModel> orgUserModels) {
        if (CollectionUtil.isNotEmpty(userModels)) {
            ServiceResult<Integer> re = userService.bulkCreateWithPasswordPreSet(userModels);
            if (!Objects.equals(userModels.size(), re.getSource())) {
                log.error("userModels.size:{} and re.getSource:{} not same!", userModels.size(), re.getSource());
                throw new MpRuntimeException(AuthErrorCode.USER_ADD_ERROR);
            }

            ServiceResult<Integer> re1 = userRoleService.bulkCreate(userRoleModels);
            if (!Objects.equals(userRoleModels.size(), re1.getSource())) {
                log.error("userRoleModels.size:{} and re.getSource:{} not same!", userRoleModels.size(), re1.getSource());
                throw new MpRuntimeException(AuthErrorCode.USER_ROLE_ADD_ERROR);
            }

            ServiceResult<Integer> re2 = orgUserService.bulkCreate(orgUserModels);
            if (!Objects.equals(orgUserModels.size(), re2.getSource())) {
                log.error("orgUserModels.size:{} and re.getSource:{} not same!", orgUserModels.size(), re2.getSource());
                throw new MpRuntimeException(AuthErrorCode.USER_ORG_ADD_ERROR);
            }
        }

        userCacheService.evictAllUserCache();
    }


}
