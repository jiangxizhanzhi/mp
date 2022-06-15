package cn.cyberpeace.mp.devops.log.request;

import cn.cyberpeace.mp.devops.log.dto.LogReportDTO;
import cn.cyberpeace.mp.devops.log.enums.LogReportTypeEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @description:
 * @author：Geek Wang
 * @date: 2022/6/15
 */
@Data
@NoArgsConstructor
public class LogReportRequest {

    @ApiModelProperty(value = "日志上报类型")
    private LogReportTypeEnum logReportTypeEnum;

    @ApiModelProperty(value = "日志上报数据集合")
    private List<LogReportDTO> list;
}
