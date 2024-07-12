 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowScriptDemoModel.java.
 // File size 1219.
 // File create date Fri Jul 12 12:55:22 CST 2024.
 // File update date Fri Jul 12 12:55:22 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @ClassName FlowScriptDemoModel
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see BaseModel
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_script_demo")
public class FlowScriptDemoModel extends BaseModel {

    public static final int TYPE_FLOW = 1;

    public static final int TYPE_PROBE = 2;

    /**
     * 脚本名称
     */
    @ApiModelProperty(value = "脚本名称")
    @DefaultStr
    private String scriptName;

    /**
     * 脚本url
     */
    @ApiModelProperty(value = "脚本url")
    @DefaultStr
    private String url;


    /**
     * 示例脚本类型 1:流量 2:探针
     */
    @ApiModelProperty(value = "示例脚本类型 1:流量 2:探针")
    @DefaultInt(1)
    private Integer type;

}
