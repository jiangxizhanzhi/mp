package cn.cyberpeace.mp.cscene.request.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;


@Data
public class AttackCountRequest extends BaseSceneElementRequest {

    @ApiModelProperty("ip列表参数中ip的类型 0:源ip,1:目标ip,默认为0")
    private int ipType = 0;

    @ApiModelProperty("用于查询的ip列表")
    private List<String> ips;

    @ApiModelProperty("查询top N")
    private Integer topSize;

    @ApiModelProperty("开始时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startTime;

    @ApiModelProperty("结束时间")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endTime;

    
    @ApiModelProperty("源IP集合")
    private List<String> sourceIps;

}
