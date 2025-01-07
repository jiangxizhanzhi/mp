package cn.cyberpeace.mp.cscene.controller.response;

import com.alibaba.excel.annotation.ExcelProperty;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description
 * @Author ling_yu
 * @Date 2025/1/2
 */
@Data
public class HostsAcquisitionFileNumExportResponse {

    @ExcelProperty(value = "时间点")
    @ApiModelProperty(value = "时间点")
    private String time;

    @ExcelProperty(value = "变更数量")
    @ApiModelProperty(value = "变更数量")
    private Integer count;

}
