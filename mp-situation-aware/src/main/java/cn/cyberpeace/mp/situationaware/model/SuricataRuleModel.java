package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


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

    
    @ApiModelProperty("规则体")
    @DefaultStr
    private String ruleMsg;

    

    @ApiModelProperty("规则名称")
    @DefaultStr
    private String ruleName;

    
    @ApiModelProperty("SID")
    @DefaultStr
    private String sid;

    
    @ApiModelProperty("Att&ck")
    @DefaultStr
    private String attck;


}
