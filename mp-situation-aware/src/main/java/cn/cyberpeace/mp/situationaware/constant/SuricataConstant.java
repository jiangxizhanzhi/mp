package cn.cyberpeace.mp.situationaware.constant;


public class SuricataConstant {

    private SuricataConstant() {}

    
    public static final String MSG_FLAG_STR = "(msg:";


    public static final String BASE_API_PATH = "/suricata";

    
    public static final String ROLE_ADMIN = "ADMIN";

    
    public static final String SPACE_REGEX = "\\s+";

    
    public static final String STARTWITH_STR = "#";

    
    public static final String MSG_SEPERATOR = "; ";

    
    public static final String SID_PREFIX ="sid:";

    
    public static final String METADATA_PREFIX ="metadata:";

    
    public static final String METADATA_SEPERATOR = ", ";

    
    public static final String ATTCK_PREFIX ="mitre_technique_id";

    
    public static final Integer SURICATA_RULE_FIELD_VALUE_PK_FIRST = 0;


    
    public enum SURICATA_FIELD {
        ACTION, 
        PROTOCOL, 
        SOURCE,
        SOURCE_PORTS,
        DIRECTION,
        DESTINATION,
        DESTINATION_PORTS
    }

}
