package cn.cyberpeace.mp.cscene.dto.scene;

import lombok.Data;

import java.util.List;

@Data
public class PortVlanRequest {
    private String csceneInstanceId;

    private List<ConnectPortRecord> connectPortRecordList;

}
