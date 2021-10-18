package cn.cyberpeace.mp.cscene.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 查询参数定义
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class CsceneInstanceQueryParams extends QueryParams {
    @ApiModelProperty(value = "场景访问用户id")
    private String accessUserId;
}
