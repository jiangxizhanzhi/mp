package cn.cyberpeace.mp.common.controller;

import cn.cyberpeace.mp.base.bo.MpSysCodeEnum;
import cn.cyberpeace.mp.base.constant.BaseErrorCode;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.common.constant.ControllerConstant;
import cn.cyberpeace.mp.common.service.impl.CommonCodeServiceImpl;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/commonCode")
public class CommonCodeController {

    @Autowired
    private CommonCodeServiceImpl commonCodeService;

    /**
     * 根据code查找下级
     * @return List<MpSysCodeEnum>
     */
    @ApiOperation("根据code查找下级")
    @GetMapping("/getNextLevelDataByCode")
    public ServiceResult getNextLevelDataByCode(@RequestParam(value = "code") String code) {
        List<MpSysCodeEnum> getNextLevelDataByCode = commonCodeService.getNextLevelDataByCode(code);
        return new ServiceResult(getNextLevelDataByCode, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据code查找下级所有(平级)
     * @return List<MpSysCodeEnum>
     */
    @ApiOperation("根据code查找下级所有(平级)")
    @GetMapping("/getNextAllDataByCode")
    public ServiceResult getNextAllDataByCode(@RequestParam(value = "code") String code) {
        List<MpSysCodeEnum> getNextAllDataByCode = commonCodeService.getNextAllDataByCode(code);
        return new ServiceResult(getNextAllDataByCode, BaseErrorCode.SUCCESS).asResult();
    }

    /**
     * 根据code查找下级所有(树级)
     * @param code
     * @param includeSelf(是否包含code本级)
     * @return List<MpSysCodeEnum>
     */
    @ApiOperation("根据code查找下级所有(树级)")
    @GetMapping("/getNextAllTreeDataByCode")
    public ServiceResult getNextAllTreeDataByCode(@RequestParam(value = "code") String code,
                                                  @RequestParam(value = "includeSelf", required = false) Boolean includeSelf) {
        List<MpSysCodeEnum> getNextAllDataByCode = commonCodeService.getNextAllTreeDataByCode(code,includeSelf);
        return new ServiceResult(getNextAllDataByCode, BaseErrorCode.SUCCESS).asResult();
    }

}
