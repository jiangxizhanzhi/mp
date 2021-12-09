package cn.cyberpeace.mp.devops.cluster.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImageCheckPortResponse {

    private List<ImageCheckPort> imageCheckPorts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ImageCheckPort{

        /**
         * 连接ID
         */
        @ApiModelProperty("连接ID")
        private Integer connectionId;


        /**
         * IP地址
         */
        @ApiModelProperty("IP地址")
        private String host;

        /**
         * 端口
         */
        @ApiModelProperty("端口")
        private Integer port;


        @ApiModelProperty("是否可连接")
        private Boolean pass;

    }

}
