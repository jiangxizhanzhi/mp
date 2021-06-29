package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends BaseModelService<UserModel, UserQueryParams> {

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

    void importExcel(List<ImportUser> list);

    void exportExcel();

    boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException;

}
