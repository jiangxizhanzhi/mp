package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @Description: 新增流量集合请求
 * @Author qiaokang
 * @Date：2021/8/23 10:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增流量集合请求")
public class AddFlowCollectionRequest {

    /**
     * 流量集合id
     */
    @ApiModelProperty(required = true, value = "流量集合id")
    private String id;

    /**
     * 流量集合标题
     */
    @ApiModelProperty(value = "流量集合标题")
    private String title;

    /**
     * 集合描述
     */
    @ApiModelProperty(value = "集合描述")
    private String description;

    /**
     * 流量集合类型   0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"
     */
    @ApiModelProperty(value = "流量集合类型")
    private Integer category;

    /**
     * 流量集合分类  0背景流量，1攻击流量，2混合流量
     */
    @ApiModelProperty(value = "流量集合分类")
    @NotNull(message = "typeFlag can not be empty")
    @DefaultInt(2)
    private Integer typeFlag;

    /**
     * 背景流量数量
     */
    @ApiModelProperty(value = "背景流量数量")
    private Integer backgroundFlowCount;

    /**
     * 攻击流量数量
     */
    @ApiModelProperty(value = "攻击流量数量")
    private Integer attackFlowCount;

    /**
     * 流量id集合
     */
    @ApiModelProperty(value = "流量id集合")
    @NotEmpty(message = "flowIds can not be empty")
    private List<String> flowIds;

}
