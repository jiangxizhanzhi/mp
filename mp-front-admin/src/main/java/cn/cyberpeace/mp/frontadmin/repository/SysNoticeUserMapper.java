package cn.cyberpeace.mp.frontadmin.repository;

import cn.cyberpeace.mp.auth.model.UserModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeUserModel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/9/20 11:13
 */
@Mapper
@Repository
@ModelClass(SysNoticeUserModel.class)
public interface SysNoticeUserMapper extends BaseModelMapper<SysNoticeUserModel> {

    List<UserModel> listByNoticeIds(@Param("noticeIds") List<String> noticeIds);

    //int deleteByNoticeIds(@Param("noticeIds") List<String> noticeIds);
    //
    //int deleteByUser(@Param("userId") String userId);

}
