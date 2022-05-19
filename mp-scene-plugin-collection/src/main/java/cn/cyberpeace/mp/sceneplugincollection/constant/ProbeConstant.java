package cn.cyberpeace.mp.sceneplugincollection.constant;

/**
 * @Description: 探针常量
 * @Author hewei
 * @Date：2022/5/6 9:23
 */
public class ProbeConstant {
    //探针类型
    public interface ProbeType {
        //信息类
        String MESSAGE_TYPE = "0";
        //事件类
        String EVENT_TYPE = "1";
        //日志类
        String LOG_TYPE = "2";
    }

    //探针ID
    public interface ProbeId {
        //硬件信息
        String HARDWARE_INFO = "001";
        //操作系统信息
        String OS_INFO = "002";
        //网络接口信息
        String NETWORK_INTERFACE_INFO = "003";
        //端口信息
        String PORT_INFO = "004";
        //进程信息
        String PROCESS_INFO = "005";
        //文件变更
        String FILE_MODIFY = "006";
        //注册表变更
        String REGISTRY_MODIFY = "007";
        //操作系统
        String OS_LOG = "008";
        //应用系统
        String APPLICATION_LOG = "009";
        //安全软件
        String SECURITY_LOG = "010";
        //日志文件
        String LOG_FILE = "011";
    }

    public ProbeConstant() {
    }
}
