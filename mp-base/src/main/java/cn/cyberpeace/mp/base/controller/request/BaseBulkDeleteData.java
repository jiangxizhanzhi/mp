package cn.cyberpeace.mp.base.controller.request;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName BaseBulkDeleteData
 * @Description
 * @Author maojun
 * @Date 2024/07/02
 * @Version 1.0.0
 */
@Deprecated
@Data
public class BaseBulkDeleteData {
    @ApiModelProperty(value = "数据id集")
    private String[] ids;
}
