package cn.cyberpeace.mp.situationaware.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;


/**
 * 流量分析Request
 * @author wanggang
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowAnalysisRequest {

    /**
     * Top数量
     */
    @ApiModelProperty("Top数量")
    private Integer topSize;

    /**
     * 开始日期
     */
    @ApiModelProperty("开始日期：yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    @ApiModelProperty("结束日期：yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    /**
     * 查询条件需要的分组
     */
    @NotNull(message = "分组id列表不能为空")
    @ApiModelProperty("分组")
    private List<String> groupList;

    public Integer getTopSize() {
        return Objects.isNull(topSize) ? 10 : topSize;
    }

}
