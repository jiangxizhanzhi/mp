package cn.cyberpeace.mp.devopscluster.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/11/8 16:05
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ComputeStatusDTO extends BaseDTO {

    /**
     * 主机名称
     */
    @NotEmpty
    @ApiModelProperty("主机名称")
    private String hostname;

}
