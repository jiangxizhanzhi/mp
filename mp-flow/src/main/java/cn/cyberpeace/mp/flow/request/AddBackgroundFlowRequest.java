package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "新增背景流量请求")
public class AddBackgroundFlowRequest {

    
    @ApiModelProperty(required = true, value = "标题")
    @NotBlank(message = "title can not be empty")
    private String title;

    
    @ApiModelProperty(required = true, value = "文件")
    private MultipartFile pcapFile;

    
    @ApiModelProperty(required = true, value = "描述")
    @NotBlank(message = "introduction can not be empty")
    private String introduction;

    
    @ApiModelProperty(required = true, value = "文件名称")
    @NotBlank(message = "fileName can not be empty")
    private String fileName;

    
    @ApiModelProperty(required = true, value = "类型 0:\"测试测量\",1:\"应用升级\",2:\"网络管理\",3:\"P2P\",4:\"金融\",5:\"邮件\"," +
            "6:\"安全工作\",7:\"分布式计算\",8:\"手机\",9:\"移动设备\",10:\"即时通讯\",11:\"SCADA\",12:\"机动\",13:\"远程控制\",14:\"音视频\"," +
            "15:\"企业应用\",16:\"社会网络搜索\",17:\"文件传输\",18:\"数据库\",19:\"身份认证\",20:\"ERP\",21:\"存储\",22:\"游戏\",23:\"安全数据传输\"")
    @NotNull(message = "category can not be empty")
    private Integer category;

    
    @ApiModelProperty("文件路径")
    private String pcapFilePath;

    
    @ApiModelProperty("流量协议字符串")
    private String protocols;

}
