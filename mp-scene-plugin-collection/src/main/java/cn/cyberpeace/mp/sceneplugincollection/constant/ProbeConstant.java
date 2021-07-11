package cn.cyberpeace.mp.sceneplugincollection.constant;


public class ProbeConstant {
    
    public interface ProbeType {
        
        String MESSAGE_TYPE = "0";
        
        String EVENT_TYPE = "1";
        
        String LOG_TYPE = "2";
    }

    
    public interface ProbeId {
        
        String HARDWARE_INFO = "001";
        
        String OS_INFO = "002";
        
        String NETWORK_INTERFACE_INFO = "003";
        
        String PORT_INFO = "004";
        
        String PROCESS_INFO = "005";
        
        String FILE_MODIFY = "006";
        
        String REGISTRY_MODIFY = "007";
        
        String OS_LOG = "008";
        
        String APPLICATION_LOG = "009";
        
        String SECURITY_LOG = "010";
        
        String LOG_FILE = "011";
        
        String USERS_MODIFY = "012";
        
        String INTERNET_CONNECTION = "013";
    }

    public ProbeConstant() {
    }
}
