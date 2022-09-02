package cn.cyberpeace.mp.auth.service;

import cn.cyberpeace.mp.auth.dto.org.OrgBO;
import cn.cyberpeace.mp.auth.dto.org.OrgDto;
import cn.cyberpeace.mp.auth.dto.org.UserOrgDto;
import cn.cyberpeace.mp.auth.dto.org.UserOrgMapDto;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.request.org.OrgUserPageRequest;
import cn.cyberpeace.mp.base.service.BaseModelService;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import com.github.pagehelper.PageInfo;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
public interface OrgUserService extends BaseModelService<OrgUserModel, QueryParams> {

    /**
     * 查询组织内所有用户
     * @param orgId
     * @return
     */
    List<UserModel> listUsersByOrgId(String orgId);

    /**
     * 查询组织内所有用户，不做登录用户权限校验
     * @param orgId
     * @return
     */
    List<UserModel> listUsersByOrgIdNoRestrict(String orgId);

    List<UserModel> listUsersByOrgAndUname(String orgId, String uname, List<String> userIds);

    /**
     * 查询用户所在的所有组织
     * @param userId
     * @return
     */
    List<OrgBO> listOrgsByUserId(String userId);

    /**
     * 批量查询用户所在组织信息，返回一个map
     * @param userIds
     * @return
     */
    List<UserOrgMapDto> listUserOrgMapDtosByUserIds(List<String> userIds);

    /**
     * 批量查询用户组织对应关系
     * @param userIds
     * @return
     */
    List<UserOrgDto> listUserOrgByUserIds(List<String> userIds);

    /**
     * 列出用户所在组织的层级关系，查出组织，上级组织直到顶级组织
     */
    List<OrgDto> listHierarchicalOrgsByUserId(String userId);

    // 增量添加组织-用户关系
    boolean additionalBindOrgUsers(String orgId, List<String> userIds);

    String getAllOrgPathByUserId(String userId);

    Integer getUserCnt(String orgId);

    PageInfo<UserDto> pageUsersByOrg(OrgUserPageRequest req);

    // 清除用户所有组织
    boolean deleteByUserId(String userId);

    boolean deleteByOrgId(String orgId);

}
