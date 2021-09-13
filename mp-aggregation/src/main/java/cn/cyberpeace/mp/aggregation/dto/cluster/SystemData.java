package cn.cyberpeace.mp.aggregation.dto.cluster;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2021/9/13 13:58
 */

@Data
@Builder
public class SystemData {

    @ApiModelProperty("cpu数据")
    private String[] CPUData;

    @ApiModelProperty("磁盘数据")
    private String[] DISKData;

    @ApiModelProperty("内存数据")
    private String[] RAMData;

    @ApiModelProperty("x轴")
    private String[] xAxis;
}
