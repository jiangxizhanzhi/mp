package cn.cyberpeace.mp.situationaware.model.dto.collection;

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
@ApiModel("采集模板查询请求体")
public class CollectionTemplatePageQueryRequest extends BasePageRequest {

    @ApiModelProperty(value = "采集模板ids")
    private String[] ids;

    @ApiModelProperty(value = "采集模板名称，模糊匹配")
    private String name;

    @ApiModelProperty(value = "null:全部, 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private Integer[] osTypes;

    @ApiModelProperty(value = "创建者用户名")
    private String creator;

}
