package cn.cyberpeace.mp.devopscluster.vo;

import cn.cyberpeace.mp.devopscluster.enums.CloudPlatformEnum;
import cn.cyberpeace.mp.devopscluster.enums.FontColorEnum;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.micrometer.core.instrument.util.StringUtils;
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
@ApiModel(value = "服务状态信息")
public class HypervisorServiceStateVO extends BaseVO {

    
    @ApiModelProperty(value = "服务")
    private String name;

    
    @ApiModelProperty(value = "是否存活")
    @JsonIgnore
    private Boolean alive;

    
    @ApiModelProperty(value = "状态")
    private String state;

    
    @ApiModelProperty(value = "字体颜色")
    private FontColorEnum fontColor;

    public HypervisorServiceStateVO(String name, Boolean alive) {
        this.name = name;
        this.alive = alive;
    }

    public HypervisorServiceStateVO(String name, String state) {
        this.name = name;
        this.state = state;
    }

    public HypervisorServiceStateVO(String name, Boolean alive, String state) {
        this.name = name;
        this.alive = alive;
        this.state = state;
    }

    public String getState() {
        return StringUtils.isNotBlank(state) ?
                state.equals(CloudPlatformEnum.UP.getState()) ?
                        CloudPlatformEnum.UP.toValue() : CloudPlatformEnum.DOWN.toValue()
                : alive ? CloudPlatformEnum.UP.toValue() : CloudPlatformEnum.DOWN.toValue();
    }

    public FontColorEnum getFontColor() {
        return getState().equals(CloudPlatformEnum.UP.toValue()) ? FontColorEnum.GREEN : FontColorEnum.RED;
    }
}
