package cn.cyberpeace.mp.devopscluster.cloud.platform.constants;


public final class BaseCloudPlatformConstant {

    private BaseCloudPlatformConstant(){
        throw new IllegalStateException("Utility class");
    }

    
    public static final String CPCLOUD_URL = "/cpcloud";

    
    public static final String CPCLOUD_INFO_URL = CPCLOUD_URL + "/info";

    
    public static final String CPCLOUD_DEVICE = CPCLOUD_URL + "/device";

    
    public static final String CPCLOUD_CCM = CPCLOUD_URL + "/ccm";

    
    public static final String CPCLOUD_SWITCH_URL = CPCLOUD_URL + "/switch";

    
    public static final String CPCLOUD_OPENFLOW = CPCLOUD_URL + "/sdn_app";

    
    public static final String URL_VERSION = "/v1";

    
    public static final String SCREENRECORD_METHOD = CPCLOUD_URL + "/guacamole" + URL_VERSION;

    
    public static final String CODE = "0";

}
