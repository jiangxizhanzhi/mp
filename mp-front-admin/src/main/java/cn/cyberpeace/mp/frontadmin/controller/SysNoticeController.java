package cn.cyberpeace.mp.frontadmin.controller;

import cn.cyberpeace.mp.auth.constant.AuthConstant;
import cn.cyberpeace.mp.base.controller.common.ResultCommon;
import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.bo.OptionBO;
import cn.cyberpeace.mp.common.service.OptionService;
import cn.cyberpeace.mp.frontadmin.constant.AdminErrorCode;
import cn.cyberpeace.mp.frontadmin.constant.FrontAdminConstant;
import cn.cyberpeace.mp.frontadmin.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.frontadmin.model.SysNoticeModel;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.frontadmin.request.notice.SysNoticeRequest;
import cn.cyberpeace.mp.frontadmin.request.validate.AddGroup;
import cn.cyberpeace.mp.frontadmin.request.validate.UpdateGroup;
import cn.cyberpeace.mp.frontadmin.service.sys.SysNoticeService;
import cn.cyberpeace.mp.log.annotation.OperateLog;
import cn.cyberpeace.mp.log.enums.OpModuleEnums;
import cn.cyberpeace.mp.log.enums.OpTargetEnums;
import cn.cyberpeace.mp.log.enums.OpTypeEnums;
import cn.hutool.core.collection.CollUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;
import java.util.Objects;


@Api(value = "公告管理接口", tags = "AUTH-公告管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService noticeService;

    @Resource(name = "sysOptionServiceImpl")
    private OptionService optionService;

    @ApiOperation("查询单个公告")
    @ApiImplicitParam(name = "id", value = "公告ID", dataTypeClass = String.class)
    @GetMapping("/{id}")
    public ServiceResult<SysNoticeDto> get(@PathVariable String id) {
        ObjServiceResult<SysNoticeModel> result = noticeService.get(id);
        if (Objects.nonNull(result.getSource())) {
            SysNoticeModel noticeModel = result.getSource().getObj();
            SysNoticeDto noticeDto = noticeService.noticeModel2NoticeDto(noticeModel, false);
            return ResultCommon.success(noticeDto);
        } else {
            throw new MpRuntimeException(AdminErrorCode.SYS_NOTICE_NOT_EXISTED.format(id));
        }
    }


    @ApiOperation("阅读公告接口")
    @ApiImplicitParam(name = "id", value = "公告ID", dataTypeClass = String.class)
    @PostMapping("/read/{id}")
    public ServiceResult read(@PathVariable String id) {
        noticeService.read(id);
        return ResultCommon.success();
    }

    @ApiOperation("置顶公告接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "公告ID", dataTypeClass = String.class),
            @ApiImplicitParam(name = "type", value = "公告置顶操作类型 1:置顶(默认)  0:取消置顶", dataTypeClass = Integer.class)
    })
    @PostMapping("/top/{id}")
    public ServiceResult top(@PathVariable String id, @RequestParam Integer type) {
        noticeService.top(id, type);
        return ResultCommon.success();
    }


    @ApiOperation("不再推送公告消息")
    @ApiImplicitParam(name = "ids", value = "公告ID列表", dataTypeClass = String.class)
    @PostMapping("/unpush")
    public ServiceResult unpush(@RequestBody String[] ids) {
        noticeService.unpush(CollUtil.toList(ids));
        return ResultCommon.success();
    }


    @ApiOperation("未读公告数量")
    @GetMapping("/unread_count")
    public ServiceResult<Integer> unReadCount() {
        int unreadCnt = noticeService.unReadCount();
        return ResultCommon.success(unreadCnt);
    }

    @ApiOperation("分页查询所有公告")
    @GetMapping("/page")
    public ServiceResult<SysNoticeDto> page(SysNoticePageRequest pageRequest) {
        pageRequest.ensure();
        return noticeService.page(pageRequest);
    }


    @ApiOperation("分页查询当前用户有权看到的公告（公告消息）")
    @GetMapping("/currentuser/page")
    public ServiceResult<SysNoticeDto> currentUserPage(SysNoticePageRequest pageRequest) {
        pageRequest.ensure();
        return noticeService.getCurrentUserNoticeMessagesPage(pageRequest);
    }


    @ApiOperation("新增公告")
    @ApiImplicitParam(name = "noticeReq", value = "新增公告请求", dataTypeClass = SysNoticeRequest.class)
    @PostMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.CREATE, title = "新增公告", target = OpTargetEnums.NOTICE)
    public ServiceResult<SysNoticeModel> create(@RequestBody @Validated(AddGroup.class) SysNoticeRequest noticeReq) {
        SysNoticeModel noticeModel = noticeService.add(noticeReq);
        return ResultCommon.success(noticeModel);
    }

    @ApiOperation("更新公告")
    @ApiImplicitParam(name = "noticeReq", value = "更新公告请求", dataTypeClass = SysNoticeRequest.class)
    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.UPDATE, title = "修改公告", target = OpTargetEnums.NOTICE)
    public ServiceResult modify(@PathVariable String id, @RequestBody @Validated(UpdateGroup.class) SysNoticeRequest noticeReq) {
        SysNoticeModel noticeModel = noticeService.modify(id, noticeReq);

        return ServiceResult.builder().source(noticeModel).build().asResult();
    }


    @ApiOperation("批量删除公告")
    @ApiImplicitParam(name = "ids", value = "公告ID列表", dataTypeClass = String[].class)
    @DeleteMapping
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.DELETE, title = "删除公告", target = OpTargetEnums.NOTICE)
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        ServiceResult<Integer> result = noticeService.bulkDelete(ids);
        return ResultCommon.success(result.getSource());
    }

    @ApiOperation("删除指定id公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", paramType = "path", value = "数据id", dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('ROLE_" + AuthConstant.ROLE_ADMIN + "')")
    @OperateLog(module = OpModuleEnums.SYSTEM, type = OpTypeEnums.DELETE, title = "删除公告", target = OpTargetEnums.NOTICE)
    public ServiceResult<Integer> delete(@PathVariable String id) {
        String[] ids = Collections.singletonList(id).toArray(new String[0]);
        ServiceResult<Integer> result = noticeService.bulkDelete(ids);
        return ResultCommon.success(result.getSource());
    }

    @ApiOperation(value = "查询系统公告类型")
    @GetMapping("/types")
    public ServiceResult<List<OptionBO>> getTypes() {
        return optionService.getIntOptions(FrontAdminConstant.MP_COMMON_OPTION_TYPE_FRONT_ADMIN_NOTICE_TYPE);
    }

}
