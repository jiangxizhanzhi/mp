package cn.cyberpeace.mp.common.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 选项通用查询条件
 * @Author: guquan
 * @Date：2023/5/5 14:31
 */
@Data
@ApiModel(value = "选项通用查询条件")
public class OptionQueryParams extends QueryParams {

    /**
     * 类型
     */
    @ApiModelProperty(value = "类型")
    private String type;

    /**
     * 名称
     */
    @ApiModelProperty(value = "名称")
    private String zhName;

}
