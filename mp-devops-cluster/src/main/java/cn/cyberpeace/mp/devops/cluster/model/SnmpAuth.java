package cn.cyberpeace.mp.devops.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SnmpAuth {

    private String privKey;

    private String username;

    private String authKey;
}
