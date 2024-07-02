package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.UserModel;


/**
 * @ClassName UserCheckService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 */
public interface UserCheckService {

    /**
     * check
     *
     * @param userModel
     */
    void check(UserModel userModel);

}
