package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_flow_info")
public class FlowInfoModel extends BaseModel {

    /**
     * 流量标题
     */
    @ApiModelProperty(value = "流量标题")
    @NotBlank(message = "title can not be empty")
    private String title;

    /**
     * 流量描述
     */
    @ApiModelProperty(value = "流量描述")
    @DefaultStr
    private String flowDesc;

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量")
    @DefaultStr
    private int interactiveType;

    /**
     * 资源类型：0 pcap包，1 脚本
     */
    @ApiModelProperty(value = "资源类型：0 pcap包，1 脚本")
    @NotBlank(message = "resourceType can not be empty")
    private int resourceType;

    /**
     * 攻击脚本运行命令
     */
    @ApiModelProperty(value = "攻击脚本运行命令")
    @DefaultStr
    private String runCmd;

    /**
     * 流量分类  0:"测试测量",1:"应用升级",2:"网络管理",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输
     */
    @ApiModelProperty(value = "流量分类")
    @NotBlank(message = "category can not be empty")
    private int category;

    /**
     * pcap文件地址
     */
    @ApiModelProperty(value = "pcap文件地址")
    @DefaultStr
    private String pcapFile;

    /**
     * 脚本文件地址
     */
    @ApiModelProperty(value = "脚本文件地址")
    @DefaultStr
    private String scriptFile;

}
