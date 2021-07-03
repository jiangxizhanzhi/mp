package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserCreateRequest;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService extends BaseModelService<UserModel, UserQueryParams> {

    UserModel add(UserCreateRequest createRequest);

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

    String batchDisable(List<String> userIds);

    /**
     * 启用
     * @param userId
     * @return
     */
    boolean enable(String userId);

    String batchEnable(List<String> userIds);

    /**
     * 审批user
     * @param userId
     * @return
     */
    boolean approve(String userId);


    String batchApprove(List<String> userIds);

    void downloadExcelTemplate();

    void importExcel(List<ImportUser> list);

    List<ExportUser> exportExcel(List<UserModel> users);

    boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException;

    void processExpiredStatus(UserModel user);

}
