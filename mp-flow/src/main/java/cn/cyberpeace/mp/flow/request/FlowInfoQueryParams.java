package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 流量查询条件
 * @Author qiaokang
 * @Date：2021/8/31 17:17
 */
@Data
@ApiModel
public class FlowInfoQueryParams extends QueryParams {

    /**
     * 流量分类  0:"测试测量",1:"应用升级",2:"网络管理",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输
     */
    @ApiModelProperty(value = "流量分类")
    private Integer category;

}
