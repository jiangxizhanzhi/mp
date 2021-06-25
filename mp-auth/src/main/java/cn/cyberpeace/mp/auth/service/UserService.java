package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.BaseModelService;
import org.springframework.web.multipart.MultipartFile;

public interface UserService extends BaseModelService<UserModel> {

    UserModel getByUserName(String userName);

    UserModel getByMobile(String mobile);

    boolean resetSelfPwd(String oldPwd, String newPwd);

    boolean resetPwd(String userId, String newPwd);

    /**
     * 禁用
     * @param userId
     * @return
     */
    boolean disable(String userId);

    /**
     * 启用
     * @param userId
     * @return
     */
    boolean enable(String userId);

    /**
     * 审批user
     * @param userId
     * @return
     */
    boolean approve(String userId);

    void downloadExcelTemplate();

    void importExcel();

    void exportExcel();

    boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException;

}
