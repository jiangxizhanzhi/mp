package cn.cyberpeace.mp.cscene.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.bo
 * @Author: hhuang
 * @CreateTime: 2023/04/07  16:41
 * @Description: 探针主机采集业务对象
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProbeOperateBO {

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

    /**
     * 创建记录时间
     */
    @ApiModelProperty(value = "操作 1:开启, 0:关闭")
    private LocalDateTime createTime;

}
