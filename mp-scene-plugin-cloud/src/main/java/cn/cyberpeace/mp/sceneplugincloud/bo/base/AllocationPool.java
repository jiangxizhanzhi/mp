package cn.cyberpeace.mp.sceneplugincloud.bo.base;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AllocationPool {
    @ApiModelProperty(value = "开始地址")
    private String start;
    @ApiModelProperty(value = "结束地址")
    private String end;
}
