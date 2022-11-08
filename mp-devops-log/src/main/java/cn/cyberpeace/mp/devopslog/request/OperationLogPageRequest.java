package cn.cyberpeace.mp.devopslog.request;

import cn.cyberpeace.mp.base.util.basic.BeanUtils;
import cn.cyberpeace.mp.devopslog.enums.OpModuleEnums;
import cn.cyberpeace.mp.devopslog.enums.OpTypeEnums;
import cn.cyberpeace.mp.devopslog.enums.SiteTypeEnums;
import cn.cyberpeace.mp.devopslog.model.OperationLogModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * 操作日志实体类
 * @author wanggang
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ApiModel
public class OperationLogPageRequest extends BasePageRequest{

    /**
     * 站点类型，0：中台
     */
    @ApiModelProperty(value = "0:中台",hidden = true)
    private SiteTypeEnums siteType = SiteTypeEnums.MP;

    /**
     * 用户名
     */
    @ApiModelProperty(value = "用户名")
    private String userName;

    /**
     * 用户ID
     */
    @ApiModelProperty(value = "用户id")
    private String userId;

    /**
     * 模块
     * 删除"考试"模块
     */
    @ApiModelProperty(value = "模块 (0:系统; 1:工具; 2:课程; 3:资源; 4:账号; 5:场景; 6:职业体系; " +
            "8:习题集管理; 9:夺旗解题; 10:理论基础; 11:试卷; 12:任务; 13:流量; 14:探针; 15:靶标；16:红蓝对抗; 17:单兵训练; 18:团队训练)")
    private Integer module;

    /**
     * 操作类型
     */
    @ApiModelProperty(value = "操作类型 (0:登入 1:登出 2:创建 3:更新 4:删除 5:学习 6:练习 7:考试 8:训练 9:测评 10:参赛 11:停止 12:暂停 13:启动 14:恢复)")
    private Integer opType;

    /**
     * 用户名/操作对象
     */
    @ApiModelProperty(value = "用户名/操作对象")
    private String userNameOrOpTarget;

    /**
     * 排除某些模块
     */
    @ApiModelProperty(value = "排除某些模块 (例如不查询'资源'跟'账号'模块的日志，传参为3,4")
    private Integer[] modulesIgnored;

    /**
     * 排除某些模块
     */
    @ApiModelProperty(value = "排除某些操作类型 (例如不查询'登入'操作的日志，传参为0")
    private Integer[] opTypesIgnored;

}
