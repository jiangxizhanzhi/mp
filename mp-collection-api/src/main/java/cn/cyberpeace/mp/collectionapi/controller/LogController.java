package cn.cyberpeace.mp.collectionapi.controller;

import cn.cyberpeace.mp.base.service.result.ListObjServiceResult;
import cn.cyberpeace.mp.collectionapi.request.LogQueryRequest;
import cn.cyberpeace.mp.collectionapi.vo.LogVO;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/log")
public class LogController {
    @GetMapping("list")
    @ApiOperation(value = "查询日志列表")
    public ListObjServiceResult<LogVO> list(LogQueryRequest request) {
        return new ListObjServiceResult<>();
    }
}
