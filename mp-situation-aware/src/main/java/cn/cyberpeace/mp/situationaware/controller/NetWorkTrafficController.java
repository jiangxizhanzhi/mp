package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.situationaware.model.SPIViewRequest;
import cn.cyberpeace.mp.situationaware.model.SessionRequest;
import cn.cyberpeace.mp.situationaware.model.SessionResult;
import cn.cyberpeace.mp.situationaware.service.NetworkTrafficService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/sessions")
@Api(value = "态势感知-流量接口", tags = "态势感知-流量接口")
public class NetWorkTrafficController {

    @Autowired
    private NetworkTrafficService netWorkTrafficService;

    @GetMapping("/pcap/{groupId}/{sessionId}")
    @ApiImplicitParam(name = "groupId", value = "分组ID", dataType = "path", dataTypeClass = String.class)
    @ApiOperation(value = "流量下载", notes = "")
    public ResponseEntity<Resource> downloadPcapFile(HttpServletResponse httpServletResponse,
        @PathVariable("sessionId") String sessionId, @PathVariable("groupId") String groupId) {
        var httpResponse = netWorkTrafficService.queryPcapFileByGroupId(groupId, sessionId);
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

    
    @PostMapping
    @ApiOperation("按照组ID查询流量信息")
    @ApiImplicitParam(name = "sessionRequest", value = "查询请求", dataType = "body", dataTypeClass = SessionRequest.class)
    public ServiceResult<List<SessionResult>> querySessionListWithGroup(
        @RequestBody @Valid SessionRequest sessionRequest) {
        return ServiceResult.<List<SessionResult>>builder().source(netWorkTrafficService.querySessionListWithGroup(sessionRequest)).build().asResult();
    }

    
    @PostMapping("pageable")
    @ApiOperation("分局分组id查询流量分页信息接口")
    @ApiImplicitParam(name = "sessionRequest", value = "查询请求", dataType = "body", dataTypeClass = SessionRequest.class)
    public ListObjServiceResult<SessionResult> querySessionListWithGroupWithPage(
        @RequestBody @Valid SessionRequest sessionRequest) {
        return new ListObjServiceResult<>(netWorkTrafficService.querySessionWithPage(sessionRequest));
    }

    @PostMapping("spiview")
    @ApiOperation("SPI视图")
    @ApiImplicitParam(name = "spiViewRequest", value = "查询请求", dataType = "body", dataTypeClass = SPIViewRequest.class)
    public ServiceResult<JSONObject> querySessionSpiView(@RequestBody @Valid SPIViewRequest spiViewRequest) {
        var spiView = netWorkTrafficService.querySPIView(spiViewRequest);
        return ServiceResult.<JSONObject>builder().source(JSONObject.parseObject(spiView)).build().asResult();
    }
}
