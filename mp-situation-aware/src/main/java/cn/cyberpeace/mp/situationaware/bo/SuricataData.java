package cn.cyberpeace.mp.situationaware.bo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @BelongsPackage: cn.cyberpeace.mp.situationaware.bo
 * @Author: hhuang
 * @CreateTime: 2023/04/21  09:43
 * @Description: Suricata规则数据
 * @Version: 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuricataData {

    @ApiModelProperty("匹配规则时进行的操作")
    private String action;

    @ApiModelProperty("协议")
    private String protocol;

    @ApiModelProperty("源ip")
    private String source;

    @ApiModelProperty("源端口")
    private String sourcePorts;

    @ApiModelProperty("方向")
    private String direction;

    @ApiModelProperty("目的ip")
    private String destination;

    @ApiModelProperty("目的端口")
    private String destinationPorts;

    /**
     * 规则体   (msg:...)
     */
    @ApiModelProperty("规则体")
    private String ruleMsg;

    //其他规则体中需要单独取出的字段

    @ApiModelProperty("规则名称")
    private String ruleName;

    /**
     * sid一个指令，是一个元数据属性，表示signature id 签名的ID
     */
    @ApiModelProperty("SID")
    private String sid;

    /**
     * Att&ck  对抗性战术，技术以及公共知识库
     */
    @ApiModelProperty("Att&ck")
    private String attck;

}
