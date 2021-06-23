package cn.cyberpeace.mp.datasandtable.model;

import com.alibaba.fastjson.annotation.JSONField;
import java.util.Date;
import java.util.List;
import lombok.Data;

@Data
public class WazuhNetifaceRoot {

    private Data data;
    private String message;
    private int error;

    public void setData(Data data) {
        this.data = data;
    }
    public Data getData() {
        return this.data;
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

    public static class Data {
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

    public static class AffectedItems {

        private Rx rx;
        private Scan scan;
        private Tx tx;
        private int mtu;
        private String type;
        private String state;
        private String name;
        private String mac;
        @JSONField(name = "agent_id")
        private String agentId;

        public void setRx(Rx rx) {
            this.rx = rx;
        }
        public Rx getRx() {
            return this.rx;
        }
        public void setScan(Scan scan) {
            this.scan = scan;
        }
        public Scan getScan() {
            return this.scan;
        }
        public void setTx(Tx tx) {
            this.tx = tx;
        }
        public Tx getTx() {
            return this.tx;
        }
        public void setMtu(int mtu) {
            this.mtu = mtu;
        }
        public int getMtu() {
            return this.mtu;
        }
        public void setType(String type) {
            this.type = type;
        }
        public String getType() {
            return this.type;
        }
        public void setState(String state) {
            this.state = state;
        }
        public String getState() {
            return this.state;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return this.name;
        }
        public void setMac(String mac) {
            this.mac = mac;
        }
        public String getMac() {
            return this.mac;
        }
        public void setAgentId(String agentId) {
            this.agentId = agentId;
        }
        public String getAgentId() {
            return this.agentId;
        }
    }

    public static class Tx {

        private int bytes;
        private int dropped;
        private int errors;
        private int packets;

        public void setBytes(int bytes) {
            this.bytes = bytes;
        }
        public int getBytes() {
            return this.bytes;
        }
        public void setDropped(int dropped) {
            this.dropped = dropped;
        }
        public int getDropped() {
            return this.dropped;
        }
        public void setErrors(int errors) {
            this.errors = errors;
        }
        public int getErrors() {
            return this.errors;
        }
        public void setPackets(int packets) {
            this.packets = packets;
        }
        public int getPackets() {
            return this.packets;
        }
    }


    public static class Scan {

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


    public static class Rx {

        private int bytes;
        private int dropped;
        private int errors;
        private int packets;

        public void setBytes(int bytes) {
            this.bytes = bytes;
        }
        public int getBytes() {
            return this.bytes;
        }
        public void setDropped(int dropped) {
            this.dropped = dropped;
        }
        public int getDropped() {
            return this.dropped;
        }
        public void setErrors(int errors) {
            this.errors = errors;
        }
        public int getErrors() {
            return this.errors;
        }
        public void setPackets(int packets) {
            this.packets = packets;
        }
        public int getPackets() {
            return this.packets;
        }
    }
}
