package cn.cyberpeace.mp.systemoperation.controller;


import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.datasandtable.model.SessionRequest;
import cn.cyberpeace.mp.datasandtable.model.SessionResult;
import cn.cyberpeace.mp.datasandtable.service.SessionService;
import cn.cyberpeace.mp.systemoperation.constant.ControllerConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@Slf4j
@Api(tags = "流量监控接口")
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/flow")
public class SystemOperFlowController {

    @Autowired
    private SessionService sessionService;

    @GetMapping("/pcap/{groupId}/{sessionId}")
    @ApiOperation("pcap包下载")
    public ResponseEntity<Resource> downloadPcapFile(HttpServletResponse httpServletResponse,
                                                     @PathVariable("sessionId") String sessionId, @PathVariable("groupId") String groupId) {
        var httpResponse = sessionService.queryPcapFileByGroupId(groupId, sessionId);
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new MpRuntimeException("获取文件流失败");
        }

        var httpHeaders = new HttpHeaders();

        httpHeaders.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        httpServletResponse.setHeader("Content-Disposition", "attachment; filename=" + groupId + ".pcap");
        return ResponseEntity.ok().headers(httpHeaders)
                .contentType(MediaType.parseMediaType("application/octet-stream")).body(resource);
    }

    /**
     * 按照groupId查询流量信息
     *
     * @param sessionRequest sessionRequest
     * @return ServiceResult<List < SessionResult>>
     */
    @PostMapping
    @ApiOperation("根据groupId查询流量信息")
    public ServiceResult<List<SessionResult>> querySessionListWithGroup(@RequestBody @Valid SessionRequest sessionRequest) {
        return ServiceResult.success(sessionService.querySessionListWithGroup(sessionRequest));
    }

    /**
     * 根据分组id查询流量分页信息接口
     *
     * @param sessionRequest
     * @return ServiceResult<ListObjResult < SessionResult>>
     */
    @PostMapping("pageable")
    @ApiOperation("根据分组id查询流量分页信息")
    public ServiceResult<ListObjResult<SessionResult>> querySessionListWithGroupWithPage(@RequestBody @Valid SessionRequest sessionRequest) {
        return ServiceResult.success(sessionService.querySessionWithPage(sessionRequest));
    }


}
