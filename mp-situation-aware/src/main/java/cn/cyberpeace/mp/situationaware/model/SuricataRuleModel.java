package cn.cyberpeace.mp.situationaware.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import cn.cyberpeace.mp.situationaware.bo.SuricataData;
import cn.cyberpeace.mp.situationaware.constant.SuricataConstant;
import cn.cyberpeace.mp.situationaware.service.ClassificationAnalyseService;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static cn.cyberpeace.mp.situationaware.constant.SuricataConstant.*;

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

    private static final Pattern CLASS_TYPE_PATTERN = Pattern.compile("classtype:(.*?);");
    private static final Pattern THRESHOLD_PATTERN = Pattern.compile("threshold:(.*?);");
    private static final Pattern REFERENCE_URL_PATTERN = Pattern.compile("reference:url,(.*?);");
    private static final Pattern SID_PATTERN = Pattern.compile("sid:([0-9]+);");
    private static final Pattern MESSAGE_PATTERN = Pattern.compile("\\(msg:(.*?)\";");
    private static final Pattern MITRE_TECHNIQUE_ID_PATTERN = Pattern.compile("mitre_technique_id (.*?),");

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

    @ApiModelProperty("分类类型cn")
    @DefaultStr
    private String classTypeCn;

    @ApiModelProperty("分类类型en")
    @DefaultStr
    private String classTypeEn;

    @ApiModelProperty("规则生成类型 0-预置,不能编辑 1-用户自定义可以编辑")
    @DefaultInt
    private Integer ruleGenType;

    /**
     * 获取原始的规则文件格式一行
     * @return
     */
    public String constructMetadata() {
        String ruleTemplate = "%s %s %s";

        String action = this.getAction();
        String header = this.constructRuleHeader();
        String ruleOptions = this.getRuleMsg();

        return String.format(ruleTemplate, action, header, ruleOptions);
    }


    /**
     * rule header The header, defining the protocol, IP addresses, ports and direction of the rule.
     * @return
     */
    public String constructRuleHeader() {
        String headerTemplate = "%s %s %s %s %s %s";
        return String.format(headerTemplate, this.protocol, this.source, this.sourcePorts, this.direction, this.destination, this.destinationPorts);
    }

    public String extractOriginClassTypeFromRuleOptions() {
        return extractFieldFromRuleOptions(CLASS_TYPE_PATTERN);
    }

    public String extractReferenceUrl() {
        return extractFieldFromRuleOptions(REFERENCE_URL_PATTERN);
    }

    public String extractThreshold() {
        return extractFieldFromRuleOptions(THRESHOLD_PATTERN);
    }

    public String extractSid() {
        return extractFieldFromRuleOptions(SID_PATTERN);
    }

    public String extractMessage() {
        return extractFieldFromRuleOptions(MESSAGE_PATTERN);
    }

    public String extractMetreTechniqueId() {
        return extractFieldFromRuleOptions(MITRE_TECHNIQUE_ID_PATTERN);
    }

    private String extractFieldFromRuleOptions(Pattern pattern) {
        Matcher matcher = pattern.matcher(this.ruleMsg);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "";
        }
    }

    public String extractCnClassType(ClassificationAnalyseService classificationAnalyseService) {
        String originClassType = this.extractOriginClassTypeFromRuleOptions();
        String cnClassType = classificationAnalyseService.getFlowEventEnToCnMap().get(originClassType);
        if (StringUtils.isEmpty(cnClassType)) {
            return originClassType;
        } else {
            return cnClassType;
        }
    }

    public static SuricataRuleModel parseRuleStringToModel(String rule, ClassificationAnalyseService classificationAnalyseService) {
        SuricataRuleModel suricataRuleModel = new SuricataRuleModel();

        int msgIndex = rule.indexOf(MSG_FLAG_STR);
        //主要信息
        String aStr = rule.substring(0, msgIndex);
        String[] split = aStr.split(SPACE_REGEX);
        //msg结构体信息
        String ruleMsg = rule.substring(msgIndex);
        //msgFlagStr前面的部分处理
        suricataRuleModel.setAction(split[SuricataConstant.SURICATA_FIELD.ACTION.ordinal()]);
        suricataRuleModel.setProtocol(split[SuricataConstant.SURICATA_FIELD.PROTOCOL.ordinal()]);
        suricataRuleModel.setSource(split[SuricataConstant.SURICATA_FIELD.SOURCE.ordinal()]);
        suricataRuleModel.setSourcePorts(split[SuricataConstant.SURICATA_FIELD.SOURCE_PORTS.ordinal()]);
        suricataRuleModel.setDirection(split[SuricataConstant.SURICATA_FIELD.DIRECTION.ordinal()]);
        suricataRuleModel.setDestination(split[SuricataConstant.SURICATA_FIELD.DESTINATION.ordinal()]);
        suricataRuleModel.setDestinationPorts(split[SuricataConstant.SURICATA_FIELD.DESTINATION_PORTS.ordinal()]);
        suricataRuleModel.setRuleMsg(ruleMsg);
        suricataRuleModel.setSid(suricataRuleModel.extractSid());
        suricataRuleModel.setAttck(suricataRuleModel.extractMetreTechniqueId());
        suricataRuleModel.setRuleName(suricataRuleModel.extractMessage());
        suricataRuleModel.setClassTypeEn(suricataRuleModel.extractOriginClassTypeFromRuleOptions());
        suricataRuleModel.setClassTypeCn(suricataRuleModel.extractCnClassType(classificationAnalyseService));
        return suricataRuleModel;
    }


    /**
     * @description: 解析处理规则的msg结构体
     * @param suricataData
     * @param ruleMsg
     * @return void
     */
    public void parseMsg(SuricataData suricataData,String ruleMsg){
        if(StringUtils.isEmpty(ruleMsg)){
            return;
        }
        String[] msgSplit = ruleMsg.split(MSG_SEPERATOR);
        //获取先获取rulename，sid
        for (String msg : msgSplit) {
            if (msg.startsWith(MSG_FLAG_STR)) {
                String name = StringUtils.stripStart(msg, MSG_FLAG_STR);
                //去除引号
                suricataData.setRuleName(name.replace("\"", ""));
            } else if (msg.startsWith(SID_PREFIX)) {
                String sid = StringUtils.stripStart(msg, SID_PREFIX);
                suricataData.setSid(sid);
            } else if (msg.startsWith(METADATA_PREFIX)) {
                //元数据信息处理获取ATT&ck
                String metadata = StringUtils.stripStart(msg, METADATA_PREFIX);
                String[] metainfos = metadata.split(METADATA_SEPERATOR);
                for (String meta : metainfos) {
                    if (meta.startsWith(ATTCK_PREFIX)) {
                        String mid = StringUtils.stripStart(meta, ATTCK_PREFIX).trim();
                        suricataData.setAttck(mid);
                    }
                }
            }
        }
    }

}
