package cn.cyberpeace.mp.log.request;

import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class BasePageRequest extends BaseRequest implements Serializable {











    
    @ApiModelProperty(value = "页码",required = false)
    private Integer page = 1;

    
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

    public void ensure() {
        if (Objects.isNull(perPage) || perPage <= 0) {
            perPage = 10;
        }

        if (Objects.isNull(page) || page <= 0) {
            page = 1;
        }
    }

}
