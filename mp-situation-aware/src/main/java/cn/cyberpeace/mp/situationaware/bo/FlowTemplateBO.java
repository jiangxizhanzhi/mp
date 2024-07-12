 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowTemplateBO.java.
 // File size 6012.
 // File create date Fri Jul 12 12:55:20 CST 2024.
 // File update date Fri Jul 12 12:55:20 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.situationaware.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
/**
 * 流模板业务对象类，用于封装流量数据的相关信息。
 */
public class FlowTemplateBO {

    /**
     * 第一个数据包的时间戳。
     */
    @JsonProperty("firstPacket")
    private Long firstPacket;

    /**
     * 最后一个数据包的时间戳。
     */
    @JsonProperty("lastPacket")
    private Long lastPacket;

    /**
     * 数据包总长度。
     */
    @JsonProperty("length")
    private Integer length;

    /**
     * IP协议类型。
     */
    @JsonProperty("ipProtocol")
    private Integer ipProtocol;

    /**
     * 源负载的8位字节表示。
     */
    @JsonProperty("srcPayload8")
    private String srcPayload8;

    /**
     * 数据流的源信息。
     */
    @JsonProperty("source")
    private SourceDTO source;

    /**
     * 数据流的目的信息。
     */
    @JsonProperty("destination")
    private DestinationDTO destination;

    /**
     * 网络相关信息。
     */
    @JsonProperty("network")
    private NetworkDTO network;

    /**
     * 客户端相关信息。
     */
    @JsonProperty("client")
    private ClientDTO client;

    /**
     * 服务端相关信息。
     */
    @JsonProperty("server")
    private ServerDTO server;

    /**
     * 总数据字节数。
     */
    @JsonProperty("totDataBytes")
    private Integer totDataBytes;

    /**
     * 分段数量。
     */
    @JsonProperty("segmentCnt")
    private Integer segmentCnt;

    /**
     * 节点标识。
     */
    @JsonProperty("node")
    private String node;

    /**
     * 数据包位置列表。
     */
    @JsonProperty("packetPos")
    private List<Integer> packetPos;

    /**
     * 文件ID列表。
     */
    @JsonProperty("fileId")
    private List<Integer> fileId;

    /**
     * 协议数量。
     */
    @JsonProperty("protocolCnt")
    private Integer protocolCnt;

    /**
     * 协议列表。
     */
    @JsonProperty("protocol")
    private List<String> protocol;

    /**
     * Suricata引擎相关数据。
     */
    @JsonProperty("suricata")
    private SuricataDTO suricata;

    /**
     * 源信息数据传输对象类，用于封装源相关的网络流量信息。
     */
    @NoArgsConstructor
    @Data
    public static class SourceDTO {
        /**
         * 源IP地址。
         */
        @JsonProperty("ip")
        private String ip;
        /**
         * 源端口。
         */
        @JsonProperty("port")
        private Integer port;
        /**
         * 发送的字节数。
         */
        @JsonProperty("bytes")
        private Integer bytes;
        /**
         * 发送的数据包数量。
         */
        @JsonProperty("packets")
        private Integer packets;
        /**
         * MAC地址数量。
         */
        @JsonProperty("mac-cnt")
        private Integer maccnt;
        /**
         * MAC地址列表。
         */
        @JsonProperty("mac")
        private List<String> mac;
    }

    /**
     * 目的信息数据传输对象类，用于封装目的相关的网络流量信息。
     */
    @NoArgsConstructor
    @Data
    public static class DestinationDTO {
        /**
         * 目的IP地址。
         */
        @JsonProperty("ip")
        private String ip;
        /**
         * 目的端口。
         */
        @JsonProperty("port")
        private Integer port;
        /**
         * 接收的字节数。
         */
        @JsonProperty("bytes")
        private Integer bytes;
        /**
         * 接收的数据包数量。
         */
        @JsonProperty("packets")
        private Integer packets;
        /**
         * MAC地址数量。
         */
        @JsonProperty("mac-cnt")
        private Integer maccnt;
        /**
         * MAC地址列表。
         */
        @JsonProperty("mac")
        private List<String> mac;
    }

    /**
     * 网络信息数据传输对象类，用于封装网络层的流量信息。
     */
    @NoArgsConstructor
    @Data
    public static class NetworkDTO {
        /**
         * 数据包数量。
         */
        @JsonProperty("packets")
        private Integer packets;
        /**
         * 字节数。
         */
        @JsonProperty("bytes")
        private Integer bytes;
        /**
         * 社区ID，用于唯一标识网络流量。
         */
        @JsonProperty("community_id")
        private String communityId;
    }

    /**
     * 客户端信息数据传输对象类，用于封装客户端相关的网络流量信息。
     */
    @NoArgsConstructor
    @Data
    public static class ClientDTO {
        /**
         * 客户端发送的字节数。
         */
        @JsonProperty("bytes")
        private Integer bytes;
    }

    /**
     * 服务端信息数据传输对象类，用于封装服务端相关的网络流量信息。
     */
    @NoArgsConstructor
    @Data
    public static class ServerDTO {
        /**
         * 服务端接收的字节数。
         */
        @JsonProperty("bytes")
        private Integer bytes;
    }

    /**
     * Suricata引擎事件信息数据传输对象类，用于封装网络流量的Suricata检测信息。
     */
    @NoArgsConstructor
    @Data
    public static class SuricataDTO {
        /**
         * 事件所属的类别列表。
         */
        @JsonProperty("category")
        private List<String> category;
        /**
         * 事件的严重性等级列表。
         */
        @JsonProperty("severity")
        private List<Integer> severity;
    }
}
