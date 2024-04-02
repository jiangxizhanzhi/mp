package cn.cyberpeace.mp.cscene.controller.controller;

import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.service.CloudSceneServiceImpl;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "云场景模板", tags = "云场景模板配置项")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_CLOUD_SCENE_API_PATH + "/config")
public class CloudSceneController {

    @Autowired
    private CloudSceneServiceImpl cloudSceneServiceImpl;

}
