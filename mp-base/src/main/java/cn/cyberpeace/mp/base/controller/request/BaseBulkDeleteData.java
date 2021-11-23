package cn.cyberpeace.mp.base.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class BaseBulkDeleteData {
    @ApiModelProperty(value = "数据id集")
    private String[] ids;
}
