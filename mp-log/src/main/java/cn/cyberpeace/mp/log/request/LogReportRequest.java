package cn.cyberpeace.mp.log.request;

import cn.cyberpeace.mp.log.dto.LogReportDTO;
import cn.cyberpeace.mp.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;


@Data
@NoArgsConstructor
public class LogReportRequest {

    @ApiModelProperty(value = "日志上报类型")
    private LogReportTypeEnum logReportTypeEnum;

    @ApiModelProperty(value = "日志上报数据集合")
    private List<LogReportDTO> list;
}
