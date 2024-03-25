package cn.cyberpeace.mp.cscene.dto.scene;


import lombok.Data;

@Data
public class ConnectPortRecord {
    private String sourcePortId;
    private String targetPortId;
    private Integer vlanId;
}
