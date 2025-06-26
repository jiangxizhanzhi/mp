package cn.cyberpeace.mp.base.bo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * 镜像新增请求信息
 */
@ApiModel(value = "工具信息")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ImageToolRequest {
    @ApiModelProperty(value = "工具id")
    private String id;
    @ApiModelProperty(value = "工具名称")
    private String name;
    // 攻击方式: 1 中断 , 2 降效 ,  3 嗅探
    @ApiModelProperty(value = "攻击方式: 1 中断 , 2 降效 ,  3 嗅探")
    private Integer attackType;
    // 攻击返回类型: 1 网络接口 , 2 硬件资源 , 3 网络流量 ,4 系统进程
    @ApiModelProperty(value = "攻击返回类型: 1 网络接口 , 2 硬件资源 , 3 网络流量 ,4 系统进程")
    private List<Integer> attackDataTypes;
    @ApiModelProperty(value = "工具参数")
    private List<Params> params;

    @Getter
    @Setter
    public static class Params {
        private String name;
        private Integer type;
        private String desc;
        private String value;
    }
}
