package cn.cyberpeace.mp.flow.v2.client.trex;

/**
 * @Description
 * @Author maojun
 * @Date 2022/10/24
 */
public interface TrexUrlConstant {
    String INITIALIZATION_TREX = "/api/v1/task/network/init";
    String UPLOAD_PCAP = "/api/v1/resource/pcap/upload";
    String START_TASK = "/api/v1/task/v2/tasks/action";
    String TASK_STATUS = "/api/v1/task/tasks/task/status";
    String LAST_RECORDS = "/api/v1/task/tasks/task/last_records";
}
