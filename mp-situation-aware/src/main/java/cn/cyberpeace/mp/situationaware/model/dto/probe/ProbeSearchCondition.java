package cn.cyberpeace.mp.situationaware.model.dto.probe;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/26 10:33
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProbeSearchCondition {

    @ApiModelProperty(value = "探针ids")
    private String[] ids;

    @ApiModelProperty(value = "探针能力ids")
    private String[] abilityIds;

    @ApiModelProperty(value = "探针名称，模糊匹配")
    private String name;

    @ApiModelProperty(value = "探针能力名称，模糊匹配")
    private String abilityName;

    @ApiModelProperty(value = "null:全部, 0:信息类 1:事件类 2:日志类")
    private Integer[] probeCategories;

    @ApiModelProperty(value = "null:全部, 0:通用探针 1:自定义探针")
    private Integer[] types;

    @ApiModelProperty(value = "null:全部, 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private Integer[] osTypes;

    @ApiModelProperty(value = "null:全部, 0:shell 1:python 2:bat")
    private Integer[] scriptTypes;

    // 是否显示脚本内容, 默认不显示
    @Builder.Default
    private Boolean needScriptContent = false;
}
