package cn.cyberpeace.mp.flow.v2.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultLong;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_flow_task_count")
public class FlowTaskCountModel extends BaseModel {
    @DefaultStr
    private String flowGeneratorId;
    @DefaultStr
    private String flowTaskRecordId;
    @DefaultStr
    private String flowId;
    @DefaultLong
    private Long bandwidth;
    @DefaultLong
    private Long tcpConnectTry;
    @DefaultLong
    private Long packets;
    @DefaultLong
    private Long tcp_connect_success;
}