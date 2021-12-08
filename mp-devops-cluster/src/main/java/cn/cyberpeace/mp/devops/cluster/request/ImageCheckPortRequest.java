package cn.cyberpeace.mp.devops.cluster.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ImageCheckPortRequest extends BaseRequest{

    /**
     * IP地址
     */
    @ApiModelProperty("IP地址")
    @NotBlank
    private String host;

    /**
     * 端口
     */
    @ApiModelProperty("端口")
    @NotNull
    private Integer port;


}
