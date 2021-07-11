package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ApiModel(description = "磁盘剩余空间返回对象")
public class ImportDiskSpaceVO extends BaseVO {

    
    @ApiModelProperty(value = "虚拟机空间")
    private Long vmSpace;

}
