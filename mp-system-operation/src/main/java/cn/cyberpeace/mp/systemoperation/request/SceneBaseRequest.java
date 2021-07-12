package cn.cyberpeace.mp.systemoperation.request;

import cn.cyberpeace.mp.base.constant.BaseConstants;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 场景接口基础请求
 * @Autho qiaokang
 * @Date：2021/7/9 09:20
 */
@Data
@ApiModel("场景接口基础请求")
public class SceneBaseRequest {

    @ApiModelProperty("页码")
    private int page = BaseConstants.DEFAULTPAGE;

    @ApiModelProperty("每页数量")
    private int per_page = BaseConstants.DEFAULTPAGESIZE;

}
