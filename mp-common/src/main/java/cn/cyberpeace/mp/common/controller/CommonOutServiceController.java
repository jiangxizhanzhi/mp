package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.common.constant.ControllerConstant;
import cn.cyberpeace.mp.common.service.impl.CommonOutServiceServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/commonOutService")
public class CommonOutServiceController {

    @Autowired
    private CommonOutServiceServiceImpl commonOutServiceService;



}
