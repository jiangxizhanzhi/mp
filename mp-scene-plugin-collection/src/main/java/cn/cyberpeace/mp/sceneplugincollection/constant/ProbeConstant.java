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
        //文件变更
        String FILE_ID = "006";
        //注册表变更
        String REGISTRY_ID = "007";
        //操作系统
        String OS_ID = "008";
        //应用系统
        String APPLICATION_ID = "009";
        //安全软件
        String SECURITY_ID = "010";
        //日志文件
        String LOG_ID = "011";
    }

    public ProbeConstant() {
    }
}
