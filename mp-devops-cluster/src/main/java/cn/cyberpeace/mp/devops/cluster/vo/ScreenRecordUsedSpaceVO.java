package cn.cyberpeace.mp.devops.cluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 录屏使用空间返回
 * @Author qiaokang
 * @Date：2021/9/13 19:55
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

}
