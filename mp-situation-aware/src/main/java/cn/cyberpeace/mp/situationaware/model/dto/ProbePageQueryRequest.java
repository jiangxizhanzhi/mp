package cn.cyberpeace.mp.situationaware.model.dto;

import cn.cyberpeace.mp.situationaware.model.wazuh.common.BasePageRequest;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel("探针查询请求体")
public class ProbePageQueryRequest extends BasePageRequest {

    @ApiModelProperty(value = "探针id")
    private String[] ids;

    @ApiModelProperty(value = "探针名称，模糊匹配")
    private String name;

    @ApiModelProperty(value = "null:全部, 0:信息类 1:事件类 2:日志类")
    private Integer[] probeCategorys;

    @ApiModelProperty(value = "null:全部, 0:通用探针 1:自定义探针")
    private Integer[] types;

    @ApiModelProperty(value = "null:全部, 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private Integer[] osTypes;

    @ApiModelProperty(value = "null:全部, 0:shell 1:python 2:bat")
    private Integer[] scriptTypes;

}
