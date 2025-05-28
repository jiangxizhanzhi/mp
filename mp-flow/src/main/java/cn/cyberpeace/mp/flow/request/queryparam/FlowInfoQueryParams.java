 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name FlowInfoQueryParams.java.
 // File size 1648.
 // File create date Fri Jul 12 12:55:23 CST 2024.
 // File update date Fri Jul 12 12:55:23 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.flow.request.queryparam;

import cn.cyberpeace.mp.base.service.param.QueryParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName FlowInfoQueryParams
 * @Description
 * @Author qiaokang
 * @Date 2024/07/02
 * @Version 1.0.0
 * @see QueryParams
 */
@Data
@ApiModel(value = "流量通用查询条件")
public class FlowInfoQueryParams extends QueryParams {

    private String title;

    /**
     * 流量分类  0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"
     */
    @ApiModelProperty(value = "流量分类 0:\"测试测量\",1:\"应用升级\",2:\"网络管理\",3:\"P2P\",4:\"金融\",5:\"邮件\",6:\"安全工作\"," +
            "7:\"分布式计算\",8:\"手机\",9:\"移动设备\",10:\"即时通讯\",11:\"SCADA\",12:\"机动\",13:\"远程控制\",14:\"音视频\",15:\"企业应用\"," +
            "16:\"社会网络搜索\",17:\"文件传输\",18:\"数据库\",19:\"身份认证\",20:\"ERP\",21:\"存储\",22:\"游戏\",23:\"安全数据传输\"")
    private Integer category;

    /**
     * 交互类型：0 背景流量，1 攻击流量，2 全流量
     */
    @ApiModelProperty(value = "交互类型：0 背景流量，1 攻击流量，2 全流量")
    private Integer interactiveType;

    @ApiModelProperty(value = "攻击流量类型: 1 DDoS, 2 蠕虫，3 垃圾邮件，4 DNS污染， 5 BGP劫持， 6 缓冲区溢出， 7 SQL注入, 8 XSS, 9 勒索病毒")
    private Integer attackFlowType;

    @ApiModelProperty(value = "流量协议类型")
    private String flowProtocol;

    private String[] flowIds;

}
