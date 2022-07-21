package cn.cyberpeace.mp.flow.enums;

/**
 * @Description 流量分类的枚举类型：0:"测试测量",1:"应用升级",2:"网络管理",3:"P2P",4:"金融",5:"邮件",6:"安全工作",7:"分布式计算",8:"手机",9:"移动设备",10:"即时通讯",11:"SCADA",12:"机动",13:"远程控制",14:"音视频",15:"企业应用",16:"社会网络搜索",17:"文件传输",18:"数据库",19:"身份认证",20:"ERP",21:"存储",22:"游戏",23:"安全数据传输"
 * @author: shgang
 * @Date: 2022/7/20
 */
public enum FlowCategoryEnum {

    /**
     * 流量分类
     */
    TEST_MEASUREMENT(0, "test_measurement"),
    APPLICATION_UPGRADE(1, "application_upgrade"),
    NETWORK_MANAGEMENT(2, "network_management"),
    P2P(3, "p2p"),
    FINANCIAL(4, "financial"),
    MAIL(5, "mail"),
    SAFETY_WORK(6, "safety_work"),
    DISTRIBUTED_COMPUTING(7, "distributed_computing"),
    MOBILE_PHONE(8, "mobile_phone"),
    MOBILE_DEVICES(9, "mobile_devices"),
    INSTANT_MESSAGE(10, "instant_message"),
    SCADA(11, "scada"),
    MOBILITY(12, "mobility"),
    REMOTE_CONTROL(13, "remote_control"),
    AUDIO_AND_VIDEO(14, "audio_and_video"),
    ENTERPRISE_APPLICATION(15, "enterprise_application"),
    SOCIAL_NETWORK_SEARCH(16, "social_network_search"),
    FILE_TRANSFER(17, "file_transfer"),
    DATABASE(18, "database"),
    IDENTITY_AUTHENTICATION(19, "identity_authentication"),
    ERP(20, "erp"),
    STORAGE(21, "storage"),
    GAME(22, "game"),
    SECURE_DATA_TRANSMISSION(23, "secure_data_transmission")
    ;


    private int code;
    private String name;

    FlowCategoryEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
