package cn.cyberpeace.mp.flow.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

/**
 * @Description: 更新背景流量请求
 * @Author qiaokang
 * @Date：2021/9/2 16:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(value = "更新背景流量请求")
public class ModifyBackgroundFlowRequest {

    /**
     * 流量id
     */
    @ApiModelProperty(required = true, value = "流量id")
    @NotBlank(message = "id can not be empty")
    private String id;

    /**
     * 标题
     */
    @ApiModelProperty(value = "标题")
    private String title;

    /**
     * 文件
     */
    @ApiModelProperty(value = "文件")
    private MultipartFile pcapFile;

    /**
     * 描述
     */
    @ApiModelProperty(value = "描述")
    private String introduction;

    /**
     * 文件名称
     */
    @ApiModelProperty(value = "文件名称")
    private String fileName;

    /**
     * 类型  0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"
     */
    @ApiModelProperty(value = "类型 0:\"测试测量\",1:\"应用升级\",2:\"网络管理\",3:\"P2P\",4:\"金融\",5:\"邮件\",6:\"安全工作\"," +
            "7:\"分布式计算\",8:\"手机\",9:\"移动设备\",10:\"即时通讯\",11:\"SCADA\",12:\"机动\",13:\"远程控制\",14:\"音视频\",15:\"企业应用\"," +
            "16:\"社会网络搜索\",17:\"文件传输\",18:\"数据库\",19:\"身份认证\",20:\"ERP\",21:\"存储\",22:\"游戏\",23:\"安全数据传输\"")
    private Integer category;

    /**
     * 文件路径
     */
    @ApiModelProperty("文件路径")
    private String pcapFilePath;

}
