package cn.cyberpeace.mp.situationaware.model.dto.probe;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/26 10:33
 */
@Data
@Builder
public class CollectionTemplateProbeDtoSearchCondition {

    @ApiModelProperty(value = "探针ids")
    private String[] probeIds;

    @ApiModelProperty(value = "探针模板ids")
    private String[] templateIds;

    @ApiModelProperty(value = "null:全部, 0:信息类 1:事件类 2:日志类")
    private Integer[] probeCategories;

    @ApiModelProperty(value = "null:全部, 0:通用探针 1:自定义探针")
    private Integer[] types;

    @ApiModelProperty(value = "null:全部, 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private Integer[] osTypes;

    @ApiModelProperty(value = "null:全部, 0:shell 1:python 2:bat")
    private Integer[] scriptTypes;

    // 是否显示脚本内容, 默认不显示
    private Boolean needScriptContent = false;
}
