package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(UserOnlineReportModel.class)
public interface UserOnlineReportMapper extends BaseModelMapper<UserOnlineReportModel> {

    @Select("select * from mp_auth_user_online_report order by is_online desc, username")
    List<UserOnlineReportModel> listAll();


    @Select("select * from mp_auth_user_online_report where user = #{userId}")
    UserOnlineReportModel getByUserId(@Param("userId") String userId);

}
