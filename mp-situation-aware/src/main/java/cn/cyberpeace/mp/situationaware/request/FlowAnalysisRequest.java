package cn.cyberpeace.mp.situationaware.request;

import cn.cyberpeace.mp.situationaware.bo.TeamBO;
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



@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FlowAnalysisRequest {

    @ApiModelProperty("页码，从1开始")
    private Integer pageNum;

    @ApiModelProperty("分页大小")
    private Integer pageSize;

    
    @ApiModelProperty("Top数量")
    private Integer topSize;

    
    @ApiModelProperty("开始日期：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDateTime;

    
    @ApiModelProperty("结束日期：yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDateTime;

    
    @ApiModelProperty("查询条件需要的分组,场景分组")
    private List<String> groupList;

    @ApiModelProperty("目的端口或源端口")
    private String port ;

    
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;

    
    @ApiModelProperty("源端口")
    private String sourcePort;

    
    @ApiModelProperty("源Mac地址集合")
    private List<String> sourceMacs;

    
    @ApiModelProperty("目标IP集合")
    private List<String> destinationIps;

    
    @ApiModelProperty("主机IP集合")
    private List<String> hosts;

    
    @ApiModelProperty("主机Mac集合")
    private List<String> hostMacs;

    
    @ApiModelProperty("强制过滤的目标IP集合")
    private List<String> filterDestinationIps;

    
    @ApiModelProperty("多协议")
    private List<String> protocols;

    
    @ApiModelProperty("目标端口")
    private String destinationPort;

    
    @ApiModelProperty("时间窗口间隔周期,单位：秒")
    private Integer secondsInterval;

    
    @ApiModelProperty("严重性，1：高，2：中，3：低，4：低")
    private SeverityEnum severityEnum;

    
    @ApiModelProperty("聚合类型")
    private AggregationTypeEnum aggregationTypeEnum;

    @ApiModelProperty(value = "索引",hidden = true)
    private String[] indices;

    @ApiModelProperty("队伍数据")
    private List<TeamBO> teamBOS;

    @ApiModelProperty("攻击类型")
    private List<String> attackTypes;

    @ApiModelProperty("攻击等级")
    private List<Integer> attackSeverities;

    public Integer getTopSize() {
        return (Objects.isNull(topSize) || topSize == 0) ? 10 : topSize;
    }
}
