package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneAuthorizeRequest {
    @ApiModelProperty("用户id集合")
    private List<String> userIds;

    @ApiModelProperty("场景id集合")
    private List<String> ids;

    @ApiModelProperty("是否授权所有")
    private Boolean isPublic;
}
