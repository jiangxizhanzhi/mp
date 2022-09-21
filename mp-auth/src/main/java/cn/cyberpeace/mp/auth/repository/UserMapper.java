package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.dto.user.UserOrgRoleDto;
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
@ModelClass(UserModel.class)
public interface UserMapper extends BaseModelMapper<UserModel> {


    // 由于 identifier 如果为 null，使用 通用 mapper 的 update 方法更新，会设置为默认值
    int updateIdentToNull(@Param("id") String id);

    List<UserOrgRoleDto> listByUserIds(String[] userIds);

}
