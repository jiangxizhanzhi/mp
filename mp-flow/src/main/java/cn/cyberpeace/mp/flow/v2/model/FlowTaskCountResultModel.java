package cn.cyberpeace.mp.flow.v2.model;

import lombok.Data;

import java.util.Date;

/**
 * @Description
 * @Author maojun
 * @Date 2022/11/1
 */
@Data
public class FlowTaskCountResultModel {
    private String flowTaskRecordId;
    private String flowId;
    private Date time;
    private Long bandwidth;
    private Long packets;
    private Long tcpConnectSuccess;
    private Long tcpConnectTry;
}
