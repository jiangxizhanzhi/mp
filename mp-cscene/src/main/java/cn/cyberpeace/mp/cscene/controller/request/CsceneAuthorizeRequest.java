package cn.cyberpeace.mp.cscene.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class CsceneAuthorizeRequest {
    @ApiModelProperty("授权目标id集合")
    private List<String> targetIds;

    @ApiModelProperty("场景id集合")
    private List<String> csceneIds;

    @ApiModelProperty("授权类型 0:授权所有 2:自定义")
    private Integer authType;
}
