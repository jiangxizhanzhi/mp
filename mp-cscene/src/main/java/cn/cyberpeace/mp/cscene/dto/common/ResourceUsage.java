package cn.cyberpeace.mp.cscene.dto.common;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 资源使用率（cpu，内存，磁盘）
 * @Author zhouxinghuai
 * @Date：2021/11/22 18:09
 */
@Data
public class ResourceUsage {

    @ApiModelProperty(notes = "x轴-时间")
    private String[] xAxis;

    @ApiModelProperty(notes = "cpu使用率")
    private float[] cpu;

    @ApiModelProperty(notes = "内存使用率")
    private float[] mem;

}
