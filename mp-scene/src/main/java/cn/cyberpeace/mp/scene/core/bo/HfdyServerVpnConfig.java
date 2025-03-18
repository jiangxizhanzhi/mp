package cn.cyberpeace.mp.scene.core.bo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class HfdyServerVpnConfig {
    private String rangeName;
    private String ip;
    private String attackLan;
}
