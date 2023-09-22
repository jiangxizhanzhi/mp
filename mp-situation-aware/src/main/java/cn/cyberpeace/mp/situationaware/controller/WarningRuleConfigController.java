package cn.cyberpeace.mp.situationaware.controller;

import cn.cyberpeace.mp.situationaware.constant.SuricataConstant;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName WarningRuleConfigController
 * @Description
 * @Author zhanzhi
 * @Date 2023/9/22 9:16
 **/
@Api(value = "告警规则配置", tags = "告警规则配置")
@Slf4j
@RestController
@RequestMapping(SuricataConstant.BASE_API_PATH + "/warning")
public class WarningRuleConfigController {

}
