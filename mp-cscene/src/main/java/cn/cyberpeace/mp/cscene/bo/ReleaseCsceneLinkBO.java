package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@ApiModel(description = "下发场景模板-连线数据")
@Data
public class ReleaseCsceneLinkBO implements Serializable {
    @ApiModelProperty(value = "连线ID")
    private String linkId;
    @ApiModelProperty(value = "设备源ID")
    private String sourceNumberId;
    @ApiModelProperty(value = "设备目标ID")
    private String targetNumberId;
    @ApiModelProperty(value = "光纤/宽带")
    private String mediumType;
    @ApiModelProperty(value = "带宽-10Gbps")
    private Integer bandWidth;
    @ApiModelProperty(value = "视频/语音")
    private String businessType;

}
