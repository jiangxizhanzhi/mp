package cn.cyberpeace.mp.auth.service;


import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.request.team.TeamPageRequest;
import cn.cyberpeace.mp.auth.request.team.TeamRequest;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;

import java.util.List;

public interface TeamService  {
    ListObjResult getPageTeam(TeamPageRequest teamPageRequest);

    TeamModel addTeam(TeamRequest teamRequest);

    TeamModel getTeam(TeamRequest teamRequest);

    TeamModel patchTeam(TeamRequest teamRequest);


    ServiceResult<Integer> deleteTeam(String[] ids);
}
