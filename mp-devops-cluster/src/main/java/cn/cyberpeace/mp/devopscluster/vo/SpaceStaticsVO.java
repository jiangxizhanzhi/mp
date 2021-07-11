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
@ApiModel(value = "资源用量情况返回")
@Builder
public class SpaceStaticsVO {

    @ApiModelProperty("系统总空间")
    private long totalSpace;

    @ApiModelProperty("剩余可用空间")
    private long leftSpace;

    @ApiModelProperty("截屏已用空间")
    private long screenshotUsed;

    @ApiModelProperty("录屏已用空间")
    private long screenRecordUsed;

    @ApiModelProperty("其他占用空间")
    private long otherUsed;

    @ApiModelProperty("资源总占用空间")
    private long usedTotal;

    @ApiModelProperty("资源已用空间占比")
    private double usedPercent;

    @ApiModelProperty("录屏占比")
    private double screenRecordPercent;

    @ApiModelProperty("截屏占比")
    private double screenshotPercent;

    @ApiModelProperty("其他已用空间占比")
    private double otherPercent;

    @ApiModelProperty("剩余可用空间占比")
    private double leftPercent;

}
