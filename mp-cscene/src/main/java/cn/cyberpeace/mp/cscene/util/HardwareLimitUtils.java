package cn.cyberpeace.mp.cscene.util;

import cn.cyberpeace.mp.base.service.code.MessageServiceCode;
import cn.cyberpeace.mp.cscene.bo.CsceneInstanceBO;
import cn.cyberpeace.mp.cscene.constant.CsceneErrorCode;
import cn.cyberpeace.mp.scene.core.bo.SceneElementBO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Optional;

@Slf4j
public class HardwareLimitUtils {
    private static HardwareCount TOTAL = new HardwareCount(64, 1823127060, 128799);

    private static HardwareCount USED = new HardwareCount(0, 0, 0);

    /**
     * initTotal
     *
     * @param cpu
     * @param ram
     * @param disk
     */
    public static void initTotal(int cpu, int ram, int disk) {
        TOTAL = new HardwareCount();
        TOTAL.setCpu(cpu);
        TOTAL.setRam(ram);
        TOTAL.setDisk(disk);
    }

    /**
     * initUsed
     *
     * @param cpu
     * @param ram
     * @param disk
     */
    public static void initUsed(int cpu, int ram, int disk) {
        USED = new HardwareCount();
        USED.setCpu(cpu);
        USED.setRam(ram);
        USED.setDisk(disk);
    }

    /**
     * addHardware
     *
     * @param instance
     * @return
     */
    public static UpdateResult addHardware(CsceneInstanceBO instance) {
        HardwareCount count = getHardwareCount(instance.getScene().getTopology().getElements());
        return updateHardware(count);
    }

    /**
     * substractHardware
     *
     * @param instance
     * @return
     */
    public static UpdateResult substractHardware(CsceneInstanceBO instance) {
        HardwareCount count = getHardwareCount(instance.getScene().getTopology().getElements());
        count.setDisk(-1 * Math.abs(count.getDisk()));
        count.setCpu(-1 * Math.abs(count.getCpu()));
        count.setRam(-1 * Math.abs(count.getRam()));
        return updateHardware(count);
    }

    /**
     * updateHardware
     *
     * @param count
     * @return
     */
    private static synchronized UpdateResult updateHardware(HardwareCount count) {
        UpdateResult successResult = new UpdateResult();
        successResult.setSuccess(true);
        if (count.getCpu() == 0 && count.getDisk() == 0 && count.getRam() == 0) {
            return successResult;
        }
        if (USED.getCpu() + count.getCpu() > TOTAL.getCpu()) {
            UpdateResult result = new UpdateResult();
            result.setSuccess(false);
            result.setLimitNode(TOTAL.getCpu());
            result.setCurrentNode(count.getCpu());
            result.setExistNode(USED.getCpu());
            result.setErrorCode(CsceneErrorCode.CPU_LIMIT);
            return result;
        }

        if (USED.getRam() + count.getRam() > TOTAL.getRam()) {
            UpdateResult result = new UpdateResult();
            result.setSuccess(false);
            result.setLimitNode(TOTAL.getRam());
            result.setCurrentNode(count.getRam());
            result.setExistNode(USED.getRam());
            result.setErrorCode(CsceneErrorCode.RAM_LIMIT);
            return result;
        }

        if (USED.getDisk() + count.getDisk() > TOTAL.getDisk()) {
            UpdateResult result = new UpdateResult();
            result.setSuccess(false);
            result.setLimitNode(TOTAL.getDisk());
            result.setCurrentNode(count.getDisk());
            result.setExistNode(USED.getDisk());
            result.setErrorCode(CsceneErrorCode.DISK_LIMIT);
            return result;
        }

        USED.setCpu(USED.getCpu() + count.getCpu());
        USED.setRam(USED.getRam() + count.getRam());
        USED.setDisk(USED.getDisk() + count.getDisk());
        if (USED.getCpu() < 0) {
            USED.setCpu(0);
        }
        if (USED.getRam() < 0) {
            USED.setRam(0);
        }
        if (USED.getDisk() < 0) {
            USED.setDisk(0);
        }
        UpdateResult result = new UpdateResult();
        result.setSuccess(true);
        return result;
    }

    /**
     * getHardwareCount
     *
     * @param elements
     * @return
     */
    private static HardwareCount getHardwareCount(List<SceneElementBO> elements) {
        int cpu = 0;
        int disk = 0;
        int ram = 0;
        for (SceneElementBO e : elements) {
            int eCpu = Optional.ofNullable(e.getCpu()).orElse(0);
            int eRam = Optional.ofNullable(e.getRam()).orElse(0);
            int eDisk = Optional.ofNullable(e.getDisk()).orElse(0);
            cpu += eCpu;
            disk += eDisk;
            ram += eRam;
        }

        HardwareCount count = new HardwareCount();
        count.setDisk(disk);
        count.setCpu(cpu);
        count.setRam(ram);
        return count;
    }

    @Getter
    @Setter
    public static class UpdateResult {
        private boolean success;
        private int limitNode;
        private int currentNode;
        private int existNode;
        private CsceneErrorCode errorCode;

        /**
         * getMessage
         *
         * @return
         */
        public MessageServiceCode getMessage() {
            return errorCode.format(String.valueOf(limitNode), String.valueOf(existNode), String.valueOf(currentNode));
        }
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    private static class HardwareCount {
        private int cpu;
        private int disk;
        private int ram;
    }
}