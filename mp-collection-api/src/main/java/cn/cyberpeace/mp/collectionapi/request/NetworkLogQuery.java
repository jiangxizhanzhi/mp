package cn.cyberpeace.mp.collectionapi.request;

import cn.hutool.core.date.LocalDateTimeUtil;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @ClassName NetworkLogQuery
 * @Description
 * @Author zhanzhi
 * @Date 2023/11/16 20:15
 **/
@Data
public class NetworkLogQuery {

    public static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ApiModelProperty(value = "开始时间, 已废弃")
    private String startTime;

    @ApiModelProperty(value = "结束时间， 已废弃")
    private String endTime;

    @ApiModelProperty(value = "分页大小")
    private Integer pageSize;

    @ApiModelProperty(value = "页码")
    private Integer page;


    public LocalDateTime getEndTime() {
        if (StringUtils.isBlank(endTime)) {
            return LocalDateTime.now();
        } else {
            return LocalDateTimeUtil.parse(endTime, dateTimeFormatter);
        }
    }

    public LocalDateTime getStartTime() {
        if (StringUtils.isBlank(startTime)) {
            return LocalDateTime.now().minusMinutes(5L);
        } else {
            return LocalDateTimeUtil.parse(startTime, dateTimeFormatter);
        }
    }

}
