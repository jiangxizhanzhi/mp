package cn.cyberpeace.mp.situationaware.request;

import cn.cyberpeace.mp.situationaware.model.enums.AggregationTypeEnum;
import cn.cyberpeace.mp.situationaware.model.enums.SeverityEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

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
     * 开始日期 yyyy-MM-dd HH:mm:ss
     */
    @ApiModelProperty("开始日期：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    /**
     * 结束日期 yyyy-MM-dd HH:mm:ss
     */
    @ApiModelProperty("结束日期：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    /**
     * 查询条件需要的分组,场景分组
     */
    private List<String> groupList;

    /**
     * 源IP
     */
    private String sourceIp;

    /**
     * 源端口
     */
    private String sourcePort;

    /**
     * 源Mac地址
     */
    private String sourceMac;

    /**
     * 目标IP
     */
    private String destinationIp;

    /**
     * 支持多协议
     */
    private List<String> protocols;

    /**
     * 目标端口
     */
    private String destinationPort;

    /**
     * 时间窗口间隔周期,单位：秒
     */
    private Integer secondsInterval;

    /**
     * 严重性，1：高，2：中，3：中低，4：低
     */
    private SeverityEnum severityEnum;

    /**
     * 聚合类型
     */
    private AggregationTypeEnum aggregationTypeEnum;

    public Integer getTopSize() {
        return Objects.isNull(topSize) ? 10 : topSize;
    }

}
