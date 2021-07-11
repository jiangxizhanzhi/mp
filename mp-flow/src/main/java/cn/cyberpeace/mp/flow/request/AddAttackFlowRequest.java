package cn.cyberpeace.mp.flow.request;

import cn.cyberpeace.mp.flow.model.FlowScriptParamModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增攻击流量请求")
public class AddAttackFlowRequest {

    
    @ApiModelProperty(required = true, value = "标题")
    @NotBlank(message = "title can not be empty")
    private String title;

    
    @ApiModelProperty(required = true, value = "描述")
    @NotBlank(message = "introduction can not be empty")
    private String introduction;

    
    @ApiModelProperty(required = true, value = "类型 0:\"测试测量\",1:\"应用升级\",2:\"网络管理\",3:\"P2P\",4:\"金融\",5:\"邮件\"," +
            "6:\"安全工作\",7:\"分布式计算\",8:\"手机\",9:\"移动设备\",10:\"即时通讯\",11:\"SCADA\",12:\"机动\",13:\"远程控制\",14:\"音视频\"," +
            "15:\"企业应用\",16:\"社会网络搜索\",17:\"文件传输\",18:\"数据库\",19:\"身份认证\",20:\"ERP\",21:\"存储\",22:\"游戏\",23:\"安全数据传输\"")
    @NotNull(message = "category can not be empty")
    private Integer category;

    
    @ApiModelProperty(value = "攻击脚本代码")
    private String code;

    
    @ApiModelProperty(value = "攻击脚本语言 0:python, 1:shell")
    private Integer suffix;

    
    @ApiModelProperty(value = "运行参数")
    private String runCmd;

    
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    
    @ApiModelProperty("文件路径")
    private String filePath;

    
    @ApiModelProperty(value = "攻击脚本运行参数")
    private List<FlowScriptParamModel> scriptParamModels;

    
    @ApiModelProperty(value = "攻击脚本运行参数")
    private String parameter;

    
    @ApiModelProperty(value = "资源类型：0 pcap包，1 脚本")
    @NotNull(message = "resourceType can not be empty")
    private Integer resourceType;

    
    @ApiModelProperty(value = "文件")
    private MultipartFile pcapFile;

    
    @ApiModelProperty("文件路径")
    private String pcapFilePath;

    
    @ApiModelProperty("流量协议字符串")
    private String protocols;

}
