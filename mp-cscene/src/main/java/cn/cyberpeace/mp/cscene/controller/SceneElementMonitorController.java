package cn.cyberpeace.mp.cscene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.cscene.constant.ControllerConstant;
import cn.cyberpeace.mp.cscene.dto.element.ElementResourceUsage;
import cn.cyberpeace.mp.cscene.dto.event.ElementMonitorInfo;
import cn.hutool.core.collection.CollectionUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 10:16
 */
@Api(value = "场景节点监控", tags = "场景节点监控")
@Slf4j
@RestController
@RequestMapping(ControllerConstant.BASE_API_PATH + "/monitor/scene/element")
public class SceneElementMonitorController {

    @ApiOperation(value = "查询节点监控信息")
    @GetMapping("/info/{elementId}")
    public ServiceResult<ElementMonitorInfo> getSceneElementMonitorInfo(@PathVariable String elementId) {
        log.info("elementId:{}", elementId);

        return ServiceResult.success(mockElementMonitorInfo());
    }


    @ApiOperation(value = "查询节点资源使用统计")
    @GetMapping("/resource_usage/{elementId}")
    public ServiceResult<ElementResourceUsage> getElementResourceUsage(@PathVariable String elementId) {
        log.info("elementId:{}", elementId);

        return null;
    }


    private ElementMonitorInfo mockElementMonitorInfo() {
        ElementMonitorInfo elementMonitorInfo = ElementMonitorInfo.builder()
                .name("kili操作机").ips(CollectionUtil.toList("192.168.0.1", "192.168.0.2")).os("centos7").imageName("centos7.2").physicalNode("计算节点1").instanceType("虚拟机")
                .processCount(5).userCount(1).portsCount(10).connectionCount(11).fileOpCount(5).registryOpCount(5).envOpCount(5)
                .nodeCPUs(8).memSize("16G").diskSize("4T")
                .build();
        return elementMonitorInfo;
    }


}
