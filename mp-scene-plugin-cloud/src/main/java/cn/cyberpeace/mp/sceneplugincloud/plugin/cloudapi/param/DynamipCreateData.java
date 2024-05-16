package cn.cyberpeace.mp.sceneplugincloud.plugin.cloudapi.param;

import cn.cyberpeace.mp.sceneplugincloud.util.cloudhttp.CloudHttpData;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamipCreateData implements CloudHttpData {
    private String name;
    // wan口数量 FIXME 这个不需要填？？？
    //private int portNumber;
    // 镜像名
    private String image;
    // 从关联的镜像填充
    private String platform;

    // dynamipsiface参数信息
    @JsonProperty(value = "dynamipsifaceOptionsParam")
    private List<IFace> dynamipsifaceOptionsParam;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class IFace{
        @JsonProperty(value = "dynamipsBoardCards")
        private List<BoardCards> dynamipsBoardCards;

        // 固定参数取值
        @JsonProperty(value = "mainBoardType")
        private String mainBoardType = "npe-400";
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class BoardCards{
        private List<Network> networks;
        // 固定参数取值
        private String type = "PA-4E";
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Network{
        //网络
        @JsonProperty(value = "networkId")
        private String networkId;

        private Ip ip;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Ip{
        @JsonProperty()
        private String gateway;
        // ip/掩码 格式，如 10.10.10.3/24
        private String cidr;
        private String mac;
        //private String ip;
        //private String port;
    }
}