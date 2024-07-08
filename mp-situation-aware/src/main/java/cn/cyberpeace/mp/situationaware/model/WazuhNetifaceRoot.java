package cn.cyberpeace.mp.situationaware.model;

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

    /**
     * Tx类用于统计传输相关的数据，包括字节数、丢弃数、错误数和包数。
     */
    public static class Tx {

        // 总共传输的字节数
        private int bytes;
        // 传输过程中被丢弃的字节数
        private int dropped;
        // 传输过程中出现的错误数
        private int errors;
        // 总共传输的包数
        private int packets;

        /**
         * 设置传输的字节数。
         *
         * @param bytes 传输的字节数
         */
        public void setBytes(int bytes) {
            this.bytes = bytes;
        }

        /**
         * 获取传输的字节数。
         *
         * @return 传输的字节数
         */
        public int getBytes() {
            return this.bytes;
        }

        /**
         * 设置传输过程中被丢弃的字节数。
         *
         * @param dropped 被丢弃的字节数
         */
        public void setDropped(int dropped) {
            this.dropped = dropped;
        }

        /**
         * 获取传输过程中被丢弃的字节数。
         *
         * @return 被丢弃的字节数
         */
        public int getDropped() {
            return this.dropped;
        }

        /**
         * 设置传输过程中出现的错误数。
         *
         * @param errors 出现的错误数
         */
        public void setErrors(int errors) {
            this.errors = errors;
        }

        /**
         * 获取传输过程中出现的错误数。
         *
         * @return 出现的错误数
         */
        public int getErrors() {
            return this.errors;
        }

        /**
         * 设置传输的包数。
         *
         * @param packets 传输的包数
         */
        public void setPackets(int packets) {
            this.packets = packets;
        }

        /**
         * 获取传输的包数。
         *
         * @return 传输的包数
         */
        public int getPackets() {
            return this.packets;
        }
    }


    /**
     * Scan类用于存储扫描操作的相关信息。
     * 主要包括扫描的唯一标识符和操作时间。
     */
    public static class Scan {

        private int id; // 扫描的唯一标识符
        private Date time; // 扫描操作的时间

        /**
         * 设置扫描的唯一标识符。
         *
         * @param id 扫描的唯一标识符
         */
        public void setId(int id) {
            this.id = id;
        }

        /**
         * 获取扫描的唯一标识符。
         *
         * @return 扫描的唯一标识符
         */
        public int getId() {
            return this.id;
        }

        /**
         * 设置扫描操作的时间。
         *
         * @param time 扫描操作的时间
         */
        public void setTime(Date time) {
            this.time = time;
        }

        /**
         * 获取扫描操作的时间。
         *
         * @return 扫描操作的时间
         */
        public Date getTime() {
            return this.time;
        }
    }

    /**
     * Rx类用于存储接收（Rx）操作的相关统计信息。
     * 主要包括接收的字节数、丢失的包数、错误数和总包数。
     */
    public static class Rx {

        private int bytes; // 接收的字节数
        private int dropped; // 丢失的包数
        private int errors; // 错误数
        private int packets; // 总包数

        /**
         * 设置接收的字节数。
         *
         * @param bytes 接收的字节数
         */
        public void setBytes(int bytes) {
            this.bytes = bytes;
        }

        /**
         * 获取接收的字节数。
         *
         * @return 接收的字节数
         */
        public int getBytes() {
            return this.bytes;
        }

        /**
         * 设置丢失的包数。
         *
         * @param dropped 丢失的包数
         */
        public void setDropped(int dropped) {
            this.dropped = dropped;
        }

        /**
         * 获取丢失的包数。
         *
         * @return 丢失的包数
         */
        public int getDropped() {
            return this.dropped;
        }

        /**
         * 设置错误数。
         *
         * @param errors 错误数
         */
        public void setErrors(int errors) {
            this.errors = errors;
        }

        /**
         * 获取错误数。
         *
         * @return 错误数
         */
        public int getErrors() {
            return this.errors;
        }

        /**
         * 设置总包数。
         *
         * @param packets 总包数
         */
        public void setPackets(int packets) {
            this.packets = packets;
        }

        /**
         * 获取总包数。
         *
         * @return 总包数
         */
        public int getPackets() {
            return this.packets;
        }
    }

}
