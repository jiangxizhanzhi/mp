package cn.cyberpeace.mp.devops.log.request;

import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.Min;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 基础的分页参数
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageRequest extends BaseRequest implements Serializable {

    /**
     * 每页条数
     */
    @Min(1)
    @ApiModelProperty(value = "每页条数",required = false,hidden = true)
    private Integer limit;

    /**
     * 页码，从1开始
     */
    @Min(1)
    @ApiModelProperty(value = "页码",required = false,hidden = true)
    private Integer offset;

    /**
     * 页码
     */
    @ApiModelProperty(value = "页码",required = false)
    private Integer page = 1;

    /**
     * 每页条数
     */
    @ApiModelProperty(value = "每页条数",required = false)
    private Integer perPage = 10;

    @ApiModelProperty(value = "日志类型")
    private LogReportTypeEnum type;

    @ApiModelProperty(value = "开始日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startDate;

    @ApiModelProperty(value = "结束日期")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endDate;

    public Integer getLimit() {
        return perPage;
    }

    public Integer getOffset() {
        return page;
    }
}
