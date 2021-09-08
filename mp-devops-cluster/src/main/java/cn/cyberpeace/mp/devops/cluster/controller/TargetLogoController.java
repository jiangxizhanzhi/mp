package cn.cyberpeace.mp.devops.cluster.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devops.cluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devops.cluster.response.base.Result;
import cn.cyberpeace.mp.devops.cluster.service.TargetLogoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/logo")
@Api(tags = "靶标管理-图标管理")
public class TargetLogoController {

    private TargetLogoService targetLogoService;

    @Autowired
    public TargetLogoController(TargetLogoService targetLogoService) {
        this.targetLogoService = targetLogoService;
    }

    @ApiOperation("初始化logo")
    @PostMapping("/init")
    public ServiceResult initLogo() {
        List<String>  urlList = targetLogoService.upload();
        int result = targetLogoService.batchAdd(urlList);
        return result > 0 ? ServiceResult.successful() : ServiceResult.fail("");
    }

    @ApiOperation("查询图标列表")
    @GetMapping("/list")
    public Result<List<String>> list() {
        List<String> list = targetLogoService.list();
        return new Result<>(list);
    }
}
