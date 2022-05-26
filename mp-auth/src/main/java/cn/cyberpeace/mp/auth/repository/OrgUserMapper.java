package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Mapper
@Repository
@ModelClass(OrgUserModel.class)
public interface OrgUserMapper extends BaseModelMapper<OrgUserModel> {

    List<UserModel> listUsersByOrgId(@Param("orgId") String orgId, @Param("selfUserId") String selfUserId, @Param("excludeRoleIds") List<String> excludeRoleIds);

    List<OrgModel> listOrgsByUserId(@Param("userId") String userId);

    /**
     * @param orgIds
     * @param userDto.username 会同时模糊查询username和name
     * @param userIds
     * @return
     */
    List<UserModel> listUsers(@Param("orgIds") List<String> orgIds,
                              @Param("userIds") List<String> userIds,
                              @Param("orders") List<String> orders,
                              @Param("userCondition") UserDto userDto,
                              @Param("selfUserId") String selfUserId,
                              @Param("excludeRoleIds") List<String> excludeRoleIds
    );

    Integer selectUserCnt(@Param("orgId") String orgId);

    void deleteByUserId(@Param("userId") String userId);

    void deleteByOrgId(@Param("orgId") String orgId);
}
