 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name TenantUserService.java.
 // File size 3220.
 // File create date Fri Jul 12 12:55:33 CST 2024.
 // File update date Fri Jul 12 12:55:33 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.bo.org.OrgBO;
import cn.cyberpeace.mp.auth.bo.org.OrgDto;
import cn.cyberpeace.mp.auth.bo.org.UserOrgBo;
import cn.cyberpeace.mp.auth.bo.org.UserOrgMapBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.org.OrgUserPageRequest;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @ClassName TenantUserService
 * @Description
 * @Author zhouxinghuai
 * @Date 2021/8/31 11:13
 * @Version 1.0.0
 * @see BaseModelService
 */
public interface TenantUserService extends BaseModelService<TenantUserModel, QueryParams> {

    /**
     * 查询组织内所有用户
     *
     * @param orgId
     * @return
     */
    List<UserModel> listUsersByOrgId(String orgId);

    /**
     * 查询组织内所有用户，不做登录用户权限校验
     *
     * @param orgId
     * @return
     */
    List<UserModel> listUsersByOrgIdNoRestrict(String orgId);

    /**
     * listUsersByOrgAndUname
     *
     * @param orgId
     * @param uname
     * @param userIds
     * @return
     */
    List<UserModel> listUsersByOrgAndUname(String orgId, String uname, List<String> userIds);

    /**
     * 查询用户所在的所有组织
     *
     * @param userId
     * @return
     */
    List<OrgBO> listOrgsByUserId(String userId);

    /**
     * listUserOrgMapDtosByUserIds
     *
     * @param userIds
     * @return
     */
    List<UserOrgMapBo> listUserOrgMapDtosByUserIds(List<String> userIds);

    /**
     * listUserOrgByUserIds
     *
     * @param userIds
     * @return
     */
    List<UserOrgBo> listUserOrgByUserIds(List<String> userIds);

    /**
     * 列出用户所在组织的层级关系，查出组织，上级组织直到顶级组织
     *
     * @param userId
     * @return
     */
    List<OrgDto> listHierarchicalOrgsByUserId(String userId);

    /**
     * 增量添加组织
     *
     * @param orgId
     * @param userIds
     * @return boolean
     */
    boolean additionalBindOrgUsers(String orgId, List<String> userIds);

    /**
     * getAllOrgPathByUserId
     *
     * @param userId
     * @return
     */
    String getAllOrgPathByUserId(String userId);

    /**
     * getUserCnt
     *
     * @param orgId
     * @return
     */
    Integer getUserCnt(String orgId);

    /**
     * pageUsersByOrg
     *
     * @param req
     * @return
     */
    PageInfo<UserBo> pageUsersByOrg(OrgUserPageRequest req);

    /**
     * listByUserIds
     *
     * @param ids
     * @return
     */
    List<UserBo> listByUserIds(List<String> ids);

    /**
     * 清除用户所有组织
     *
     * @param userId
     * @return boolean
     */
    boolean deleteByUserId(String userId);

    /**
     * deleteByOrgId
     *
     * @param orgId
     * @return boolean
     */
    boolean deleteByOrgId(String orgId);


}
