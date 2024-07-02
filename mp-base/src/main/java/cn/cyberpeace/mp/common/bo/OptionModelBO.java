package cn.cyberpeace.mp.common.bo;

import cn.cyberpeace.mp.common.model.OptionModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName OptionModelBO
 * @Description 选项
 * @Author guquan
 * @Date 2023/6/6 9:30
 * @Version 1.0.0
 * @see OptionModel
 */
@Data
@ApiModel(value = "选项")
public class OptionModelBO extends OptionModel {

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String name;
}
