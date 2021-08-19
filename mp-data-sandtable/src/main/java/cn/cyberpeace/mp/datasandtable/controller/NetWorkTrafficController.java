package cn.cyberpeace.mp.datasandtable.controller;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.datasandtable.model.SPIViewRequest;
import cn.cyberpeace.mp.datasandtable.model.SessionRequest;
import cn.cyberpeace.mp.datasandtable.model.SessionResult;
import cn.cyberpeace.mp.datasandtable.service.NetWorkTrafficService;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/sessions")
@Api(value = "数据沙盘-流量接口", tags = "数据沙盘-流量接口")
public class NetWorkTrafficController {

    @Autowired
    private NetWorkTrafficService netWorkTrafficService;

    @GetMapping("/pcap/{groupId}/{sessionId}")
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

    /**
     * 按照groupId查询流量信息
     *
     * @param sessionRequest sessionRequest
     * @return ServiceResult<List < SessionResult>>
     */
    @PostMapping
    public ServiceResult<List<SessionResult>> querySessionListWithGroup(
        @RequestBody @Valid SessionRequest sessionRequest) {
        return ServiceResult.<List<SessionResult>>builder().data(netWorkTrafficService.querySessionListWithGroup(sessionRequest)).build();
    }

    /**
     * 分局分组id查询流量分页信息接口
     *
     * @param sessionRequest
     * @return ServiceResult<ListObjResult < SessionResult>>
     */
    @PostMapping("pageable")
    public ListObjServiceResult<SessionResult> querySessionListWithGroupWithPage(
        @RequestBody @Valid SessionRequest sessionRequest) {
        return new ListObjServiceResult<>(netWorkTrafficService.querySessionWithPage(sessionRequest));
    }

    @PostMapping("spiview")
    public ServiceResult<JSONObject> querySessionSpiView(@RequestBody @Valid SPIViewRequest spiViewRequest) {
        var spiView = netWorkTrafficService.querySPIView(spiViewRequest);
        return ServiceResult.<JSONObject>builder().data(JSONObject.parseObject(spiView)).build();
    }
}
