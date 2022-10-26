package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AttackTargetTopVO {
    @ApiModelProperty(value = "源列表")
    private List<Source> sources;

    @Data
    public static class Source {
        @ApiModelProperty(value = "目的列表")
        private List<AttackTargetVO> targets;

        @ApiModelProperty(value = "名称")
        private String name;
    }
}
