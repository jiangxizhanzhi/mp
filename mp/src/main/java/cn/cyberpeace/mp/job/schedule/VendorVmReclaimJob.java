package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.service.VendorVmReclaimService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Independent staggered jobs prevent one unavailable provider from blocking the others. */
@Component
@AllArgsConstructor
@Slf4j
public class VendorVmReclaimJob {
    private final VendorVmReclaimService service;

    @MpScheduled(cron = "0 0/5 * * * ?", id = "vendor_first_vm_reclaim", description = "厂商一已关闭场景残留虚拟机兜底回收")
    public void reclaimFirst() { run(1); }

    @MpScheduled(cron = "5 0/5 * * * ?", id = "vendor_second_vm_reclaim", description = "厂商二已关闭场景残留虚拟机兜底回收")
    public void reclaimSecond() { run(2); }

    @MpScheduled(cron = "10 0/5 * * * ?", id = "vendor_third_vm_reclaim", description = "厂商三已关闭场景残留虚拟机兜底回收")
    public void reclaimThird() { run(3); }

    private void run(int vendor) {
        try {
            service.reclaim(vendor);
        } catch (RuntimeException e) {
            log.warn("厂商{}兜底回收巡查未完成，本轮保留未确认资源", vendor, e);
        }
    }
}
