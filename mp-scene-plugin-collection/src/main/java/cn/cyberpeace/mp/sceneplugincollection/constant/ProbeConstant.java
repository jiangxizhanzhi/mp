package cn.cyberpeace.mp.sceneplugincollection.constant;

/**
 * @Description: 探针常量
 * @Author hewei
 * @Date：2022/5/6 9:23
 */
public class ProbeConstant {
    //探针类型
    public interface ProbeType {
        String MESSAGE_TYPE = "0";
        String EVENT_TYPE = "1";
        String LOG_TYPE = "2";
    }

    //探针ID
    public interface ProbeId {
        String FILE_ID = "006";
        String REGISTRY_ID = "007";
        String OS_ID = "008";
        String APPLICATION_ID = "009";
        String SECURITY_ID = "010";
        String LOG_ID = "011";
    }

    public ProbeConstant() {
    }
}
