package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBOExt;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/import")
public class ImportResourceTypeController {

    public static CsceneBOExt csceneBOExt = new CsceneBOExt();

    @PostMapping("/import_resource_type")
    public ServiceResult<Boolean> importResourceType(@RequestBody CsceneBOExt csceneBOExt) {
        this.csceneBOExt.setSceneBizType(csceneBOExt.getSceneBizType());
        this.csceneBOExt.setImageCate(csceneBOExt.getImageCate());
        this.csceneBOExt.setSceneConvertTemplate(csceneBOExt.getSceneConvertTemplate());
        return new ServiceResult<>(true);
    }

    @GetMapping("/query_resource_type")
    public ServiceResult<CsceneBOExt> query() {
        return new ServiceResult<>(csceneBOExt).asResult();
    }
}
