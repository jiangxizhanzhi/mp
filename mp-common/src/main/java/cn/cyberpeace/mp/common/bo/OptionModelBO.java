package cn.cyberpeace.mp.common.bo;

import cn.cyberpeace.mp.common.model.OptionModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @Description: 选项
 * @Author: guquan
 * @Date：2023/6/6 9:30
 */
@Data
@ApiModel(value = "选项")
public class OptionModelBO extends OptionModel {

    private String name;
}
