package cn.cyberpeace.mp.collectionapi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AttackFlowBaseRequest {
    @ApiModelProperty(value = "源")
    private List<IPField> sources;

    @ApiModelProperty(value = "目的")
    private List<IPField> targets;

    @Data
    public static class IPField {
        @ApiModelProperty(value = "名称")
        private String name;

        @ApiModelProperty(value = "ip集合")
        private List<String> ips;

        @ApiModelProperty(value = "地址段")
        private String cidr;
    }
}
