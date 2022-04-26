package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.situationaware.model.dto.validate.AddGroup;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * mp_situation_collection_template
 * @author 
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table("mp_situation_collection_template")
public class CollectionTemplateModel extends BaseModel implements Serializable {

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

}
