package cn.cyberpeace.mp.situationaware.model.dto.collection;

import cn.cyberpeace.mp.situationaware.model.dto.validate.AddGroup;
import cn.cyberpeace.mp.situationaware.model.dto.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@ApiModel("采集模板请求体")
public class CollectionTemplateRequest {

    @ApiModelProperty(value = "ID")
    @NotBlank(groups = UpdateGroup.class)
    private String id;

    
    @ApiModelProperty(value = "模板名称")
    @NotBlank(groups = AddGroup.class)
    private String name;

    
    @ApiModelProperty(value = "创建者用户名")
    private String creator;

    
    @ApiModelProperty(value = "模板描述")
    private String description;

    
    @ApiModelProperty(value = "(单选) 操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    @NotNull(groups = AddGroup.class)
    private Integer osType;





    @ApiModelProperty(value = "事件采集探针列表")
    private List<ColTempCommonProbeRequest> eventProbes;

    @ApiModelProperty(value = "日志采集探针列表")
    private List<ColTempCommonProbeRequest> logProbes;

    @ApiModelProperty(value = "自定义探针列表")
    private List<ColTempCustomProbeRequest> customProbes;

}
