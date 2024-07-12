package cn.cyberpeace.mp.devopscluster.controller;

import cn.cyberpeace.mp.base.annotation.AuthTarget;
import cn.cyberpeace.mp.base.constant.TargetEnum;
import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.SdnSwitch;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.SdnSwitchBase;
import cn.cyberpeace.mp.devopscluster.cloud.platform.model.base.SdnSwitchBaseData;
import cn.cyberpeace.mp.devopscluster.constant.ControllerConstant;
import cn.cyberpeace.mp.devopscluster.converter.SwitchConverter;
import cn.cyberpeace.mp.devopscluster.request.ModifySdnSwitchCommand;
import cn.cyberpeace.mp.devopscluster.request.RegisterSdnSwitchCommand;
import cn.cyberpeace.mp.devopscluster.request.SearchSdnSwitchRequest;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @ClassName SdnSwitchController
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/sdn_switch")
@Api(tags = "靶标管理-sdn交换机信息管理")
@RequiredArgsConstructor
@AuthTarget(TargetEnum.NO_AUTH)
public class SdnSwitchController {

    @Autowired
    private SwitchConverter switchConverter;


    /**
     * 注册新的sdn交换机
     *
     * @param registerSdnSwitchCommand
     * @return
     */
    @ApiOperation("注册新的sdn交换机")
    @PostMapping("/add")
    public ServiceResult<Boolean> addSdnSwitch(@RequestBody RegisterSdnSwitchCommand registerSdnSwitchCommand) {
        switchConverter.registerSdnSwitch(registerSdnSwitchCommand);
        return new ServiceResult<>(Boolean.TRUE).asResult();
    }

    /**
     * 分页查询sdn交换机
     *
     * @param searchSdnSwitchRequest
     * @return
     */
    @ApiOperation("分页查询sdn交换机")
    @GetMapping("/page")
    public ServiceResult<PageInfo<SdnSwitch>> pageSdnSwitch(SearchSdnSwitchRequest searchSdnSwitchRequest) {
        return new ServiceResult<>(switchConverter.pageSdnSwitch(searchSdnSwitchRequest)).asResult();
    }

    /**
     * 获取物理交换机信息
     *
     * @param name 交换机名称
     * @return 交换机信息
     */
    @ApiOperation("获取物理交换机信息")
    @GetMapping("/{name}")
    public ServiceResult<SdnSwitchBaseData> getSdnSwitch(@PathVariable(name = "name") String name) {
        SdnSwitchBaseData sdnSwitchBaseData = switchConverter.getSdnSwitch(name);
        return new ServiceResult<>(sdnSwitchBaseData).asResult();
    }

    /**
     * 修改物理交换机信息
     *
     * @param name
     * @param modifySdnSwitchCommand
     * @return
     */
    @ApiOperation("修改物理交换机信息")
    @PostMapping("/modify/{name}")
    public ServiceResult<Boolean> modifySdnSwitch(@PathVariable(name = "name") String name, @Validated @RequestBody ModifySdnSwitchCommand modifySdnSwitchCommand) {
        switchConverter.modifySdnSwitch(modifySdnSwitchCommand, name);
        return new ServiceResult<>(Boolean.TRUE).asResult();
    }

    /**
     * 删除物理交换机
     *
     * @param name
     * @return
     */
    @ApiOperation("删除物理交换机")
    @DeleteMapping("/delete/{name}")
    public ServiceResult<Boolean> deleteSdnSwitch(@PathVariable(name = "name") String name) {
        switchConverter.deleteSdnSwitch(name);
        return new ServiceResult<>(Boolean.TRUE).asResult();
    }
}
