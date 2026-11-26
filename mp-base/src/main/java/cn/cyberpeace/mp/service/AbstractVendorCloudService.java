package cn.cyberpeace.mp.service;

import cn.cyberpeace.mp.base.exception.MpRuntimeException;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/** Shared lifecycle orchestration; providers implement their existing power/state APIs. */
@Slf4j
abstract class AbstractVendorCloudService implements MultiCloudService {

    protected abstract void submitPowerAction(String vmUuid, VmPowerState target);

    protected long powerTimeoutMillis() {
        return TimeUnit.MINUTES.toMillis(5);
    }

    protected long powerPollIntervalMillis() {
        return 2000;
    }

    protected static void requireVmId(String vmUuid) {
        if (vmUuid == null || !vmUuid.matches("[A-Za-z0-9_-]+")) {
            throw new MpRuntimeException("厂商虚拟机标识无效");
        }
    }

    @Override
    public void startVm(String vmUuid) {
        changePowerState(vmUuid, VmPowerState.RUNNING);
    }

    @Override
    public void stopVm(String vmUuid) {
        changePowerState(vmUuid, VmPowerState.STOPPED);
    }

    private void changePowerState(String vmUuid, VmPowerState target) {
        requireVmId(vmUuid);
        VmPowerState before = getVmPowerState(vmUuid);
        if (before == target) return;
        if (before == null || before == VmPowerState.UNKNOWN) {
            throw new MpRuntimeException("厂商" + getVendor() + " 虚拟机 " + vmUuid + " 当前状态未确认，未提交电源操作");
        }
        // Do not automatically repeat a submitted mutation after a timeout.
        submitPowerAction(vmUuid, target);
        long deadline = System.nanoTime() + TimeUnit.MILLISECONDS.toNanos(powerTimeoutMillis());
        VmPowerState observed = VmPowerState.UNKNOWN;
        do {
            try {
                observed = getVmPowerState(vmUuid);
                if (target == observed) return;
            } catch (RuntimeException e) {
                observed = VmPowerState.UNKNOWN;
                log.warn("厂商{} VM {} 电源状态暂不可查询：{}", getVendor(), vmUuid, e.getMessage());
            }
            long remaining = TimeUnit.NANOSECONDS.toMillis(deadline - System.nanoTime());
            if (remaining <= 0) break;
            try {
                Thread.sleep(Math.min(powerPollIntervalMillis(), remaining));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new MpRuntimeException("厂商" + getVendor() + " 虚拟机电源状态确认被中断，结果待核查").e(e);
            }
        } while (System.nanoTime() < deadline);
        throw new MpRuntimeException("厂商" + getVendor() + " 虚拟机 " + vmUuid
                + " 电源操作超时，目标状态=" + target + "，最后观测状态=" + observed + "，结果待核查");
    }
}
