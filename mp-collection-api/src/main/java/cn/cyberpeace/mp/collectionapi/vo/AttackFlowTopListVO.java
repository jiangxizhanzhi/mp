package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class AttackFlowTopListVO {
    @ApiModelProperty(value = "名称,跟随请求")
    private String name;

    @ApiModelProperty(value = "攻击流量统计数据列表")
    private List<AttackFlowData> attackFlowDataList;

    @Data
    public static class AttackFlowData {
        @ApiModelProperty(value = "数量")
        private long count;

        @ApiModelProperty(value = "占比")
        private Double percent;

        @ApiModelProperty(value = "统计字段名")
        private Double filedName;
    }
}
