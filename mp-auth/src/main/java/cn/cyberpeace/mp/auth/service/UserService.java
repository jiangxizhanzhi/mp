package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.service.BaseModelService;

public interface UserService extends BaseModelService<UserModel> {

    UserModel getByUserName(String userName);

    UserModel getByMobile(String mobile);

    boolean resetSelfPwd(String newPwd);

    boolean resetPwd(String userId, String newPwd);

}
