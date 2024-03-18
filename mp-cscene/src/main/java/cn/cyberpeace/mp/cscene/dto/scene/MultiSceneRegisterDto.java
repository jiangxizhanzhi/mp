package cn.cyberpeace.mp.cscene.dto.scene;

import cn.cyberpeace.mp.base.util.http.SnakeHttpData;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MultiSceneRegisterDto implements SnakeHttpData {

    @NotBlank(message = "靶场标识不能为空")
    @ApiModelProperty("靶场标识")
    private String rangeTag;

    @NotBlank(message = "靶场ip不能为空")
    @ApiModelProperty("靶场ip")
    private String rangeIp;

    @NotBlank(message = "靶场端口不能为空")
    @ApiModelProperty("靶场端口")
    private String rangePort;
}
