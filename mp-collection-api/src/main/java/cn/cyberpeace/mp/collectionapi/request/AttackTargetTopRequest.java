package cn.cyberpeace.mp.collectionapi.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AttackTargetTopRequest {
    @ApiModelProperty(value = "源列表")
    private List<Source> sources;

    @ApiModelProperty(value = "topSize")
    private Integer topSize;

    @Data
    public static class Source {
        @ApiModelProperty(value = "目的列表")
        private List<AttackFlowBaseRequest.IPField> targets;

        @ApiModelProperty(value = "ip集合")
        private List<String> ips;

        @ApiModelProperty(value = "名称")
        private String name;
    }
}
