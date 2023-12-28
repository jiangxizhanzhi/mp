package cn.cyberpeace.mp.aggregation.localservice;

import cn.cyberpeace.mp.aggregation.config.AggregationConfig;
import cn.cyberpeace.mp.base.annotation.LocalCallFunc;
import cn.cyberpeace.mp.base.annotation.LocalCallServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * @Description
 * @Author hewei
 * @Date 2022/8/2
 */
@Slf4j
@Component
@LocalCallServer
public class ExportSceneImageLimitConfigService {

    @Resource
    private AggregationConfig aggregationConfig;

    /**
     * 主动触发初始靶标同步
     */
    @LocalCallFunc
    public void update(boolean enable) {
        log.info("update export scene image limit switch from {} to  {}", aggregationConfig.getExportSceneImageLimitEnable(), enable);
        aggregationConfig.setExportSceneImageLimitEnable(enable);
    }
}
