package cn.cyberpeace.mp.devops.cluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 资源用量情况返回
 * @Author qiaokang
 * @Date：2021/9/13 19:55
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "资源用量情况返回")
@Builder
public class SpaceStaticsVO {

    @ApiModelProperty("系统总空间")
    @Builder.Default
    long totalSpace = 0L;

    @ApiModelProperty("剩余可用空间")
    @Builder.Default
    long leftSpace = 0L;

    @ApiModelProperty("截屏已用空间")
    @Builder.Default
    long screenshotUsed = 0L;

    @ApiModelProperty("录屏已用空间")
    @Builder.Default
    long screenRecordUsed = 0L;

    @ApiModelProperty("其他占用空间")
    @Builder.Default
    long otherUsed = 0L;

    @ApiModelProperty("资源总占用空间")
    @Builder.Default
    long usedTotal = 0L;

    @ApiModelProperty("资源已用空间占比")
    @Builder.Default
    double usedPercent = 0;

}
