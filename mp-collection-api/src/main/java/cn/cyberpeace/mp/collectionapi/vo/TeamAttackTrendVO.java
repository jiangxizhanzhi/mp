package cn.cyberpeace.mp.collectionapi.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class TeamAttackTrendVO {
    @ApiModelProperty(value = "战队攻击趋势")
    private List<TeamAttackCount> teamAttackCounts;

    @Data
    public static class TeamAttackCount {
        private long count;

        private LocalDateTime time;
    }
}
