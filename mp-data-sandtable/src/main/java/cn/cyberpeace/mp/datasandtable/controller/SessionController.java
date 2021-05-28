package cn.cyberpeace.mp.datasandtable.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.exception.MqRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.datasandtable.model.SessionRequest;
import cn.cyberpeace.mp.datasandtable.model.SessionResult;
import cn.cyberpeace.mp.datasandtable.service.SessionService;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.apache.http.HttpResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
public class SessionController {

    @Autowired
    private SessionService sessionService;

    @RequestMapping(value = "/pcap/{groupId}/{sessionId}", method = RequestMethod.GET)
    public ResponseEntity<Resource> downloadPcapFile(HttpServletResponse httpServletResponse,
        @PathVariable("sessionId") String sessionId, @PathVariable("groupId") String groupId) {
        HttpResponse httpResponse = sessionService.queryPcapFileByGroupId(groupId, sessionId);
        InputStreamResource resource = null;
        try {
            resource = new InputStreamResource(httpResponse.getEntity().getContent());
        } catch (IOException e) {
            throw new MqRuntimeException("获取文件流失败");
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
    public ServiceResult<List<SessionResult>> querySessionListWithGroup(
        @RequestBody @Valid SessionRequest sessionRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ServiceResult.<List<SessionResult>>builder()
                .code(BaseErrorCode.PARAMS_INVALID.getCode()).message(bindingResult.getAllErrors().get(0).getDefaultMessage()).build();
        }

        return ServiceResult.<List<SessionResult>>builder().data(sessionService.querySessionListWithGroup(sessionRequest)).build();
    }

    /**
     * 分局分组id查询流量分页信息接口
     *
     * @param sessionRequest
     * @return ServiceResult<ListObjResult < SessionResult>>
     */
    @PostMapping("pageable")
    public ServiceResult<ListObjResult<SessionResult>> querySessionListWithGroupWithPage(
        @RequestBody @Valid SessionRequest sessionRequest, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return ServiceResult.<ListObjResult<SessionResult>>builder()
                .code(BaseErrorCode.PARAMS_INVALID.getCode()).message(bindingResult.getAllErrors().get(0).getDefaultMessage()).build();
        }
        return ServiceResult.<ListObjResult<SessionResult>>builder().data(sessionService.querySessionWithPage(sessionRequest)).build();
    }

}
