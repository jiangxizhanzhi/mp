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


public interface TenantUserService extends BaseModelService<TenantUserModel, QueryParams> {

    
    List<UserModel> listUsersByOrgId(String orgId);

    
    List<UserModel> listUsersByOrgIdNoRestrict(String orgId);

    List<UserModel> listUsersByOrgAndUname(String orgId, String uname, List<String> userIds);

    
    List<OrgBO> listOrgsByUserId(String userId);

    
    List<UserOrgMapBo> listUserOrgMapDtosByUserIds(List<String> userIds);

    
    List<UserOrgBo> listUserOrgByUserIds(List<String> userIds);

    
    List<OrgDto> listHierarchicalOrgsByUserId(String userId);

    
    boolean additionalBindOrgUsers(String orgId, List<String> userIds);

    String getAllOrgPathByUserId(String userId);

    Integer getUserCnt(String orgId);

    PageInfo<UserBo> pageUsersByOrg(OrgUserPageRequest req);

    List<UserBo> listByUserIds(List<String> ids);

    
    boolean deleteByUserId(String userId);

    boolean deleteByOrgId(String orgId);

    boolean deleteByTenantId(String tenantId);


}
