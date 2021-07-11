package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "录屏使用空间返回")
public class ScreenRecordUsedSpaceVO {

    
    @ApiModelProperty("已用空间")
    private Long usedSpace = 0L;

    
    @ApiModelProperty("总共空间")
    private Long totalSpace = 0L;

    
    @ApiModelProperty("其他占用空间大小")
    private long otherUseSize = 0L;

}
