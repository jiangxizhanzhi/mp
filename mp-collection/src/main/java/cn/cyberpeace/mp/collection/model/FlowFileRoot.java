package cn.cyberpeace.mp.collection.model;

import com.alibaba.fastjson.annotation.JSONField;
import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * json解析
 */
@Data
public class FlowFileRoot {

    private Source source;

    private Destination destination;

    private List<String> protocol;

    @JSONField(name = "@timestamp")
    private Long timestamp;

    private String id;

    @JSONField(name = "teamId")
    private String teamId;

    public static class Source {

        private String ip;

        private Long port;

        private Long bytes;

        private Integer packets;

        public List<String> getMac() {
            return mac;
        }

        public void setMac(List<String> mac) {
            this.mac = mac;
        }

        private List<String> mac;

        public String getIp() {
            return ip;
        }

        public Long getPort() {
            return port;
        }

        public Long getBytes() {
            return bytes;
        }

        public Integer getPackets() {
            return packets;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setPort(Long port) {
            this.port = port;
        }

        public void setBytes(Long bytes) {
            this.bytes = bytes;
        }

        public void setPackets(Integer packets) {
            this.packets = packets;
        }


    }

    public static class Destination {

        private String ip;

        private Long port;

        private Long bytes;

        private Integer packets;

        private List<String> mac;

        public List<String> getMac() {
            return mac;
        }

        public void setMac(List<String> mac) {
            this.mac = mac;
        }

        public String getIp() {
            return ip;
        }

        public Long getPort() {
            return port;
        }

        public Long getBytes() {
            return bytes;
        }

        public Integer getPackets() {
            return packets;
        }

        public void setIp(String ip) {
            this.ip = ip;
        }

        public void setPort(Long port) {
            this.port = port;
        }

        public void setBytes(Long bytes) {
            this.bytes = bytes;
        }

        public void setPackets(Integer packets) {
            this.packets = packets;
        }

    }


}
