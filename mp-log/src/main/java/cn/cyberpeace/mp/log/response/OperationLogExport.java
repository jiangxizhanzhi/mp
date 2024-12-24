package cn.cyberpeace.mp.log.response;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@HeadRowHeight(20)
@ApiModel(value = "日志分页返回信息")
public class OperationLogExport {



    /**
     * 用户名称
     */
    @ApiModelProperty(value = "用户名称")
    @ExcelProperty("用户名称")
    private String userName;


    @ExcelProperty("姓名")
    @ApiModelProperty(value = "姓名")
    private String name;


    /**
     * !!!删除"7.考试"模块!!!
     */
    @ApiModelProperty(value = "模块 (0:系统; 1:工具; 2:课程; 3:资源; 4:账号; 5:场景; 6:职业体系; " +
            "8:习题集管理; 9:夺旗解题; 10:理论基础; 11:试卷; 12:任务; 13:流量; 14:探针; 15:靶标；16:红蓝对抗; 17:单兵训练; 18:团队训练;" +
            "19:赛场, 20:理论赛, 21:运维赛, 22:夺旗赛, 23:攻防赛, 24:渗透赛, 25:战队)")
    @ExcelProperty("模块")
    private String opModule;


    /**
     * 操作类型，0:创建，1：更新，2：删除，3：登录登出
     */
    @ApiModelProperty(value = "操作类型 (0:登入 1:登出 2:创建 3:更新 4:删除 5:学习 6:练习 7:考试 8:训练 9:测评 10:参赛 11:停止 12:暂停 13:启动 14:恢复)")
    @ExcelProperty("操作类型")
    private String opType;


    /**
     * 操作对象
     */
    @ExcelProperty("操作对象")
    @ApiModelProperty(value = "操作对象")
    private String opTarget;


    /**
     * 操作时间
     */
    @ApiModelProperty(value = "操作时间/创建时间")
    @ExcelProperty("操作时间")
    private LocalDateTime opTime;



}
