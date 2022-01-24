package cn.cyberpeace.mp.front.admin.dto.activation;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Description: 授权项
 *
 * 授权功能：
 * LCS-FUNC-CR-SHIXUN(实训模块)
 * LCS-FUNC-CR-JINGSAI(竞赛模块)
 * LCS-FUNC-CR-HUOTILOUDONG(活体漏洞模块)
 * LCS-FUNC-CR-ZHISHIKU(实战知识库)
 * LCS-FUNC-CR-ZONGHEXUNLIAN(综合训练模块)
 * LCS-FUNC-CR-HONGLANDUIKANG(红蓝对抗模块)
 * LCS-FUNC-CR-SHENTOUSAI(渗透赛模块)
 * LCS-FUNC-CR-YUNWEISAI(运维赛模块)
 * LCS-FUNC-CR-CEPING(测评模块)
 *
 * 授权资源：
 * LCS-RES-CR-ACTIVENODES(最大并发启动场景节点数)
 * LCS-RES-CR-EDITNODES(标准版最大可编辑节点)
 *
 * @Author zhouxinghuai
 * @Date：2021/8/31 11:13
 */
@Data
public class ActivationItem {

    @ApiModelProperty(value = "授权项key")
    private String key;

    @ApiModelProperty(value = "授权项值")
    private String value;

    @ApiModelProperty(value = "1:授权功能  2:授权资源")
    private Integer type;

}
