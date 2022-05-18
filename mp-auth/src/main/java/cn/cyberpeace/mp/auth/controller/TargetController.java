package cn.cyberpeace.mp.auth.controller;

import cn.cyberpeace.mp.auth.dto.validate.AddGroup;
import cn.cyberpeace.mp.auth.model.TargetModel;
import cn.cyberpeace.mp.auth.service.TargetService;
import cn.cyberpeace.mp.base.controller.BaseModelController;
import cn.cyberpeace.mp.base.service.param.QueryParams;
import cn.cyberpeace.mp.base.service.result.ObjServiceResult;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import springfox.documentation.annotations.ApiIgnore;

/**
 * @Description: 权限管理接口
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@ApiIgnore
@Api(value = "权限管理接口", tags = "AUTH-权限管理接口")
@Slf4j
//@RestController
//@RequestMapping(ControllerConstant.BASE_API_PATH + "/targets")
public class TargetController {


}
