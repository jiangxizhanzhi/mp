package cn.cyberpeace.mp.auth.service;


import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.request.team.TeamPageRequest;
import cn.cyberpeace.mp.auth.request.team.TeamRequest;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TeamService  {
    ListObjResult getPageTeam(TeamPageRequest teamPageRequest);

    TeamModel addTeam(TeamRequest teamRequest);

    TeamModel getTeam(TeamRequest teamRequest);

    TeamModel patchTeam(TeamRequest teamRequest);


    ServiceResult<Integer> deleteTeam(String[] ids);

    void exportTeamTemplate(HttpServletResponse response);

    ServiceResult<String> importTeamTemplate(MultipartFile file);

    void exportPersonnelTemplate(HttpServletResponse response);

    ServiceResult<String> importPersonnelTemplate(MultipartFile file);
}
