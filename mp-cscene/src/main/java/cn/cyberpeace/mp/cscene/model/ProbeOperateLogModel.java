package cn.cyberpeace.mp.cscene.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @BelongsPackage: cn.cyberpeace.mp.cscene.model
 * @Author: hhuang
 * @CreateTime: 2023/04/07  15:33
 * @Description: 主机采集数据开关操作记录
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_cscene_probeoperate_log")
@Builder
public class ProbeOperateLogModel extends BaseModel {

    /**
     * 场景id
     */
    @ApiModelProperty(value = "场景id")
    @DefaultStr
    private String scene;

    /**
     * 主机采集数据类型
     */
    @ApiModelProperty(value = "主机采集数据类型(changeEventState,originLog,logEvent,customCollection)")
    @DefaultStr
    private String type;

    /**
     * 操作 1:开启, 0:关闭
     */
    @ApiModelProperty(value = "操作 1:开启, 0:关闭")
    @DefaultInt(0)
    private Integer operate;
}
