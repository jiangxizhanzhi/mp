package cn.cyberpeace.mp.job.init;

import cn.cyberpeace.mp.cscene.service.HardwareLimitRefreshService;
import cn.cyberpeace.mp.job.base.init.MpInitiate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 启动完后生命周期进行
 */
@Component
@Slf4j
public class HardwareLimitJob {

    @Resource
    private HardwareLimitRefreshService hardwareLimitRefreshService;

    @MpInitiate(description = "cpu、内存、磁盘使用数量的初始化", id = "hardware_limit_init")
    public void exec() {
        hardwareLimitRefreshService.init();
    }
}
