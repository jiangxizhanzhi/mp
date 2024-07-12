 // Copyright (C) 2024
 // All rights reserved.
 // Project name mp
 // Description:
 // Author:
 // File name ProbeConstant.java.
 // File size 1412.
 // File create date Fri Jul 12 12:55:26 CST 2024.
 // File update date Fri Jul 12 12:55:26 CST 2024.
 // Version 1.0.0
package cn.cyberpeace.mp.sceneplugincollection.constant;

/**
 * @ClassName ProbeConstant
 * @Description
 * @Author hewei
 * @Date 2024/07/02
 * @Version 1.0.0
 */
public class ProbeConstant {
    // 探针类型
    public interface ProbeType {
        // 信息类
        String MESSAGE_TYPE = "0";
        // 事件类
        String EVENT_TYPE = "1";
        // 日志类
        String LOG_TYPE = "2";
    }

    //探针ID
    public interface ProbeId {
        // 硬件信息
        String HARDWARE_INFO = "001";
        // 操作系统信息
        String OS_INFO = "002";
        // 网络接口信息
        String NETWORK_INTERFACE_INFO = "003";
        // 端口信息
        String PORT_INFO = "004";
        // 进程信息
        String PROCESS_INFO = "005";
        // 文件变更
        String FILE_MODIFY = "006";
        // 注册表变更
        String REGISTRY_MODIFY = "007";
        // 操作系统
        String OS_LOG = "008";
        // 应用系统
        String APPLICATION_LOG = "009";
        // 安全软件
        String SECURITY_LOG = "010";
        // 日志文件
        String LOG_FILE = "011";
        // 用户变更
        String USERS_MODIFY = "012";
        // 网络连接
        String INTERNET_CONNECTION = "013";
    }

    /**
     * ProbeConstant
     */
    public ProbeConstant() {
    }
}
