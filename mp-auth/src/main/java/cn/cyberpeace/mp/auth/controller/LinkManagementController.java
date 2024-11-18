package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.constant.ControllerConstant;
import cn.cyberpeace.mp.auth.model.LinkManagementModel;
import cn.cyberpeace.mp.auth.model.TeamModel;
import cn.cyberpeace.mp.auth.request.team.LinkManagementRequest;
import cn.cyberpeace.mp.auth.request.team.LinkManagementRequestPage;
import cn.cyberpeace.mp.auth.request.team.LinkManagementRequest;
import cn.cyberpeace.mp.auth.service.LinkManagementService;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Api(value = "链路管理接口", tags = "链路")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/Link")
public class LinkManagementController {

    @Autowired
    private LinkManagementService linkManagementService;

    @ApiOperation(value = "获取链路分页信息", notes = "")
    @GetMapping("/get/page")
    public ListObjServiceResult<Object> getPageLink(LinkManagementRequestPage linkManagementRequest) {
        linkManagementRequest.ensure();
        return new ListObjServiceResult(linkManagementService.getPageLink(linkManagementRequest)).asResult();
    }

    @ApiOperation(value = "获取链路单个信息", notes = "")
    @GetMapping("/get")
    public ServiceResult<LinkManagementModel> getLink(LinkManagementRequest linkManagementRequest) {
        return new ServiceResult<>(linkManagementService.getLink(linkManagementRequest)).asResult();
    }


    @ApiOperation(value = "新增链路信息", notes = "")
    @PostMapping("/add")
    public ServiceResult<Boolean> addLink(@RequestBody LinkManagementRequest LinkManagementRequest) {

        return new ServiceResult<>(linkManagementService.addLink(LinkManagementRequest)).asResult();
    }

    @ApiOperation(value = "修改链路信息", notes = "")
    @PatchMapping("/patch")
    public ServiceResult<Boolean> patchLink(@RequestBody LinkManagementRequest LinkManagementRequest) {

        return new ServiceResult<>(linkManagementService.patchLink(LinkManagementRequest)).asResult();
    }


    @ApiOperation(value = "删除链路信息", notes = "")
    @DeleteMapping("/delete")
    public ServiceResult<Integer> deleteLink(@RequestBody String[] ids) {

        return linkManagementService.deleteLink(ids).asResult();
    }
}


