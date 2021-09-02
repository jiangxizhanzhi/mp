package cn.cyberpeace.mp.flow.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 流量集合对象
 * @Author qiaokang
 * @Date：2021/8/31 10:31
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_collection")
public class FlowCollectionModel extends BaseModel {

    /**
     * 流量集合标题
     */
    @ApiModelProperty(value = "流量集合标题")
    @NotBlank(message = "title can not be empty")
    private String title;

    /**
     * 集合描述
     */
    @ApiModelProperty(value = "集合描述")
    @DefaultStr
    private String collectionDesc;

    /**
     * 流量集合类型   0:"测试测量",1:"应用升级",2:"网络管理",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输
     */
    @ApiModelProperty(value = "流量集合类型")
    @NotBlank(message = "category can not be empty")
    private Integer category;

    /**
     * 背景流量数量
     */
    @ApiModelProperty(value = "背景流量数量")
    @DefaultInt
    private Integer backgroundCount;

    /**
     * 攻击流量数量
     */
    @ApiModelProperty(value = "攻击流量数量")
    @DefaultInt
    private Integer attackCount;

}
