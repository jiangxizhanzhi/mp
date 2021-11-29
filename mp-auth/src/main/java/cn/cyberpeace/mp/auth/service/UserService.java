package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.excel.ExportUser;
import cn.cyberpeace.mp.auth.dto.excel.ImportUser;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserRequest;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.BaseModelService;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface UserService extends BaseModelService<UserModel, UserQueryParams> {

    UserDto getUserDto(String id);

    UserDto getCurrentUserDto();

    UserModel add(UserRequest createRequest);

    UserModel modify(UserRequest modifyRequest);

    UserModel getByUserName(String userName);

    UserModel getByIdent(String ident);

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
     * 审核user
     * @param userId
     * @return
     */
    boolean approve(String userId);


    String batchApprove(List<String> userIds);

    void downloadExcelTemplate();

    void importExcel(List<ImportUser> list);

    List<ExportUser> exportExcel(List<UserModel> users);

    boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException;

    void processExpiredStatus(UserDto user);

    // userModel转换为userdto，附加组织，角色信息
    UserDto userModel2UserDto(UserModel userModel);

    // userModel转换为userdto，附加角色信息，不包括组织相关信息
    UserDto userModel2UserDtoWithoutOrg(UserModel userModel);

    UserDto doModel2Dto(UserModel userModel, boolean needOrg, boolean needRole);

//    PageInfo onlineReport(UserOnlineReportPageRequest pageRequest);

    List<UserModel> listNotAudited();

    List<UserDto> listByUserIds(String[] userIds);

    int totalUserCnt();

    boolean isAdmin(String userId);

}
