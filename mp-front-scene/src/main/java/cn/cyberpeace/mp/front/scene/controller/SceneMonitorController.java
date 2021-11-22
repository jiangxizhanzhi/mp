package cn.cyberpeace.mp.front.scene.controller;

import cn.cyberpeace.mp.base.service.result.ServiceResult;
import cn.cyberpeace.mp.front.scene.dto.scene.SceneMonitorInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/22 10:15
 */
@Api(value = "场景监控", tags = "场景监控")
@Slf4j
@RestController
@RequestMapping("/monitor/scene")
public class SceneMonitorController {

    @ApiOperation(value = "查询场景监控信息")
    @GetMapping("/info/{sceneId}")
    public ServiceResult<SceneMonitorInfo> getSceneMonitorInfo(@PathVariable String sceneId) {
        log.info("sceneId:{}", sceneId);

        return ServiceResult.success(mockSceneMonitorInfo());
    }

    private SceneMonitorInfo mockSceneMonitorInfo() {
        SceneMonitorInfo sceneMonitorInfo = SceneMonitorInfo.builder()
                .creator("admin").createTime(LocalDateTime.now()).runMinutes(100).property("个人隔离")
                .networkCount(2).elasticIpCount(2).routerCount(2).firewallCount(2).flowTableCount(2)
                .vmCount(5).containerCount(10).entityCount(2)
                .build();
        return sceneMonitorInfo;
    }



}
