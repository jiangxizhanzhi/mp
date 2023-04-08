package cn.cyberpeace.mp.cscene.service.param;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.service.param
 * @Author: hhuang
 * @CreateTime: 2023/04/08  14:26
 * @Description: 探针开关记录请求参数
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class ProbeOperateQueryParams extends QueryParams {

    /**
     * 场景id
     */
    @ApiModelProperty(value = "场景id")
    private String scene;

    /**
     * 主机采集数据类型
     */
    @ApiModelProperty(value = "主机采集数据类型(changeEventState,originLog,logEvent,customCollection)")
    private String type;

    /**
     * 操作 1:开启, 0:关闭
     */
    @ApiModelProperty(value = "操作 1:开启, 0:关闭")
    private Integer operate;
}
