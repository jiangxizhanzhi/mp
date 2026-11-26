package cn.cyberpeace.mp.job.schedule;

import cn.cyberpeace.mp.job.base.schedule.MpScheduled;
import cn.cyberpeace.mp.service.VendorSecondCloudServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/** Reconcile only inventory and ledger state; never automatically delete provider resources. */
@Component
@AllArgsConstructor
@Slf4j
public class VendorSecondAddressReconcileJob {
    private final VendorSecondCloudServiceImpl vendor;

    @MpScheduled(cron = "0 * * * * ?", description = "核对厂商二IP占用及回收状态", id = "vendor_second_address_reconcile")
    public void reconcile() {
        try {
            vendor.reconcileAddressPool();
        } catch (RuntimeException e) {
            log.error("厂商二IP台账定时核对失败，保留现有地址状态", e);
        }
    }
}
