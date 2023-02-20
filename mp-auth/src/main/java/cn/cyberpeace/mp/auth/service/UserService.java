package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.excel.ExportUserBo;
import cn.cyberpeace.mp.auth.bo.excel.ExportUserEnBo;
import cn.cyberpeace.mp.auth.bo.excel.ImportUserBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.bo.user.UserOrgRoleBo;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.user.UserRequest;
import cn.cyberpeace.mp.auth.service.param.UserQueryParams;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface UserService extends BaseModelService<UserModel, UserQueryParams> {

    UserBo getUserDto(String id);

    UserBo getUserDtoNoRestrict(String id);

    List<UserModel> getUserModelNoRestrictByUserIds(List<String> ids);

    UserBo getCurrentUserDto();

    UserModel add(UserRequest createRequest);

    UserModel modify(UserRequest modifyRequest);

    UserModel getByUserName(String userName);

    List<UserModel> listByUserName(List<String> userNames);

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

    void importUsers(List<ImportUserBo> list);

    List<ExportUserBo> exportExcel(List<UserModel> users);

    List<ExportUserEnBo> exportExcelEn(List<UserModel> users);

    boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException;

    void processExpiredStatus(UserBo user);

    // userModel转换为userdto，附加组织，角色信息
    UserBo userModel2UserDto(UserModel userModel);

    // userModel转换为userdto，附加角色信息，不包括组织相关信息
    UserBo userModel2UserDtoWithoutOrg(UserModel userModel);

    List<UserBo> userOrgRoleDtos2UserDtos(List<UserOrgRoleBo> userOrgRoleBos);

    UserBo doModel2Dto(UserModel userModel, boolean needOrg, boolean needRole);

    List<UserModel> listNotAudited();

    List<UserBo> listByUserIds(String[] userIds);

    List<UserModel> listByIds(String[] userIds);

    int totalUserCnt();

    String getDefaultUserAvatar();

    void setDefaultIfNoAvatar(UserModel userModel);

    void batchSetDefaultIfNoAvatar(List<UserBo> userBos);

    ServiceResult<Integer> bulkCreateWithPasswordPreSet(List<UserModel> objs);

   List<String> listExistedUserNames(List<String> userNames, Integer limit);

}
