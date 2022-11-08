package cn.cyberpeace.mp.situationaware.model;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.List;

public class WazuhRamRoot {
    private WazuhRamData data;

    private String message;

    private int error;

    public WazuhRamData getData() {
        return data;
    }

    public void setData(WazuhRamData data) {
        this.data = data;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

    public void setError(int error) {
        this.error = error;
    }

    public int getError() {
        return this.error;
    }

    static class WazuhRamData {
        @JSONField(name = "affected_items")
        private List<AffectedItems> affectedItems;
        @JSONField(name = "total_affected_items")
        private int totalAffectedItems;
        @JSONField(name = "total_failed_items")
        private int totalFailedItems;
        @JSONField(name = "failed_items")
        private List<String> failedItems;

        public void setAffectedItems(List<AffectedItems> affectedItems) {
            this.affectedItems = affectedItems;
        }

        public List<AffectedItems> getAffectedItems() {
            return this.affectedItems;
        }

        public void setTotalAffectedItems(int totalAffectedItems) {
            this.totalAffectedItems = totalAffectedItems;
        }

        public int getTotalAffectedItems() {
            return this.totalAffectedItems;
        }

        public void setTotalFailedItems(int totalFailedItems) {
            this.totalFailedItems = totalFailedItems;
        }

        public int getTotalFailedItems() {
            return this.totalFailedItems;
        }

        public void setFailedItems(List<String> failedItems) {
            this.failedItems = failedItems;
        }

        public List<String> getFailedItems() {
            return this.failedItems;
        }
    }

    static class Failed_items {
    }

    static class AffectedItems {
        private Cpu cpu;
        private Ram ram;
        private Scan scan;
        @JSONField(name = "board_serial")
        private String boardSerial;
        @JSONField(name = "agent_id")
        private String agentId;

        public void setCpu(Cpu cpu) {
            this.cpu = cpu;
        }

        public Cpu getCpu() {
            return this.cpu;
        }

        public void setRam(Ram ram) {
            this.ram = ram;
        }

        public Ram getRam() {
            return this.ram;
        }

        public void setScan(Scan scan) {
            this.scan = scan;
        }

        public Scan getScan() {
            return this.scan;
        }

        public void setBoardSerial(String boardSerial) {
            this.boardSerial = boardSerial;
        }

        public String getBoardSerial() {
            return this.boardSerial;
        }

        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }

        public String getAgentId() {
            return this.agentId;
        }
    }

    static class Ram {
        private int free;
        private int total;
        private int usage;

        public void setFree(int free) {
            this.free = free;
        }

        public int getFree() {
            return this.free;
        }

        public void setTotal(int total) {
            this.total = total;
        }

        public int getTotal() {
            return this.total;
        }

        public void setUsage(int usage) {
            this.usage = usage;
        }

        public int getUsage() {
            return this.usage;
        }
    }

    static class Cpu {
        private int cores;
        private double mhz;
        private String name;

        public void setCores(int cores) {
            this.cores = cores;
        }

        public int getCores() {
            return this.cores;
        }

        public void setMhz(double mhz) {
            this.mhz = mhz;
        }

        public double getMhz() {
            return this.mhz;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getName() {
            return this.name;
        }
    }

    static class Scan {
        private int id;
        private Date time;

        public void setId(int id) {
            this.id = id;
        }

        public int getId() {
            return this.id;
        }

        public void setTime(Date time) {
            this.time = time;
        }

        public Date getTime() {
            return this.time;
        }
    }
}
