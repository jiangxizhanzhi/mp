package cn.cyberpeace.mp.devopslog.request;

import cn.cyberpeace.mp.devopslog.dto.LogReportDTO;
import cn.cyberpeace.mp.devopslog.enums.LogReportTypeEnum;
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
