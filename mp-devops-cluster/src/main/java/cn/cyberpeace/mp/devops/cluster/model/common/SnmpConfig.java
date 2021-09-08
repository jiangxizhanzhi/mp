package cn.cyberpeace.mp.devops.cluster.model.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpConfig {

    private String ip;

    private String communityName;

    private String privKey;

    private String username;

    private String authKey;
}
