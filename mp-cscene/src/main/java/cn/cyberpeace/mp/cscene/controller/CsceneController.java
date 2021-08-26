package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.bo.CsceneBO;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.service.CsceneService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/cscenes")
@Data
public class CsceneController {

    private CsceneService csceneService;

    @Autowired
    public CsceneController(CsceneService csceneService) {
        setCsceneService(csceneService);
    }

    @GetMapping(params = "id")
    public ServiceResult<CsceneBO> get(@RequestParam String id) {
        return csceneService.getById(id).asResult();
    }

    @PostMapping("/sync")
    public ServiceResult<CsceneBO> sync(@RequestBody CsceneBO csceneBO) {
        return csceneService.sync(csceneBO).asResult();
    }

    @PostMapping(value = "/test_instance", params = "id")
    public ServiceResult<CsceneInstanceBO> createTestInstance(@RequestParam String id) {
        return csceneService.createTestInstance(id, true).asResult();
    }

    @PostMapping(value = "/app_instance", params = "id")
    public ServiceResult<CsceneInstanceBO> createAppInstance(@RequestParam String id) {
        return csceneService.createAppInstance(id, true).asResult();
    }

    @DeleteMapping(params = "id")
    public ServiceResult<Boolean> delete(@RequestParam String id) {
        return csceneService.delete(id).asResult();
    }

    @DeleteMapping("/bulk_delete")
    public ServiceResult<Integer> bulkDelete(@RequestBody String[] ids) {
        return csceneService.bulkDelete(ids).asResult();
    }

}
