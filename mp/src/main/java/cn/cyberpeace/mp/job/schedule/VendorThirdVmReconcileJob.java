package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.service.VendorThirdCloudServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
@Slf4j
public class VendorThirdVmReconcileJob {
    private final VendorThirdCloudServiceImpl vendor;

    @MpScheduled(cron = "0 * * * * ?", description = "核对厂商三待确认虚拟机和回收状态", id = "vendor_third_vm_reconcile")
    public void reconcile() {
        try {
            vendor.reconcileVmLedger();
        } catch (RuntimeException e) {
            log.warn("厂商三资源台账定时核对失败，保留关联状态", e);
        }
    }
}
