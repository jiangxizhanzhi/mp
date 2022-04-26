package cn.cyberpeace.mp.situationaware.model.dto.probe;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description:
 * @Author zhouxinghuai
 * @Date：2022/4/26 10:33
 */
@Data
public class CollectionTemplateSearchCondition {

    @ApiModelProperty(value = "采集模板ids")
    private String[] ids;

    @ApiModelProperty(value = "采集模板名称，模糊匹配")
    private String name;

    @ApiModelProperty(value = "null:全部, 0:windows, 1:Unix, 2:Linux, 3:BSD, 4:Other")
    private Integer[] osTypes;

    @ApiModelProperty(value = "创建者用户名")
    private String creator;

}
