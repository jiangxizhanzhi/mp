package cn.cyberpeace.mp.situationaware.constant;

/*
 * @BelongsPackage: cn.cyberpeace.mp.situationaware.constant
 * @Author: hhuang
 * @CreateTime: 2023/04/21  09:29
 * @Description: Suricata常量类
 * @Version: 1.0
 */
public class SuricataConstant {

    private SuricataConstant() {}

    //规则体分界字符串
    public static final String MSG_FLAG_STR = "(msg:";


    public static final String BASE_API_PATH = "/suricata";

    //Admin角色
    public static final String ROLE_ADMIN = "ADMIN";

    //空格
    public static final String SPACE_REGEX = "\\s";

    //开头的注释符
    public static final String STARTWITH_STR = "#";

    //消息结构体分隔符
    public static final String MSG_SEPERATOR = "; ";

    //sid前缀
    public static final String SID_PREFIX ="sid:";

    //metadata前缀
    public static final String METADATA_PREFIX ="metadata:";

    //metadata分隔符
    public static final String METADATA_SEPERATOR = ", ";

    //ATT&CK字段前缀
    public static final String ATTCK_PREFIX ="mitre_technique_id";

    //SURICATA_RULE表的pk字段的初始位置
    public static final Integer SURICATA_RULE_FIELD_VALUE_PK_FIRST = 0;


    /**
     * 规则字段和解析对应
     * 规则描述：  ! 排除; : 范围; [] 包含; , 分隔符
     */
    public enum SURICATA_FIELD {
        ACTION, //操作  匹配规则到时进行什么样的操作
        PROTOCOL, //协议
        SOURCE,//源地址     （可能为字符串或者规则描述）
        SOURCE_PORTS,//源端口  （可能为字符串或者规则描述）
        DIRECTION,//流向   ->  <>
        DESTINATION,//目标地址   （可能为字符串或者规则描述）
        DESTINATION_PORTS//目标端口  （可能为字符串或者规则描述）
    }

}
