package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.bo.org.UserOrgBo;
import cn.cyberpeace.mp.auth.bo.org.UserOrgMapBo;
import cn.cyberpeace.mp.auth.bo.user.UserBo;
import cn.cyberpeace.mp.auth.bo.user.UserOrgRoleBo;
import cn.cyberpeace.mp.auth.model.TenantUserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(TenantUserModel.class)
public interface TenantUserMapper extends BaseModelMapper<TenantUserModel> {

//    List<OrgModel> listOrgsByUserId(@Param("userId") String userId);
    /**
     * @param orgIds
     * @param userIds
     * @return
     */
    List<UserOrgRoleBo> listUsers(@Param("orgIds") List<String> orgIds,
                                  @Param("userIds") List<String> userIds,
                                  @Param("orders") List<String> orders,
                                  @Param("userCondition") UserBo userBo,
                                  @Param("selfUserId") String selfUserId,
                                  @Param("excludeRoleIds") List<String> excludeRoleIds,
                                  @Param("excludeUserIds") List<String> excludeUserIds,
                                  @Param("currentDateTime") LocalDateTime currentDateTime
    );

}
