package cn.cyberpeace.mp.flow.v2.model;

import cn.cyberpeace.mp.base.model.BaseModel;
import cn.cyberpeace.mp.base.model.stereotype.DefaultInt;
import cn.cyberpeace.mp.base.model.stereotype.DefaultStr;
import cn.cyberpeace.mp.base.model.stereotype.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("mp_flow_flow_task_record")
public class FlowTaskRecordModel extends BaseModel {
    private String flowGeneratorId;
    private String targetIp;
    private String targetMac;
    private Integer status;

    private String name;
    @DefaultStr
    private String description;
    @DefaultInt
    private Integer category;
    @DefaultInt
    private Integer bandwidth;
    private String loadMode;
    @DefaultStr
    private String filterProtocols;
    @DefaultStr
    private String flows;
}
