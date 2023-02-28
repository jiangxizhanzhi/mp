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

    List<UserModel> listUsersByOrgId(@Param("orgId") String orgId);

    /**
     * 查询组织下的非管理员用户
     * @param orgIds
     * @param userId
     * @param userName
     * @return
     */
    List<UserModel> listUsersByOrgIdExcludeManager(@Param("orgIds") List<String> orgIds, @Param("userId") String userId, @Param("userName") String userName);

    /**
     * 查询所有正常状态用户
     * @param userName
     * @return
     */
    List<UserModel> listAllEnabledUsers(@Param("userName") String userName);

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
                              @Param("userCondition") UserDto userDto
    );

    /**
     * 查询同组织下的用户(不包括同级别的管理员)
     * @param orgIds
     * @param userIds
     * @param orders
     * @param userDto
     * @return
     */
    List<UserModel> listUsersExcludeManager(@Param("orgIds") List<String> orgIds,
                                            @Param("userIds") List<String> userIds,
                                            @Param("orders") List<String> orders,
                                            @Param("userCondition") UserDto userDto,
                                            @Param("curUserId") String curUserId);


    Integer selectUserCnt(@Param("orgId") String orgId);

    void deleteByUserId(@Param("userId") String userId);

    void deleteByOrgId(@Param("orgId") String orgId);
}
