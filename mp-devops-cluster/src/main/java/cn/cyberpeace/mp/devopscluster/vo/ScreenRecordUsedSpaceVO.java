package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName ScreenRecordUsedSpaceVO
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "录屏使用空间返回")
public class ScreenRecordUsedSpaceVO {

    /**
     * 已用空间
     */
    @ApiModelProperty("已用空间")
    private Long usedSpace = 0L;

    /**
     * 总共空间
     */
    @ApiModelProperty("总共空间")
    private Long totalSpace = 0L;

    /**
     * 其他占用空间大小
     */
    @ApiModelProperty("其他占用空间大小")
    private long otherUseSize = 0L;

}
