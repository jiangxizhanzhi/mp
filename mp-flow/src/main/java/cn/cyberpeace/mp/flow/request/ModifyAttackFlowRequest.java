package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * @Description: 更新攻击流量请求
 * @Author qiaokang
 * @Date：2021/8/10 15:50
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "更新攻击流量请求")
public class ModifyAttackFlowRequest {

    /**
     * 流量id
     */
    @ApiModelProperty(required = true, value = "流量id")
    @NotBlank(message = "id can not be empty")
    private String id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String introduction;

    /**
     * 类型  0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"
     */
    @ApiModelProperty(value = "类型 0:\"测试测量\",1:\"应用升级\",2:\"网络管理\",3:\"P2P\",4:\"金融\",5:\"邮件\",6:\"安全工作\"," +
            "7:\"分布式计算\",8:\"手机\",9:\"移动设备\",10:\"即时通讯\",11:\"SCADA\",12:\"机动\",13:\"远程控制\",14:\"音视频\",15:\"企业应用\"," +
            "16:\"社会网络搜索\",17:\"文件传输\",18:\"数据库\",19:\"身份认证\",20:\"ERP\",21:\"存储\",22:\"游戏\",23:\"安全数据传输\"")
    private Integer category;

    /**
     * 攻击脚本代码
     */
    @ApiModelProperty(value = "攻击脚本代码")
    private String code;

    /**
     * 攻击脚本语言 0:python, 1:shell
     */
    @ApiModelProperty(value = "攻击脚本语言 0:python, 1:shell")
    private Integer suffix;

    /**
     * 运行参数
     */
    @ApiModelProperty(value = "运行参数")
    private String runCmd;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 文件路径
     */
    @ApiModelProperty("文件路径")
    private String filePath;

    /**
     * 攻击脚本参数列表
     */
    @ApiModelProperty(value = "攻击脚本运行参数")
    private List<FlowScriptParamModel> scriptParamModels;

    /**
     * 攻击脚本运行参数(JSON)
     */
    @ApiModelProperty(value = "攻击脚本运行参数")
    private String parameter;

}
