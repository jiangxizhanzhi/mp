package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.scene.service.SceneService;
import cn.cyberpeace.mp.service.VendorSceneDeleteRetryService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class VendorSceneDeleteRetryJob {
    private final VendorSceneDeleteRetryService retries;
    private final SceneService scenes;

    @MpScheduled(cron = "10 * * * * ?", id = "vendor_scene_delete_retry", description = "重试已关闭实验的厂商场景回收")
    public void retry() {
        try {
            retries.retry(id -> {
                if (!Boolean.TRUE.equals(scenes.delete(id, true, null).getSource()))
                    throw new IllegalStateException("场景回收重试未提交，scene=" + id);
            });
        } catch (RuntimeException e) {
            log.warn("厂商场景回收重试检查未完成", e);
        }
    }
}
