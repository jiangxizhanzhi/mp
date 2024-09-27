package cn.cyberpeace.mp.devopscluster.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(description = "base image")
public class ImageVO extends BaseVO {

    // 定制化字段

    /**
     * 业务标签
     */
    @ApiModelProperty(value = "业务标签")
    private Integer businessTag;

    /**
     * 引用过的次数
     */
    @ApiModelProperty(value = "引用过的次数")
    private Integer referenceNums;

    /**
     * 靶标管理中启动过的时长
     */
    @ApiModelProperty(value = "靶标管理 中启动过的时长")
    private Integer useDuration;

}
