package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.CommonOutServiceServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/commonOutService")
public class CommonOutServiceController {

    @Autowired
    private CommonOutServiceServiceImpl commonOutServiceService;

    /*
     * 获取共性平台外部链接
     * */
    @ApiOperation(value = "获取共性平台外部链接", notes = "")
    @GetMapping("/getGXPTServiceUrl")
    public ServiceResult getGXPTServiceUrl() {
        String url = commonOutServiceService.getGXPTServiceUrl();
        return new ServiceResult(url, BaseErrorCode.SUCCESS).asResult();
    }

}
