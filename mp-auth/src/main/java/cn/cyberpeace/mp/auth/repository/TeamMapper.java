package cn.cyberpeace.mp.auth.repository;


import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.base.repository.BaseModelMapper;
import cn.cyberpeace.mp.base.repository.stereotype.ModelClass;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
@ModelClass(TeamModel.class)
public interface TeamMapper extends BaseModelMapper<TeamModel> {
    List<TeamModel> existTeamList(@Param("teamList") List<String> teamList);
}
