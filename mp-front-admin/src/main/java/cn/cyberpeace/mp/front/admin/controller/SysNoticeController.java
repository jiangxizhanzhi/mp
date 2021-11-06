package cn.cyberpeace.mp.front.admin.controller;

import cn.cyberpeace.mp.front.admin.dto.notice.SysNoticeDto;
import cn.cyberpeace.mp.front.admin.dto.notice.SysNoticePageRequest;
import cn.cyberpeace.mp.front.admin.dto.notice.SysNoticeRequest;
import cn.cyberpeace.mp.front.admin.dto.validate.AddGroup;
import cn.cyberpeace.mp.front.admin.dto.validate.UpdateGroup;
import cn.cyberpeace.mp.front.admin.model.SysNoticeModel;
import cn.cyberpeace.mp.front.admin.service.sys.SysNoticeService;
import cn.cyberpeace.mp.front.admin.util.ResultUtils;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.hutool.core.collection.CollectionUtil;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Objects;

/**
 * @Description: 系统公告管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Api(value = "公告管理接口", tags = "AUTH-公告管理接口")
@Slf4j
@RestController
@RequestMapping("/sys/notice")
public class SysNoticeController {

    @Autowired
    private SysNoticeService noticeService;

    @ApiOperation("查询单个公告")
    @GetMapping("/{id}")
    public ServiceResult<SysNoticeDto> get(@PathVariable String id) {
        ObjServiceResult<SysNoticeModel> result = noticeService.get(id);
        if (Objects.nonNull(result.getSource())) {
            SysNoticeModel noticeModel = result.getSource().getObj();
            SysNoticeDto noticeDto = noticeService.noticeModel2NoticeDto(noticeModel, false);
            return ServiceResult.success(noticeDto);
        } else {
            return ServiceResult.fail("没有查询到ID为:" + id + "的公告！");
        }
    }


    @ApiOperation("阅读公告接口")
    @PostMapping("/read/{id}")
    public ServiceResult read(@PathVariable String id) {
        noticeService.read(id);
        return ServiceResult.successful();
    }


    @ApiOperation("不再推送公告消息")
    @PostMapping("/unpush")
    public ServiceResult unpush(@RequestBody String[] ids) {
        noticeService.unpush(CollectionUtil.toList(ids));
        return ServiceResult.successful();
    }


    @ApiOperation("未读公告数量")
    @GetMapping("/unread_count")
    public ServiceResult<Integer> unReadCount() {
        int unreadCnt = noticeService.unReadCount();
        return ServiceResult.success(unreadCnt);
    }

    @ApiOperation("分页查询所有公告")
    @GetMapping("/page")
    public ServiceResult<SysNoticeDto> page(SysNoticePageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo pageInfo = noticeService.page(pageRequest);

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @ApiOperation("分页查询当前用户有权看到的公告（公告消息）")
    @GetMapping("/currentuser/page")
    public ServiceResult<SysNoticeDto> currentUserPage(SysNoticePageRequest pageRequest) {
        pageRequest.ensure();
        PageInfo pageInfo = noticeService.getCurrentUserNoticeMessagesPage(pageRequest);

        return ResultUtils.pageInfo2ObjServiceResult(pageInfo);
    }


    @ApiOperation("新增公告")
    @PostMapping
    public ServiceResult<SysNoticeModel> create(@RequestBody @Validated(AddGroup.class) SysNoticeRequest noticeReq) {
        SysNoticeModel noticeModel = noticeService.add(noticeReq);
        return ServiceResult.success(noticeModel);
    }

    @ApiOperation("更新公告")
    @PatchMapping("/{id}")
    public ServiceResult modify(@PathVariable String id, @RequestBody @Validated(UpdateGroup.class) SysNoticeRequest noticeReq) {
        SysNoticeModel noticeModel = noticeService.modify(id, noticeReq);

        return ServiceResult.builder().source(noticeModel).build().asResult();
    }


    @ApiOperation("批量删除公告")
    @DeleteMapping
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return noticeService.bulkDelete(ids);
    }

    @ApiOperation("删除指定id公告")
    @ApiImplicitParams({
            @ApiImplicitParam(name="id", paramType="path", value="数据id",dataTypeClass = String.class),
    })
    @DeleteMapping("/{id}")
    public ServiceResult<Integer> delete(@PathVariable String id) {
        String[] ids = Collections.singletonList(id).toArray(new String[0]);
        return noticeService.bulkDelete(ids);
    }




}
