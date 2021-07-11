package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.situationaware.model.CollectionTemplateProbeModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;


@Data
public class ColTempCustomProbeRequest extends CollectionTemplateProbeModel {

    
    
    
    

    
    
    

    
    
    

    @ApiModelProperty(value = "1:true, 0:false  自定义探针是否执行导入操作，如果是导入，表示需要从自定义探针配置抓取一份最新的代码")
    private Integer reload;
}
