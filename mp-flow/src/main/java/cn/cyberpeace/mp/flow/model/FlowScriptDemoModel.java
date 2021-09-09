package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Description: 流量脚本模板对象
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_script_demo")
public class FlowScriptDemoModel extends BaseModel {

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

}
