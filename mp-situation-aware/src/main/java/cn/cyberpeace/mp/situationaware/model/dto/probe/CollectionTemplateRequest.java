package cn.cyberpeace.mp.situationaware.model.dto.probe;

import cn.cyberpeace.mp.situationaware.model.dto.validate.AddGroup;
import cn.cyberpeace.mp.situationaware.model.dto.validate.UpdateGroup;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/26 16:26
 */
@Data
@ApiModel("采集模板请求体")
public class CollectionTemplateRequest {

    @ApiModelProperty(value = "ID")
    @NotBlank(groups = UpdateGroup.class)
    private String id;

    /**
     * 模板名称
     */
    @ApiModelProperty(value = "模板名称")
    @NotBlank(groups = AddGroup.class)
    private String name;

    /**
     * 创建者用户名
     */
    @ApiModelProperty(value = "创建者用户名")
    private String creator;

    /**
     * 模板描述
     */
    @ApiModelProperty(value = "模板描述")
    private String description;

    /**
     * (单选) 操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other
     */
    @ApiModelProperty(value = "(单选) 操作系统类型 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    @NotBlank(groups = AddGroup.class)
    private Integer osType;


    @ApiModelProperty(value = "基础信息探针列表")
    private List<CollectionTemplateProbeDto> basicInfoProbes;

    @ApiModelProperty(value = "事件采集探针列表")
    private List<CollectionTemplateProbeDto> eventProbes;

    @ApiModelProperty(value = "日志采集探针列表")
    private List<CollectionTemplateProbeDto> logProbes;

    @ApiModelProperty(value = "自定义探针列表")
    private List<CollectionTemplateProbeDto> customProbes;

}
