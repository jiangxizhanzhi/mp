package cn.cyberpeace.mp.aggregation.controller;

import cn.cyberpeace.mp.aggregation.constant.ControllerConstant;
import cn.cyberpeace.mp.aggregation.dto.sceneimport.SceneImportRequest;
import cn.cyberpeace.mp.aggregation.service.ResourceCenterService;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description
 * @Author maojun
 * @Date 2021/10/11
 */
@Api(value = "场景导入相关接口", tags = "场景导入相关接口")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
public class SceneImportController {

    @Resource
    private ResourceCenterService resourceCenterService;

    @PostMapping("/import_resource_center")
    public ServiceResult<String> importResourceCenter(@RequestBody SceneImportRequest request) {
        if (StringUtils.isEmpty(request.getPath())) {
            return ServiceResult.fail(BaseErrorCode.PARAMS_INVALID);
        }
        ServiceResult<CsceneBO> serviceResult = resourceCenterService.importByYmlConfig(request.getPath()+"/topo.yml");
        return ServiceResult.success(serviceResult.getSource().getId());
    }
}
