package cn.cyberpeace.mp.flow.response;

import cn.cyberpeace.mp.flow.model.FlowInfoModel;
import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import cn.hutool.core.date.DateUtil;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;

import java.util.List;
import java.util.Objects;

/**
 * @Description: 流量返回信息
 * @Author qiaokang
 * @Date：2021/8/10 16:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "流量返回信息")
public class FlowInfoResponse {

    /**
     * 流量id
     */
    @ApiModelProperty(value = "流量id")
    private String resourceId;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 简述
     */
    @ApiModelProperty(value = "简述")
    private String introduction;

    /**
     * 类型  0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"
     */
    @ApiModelProperty(value = "类型 0:\"测试测量\",1:\"应用升级\",2:\"网络管理\",3:\"P2P\",4:\"金融\",5:\"邮件\",6:\"安全工作\"," +
            "7:\"分布式计算\",8:\"手机\",9:\"移动设备\",10:\"即时通讯\",11:\"SCADA\",12:\"机动\",13:\"远程控制\",14:\"音视频\",15:\"企业应用\"," +
            "16:\"社会网络搜索\",17:\"文件传输\",18:\"数据库\",19:\"身份认证\",20:\"ERP\",21:\"存储\",22:\"游戏\",23:\"安全数据传输\"")
    private Integer category;

    /**
     * 创建时间
     */
    @ApiModelProperty(value = "创建时间")
    private String createTime;

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量")
    private Integer interactiveType;

    /**
     * 攻击脚本参数列表
     */
    @ApiModelProperty(value = "攻击脚本运行参数")
    private List<FlowScriptParamModel> scriptParamModels;

    public FlowInfoResponse(FlowInfoModel flowInfoModel, List<FlowScriptParamModel> scriptParamModels) {
        setResourceId(flowInfoModel.getId());
        setTitle(flowInfoModel.getTitle());
        setIntroduction(flowInfoModel.getFlowDesc());
        setCategory(flowInfoModel.getCategory());
        if (Objects.nonNull(flowInfoModel.getCreateTime())) {
            setCreateTime(DateUtil.format(flowInfoModel.getCreateTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        setInteractiveType(flowInfoModel.getInteractiveType());

        if (CollectionUtils.isNotEmpty(scriptParamModels)) {
            setScriptParamModels(scriptParamModels);
        }
    }

}
