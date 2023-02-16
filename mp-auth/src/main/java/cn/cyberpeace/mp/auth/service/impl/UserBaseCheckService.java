package cn.cyberpeace.mp.auth.service.impl;

import cn.cyberpeace.mp.auth.constant.AuthErrorCode;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.UserCheckService;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import org.apache.commons.lang3.StringUtils;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2023/2/16 10:10
 */
public class UserBaseCheckService implements UserCheckService {

    @Override
    public void check(UserModel user) {
        if (StringUtils.isNotBlank(user.getRank())) {
            checkRank(user.getRank());
        }
    }

    private void checkRank(String rank) {
        if (StringUtils.length(rank) < 2 || StringUtils.length(rank) > 20) {
            throw new MpRuntimeException(AuthErrorCode.RANK_LENGTH_ERROR);
        }
    }
}
