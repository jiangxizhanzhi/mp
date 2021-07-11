package cn.cyberpeace.mp.aggregation.dto.cluster;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;



@Data
@Builder
@ApiModel("系统数据")
public class SystemData {

    @ApiModelProperty("cpu数据")
    private Integer[] CPUData;

    @ApiModelProperty("磁盘数据")
    private Integer[] DISKData;

    @ApiModelProperty("内存数据")
    private Integer[] RAMData;

    @ApiModelProperty("x轴")
    private String[] xAxis;
}
