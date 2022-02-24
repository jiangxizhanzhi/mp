package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/11/29 19:52
 */
@Data
public class AttackCountRequest extends BaseSceneElementRequest {
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

}
