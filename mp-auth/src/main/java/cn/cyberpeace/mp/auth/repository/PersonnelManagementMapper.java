package cn.cyberpeace.mp.auth.repository;


import cn.cyberpeace.mp.auth.bo.user.TeamInfoIdAndName;
import cn.cyberpeace.mp.auth.model.PersonnelManagementModel;
import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import com.alibaba.fastjson.JSONObject;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(PersonnelManagementModel.class)
public interface PersonnelManagementMapper extends BaseModelMapper<PersonnelManagementModel> {


    List<PersonnelManagementModel> getUserExist(@Param("teamId") String teamId, @Param("userIds") List<String> userIds);

    List<PersonnelManagementModel> selectALl();

    String selectByNameToTeamName(@Param("creator") String creator);

    TeamInfoIdAndName selectByUserIdToTeamInfo(@Param("userId") String userId);
}
