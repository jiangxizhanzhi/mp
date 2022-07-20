package cn.cyberpeace.mp.cscene.dto.scene;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @Description
 * @Author maojun
 * @Date 2022/7/19
 */
@Getter
@Setter
public class ElementCommandExecResult {
    @ApiModelProperty(value = "执行状态")
    private Boolean status;
    @ApiModelProperty(value = "执行结果")
    private String output;
}
