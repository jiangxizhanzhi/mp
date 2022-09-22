package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.org.UserOrgDto;
import cn.cyberpeace.mp.auth.dto.org.UserOrgMapDto;
import cn.cyberpeace.mp.auth.dto.user.UserDto;
import cn.cyberpeace.mp.auth.dto.user.UserOrgRoleDto;
import cn.cyberpeace.mp.auth.model.OrgModel;
import cn.cyberpeace.mp.auth.model.OrgUserModel;
import cn.cyberpeace.mp.auth.model.UserModel;
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
@ModelClass(OrgUserModel.class)
public interface OrgUserMapper extends BaseModelMapper<OrgUserModel> {

    List<UserModel> listUsersByOrgId(@Param("orgId") String orgId, @Param("selfUserId") String selfUserId,
                                     @Param("excludeRoleIds") List<String> excludeRoleIds,
                                     @Param("excludeUserIds") List<String> excludeUserIds);

    List<OrgModel> listOrgsByUserId(@Param("userId") String userId);

    List<UserOrgMapDto> listUserOrgMapDtosByUserIds(@Param("userIds") List<String> userIds);

    List<UserOrgDto> listUserOrgByUserIds(@Param("userIds") List<String> userIds);

    /**
     * @param orgIds
     * @param userIds
     * @return
     */
    List<UserOrgRoleDto> listUsers(@Param("orgIds") List<String> orgIds,
                                   @Param("userIds") List<String> userIds,
                                   @Param("orders") List<String> orders,
                                   @Param("userCondition") UserDto userDto,
                                   @Param("selfUserId") String selfUserId,
                                   @Param("excludeRoleIds") List<String> excludeRoleIds,
                                   @Param("excludeUserIds") List<String> excludeUserIds,
                                   @Param("currentDateTime") LocalDateTime currentDateTime
                                   );


}
