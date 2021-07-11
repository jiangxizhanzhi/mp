package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.situationaware.model.CollectionTemplateModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
@ApiModel("采集模板分页项")
public class CollectionTemplatePageItemDto extends CollectionTemplateModel {

    @ApiModelProperty(value = "探针个数")
    private Integer probeCounts;
}
