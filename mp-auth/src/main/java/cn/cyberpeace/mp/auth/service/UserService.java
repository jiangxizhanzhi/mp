 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name UserService.java.
 // File size 6160.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
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
 * @ClassName UserService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface UserService extends BaseModelService<UserModel, UserQueryParams> {

    /**
     * getUserDto
     *
     * @param id
     * @return
     */
    UserBo getUserDto(String id);

    /**
     * getUserDtoNoRestrict
     *
     * @param id
     * @return
     */
    UserBo getUserDtoNoRestrict(String id);

    /**
     * getUserModelNoRestrictByUserIds
     *
     * @param ids
     * @return
     */
    List<UserModel> getUserModelNoRestrictByUserIds(List<String> ids);

    /**
     * getCurrentUserDto
     *
     * @return
     */
    UserBo getCurrentUserDto();

    /**
     * add
     *
     * @param createRequest
     * @return
     */
    UserModel add(UserRequest createRequest);

    /**
     * modify
     *
     * @param modifyRequest
     * @return
     */
    UserModel modify(UserRequest modifyRequest);

    /**
     * getByUserName
     *
     * @param userName
     * @return
     */
    UserModel getByUserName(String userName);

    /**
     * getByIdent
     *
     * @param ident
     * @return
     */
    UserModel getByIdent(String ident);

    /**
     * getByMobile
     *
     * @param mobile
     * @return
     */
    UserModel getByMobile(String mobile);

    /**
     * resetSelfPwd
     *
     * @param oldPwd
     * @param newPwd
     * @return boolean
     */
    boolean resetSelfPwd(String oldPwd, String newPwd);

    /**
     * resetPwd
     *
     * @param userId
     * @param newPwd
     * @return boolean
     */
    boolean resetPwd(String userId, String newPwd);

    /**
     * 禁用
     *
     * @param userId
     * @return
     */
    boolean disable(String userId);

    /**
     * batchDisable
     *
     * @param userIds
     * @return
     */
    String batchDisable(List<String> userIds);

    /**
     * 启用
     *
     * @param userId
     * @return
     */
    boolean enable(String userId);

    /**
     * batchEnable
     *
     * @param userIds
     * @return
     */
    String batchEnable(List<String> userIds);

    /**
     * 审核user
     *
     * @param userId
     * @return
     */
    boolean approve(String userId);


    /**
     * batchApprove
     *
     * @param userIds
     * @return
     */
    String batchApprove(List<String> userIds);

    /**
     * downloadExcelTemplate
     */
    void downloadExcelTemplate();

    /**
     * importUsers
     *
     * @param list
     */
    void importUsers(List<ImportUserBo> list);

    /**
     * exportExcel
     *
     * @param users
     * @return
     */
    List<ExportUserBo> exportExcel(List<UserModel> users);

    /**
     * exportExcelEn
     *
     * @param users
     * @return
     */
    List<ExportUserEnBo> exportExcelEn(List<UserModel> users);

    /**
     * updateAvatar
     *
     * @param userId
     * @param multipartFile
     * @return boolean
     */
    boolean updateAvatar(String userId, MultipartFile multipartFile) throws MpRuntimeException;

    /**
     * processExpiredStatus
     *
     * @param user
     */
    void processExpiredStatus(UserBo user);

    /**
     * userModel转换为userdto，附加组织，角色信息
     *
     * @param userModel
     * @return
     */
    UserBo userModel2UserDto(UserModel userModel);

    /**
     * userModel转换为userdto，附加角色信息，不包括组织相关信息
     *
     * @param userModel
     * @return
     */
    UserBo userModel2UserDtoWithoutOrg(UserModel userModel);

    /**
     * userOrgRoleDtos2UserDtos
     *
     * @param userOrgRoleBos
     * @return
     */
    List<UserBo> userOrgRoleDtos2UserDtos(List<UserOrgRoleBo> userOrgRoleBos);

    /**
     * doModel2Dto
     *
     * @param userModel
     * @param needOrg
     * @param needRole
     * @return
     */
    UserBo doModel2Dto(UserModel userModel, boolean needOrg, boolean needRole);

    /**
     * listNotAudited
     *
     * @return
     */
    List<UserModel> listNotAudited();

    /**
     * listByUserIds
     *
     * @param userIds
     * @return
     */
    List<UserBo> listByUserIds(String[] userIds);

    /**
     * totalUserCnt
     *
     * @return int
     */
    int totalUserCnt();

    /**
     * getDefaultUserAvatar
     *
     * @return
     */
    String getDefaultUserAvatar();

    /**
     * setDefaultIfNoAvatar
     *
     * @param userModel
     */
    void setDefaultIfNoAvatar(UserModel userModel);

    /**
     * setDefaultIfNoAvatars
     *
     * @param userModels
     */
    void setDefaultIfNoAvatars(List<UserModel> userModels);

    /**
     * batchSetDefaultIfNoAvatar
     *
     * @param userBos
     */
    void batchSetDefaultIfNoAvatar(List<UserBo> userBos);

    /**
     * bulkCreateWithPasswordPreSet
     *
     * @param objs
     * @return
     */
    ServiceResult<Integer> bulkCreateWithPasswordPreSet(List<UserModel> objs);

    /**
     * listExistedUserNames
     *
     * @param userNames
     * @param limit
     * @return
     */
    List<String> listExistedUserNames(List<String> userNames, Integer limit);

}
