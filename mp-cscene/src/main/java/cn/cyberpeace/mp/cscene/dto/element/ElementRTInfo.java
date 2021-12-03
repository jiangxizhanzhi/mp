package cn.cyberpeace.mp.cscene.dto.element;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 节点实时状态信息
 * @Author zhouxinghuai
 * @Date：2021/11/22 14:03
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel(value = "节点实时状态信息")
public class ElementRTInfo {

    @ApiModelProperty(value = "实时状态-进程")
    private Integer processCount;

    @ApiModelProperty(value = "实时状态-用户")
    private Integer userCount;

    @ApiModelProperty(value = "实时状态-端口")
    private Integer portsCount;

    @ApiModelProperty(value = "实时状态-网络连接")
    private Integer connectionCount;

    @ApiModelProperty(value = "实时状态-文件操作")
    private Integer fileOpCount;

    @ApiModelProperty(value = "实时状态-注册表操作")
    private Integer registryOpCount;

    @ApiModelProperty(value = "实时状态-环境变量操作")
    private Integer envOpCount;

}
