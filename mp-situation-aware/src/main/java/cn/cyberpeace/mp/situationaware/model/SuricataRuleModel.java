package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/*
 * @BelongsPackage: cn.cyberpeace.mp.situationaware.model
 * @Author: hhuang
 * @CreateTime: 2023/04/21  16:03
 * @Description: Suricata Rule Model
 * @Version: 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_situation_suricatarule")
@Builder
public class SuricataRuleModel extends BaseModel {

    @ApiModelProperty("匹配规则时进行的操作")
    @DefaultStr
    private String action;

    @ApiModelProperty("协议")
    @DefaultStr
    private String protocol;

    @ApiModelProperty("源ip")
    @DefaultStr
    private String source;

    @ApiModelProperty("源端口")
    @DefaultStr
    private String sourcePorts;

    @ApiModelProperty("方向")
    @DefaultStr
    private String direction;

    @ApiModelProperty("目的ip")
    @DefaultStr
    private String destination;

    @ApiModelProperty("目的端口")
    @DefaultStr
    private String destinationPorts;

    /**
     * 规则体   (msg:...)
     */
    @ApiModelProperty("规则体")
    @DefaultStr
    private String ruleMsg;

    //其他规则体中需要单独取出的字段

    @ApiModelProperty("规则名称")
    @DefaultStr
    private String ruleName;

    /**
     * sid一个指令，是一个元数据属性，表示signature id 签名的ID
     */
    @ApiModelProperty("SID")
    @DefaultStr
    private String sid;

    /**
     * Att&ck  对抗性战术，技术以及公共知识库
     */
    @ApiModelProperty("Att&ck")
    @DefaultStr
    private String attck;


}
