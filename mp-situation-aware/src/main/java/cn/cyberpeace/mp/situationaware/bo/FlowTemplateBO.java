package cn.cyberpeace.mp.situationaware.bo;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@Data
public class FlowTemplateBO {

    @JsonProperty("firstPacket")
    private Long firstPacket;

    @JsonProperty("lastPacket")
    private Long lastPacket;

    @JsonProperty("length")
    private Integer length;

    @JsonProperty("ipProtocol")
    private Integer ipProtocol;

    @JsonProperty("srcPayload8")
    private String srcPayload8;

    @JsonProperty("source")
    private SourceDTO source;

    @JsonProperty("destination")
    private DestinationDTO destination;

    @JsonProperty("network")
    private NetworkDTO network;

    @JsonProperty("client")
    private ClientDTO client;
    @JsonProperty("server")
    private ServerDTO server;
    @JsonProperty("totDataBytes")
    private Integer totDataBytes;
    @JsonProperty("segmentCnt")
    private Integer segmentCnt;
    @JsonProperty("node")
    private String node;
    @JsonProperty("packetPos")
    private List<Integer> packetPos;
    @JsonProperty("fileId")
    private List<Integer> fileId;
    @JsonProperty("protocolCnt")
    private Integer protocolCnt;
    @JsonProperty("protocol")
    private List<String> protocol;
    @JsonProperty("suricata")
    private SuricataDTO suricata;

    @NoArgsConstructor
    @Data
    public static class SourceDTO {
        @JsonProperty("ip")
        private String ip;
        @JsonProperty("port")
        private Integer port;
        @JsonProperty("bytes")
        private Integer bytes;
        @JsonProperty("packets")
        private Integer packets;
        @JsonProperty("mac-cnt")
        private Integer maccnt;
        @JsonProperty("mac")
        private List<String> mac;
    }

    @NoArgsConstructor
    @Data
    public static class DestinationDTO {
        @JsonProperty("ip")
        private String ip;
        @JsonProperty("port")
        private Integer port;
        @JsonProperty("bytes")
        private Integer bytes;
        @JsonProperty("packets")
        private Integer packets;
        @JsonProperty("mac-cnt")
        private Integer maccnt;
        @JsonProperty("mac")
        private List<String> mac;
    }

    @NoArgsConstructor
    @Data
    public static class NetworkDTO {
        @JsonProperty("packets")
        private Integer packets;
        @JsonProperty("bytes")
        private Integer bytes;
        @JsonProperty("community_id")
        private String communityId;
    }

    @NoArgsConstructor
    @Data
    public static class ClientDTO {
        @JsonProperty("bytes")
        private Integer bytes;
    }

    @NoArgsConstructor
    @Data
    public static class ServerDTO {
        @JsonProperty("bytes")
        private Integer bytes;
    }

    @NoArgsConstructor
    @Data
    public static class SuricataDTO {
        @JsonProperty("category")
        private List<String> category;
        @JsonProperty("severity")
        private List<Integer> severity;
    }
}
