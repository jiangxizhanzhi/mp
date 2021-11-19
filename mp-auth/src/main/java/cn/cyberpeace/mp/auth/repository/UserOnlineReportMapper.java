package cn.cyberpeace.mp.auth.repository;

import cn.cyberpeace.mp.auth.model.UserOnlineReportModel;
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
@ModelClass(UserOnlineReportModel.class)
public interface UserOnlineReportMapper extends BaseModelMapper<UserOnlineReportModel> {

    List<UserOnlineReportModel> listAll();

    UserOnlineReportModel getByUserId(@Param("userId") String userId);

}
