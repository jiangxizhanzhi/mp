package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.auth.model.UserPermissionModel;
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
@ModelClass(UserPermissionModel.class)
public interface UserPermissionMapper extends BaseModelMapper<UserPermissionModel> {

    List<TargetModel> listTargetsByUserId(@Param("userId") String userId);

    List<UserModel> listUsersByTargetId(@Param("targetId") String targetId);

    void deleteByUserId(@Param("userId") String userId);

    void deleteByTargetId(@Param("targetId") String targetId);

}
