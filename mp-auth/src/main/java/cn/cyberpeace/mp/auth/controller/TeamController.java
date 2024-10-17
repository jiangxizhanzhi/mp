package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.bo.Personnel.management.PersonnelManagementBo;
import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.request.management.PersonnelManagementPageRequest;
import cn.cyberpeace.mp.auth.request.management.PersonnelManagementRequest;
import cn.cyberpeace.mp.auth.request.team.TeamPageRequest;
import cn.cyberpeace.mp.auth.request.team.TeamRequest;
import cn.cyberpeace.mp.auth.service.PersonnelManagementService;
import cn.cyberpeace.mp.auth.service.TeamService;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(value = "战队接口", tags = "战队")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/team")
public class TeamController {

    @Autowired
    private TeamService teamService;

    @Autowired
    private PersonnelManagementService personnelManagementService;


    @ApiOperation(value = "获取战队信息", notes = "")
    @GetMapping("/get/page")
    public ListObjServiceResult<Object> getPageTeam(TeamPageRequest teamPageRequest) {
        teamPageRequest.ensure();
        return new ListObjServiceResult(teamService.getPageTeam(teamPageRequest)).asResult();
    }

    @ApiOperation(value = "获取战队信息", notes = "")
    @GetMapping("/get")
    public ServiceResult<TeamModel> getPageTeam(TeamRequest teamRequest) {

        return new ServiceResult<>(teamService.getTeam(teamRequest)).asResult();
    }


    @ApiOperation(value = "新增战队", notes = "")
    @PostMapping("/add")
    public ServiceResult<TeamModel> addTeam(@RequestBody TeamRequest teamRequest) {

        return new ServiceResult<>(teamService.addTeam(teamRequest)).asResult();
    }

    @ApiOperation(value = "修改战队信息", notes = "")
    @PatchMapping("/patch")
    public ServiceResult<TeamModel> patchTeam(@RequestBody TeamRequest teamRequest) {

        return new ServiceResult<>(teamService.patchTeam(teamRequest)).asResult();
    }

    @ApiOperation(value = "删除战队", notes = "")
    @DeleteMapping("/delete")
    public ServiceResult<Integer> deleteTeam(@RequestBody String[] ids) {

        return teamService.deleteTeam(ids).asResult();
    }

    @ApiOperation(value = "获取成员管理", notes = "")
    @GetMapping("/get/page/personnel")
    public ListObjServiceResult<Object> getPageTeam(PersonnelManagementPageRequest personnelManagementPageRequest) {
        personnelManagementPageRequest.ensure();
        return new ListObjServiceResult(personnelManagementService.getPagePersonnelManagement(personnelManagementPageRequest)).asResult();
    }


    @ApiOperation(value = "修改成员管理", notes = "")
    @PatchMapping("/patch/personnel")
    public ServiceResult<PersonnelManagementBo> patchPersonnelManagement(@RequestBody List<PersonnelManagementRequest> PersonnelManagementRequestList) {
        personnelManagementService.patchPersonnelManagement(PersonnelManagementRequestList);
        return ResultCommon.success();
    }

}


