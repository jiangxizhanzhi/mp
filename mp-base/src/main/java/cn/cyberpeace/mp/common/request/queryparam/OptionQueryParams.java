package cn.cyberpeace.mp.common.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName OptionQueryParams
 * @Description 选项通用查询条件
 * @Author guquan
 * @Date 2023/5/5 14:31
 * @Version 1.0.0
 * @see QueryParams
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

    @ApiModelProperty(value = "前端传递的名称字段")
    private String pattern;

    @ApiModelProperty(value = "名称en")
    private String enName;

}
